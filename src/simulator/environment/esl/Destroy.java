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
package simulator.environment.esl;

import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiOutputAction;
import alevos.expression.picalculus.PiPrefix;
import alevos.expression.picalculus.PiProcess;

public class Destroy extends ESLOperation {
  
  AgentProfile agent1;
  
  Stimulus stimulus;
  
  Action action;
  
  AgentProfile agent2;
  
  

  public Destroy(AgentProfile agent1, Action action, Stimulus stimulus,
      AgentProfile agent2) {
    super();
    this.agent1 = agent1;
    this.stimulus = stimulus;
    this.action = action;
    this.agent2 = agent2;
  }



  @Override
  public PiProcess toPiProcess(Context context) throws IllegalSemanticsException {
    
    PiName destroy = new PiName(NAME_DESTROY);
    destroy.setDecorator("agentId1", agent1.getIdentifier());
    destroy.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    destroy.setDecorator("action", action.toEnvironmentAction());
    destroy.setDecorator("agentId2", agent2.getIdentifier());
    
    PiName done = new PiName(NAME_DONE);
    
    PiName x = new PiName("x");
    
    PiOutputAction aDestroy = new PiOutputAction(destroy, x);
    PiOutputAction aDone = new PiOutputAction(done, x);
    
    return new PiPrefix(aDestroy, aDone);
  }

}
