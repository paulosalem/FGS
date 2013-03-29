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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiChoice;
import alevos.expression.picalculus.PiDefinition;
import alevos.expression.picalculus.PiIdentifier;
import alevos.expression.picalculus.PiInputAction;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiNilProcess;
import alevos.expression.picalculus.PiOutputAction;
import alevos.expression.picalculus.PiParallel;
import alevos.expression.picalculus.PiPrefix;
import alevos.expression.picalculus.PiProcess;
import alevos.expression.picalculus.PiReplication;
import alevos.expression.picalculus.PiRestriction;

public class Environment extends ESLStructure {
  
  Collection<AgentProfile> agents;
  
  Collection<ActionTransformer> actionTransformers;
  
  Collection<ESLOperation> behaviors;
  
  
  static PiDefinition tDef = null;
  
  
  public Environment(Collection<AgentProfile> agents,
      Collection<ActionTransformer> actionTransformers, Collection<ESLOperation> behaviors) {
    super();
    this.agents = agents;
    this.actionTransformers = actionTransformers;
    this.behaviors = behaviors;
    
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
    
    PiChoice choice = new PiChoice(normal, new PiPrefix(aDestroy));
    
    
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
    
    PiName x = new PiName("x"); // Some dummy parameter name
    
    PiName clock = new PiName(NAME_COMMIT); // The commit name
    
    // The commit event
    PiOutputAction aClock = new PiOutputAction(clock, x);


    //
    // Setup the creation of new action transformers
    //
    PiProcess newAT = buildNewAT(); 
    
    PiReplication repNewAT = new PiReplication(newAT);

    
    //
    // Put components in parallel
    //
    
    List<PiProcess> procs = new LinkedList<PiProcess>();
    
    for(AgentProfile ag: agents){
      procs.add(ag.toPiProcess(context));
    }
    
    for(ActionTransformer at: actionTransformers){
      procs.add(at.toPiProcess(context));
    }
    
    for(ESLOperation op: behaviors){
      procs.add(op.toPiProcess(context));
    }
    
    procs.add(repNewAT);
    
      
    PiProcess time = new PiReplication(new PiPrefix(aClock, new PiNilProcess()));
    procs.add(time);
    
    PiParallel par = new PiParallel(procs);
    
    
    
    
    // TODO alternative construction, changing the order. Erase if you give up changing the order..
    /*
         List<PiProcess> procs = new LinkedList<PiProcess>();
    
    PiProcess time = new PiReplication(new PiPrefix(aClock, new PiNilProcess()));
    procs.add(time);
    
    List<PiProcess> procsBehav = new LinkedList<PiProcess>();
    for(ESLOperation op: behaviors){
      
      procsBehav.add(op.toPiProcess(context));
    }
    
    procs.add(repNewAT);    
      
    List<PiProcess> procsAT = new LinkedList<PiProcess>();
    for(ActionTransformer at: actionTransformers){
      procsAT.add(at.toPiProcess(context));
    }
    
    List<PiProcess> procsAgents = new LinkedList<PiProcess>();
    for(AgentProfile ag: agents){
      procsAgents.add(ag.toPiProcess(context));
    }

    PiParallel parGroup1 = new PiParallel(new PiParallel(procsAT), new PiParallel(procsAgents));
    PiParallel parGroup2 = new PiParallel(new PiParallel(procsBehav) ,parGroup1);
    
    PiParallel par = new PiParallel(new PiParallel(procs), parGroup2);
      
     */
     
    
    
    
    //
    // Restrict the environment names and we are done
    //
    List<PiName> resNames = buildResNames();
    
    PiRestriction environment = new PiRestriction(resNames, par);
    
    return environment;
  }
  
  private List<PiName> buildResNames(){
    
    List<PiName> resNames = new LinkedList<PiName>(); 
    
    PiName ccn = new PiName(NAME_CREATE);
    resNames.add(ccn);
    
    PiName beginning = new PiName(NAME_BEGINNING);
    resNames.add(beginning);
    
    PiName stable = new PiName(NAME_STABLE);
    resNames.add(stable);
    
    PiName ending = new PiName(NAME_ENDING);
    resNames.add(ending);
    
    PiName absent = new PiName(NAME_ABSENT);
    resNames.add(absent);
    
    PiName emit = new PiName(NAME_EMIT);
    resNames.add(emit);
    
    PiName stop = new PiName(NAME_STOP);
    resNames.add(stop);
    
    PiName destroy = new PiName(NAME_DESTROY);
    resNames.add(destroy);
    
    PiName done = new PiName(NAME_DONE);
    resNames.add(done);
    
    return resNames;
  }

  
  private PiProcess buildNewAT(){
    
    PiName ccn = new PiName(NAME_CREATE);
    PiName beginning = new PiName(NAME_BEGINNING);
    PiName stable = new PiName(NAME_STABLE);
    PiName ending = new PiName(NAME_ENDING);
    PiName absent = new PiName(NAME_ABSENT);
    PiName emit = new PiName(NAME_EMIT);    
    PiName stop = new PiName(NAME_STOP);
    PiName destroy = new PiName(NAME_DESTROY);

    // The channel to invoke the new action transformer
    PiInputAction aCcn = new PiInputAction(ccn, emit, stop, absent, beginning, stable, ending, destroy);
    
    // Setup the identifier for the transformer
    PiIdentifier t = new PiIdentifier("T", emit, stop, absent, beginning, stable, ending, destroy);
    t.setDefinition(tDef);
    
    // Put everything together
    return new PiPrefix(aCcn, t);
    
    
  }
}
