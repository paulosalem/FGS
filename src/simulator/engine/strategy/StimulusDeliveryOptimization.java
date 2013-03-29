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
package simulator.engine.strategy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import simulator.agent.IAgent;
import simulator.agent.IAgentControl;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.analysis.INumericProperty;
import simulator.analysis.IProperty;
import simulator.analysis.PropertyBearerWrapper;
import simulator.analysis.UndefinedPropertyException;
import simulator.components.ComponentInstantiationException;
import simulator.engine.SimulationState;
import simulator.engine.runner.ExecutionMode;
import simulator.engine.runner.SimulationRunException;
import simulator.engine.runner.SimulationRun.StorageMode;
import simulator.util.Assert;

/**
 * Given a stimulus, a positive integer K and a numeric property, finds out
 * a sequence of K agents (repeated or not) that maximizes the value
 * of the property if the stimulus is delivered to them in the beginning
 * of the simulation.
 * 
 * @author Paulo Salem
 *
 */
public class StimulusDeliveryOptimization extends SimulationStrategy {
  
  /**
   * The maximum number of simulation runs to be executed.
   */
  protected int runs = 1;
  
  
  /**
   * The maximum number of iterations per simulation run.
   */
  protected int iterationsPerRun = 10;
  
  private EnvironmentStimulus environmentStimulus;
  
  private IProperty property;
  
  private int propertyId;
  
  private INumericProperty numericProperty;

  private int numberOfAgents;
  
  /**
   * The property bearer to be examined by the chosen property.
   */
  private PropertyBearerWrapper propertyBearerWrapper;
  
  /**
   * The best sequence of agents to be stimulated.
   */
  private List<Integer> bestTarget;
  
  
  /**
   * The value of the desired property concerning the best sequence
   * of agents. 
   */
  private Double bestValue;
  
  

  public StimulusDeliveryOptimization(int runs, int iterationsPerRun, EnvironmentStimulus environmentStimulus, int propertyId, PropertyBearerWrapper propertyBearerWrapper, int numberOfAgents){
    super();
    Assert.nonNegativeNonZero(runs);
    Assert.nonNegativeNonZero(iterationsPerRun);
    
    this.name = "Stimulus Delivery Optimization";
    this.runs = runs;
    this.iterationsPerRun = iterationsPerRun;
    
    Assert.notNull(environmentStimulus);
    Assert.nonNegative(numberOfAgents);
    Assert.notNull(propertyBearerWrapper);
    
    this.environmentStimulus = environmentStimulus;
    this.propertyId = propertyId;
    this.numberOfAgents = numberOfAgents;
    this.propertyBearerWrapper = propertyBearerWrapper;
    
  }
  
  
  @Override
  public void execute() throws ComponentInstantiationException,
      UndefinedPropertyException, SimulationRunException {
    
    // Prepare to run
    runner.setupRun(StorageMode.NONE, ExecutionMode.EXPLORATION);

    // The current state is the initial one for now. We get it and set it up for the strategy.
    SimulationState initial = runner.getCurrentState();
    
    // Create a list containing all agents' IDs
    List<Integer> ids = new LinkedList<Integer>();
    for(IAgentControl ac: initial.getAgentControls()){
      ids.add(ac.getId());        
    }

    
    // TODO how to choose an efficient iteration method?
    
    // This will give us all the possible sequences of agents with
    // size less then or equal to the specified for this strategy
    //Iterator<List<Integer>> targets = new CombinationIterator<Integer>(ids, numberOfAgents);
    
    Iterator<List<Integer>> targets = new RandomCombinationIterator<Integer>(ids, numberOfAgents, 200);

    
    // Temporary values for the best target sequence
    bestTarget = ids.subList(0, numberOfAgents - 1);
    bestValue = Double.MIN_VALUE;

    
    

    // Iterate over all sequence of agents to find the best one
    while(targets.hasNext()){
      
      // Refresh the initial state
      runner.resetRun();
      
      // The current state of the run is the initial one
      initial = runner.getCurrentState();

      // Fetch the actual property to optmize
      IProperty p = initial.getProperty(propertyId);
      Assert.notNull(p);
      this.property = p;
      this.numericProperty = (INumericProperty) property;
      
      
      List<Integer> target = targets.next();

      //
      // Run several times
      //
      
      // We'll store the average value for each run here
      Double avgValue = 0.0;
      
      
      for(int r = 0; r < runs; r++){
        
        // Select agents
        for(Integer id: target){
          IAgent a = initial.getAgentControl(id).getAgent();
        
          // Deliver the stimulus to the agent
          a.receiveStimulus(environmentStimulus);
        }
        
        // Simulate
        runner.runSteps(iterationsPerRun);
        
        // Apply the relevant property to the appropriate bearer
        Double v = numericProperty.getValueAsDouble(propertyBearerWrapper);
        
        // Update the average value for this target
        avgValue = avgValue + v/(double)runs;
  
      }
      
      // Store the best result so far
      if(avgValue > bestValue){
        bestTarget = target;
        bestValue = avgValue;
      }
    } 

  }


  @Override
  public String toString() {
    String s = name + " strategy                \n";
    s = s +    "====================================================== \n";
    s = s +    " Best sequence of agents to deliver the stimulus:      \n";
    
    for(Integer id: bestTarget){
      s = s +  "  ID = " + id + " \n";
    }
    
    s = s +    " Optimized Property:                                   \n";
    s = s +    "   Property's name = "+ property.getName() + "         \n";
    s = s +    "   Property's ID = "+ property.getId() + "             \n";
    s = s +    "   Property's value = "+ bestValue + "           \n";
    
    return s;
  }

}
