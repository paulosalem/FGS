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
package simulator.environment;

import simulator.Scenario;
import simulator.engine.runner.ExecutionMode;
import simulator.util.Assert;
import alevos.ts.AnnotatedTransitionSystem;

/**
 * An abstract implementation for any environment that uses ALEVOS as an exogenous coordination
 * mechanism.
 * 
 * @author Paulo Salem
 *
 */
public abstract class ALEVOSEnvironment extends AbstractEnvironment implements IALEVOSEnvironment {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The transition system that defines the environment behavior.
   */
  protected transient AnnotatedTransitionSystem ats; // TODO transient ??
  
  public ALEVOSEnvironment(Scenario scenario, AnnotatedTransitionSystem ats) {
    super(scenario);
    Assert.notNull(ats);
    this.ats = ats;
  }

  @Override
  public void step(ExecutionMode mode) {
    if(mode.equals(ExecutionMode.EXPLORATION)){
      explorationStep();
    }
    else if(mode.equals(ExecutionMode.VERIFICATION)){
      verificationStep();
    }
    else{
      throw new IllegalArgumentException("The requested execution mode is invalid.");
    }
    
  }
  
  protected abstract void verificationStep();
  
  protected abstract void explorationStep();

  /////////////////////////////////////////////////////////////////////////////
  // IALEVOSEnvironment methods
  /////////////////////////////////////////////////////////////////////////////

  @Override
  public AnnotatedTransitionSystem getATS() {
    return ats;
  }



  

}
