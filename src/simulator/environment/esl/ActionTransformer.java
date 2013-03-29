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

import java.util.LinkedList;
import java.util.List;

import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiChoice;
import alevos.expression.picalculus.PiDefinition;
import alevos.expression.picalculus.PiIdentifier;
import alevos.expression.picalculus.PiInputAction;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiOutputAction;
import alevos.expression.picalculus.PiPrefix;
import alevos.expression.picalculus.PiProcess;

public class ActionTransformer extends ESLStructure {
  
  AgentProfile agent1;
  
  Action action;
  
  Stimulus stimulus;
  
  AgentProfile agent2;
  
  static PiDefinition tDef = null;

  public ActionTransformer(AgentProfile agent1, Action action,
      Stimulus stimulus, AgentProfile agent2) {
    super();
    this.agent1 = agent1;
    this.action = action;
    this.stimulus = stimulus;
    this.agent2 = agent2;
    
    if(tDef == null){
      initialize();
    }
  }
  
  
  static private void initialize(){
    
    PiName beginning = new PiName(NAME_BEGINNING);
    PiName stable = new PiName(NAME_STABLE);
    PiName ending = new PiName(NAME_ENDING);
    PiName absent = new PiName(NAME_ABSENT);
     
    PiName emit = new PiName(NAME_EMIT);
    PiName stop = new PiName(NAME_STOP);
    
    PiName destroy = new PiName(NAME_DESTROY);
    
    PiName x = new PiName("x"); // A dummy parameter name

    PiOutputAction aBeginning = new PiOutputAction(beginning, x);
    PiOutputAction aStable = new PiOutputAction(stable, x);
    PiOutputAction aEnding = new PiOutputAction(ending, x);
    PiOutputAction aAbsent = new PiOutputAction(absent, x);
    
    PiInputAction aEmit = new PiInputAction(emit, x);
    PiInputAction aStop = new PiInputAction(stop, x);
    PiInputAction aDestroy = new PiInputAction(destroy, x);
    
    //
    // Setup the identifier
    //
    
    PiIdentifier t = new PiIdentifier("T", emit, stop, absent, beginning, stable, ending, destroy);
    
    
    //
    // Compose the definition
    //
    
    
    PiPrefix normal = new PiPrefix(t, aEmit, aBeginning, aStable, aStop, aEnding, aAbsent);
    mark(normal); // TODO it is not marking all processes, only the top-most on the tree of proc!!!
    
    PiPrefix terminate = new PiPrefix(aDestroy);
    mark(terminate);
    
    PiChoice choice = new PiChoice(normal, terminate);
    
    
    List<PiName> parameters = new LinkedList<PiName>();
    parameters.add(emit);
    parameters.add(stop);
    parameters.add(absent);
    parameters.add(beginning);
    parameters.add(stable);
    parameters.add(ending);
    parameters.add(destroy);
    
    tDef = new PiDefinition(t.getName(), parameters, choice);
    t.setDefinition(tDef);
  }



  @Override
  public PiProcess toPiProcess(Context context) throws IllegalSemanticsException {
    
    PiName beginning = new PiName(NAME_BEGINNING);
    beginning.setDecorator("agentId", agent2.getIdentifier());
    beginning.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    
    PiName stable = new PiName(NAME_STABLE);
    stable.setDecorator("agentId", agent2.getIdentifier());
    stable.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    
    PiName ending = new PiName(NAME_ENDING);
    ending.setDecorator("agentId", agent2.getIdentifier());
    ending.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    
    PiName absent = new PiName(NAME_ABSENT);
    absent.setDecorator("agentId", agent2.getIdentifier());
    absent.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    
    PiName emit = new PiName(NAME_EMIT);
    emit.setDecorator("agentId", agent1.getIdentifier());
    emit.setDecorator("action", action.toEnvironmentAction());
    
    PiName stop = new PiName(NAME_STOP);
    stop.setDecorator("agentId", agent1.getIdentifier());
    stop.setDecorator("action", action.toEnvironmentAction());

    PiName destroy = new PiName(NAME_DESTROY);
    destroy.setDecorator("agentId1", agent1.getIdentifier());
    destroy.setDecorator("stimulus", stimulus.toEnvironmentStimulus());
    destroy.setDecorator("action", action.toEnvironmentAction());
    destroy.setDecorator("agentId2", agent2.getIdentifier());

    
    // Setup the identifier
    PiIdentifier t = new PiIdentifier("T", emit, stop, absent, beginning, stable, ending, destroy);
    t.setDefinition(tDef);
    
    return t;
    
  }

  private static void mark(PiProcess proc){
    proc.setMarker(MARKER_ACTION_TRANSFORMER);
    proc.addIncompatibleMarker(MARKER_ACTION_TRANSFORMER);
  }
}
