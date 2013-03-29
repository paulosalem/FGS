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
import alevos.expression.picalculus.PiInputAction;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiProcess;

public class EnvironmentResponse extends ESLOperation {
  
  Action action;
  
  AgentProfile agent;
  
  ESLOperation operation;
  
  

  public EnvironmentResponse(Action action, AgentProfile agent,
      ESLOperation operation) {
    super();
    this.action = action;
    this.agent = agent;
    this.operation = operation;
  }



  @Override
  public PiProcess toPiProcess(Context context) throws IllegalSemanticsException {
    
    PiName emit = new PiName(NAME_EMIT);
    emit.setDecorator("agentId", agent.getIdentifier());
    emit.setDecorator("action", action.toEnvironmentAction());
    
    PiName stop = new PiName(NAME_STOP);
    stop.setDecorator("agentId", agent.getIdentifier());
    stop.setDecorator("action", action.toEnvironmentAction());
    
    
    PiName x = new PiName("x"); // A dummy parameter name
    
    PiInputAction aEmit = new PiInputAction(emit, x);
    PiInputAction aStop = new PiInputAction(stop, x);
    

    // We shall build something like this:
    //
    //      Forever(emit action ; operation; stop action)
    
    PiActionPrefixOperation emitOp = new PiActionPrefixOperation(aEmit);
    PiActionPrefixOperation stopOp = new PiActionPrefixOperation(aStop);
    SequentialComposition sc = new SequentialComposition(emitOp, operation, stopOp);
    UnboundedSequence er = new UnboundedSequence(sc);
    
    return er.toPiProcess(context);
  }

}
