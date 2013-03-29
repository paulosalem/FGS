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
import simulator.engine.SimulationState;
import simulator.engine.runner.SimulationRunException;
import simulator.util.Assert;



/**
 * A simple simulation strategy. It just runs the model and present
 * the final result.
 * 
 * @author  Paulo Salem
 */
public class StandardSimulation extends SimulationStrategy{

  /**
   * The maximum number of simulation runs to be executed.
   */
  protected int runs = 1;
  
  
  /**
   * The maximum number of iterations per simulation run.
   */
  protected int iterationsPerRun = 10;
  
  /**
   * Builds a new instance.
   * 
   * @param runs The maximum number of simulation runs to be executed.
   * @param iterationsPerRun The maximum number of iterations per simulation run.
   */
  public StandardSimulation(int runs, int iterationsPerRun) {
    super();
    Assert.nonNegativeNonZero(runs);
    Assert.nonNegativeNonZero(iterationsPerRun);
    
    this.name = "Standard Simulation";
    this.runs = runs;
    this.iterationsPerRun = iterationsPerRun;
    

  }
  
  @Override
  public void execute() throws ComponentInstantiationException, UndefinedPropertyException, SimulationRunException{
    
    for(int i = 0; i < runs; i++){
      runner.runSteps(iterationsPerRun);
    }
    

    
  }

@Override
  public String toString() {
    String s = "Standard simulation strategy                           \n";
    s = s +    "====================================================== \n";
    s = s + runner.getCurrentState().toString() + "\n";
    
    return s;
  }
  
  
  /**
   * 
   * @return The last simulation state.
   */
  public SimulationState getLastState(){
    return runner.getCurrentState();
  }

  
}
