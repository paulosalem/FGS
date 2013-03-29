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
package simulator.engine.alevos;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import simulator.agent.IBehavioralAgent.ActionStatus;
import simulator.agent.IBehavioralAgent.StimulationStatus;
import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.environment.esl.ESLExpression;
import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiActionPrefix;
import alevos.expression.picalculus.PiInputAction;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiOutputAction;
import alevos.expression.picalculus.PiProcess;

public class PiEMMASAgentProfile extends PiProcess {
  
  private static PiName dummyName = new PiName("x");
  
  private Integer agentId;
  
  private HashMap<EnvironmentAction, ActionStatus> actionStatus = new HashMap<EnvironmentAction, ActionStatus>();
  private HashMap<EnvironmentStimulus, StimulationStatus> stimulationStatus = new HashMap<EnvironmentStimulus, StimulationStatus>();  
    
  private HashMap<EnvironmentAction, PiName> stopNameFor = new HashMap<EnvironmentAction, PiName>();
  private HashMap<EnvironmentAction, PiName> emitNameFor = new HashMap<EnvironmentAction, PiName>();
  
  private HashMap<EnvironmentStimulus, PiName> absentNameFor = new HashMap<EnvironmentStimulus, PiName>();
  private HashMap<EnvironmentStimulus, PiName> beginningNameFor = new HashMap<EnvironmentStimulus, PiName>();
  private HashMap<EnvironmentStimulus, PiName> stableNameFor = new HashMap<EnvironmentStimulus, PiName>();
  private HashMap<EnvironmentStimulus, PiName> endingNameFor = new HashMap<EnvironmentStimulus, PiName>();
  
  
  
  
  private  HashSet<PiName> freeNames = new HashSet<PiName>();
  private  HashSet<PiName> boundNames = new HashSet<PiName>(); // Empty
  
 
  
  public PiEMMASAgentProfile(Integer agentId, Set<EnvironmentAction> actions, Set<EnvironmentStimulus> stimuli){
    
    this.agentId = agentId;
    
    //
    // Setup: (i) action and stimulation status; (ii) the corresponding PiNames; (iii) free names.
    //
    
    for(EnvironmentAction ea: actions){
      actionStatus.put(ea, ActionStatus.NOT_EMITTING);
      
      PiName emi = new PiName(ESLExpression.NAME_EMIT);
      emi.setDecorator("agentId", agentId);
      emi.setDecorator("action", ea);
      emitNameFor.put(ea, emi);
      freeNames.add(emi);
      
      PiName sto = new PiName(ESLExpression.NAME_STOP);
      sto.setDecorator("agentId", agentId);
      sto.setDecorator("action", ea);
      stopNameFor.put(ea, sto);
      freeNames.add(sto);
    }
    
    for(EnvironmentStimulus es: stimuli){
      stimulationStatus.put(es, StimulationStatus.ABSENT);
      
      PiName beginning = new PiName(ESLExpression.NAME_BEGINNING);
      beginning.setDecorator("agentId", agentId);
      beginning.setDecorator("stimulus", es);
      beginningNameFor.put(es, beginning);
      freeNames.add(beginning);
      
      
      PiName stable = new PiName(ESLExpression.NAME_STABLE);
      stable.setDecorator("agentId", agentId);
      stable.setDecorator("stimulus", es);
      stableNameFor.put(es, stable);
      freeNames.add(stable);
      
      PiName ending = new PiName(ESLExpression.NAME_ENDING);
      ending.setDecorator("agentId", agentId);
      ending.setDecorator("stimulus", es);
      endingNameFor.put(es, ending);
      freeNames.add(ending);
      
      PiName absent = new PiName(ESLExpression.NAME_ABSENT);
      absent.setDecorator("agentId", agentId);
      absent.setDecorator("stimulus", es);
      absentNameFor.put(es, absent);
      freeNames.add(absent);
    }
    

    // A special semantic rule is needed to handle this process
    this.addApplicableRule(new PiEMMASAgentProfileRule()); // TODO use a singleton instance??
  }
  
  
  //used only for cloning  
  private PiEMMASAgentProfile(){
    
  }

  @Override
  public Set<PiName> boundNames() throws IllegalSemanticsException {
    return boundNames; // No names are bound
  }

  @Override
  public Set<PiName> freeNames() {
    return freeNames;
  }

  @Override
  public boolean substitute(Map<PiName, PiName> substitution) throws IllegalSemanticsException {

    // No action or stimulus name is supposed to be substituted
    for(PiName name1: substitution.keySet()){
      for(PiName name2: freeNames){
        if(name1.equals(name2)){
          throw new IllegalSemanticsException();
        }
      }
    }
    
    return false;
  }

  @Override
  public void alphaConversion() throws IllegalSemanticsException {
    // Nothing to be done, since there are no bound names

  }
  
  @Override
  public int size() {
    // The mere number of actions and stimuli suffices to give an order of magnitude
    // to the size of this structure. Since there are no actual pi-names and pi-processes used explicitly here,
    // we adopt this approximation.
    return actionStatus.keySet().size() + stimulationStatus.keySet().size();
  }
  
  @Override
  public int cachedSuccessorsSize() {
    // No cache is used, since there are no sub-expressions
    return 0;
  }

  @Override
  public Object clone() {
    PiEMMASAgentProfile clone = new PiEMMASAgentProfile();
    
    clone.applicableRules = this.applicableRules;
    
    clone.agentId = this.agentId;
    clone.actionStatus = (HashMap<EnvironmentAction, ActionStatus>) this.actionStatus.clone();
    clone.stimulationStatus = (HashMap<EnvironmentStimulus, StimulationStatus>) this.stimulationStatus.clone();
    clone.stopNameFor = (HashMap<EnvironmentAction, PiName>) this.stopNameFor.clone();
    clone.emitNameFor = (HashMap<EnvironmentAction, PiName>) this.emitNameFor.clone();
    clone.absentNameFor = (HashMap<EnvironmentStimulus, PiName>) this.absentNameFor.clone();
    clone.beginningNameFor = (HashMap<EnvironmentStimulus, PiName>) this.beginningNameFor.clone();
    clone.stableNameFor = (HashMap<EnvironmentStimulus, PiName>) this.stableNameFor.clone();
    clone.endingNameFor = (HashMap<EnvironmentStimulus, PiName>) this.endingNameFor.clone();
    clone.freeNames = (HashSet<PiName>) this.freeNames.clone();
    clone.boundNames = (HashSet<PiName>) this.boundNames.clone();
    
    
    return clone;
  }

  @Override
  public String toString() {
    String s = "{Agent " + agentId + "}";    
    return s;
  }

  public Collection<EnvironmentAction> getActions() {
    return actionStatus.keySet();
  }

  public Collection<EnvironmentStimulus> getStimuli() {
    return stimulationStatus.keySet();
  }
  
  // TODO wrong! Don't forget the replication operator!
  public PiActionPrefix nextClonedPrefixFor(EnvironmentAction ea){
    
    PiActionPrefix prefix = null;
    
    switch(actionStatus.get(ea)){
      case EMITTING:
        prefix = new PiOutputAction(stopNameFor.get(ea), dummyName);
        break;
        
      case NOT_EMITTING:
        prefix = new PiOutputAction(emitNameFor.get(ea), dummyName);
        break;
        
    }
    
    return prefix;
    
  }
  
  public PiActionPrefix nextClonedPrefixFor(EnvironmentStimulus es){
    
    PiActionPrefix prefix = null;
    
    switch(stimulationStatus.get(es)){
    
       case ABSENT:
         prefix = new PiInputAction(beginningNameFor.get(es), dummyName);
         break;
      
       case BEGINNING:
         prefix = new PiInputAction(stableNameFor.get(es), dummyName);
         break;
         
       case STABLE:
         prefix = new PiInputAction(endingNameFor.get(es), dummyName);
         break;
         
       case ENDING:
         prefix = new PiInputAction(absentNameFor.get(es), dummyName);
         break;
    }
    
    return prefix;
  }
  
  // TODO wrong! Don't forget the replication operator!
  public PiEMMASAgentProfile clonedAgentAfter(EnvironmentAction ea){
  
    PiEMMASAgentProfile clone = (PiEMMASAgentProfile) this.clone();
    
    switch(actionStatus.get(ea)){
    case EMITTING:
      clone.actionStatus.put(ea, ActionStatus.NOT_EMITTING);
      break;
      
    case NOT_EMITTING:
      clone.actionStatus.put(ea, ActionStatus.EMITTING);
      break;
      
  }
    
    return clone;
  }
  
  
  public PiEMMASAgentProfile clonedAgentAfter(EnvironmentStimulus es){
    
    PiEMMASAgentProfile clone = (PiEMMASAgentProfile) this.clone();
    
    switch(stimulationStatus.get(es)){
    
    case ABSENT:
      clone.stimulationStatus.put(es, StimulationStatus.BEGINNING);
      break;
   
    case BEGINNING:
      clone.stimulationStatus.put(es, StimulationStatus.STABLE);
      break;
      
    case STABLE:
      clone.stimulationStatus.put(es, StimulationStatus.ENDING);
      break;
      
    case ENDING:
      clone.stimulationStatus.put(es, StimulationStatus.ABSENT);
      break;
 }
    
    return clone;
  }
  
  
  // TODO erase below?
  
  public void setActionStatus(EnvironmentAction ea, ActionStatus status){
    this.actionStatus.put(ea, status);
  }
  
  public ActionStatus getActionStatus(EnvironmentAction ea){
    return this.actionStatus.get(ea);
  }
  
  public void setStimulationStatus(EnvironmentStimulus es, StimulationStatus status){
    this.stimulationStatus.put(es, status);
  }
  
  public StimulationStatus getStimulationStatus(EnvironmentStimulus es){
    return this.stimulationStatus.get(es);
  }
}
