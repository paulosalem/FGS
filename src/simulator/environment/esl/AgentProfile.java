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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.engine.alevos.PiEMMASAgentProfile;
import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiDefinition;
import alevos.expression.picalculus.PiIdentifier;
import alevos.expression.picalculus.PiInputAction;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiOutputAction;
import alevos.expression.picalculus.PiParallel;
import alevos.expression.picalculus.PiPrefix;
import alevos.expression.picalculus.PiProcess;
import alevos.expression.picalculus.PiReplication;

public class AgentProfile extends ESLStructure {
  
  Integer identifier;
  
  Set<Action> actions;
  
  Set<Stimulus> stimuli;
  
  static PiDefinition stimDef = null;

  
  public AgentProfile(Integer identifier, Set<Action> actions,
      Set<Stimulus> stimuli) {
    super();
    this.identifier = identifier;
    this.actions = actions;
    this.stimuli = stimuli;
    
    if(stimDef == null){
      initialize();
    }
  }
  
  
  static private void initialize(){
    PiName beginning = new PiName(NAME_BEGINNING);
    PiName stable = new PiName(NAME_STABLE);
    PiName ending = new PiName(NAME_ENDING);
    PiName absent = new PiName(NAME_ABSENT);
    
    PiName x = new PiName("x"); // A dummy parameter name

    PiInputAction aBeginning = new PiInputAction(beginning, x);
    PiInputAction aStable = new PiInputAction(stable, x);
    PiInputAction aEnding = new PiInputAction(ending, x);
    PiInputAction aAbsent = new PiInputAction(absent, x);
    
    //
    // Setup the identifier
    //
    
    PiIdentifier piStim = new PiIdentifier("piStim", beginning, stable, ending, absent);
    
    
    //
    // Compose the definition
    //
    
    PiPrefix proc = new PiPrefix(piStim, aBeginning, aStable, aEnding, aAbsent);
    mark(proc); // TODO it is not marking all processes, only the top-most on the tree of proc!!!
    
    List<PiName> parameters = new LinkedList<PiName>();
    parameters.add(beginning);
    parameters.add(stable);
    parameters.add(ending);
    parameters.add(absent);
    
    stimDef = new PiDefinition(piStim.getName(), parameters, proc);
    piStim.setDefinition(stimDef);
  }



  @Override
  public PiProcess toPiProcess(Context context) throws IllegalSemanticsException {
    
    //return compressedAgent();
    
    
    // TODO Uncomment to build the agent in the normal way (i.e., using pi-calculus explicitly)
    //
    return normalAgent();
  }
  
  // Testing
  private PiProcess compressedAgent(){
    
    Set<EnvironmentAction> eas = new HashSet<EnvironmentAction>();
    Set<EnvironmentStimulus> ess = new HashSet<EnvironmentStimulus>();
    
    for(Action a: actions){
      eas.add(a.toEnvironmentAction());
    }
    
    for(Stimulus s: stimuli){
      ess.add(s.toEnvironmentStimulus());
    }
    
    return new PiEMMASAgentProfile(identifier, eas, ess);
    
  }
  
  private PiProcess normalAgent(){
    
    //
    // Put components in parallel
    //
    
    List<PiProcess> procs = new LinkedList<PiProcess>();
    
    for(Action a: actions){
      procs.add(act(a, identifier));
    }
    
    for(Stimulus s: stimuli){
      procs.add(stim(s, identifier));
    }
    
    PiParallel agent = new PiParallel(procs);
    
    return agent;
  }
  
  private PiProcess act(Action a, Integer id){
    
    PiName emi = new PiName(NAME_EMIT);
    emi.setDecorator("agentId", id);
    emi.setDecorator("action", a.toEnvironmentAction());
    
    PiName sto = new PiName(NAME_STOP);
    sto.setDecorator("agentId", id);
    sto.setDecorator("action", a.toEnvironmentAction());
    
    PiName x = new PiName("x"); // Some dummy parameter name
    
    PiOutputAction aEmi = new PiOutputAction(emi, x);
    PiOutputAction aSto = new PiOutputAction(sto, x);
    
    
    PiPrefix prefix = new PiPrefix(aEmi, aSto);
    mark(prefix); // TODO it is not marking all processes, only the top-most on the tree of prefix!!!
    PiReplication act = new PiReplication(prefix);
    
    return act;
  }
  
  
  private PiProcess stim(Stimulus s, Integer id){
    
    PiName beginning = new PiName(NAME_BEGINNING);
    beginning.setDecorator("agentId", id);
    beginning.setDecorator("stimulus", s.toEnvironmentStimulus());
    
    PiName stable = new PiName(NAME_STABLE);
    stable.setDecorator("agentId", id);
    stable.setDecorator("stimulus", s.toEnvironmentStimulus());
    
    PiName ending = new PiName(NAME_ENDING);
    ending.setDecorator("agentId", id);
    ending.setDecorator("stimulus", s.toEnvironmentStimulus());
    
    PiName absent = new PiName(NAME_ABSENT);
    absent.setDecorator("agentId", id);
    absent.setDecorator("stimulus", s.toEnvironmentStimulus());
    
    // Setup the identifier
    PiIdentifier piStim = new PiIdentifier("piStim", beginning, stable, ending, absent);
    piStim.setDefinition(stimDef);
    
    return piStim;
  }



  public Integer getIdentifier() {
    return identifier;
  }
  
  private static void mark(PiProcess proc){
    proc.setMarker(MARKER_AGENT);
    proc.addIncompatibleMarker(MARKER_AGENT);
  }
  
  

}
