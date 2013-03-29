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

import java.util.ArrayList;
import java.util.List;

import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.analysis.PropertyBearerWrapper;
import simulator.engine.strategy.ALEVOSRandomExploration;
import simulator.engine.strategy.SimulationPurposeVerification;
import simulator.engine.strategy.SimulationStrategy;
import simulator.engine.strategy.StandardSimulation;
import simulator.engine.strategy.StimulusDeliveryOptimization;
import simulator.util.Assert;
import alevos.ts.sp.SimulationPurpose;
import alevos.verification.VerificationAlgorithm;


/**
 * Represents a simulation experiment. An experiment is, essentially, a collection
 * of simulation strategies to be executed. 
 * 
 * @author Paulo Salem
 *
 */
public class Experiment {

 
  /**
   * A user friendly name.
   */
  private String name;
  
  
  /**
   * A user friendly description.
   */
  private String description;
  
  
  public Experiment(String name, String description){
    Assert.notNull(name);
    Assert.notNull(description);
    
    this.name = name;
    this.description = description;
  }
  
  /**
   * The simulation strategies to be adopted during the experiment.
   */
  private List<SimulationStrategy> strategies = new ArrayList<SimulationStrategy>();
  
  
  /**
   * @return The simulation strategies to be adopted during the experiment.
   */
  public List<SimulationStrategy> getSimulationStrategies(){

    return strategies;
  }



  
  /////////////////////////////////////////////////////////////////////////////
  // Convenience methods to add new strategies
  /////////////////////////////////////////////////////////////////////////////
   
  
  public void addStandardSimulationStrategy(int runs, int iterationsPerRun){

    SimulationStrategy ss = new StandardSimulation(runs, iterationsPerRun);
    strategies.add(ss);
  }
  
  public void addSimPurpVerifStrategy(SimulationPurpose sp, VerificationAlgorithm va, String group){
    SimulationPurposeVerification spv = new SimulationPurposeVerification(sp, va, group);
    strategies.add(spv);
  }
  
  public void addALEVOSRandomExplorationStrategy(int runs, int iterationsPerRun, String group){
    ALEVOSRandomExploration are = new ALEVOSRandomExploration(runs, iterationsPerRun, group);
    strategies.add(are);
  }

  
  public void addStimulusDeliveryOptimization(int runs, int iterationsPerRun, EnvironmentStimulus environmentStimulus, int propertyId, PropertyBearerWrapper pbw, int targets){
    Assert.notNull(environmentStimulus);
    
    SimulationStrategy ss = new StimulusDeliveryOptimization(runs, iterationsPerRun, environmentStimulus, propertyId, pbw, targets);
    strategies.add(ss);
  }
  
  
  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }
}
