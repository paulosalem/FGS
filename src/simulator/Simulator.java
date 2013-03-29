/*******************************************************************************
 * FGS - Formally Guided Simulator
 * 
 * This software was developed by Paulo Salem da Silva for his doctoral thesis, 
 * which is entitled
 *   
 *   "Verification of Behaviourist Multi-Agent Systems by means of 
 *    Formally Guided Simulations"
 * 
 * This software, therefore, constitutes a companion to the thesis. As such, 
 * it should be seen as an experimental product, suitable for research purposes,
 * but not ready for production.
 * 
 * 
 * Copyright (c) 2008 - 2012, Paulo Salem da Silva
 * All rights reserved.
 * 
 * This software may be used, modified and distributed freely, provided that the 
 * following rules are followed:
 * 
 *   (i)   this copyright notice must be maintained in any redistribution, in both 
 *         original and modified form,  of this software;
 *   (ii)  this software must be provided free of charge, although services which 
 *         require the software may be charged;
 *   (iii) for non-commercial purposes, this software may be used, modified and 
 *         distributed free of charge;
 *   (iv)  for commercial purposes, only the original, unmodified, version of this 
 *         software may be used.
 * 
 * For other uses of the software, please contact the author.
 ******************************************************************************/
package simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import simulator.analysis.UndefinedPropertyException;
import simulator.components.ComponentInstantiationException;
import simulator.components.ComponentsRegistry;
import simulator.engine.SimulationEngine;
import simulator.engine.runner.SimulationRunException;
import simulator.engine.strategy.SimulationStrategy;
import simulator.engine.strategy.StandardSimulation;
import simulator.environment.InvalidEnvironmentException;
import simulator.io.ComponentsLoader;
import simulator.io.ExperimentLoadingException;
import simulator.io.ScenarioLoadingException;
import simulator.io.XMLExperimentLoader;
import simulator.io.XMLScenarioLoader;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import simulator.util.Assert;


/**
 * The main simulator class, which holds also its several configurations. This class is provided
 * as singleton since there is no need to be instantiated twice and in this way access to
 * configuration information is facilitated.
 * 
 * @author Paulo Salem
 */
public class Simulator {
  
  private static Simulator instance = null;
  
  private final String PROP_REPOSITORY_PATH = "Repository's Path";
  private final String PROP_LIBRARIES_PATH = "Libraries' Path";
  
  /**
   * The simulator's component registry.
   * 
   */
  private ComponentsRegistry cr = null;
  
  /**
   * The propertie's file where simulator preferences are stored.
   */
  private File preferencesFile = new File("preferences.xml");
  
  /**
   * The folder where the component repository is.
   */
  private File repositoryPath;
  
  /**
   * The folder where the libraries used by the components are.
   */
  private File librariesPath;

  /**
   * The maximum depth of algorithms based on search.
   */
  private int maxDepth = 1000;

  /**
   * Whether non-deterministic choices should be randomized.
   */
  private boolean randomize = true;
  
  /**
   * The maximum number of synchronizations algoritms depending on synchronous products
   * should perform. The <code>null</code> value indicates that no such limit exists
   * (i.e., it is infinite).
   */
  private Integer maxSynchSteps = null;
  
  
  /**
   * The simulation scenario.
   */
  private Scenario scenario = null;
  
  
  /**
   * The experiment to be performed.
   */
  private Experiment experiment = null;
  
  
  private Simulator() throws FileNotFoundException, 
                            IOException, 
                            ClassNotFoundException, 
                            IllegalAccessException, 
                            InstantiationException {

    // Load preferences
    loadPreferences();
    
  	// Create a new registry
    this.cr = new ComponentsRegistry();
    
	  
    // Load known components
    loadComponents();
  }
  
  public static Simulator instance() throws FileNotFoundException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException{
    if(instance == null){
      instance = new Simulator();
    }
    
    return instance;
  }
  
  /**
   * Performs the experiment loaded in the simulator.
   * 
   * @throws ComponentInstantiationException In case the necessary components
   * cannot be instantiated.
   * @throws SimulationRunException 
   * @throws InvalidEnvironmentException 
   */
  public void runExperiment() throws ComponentInstantiationException, UndefinedPropertyException, InvalidEnvironmentException, SimulationRunException{
    
    Assert.notNull(experiment, "An experiment must be loaded in order to proceed.");

    List<SimulationStrategy> strategies = experiment.getSimulationStrategies();
    
    SimulationEngine se = new SimulationEngine(scenario, strategies, cr);
    
    
    // Run the simulator.engine
    se.executeStrategies();
    
    // Print strategies results
    for(SimulationStrategy ss: strategies){
      SimulatorUI.instance().getMessenger().printMsg("\n"+ss.toString(), Messenger.CRUCIAL_MSG);
    }
    
  }
 

  
  /**
   * Loads a scenario description from the specified XML file.
   * 
   * @param f A XML file containing the scenario description.
   * 
   * @throws ScenarioLoadingException If for some reason the scenario
   * cannot be loaded, this exception is thrown.
   */
  public void loadScenario(File f) throws ScenarioLoadingException{
	  Assert.notNull(f);
	  
	  XMLScenarioLoader loader = new XMLScenarioLoader(cr);
	  
	  scenario = loader.loadScenario(f);
  }

  
  /**
   * Loads an experiment description from the specified XML file.
   * 
   * @param f A XML file containing the experiment description.
   * 
   * @throws ExperimentLoadingException If for some reason the experiment
   * cannot be loaded, this exception is thrown.
   */
  public void loadExperiment(File f) throws ExperimentLoadingException, ComponentInstantiationException{
    Assert.notNull(f);
    
    XMLExperimentLoader loader = new XMLExperimentLoader(scenario.createInitialState(cr).getEnvironment());
    
    experiment = loader.loadExperiment(f);
  }
  
  
  public void setScenario(Scenario scenario){
	Assert.notNull(scenario);
    
    this.scenario = scenario;    
  }
  
  public void setExperiment(Experiment experiment){
    Assert.notNull(experiment);
    
    this.experiment = experiment;
  }
  
  
  
  
  public int getMaxDepth() {
    return maxDepth;
  }

  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  public boolean getRandomize() {
    return randomize;
  }

  public void setRandomize(boolean randomize) {
    this.randomize = randomize;
  }

  public Integer getMaxSynchSteps() {
    return maxSynchSteps;
  }

  public void setMaxSynchSteps(Integer maxSynchSteps) {
    this.maxSynchSteps = maxSynchSteps;
  }

  /**
   * Loads the simulator preferences.
   *
   */
  private void loadPreferences() throws FileNotFoundException, IOException{
    
    SimulatorUI.instance().getMessenger().printMsg("\nLoading preferences from " + preferencesFile.getAbsolutePath() + "...", Messenger.NORMAL_MSG);
    
    Properties ps = new Properties();
    
    ps.loadFromXML(new FileInputStream(preferencesFile));
    
    
    String rp = ps.getProperty(PROP_REPOSITORY_PATH, System.getProperty("user.home") + "/simulation-components/").trim();
    String lp = ps.getProperty(PROP_LIBRARIES_PATH, System.getProperty("user.home") + "/simulation-libraries/").trim();
    
    SimulatorUI.instance().getMessenger().printMsg("  Repository's Path: " + rp, Messenger.NORMAL_MSG);
    SimulatorUI.instance().getMessenger().printMsg("  Libraries' Path: " + lp, Messenger.NORMAL_MSG);
    
    repositoryPath = new File(rp);
    librariesPath = new File(lp);
    
    SimulatorUI.instance().getMessenger().printMsg("Preferences have been loaded.", Messenger.NORMAL_MSG);
  }

  
  /**
   * Loads the available components.
   * 
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */  
  private void loadComponents() throws FileNotFoundException, 
                                         IOException, 
                                         ClassNotFoundException, 
                                         IllegalAccessException, 
                                         InstantiationException {
    
    SimulatorUI.instance().getMessenger().printMsg("\nLoading components...", Messenger.NORMAL_MSG);
    
    ComponentsLoader cl = new ComponentsLoader(cr, repositoryPath, librariesPath);
    cl.loadComponents();
    
    SimulatorUI.instance().getMessenger().printMsg(cr.size() + " components have been loaded.", Messenger.NORMAL_MSG);
  }

}
