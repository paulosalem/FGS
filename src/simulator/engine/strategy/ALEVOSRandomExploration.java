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

import simulator.analysis.UndefinedPropertyException;
import simulator.components.ComponentInstantiationException;
import simulator.engine.runner.SimulationRunException;
import simulator.environment.IALEVOSEnvironment;
import simulator.environment.IEnvironment;
import simulator.environment.InvalidEnvironmentException;
import simulator.util.Assert;
import alevos.IllegalSemanticsException;
import alevos.exploration.RandomWalkExploration;
import alevos.simulation.InvalidSimulatorRequest;
import alevos.simulation.SimulatorConnector;
import alevos.ts.AnnotatedTransitionSystem;

public class ALEVOSRandomExploration extends ALEVOSSimulationStrategy {

  /**
   * The maximum number of simulation runs to be executed.
   */
  protected int runs = 1;
  
  
  /**
   * The maximum number of iterations per simulation run.
   */
  protected int iterationsPerRun = 100;
  
  
  /**
   * Exploration algorithm.
   */
  protected RandomWalkExploration rwe;
  
  /**
   * Builds a new instance.
   * 
   * @param runs The maximum number of simulation runs to be executed.
   * @param iterationsPerRun The maximum number of iterations per simulation run.
   */
  public ALEVOSRandomExploration(int runs, int iterationsPerRun, String group) {
    super(group);
    Assert.nonNegativeNonZero(runs);
    Assert.nonNegativeNonZero(iterationsPerRun);
    
    this.name = "ALEVOS Random Exploration";
    this.runs = runs;
    this.iterationsPerRun = iterationsPerRun;
    this.rwe = new RandomWalkExploration(runs, iterationsPerRun);
  }
  
  
  @Override
  public void execute() throws ComponentInstantiationException,
      UndefinedPropertyException, InvalidEnvironmentException,
      SimulationRunException {
    
    IEnvironment environment = this.runner.getInitialEnvironment();
    AnnotatedTransitionSystem ats = ((IALEVOSEnvironment) environment).getATS();
    
    try {
      rwe.explore(ats, simulatorConnector);
      
    } catch (IllegalSemanticsException e) {
      
      throw new SimulationRunException("The semantics expected from a transition system was violated.", e);
    
    } catch (InvalidSimulatorRequest e) {
      
      throw new SimulationRunException("ALEVOS made an invalid request to the simulator.", e);
    }

  }

  @Override
  public String toString() {
    String s = "Random Exploration strategy                     \n";
    s = s +    "====================================================== \n";
    s = s +    "Running time = " + this.runningTime() / 1000000000 + "s";
    
    return s;
  }

}
