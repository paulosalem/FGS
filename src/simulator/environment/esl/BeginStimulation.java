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

public class BeginStimulation extends ESLOperation {
  
  Stimulus stimulus;
  
  AgentProfile agent;
  
  

  public BeginStimulation(Stimulus stimulus, AgentProfile agent) {
    super();
    this.stimulus = stimulus;
    this.agent = agent;
  }



  @Override
  public PiProcess toPiProcess(Context context) throws IllegalSemanticsException {
    
    PiName beginning = new PiName(NAME_BEGINNING);
    beginning.setDecorator("agentId", agent.getIdentifier());
    beginning.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    
    PiName stable = new PiName(NAME_STABLE);
    stable.setDecorator("agentId", agent.getIdentifier());
    stable.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    
    PiName done = new PiName(NAME_DONE);
    
    PiName x = new PiName("x"); // A dummy parameter name
    
    PiOutputAction aBeginning = new PiOutputAction(beginning, x);
    PiOutputAction aStable = new PiOutputAction(stable, x);
    PiOutputAction aDone = new PiOutputAction(done, x);
    
    return new PiPrefix(aBeginning, aStable, aDone);
  }

}
