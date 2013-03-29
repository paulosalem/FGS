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
package simulator.engine;

import java.util.Collection;
import java.util.LinkedList;

import simulator.Scenario;
import simulator.analysis.UndefinedPropertyException;
import simulator.components.ComponentInstantiationException;
import simulator.components.ComponentsRegistry;
import simulator.engine.alevos.EMMASSimulatorConnector;
import simulator.engine.runner.SimulationRunException;
import simulator.engine.runner.SimulationRunner;
import simulator.engine.strategy.ALEVOSSimulationStrategy;
import simulator.engine.strategy.SimulationStrategy;
import simulator.environment.InvalidEnvironmentException;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import simulator.visualization.StateVisualizer;


/**
 * Manages the execution of the simulation experiment.
 * 
 * @author     Paulo Salem
 */
public class SimulationEngine {
  
  // TODO Idea: there could be one SimulationRunner for each SimulationStrategy, and all of them
  //            could run in parallel.
  
  private SimulationRunner runner;
  
  private Collection<SimulationStrategy> strategies = new LinkedList<SimulationStrategy>();
  
  private Scenario scenario = null;
  
  private ComponentsRegistry registry = null;
  
  
  /**
   * Creates a new <code>SimulationEngine</code>.
   * 
   * @param scenario The simulation scenario to be executed.
   * 
   * @param strategy The simulation strategy to be employed.
   * @throws ComponentInstantiationException If there are problems while building the initial state.
   */
  public SimulationEngine(Scenario scenario, Collection<SimulationStrategy> strategies, ComponentsRegistry registry) throws ComponentInstantiationException{

    // Check parameters
    if(scenario == null || strategies == null || registry == null){
      throw new IllegalArgumentException();
    }
    
    this.strategies = strategies;
    this.scenario = scenario;
    this.registry = registry;
    
    runner = new SimulationRunner(scenario.createInitialState(registry));
    
    // Setup strategies
    for(SimulationStrategy ss: strategies){
      
      // Connect the strategy to the appropriate simulation runner
      ss.setSimulationRunner(runner);
      
      // If it is an ALEVOS strategy, we shall also need a simulator connector
      if(ss instanceof ALEVOSSimulationStrategy){
        if(scenario.getEMMAS() != null){
          ((ALEVOSSimulationStrategy)ss).setSimulatorConnector(new EMMASSimulatorConnector(runner));
        }
        else{
          throw new IllegalArgumentException("Any ALEVOS simulation strategy must have an appropriate simulator connector.");
        }
      }
    }
  }
  
  /**
   * Runs the simulation. Each new call to this method shall
   * replace the results obtained with the previous one.
   * @throws SimulationRunException 
   * @throws InvalidEnvironmentException 
   */
  public void executeStrategies() throws ComponentInstantiationException, UndefinedPropertyException, InvalidEnvironmentException, SimulationRunException{
    
    // Run all the strategies
    for(SimulationStrategy ss: strategies){
      SimulatorUI.instance().getMessenger().printMsg("\nRunning the " + ss.getName() + " strategy...", Messenger.IMPORTANT_MSG);
      ss.startChronometer();
      ss.execute();
      ss.stopChronometer();
    }
    
  }
  
  /**
   * 
   * @return The current state of the simulation.
   */
  public SimulationState getCurrentState(){
    return runner.getCurrentState();
  }
  
/**
 * @return  The simulation scenario of the engine.
 */
  public Scenario getScenario(){
    return scenario;
  }
  
  /**
   * 
   * @return The components registry of the engine.
   */
  public ComponentsRegistry getComponentsRegistry(){
    return registry;
  }
  

}
