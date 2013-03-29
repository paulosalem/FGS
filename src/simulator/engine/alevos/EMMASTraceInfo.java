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
import java.util.HashSet;

import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import alevos.util.Pair;
import alevos.verification.TraceInfo;

public class EMMASTraceInfo extends TraceInfo {

  //
  // Safeguards used to implement the trace constraints
  //
  
  protected HashSet<Pair<Integer, EnvironmentStimulus>> stimulationSafeguard = new HashSet<Pair<Integer, EnvironmentStimulus>>();
  
  protected HashSet<Pair<Integer, EnvironmentAction>> actionSafeguard = new HashSet<Pair<Integer, EnvironmentAction>>();

  
  public EMMASTraceInfo() {
    super();
  }

  public EMMASTraceInfo(
      HashSet<Pair<Integer, EnvironmentStimulus>> stimulationSafeguard,
      HashSet<Pair<Integer, EnvironmentAction>> actionSafeguard) {
    super();
    this.stimulationSafeguard = stimulationSafeguard;
    this.actionSafeguard = actionSafeguard;
  }

  public void putStimulationSafeguard(Integer agentId, EnvironmentStimulus stimulus){
    Pair<Integer, EnvironmentStimulus> as = new Pair<Integer, EnvironmentStimulus>(agentId, stimulus);
    stimulationSafeguard.add(as);
  }
  
  public boolean hasStimulationSafeguard(Integer agentId, EnvironmentStimulus stimulus){
    Pair<Integer, EnvironmentStimulus> as = new Pair<Integer, EnvironmentStimulus>(agentId, stimulus);
    return stimulationSafeguard.contains(as);
  }
  
  public void clearStimulationSafeguards(){
    stimulationSafeguard.clear();
  }
  
  
  public void putActionSafeguard(Integer agentId, EnvironmentAction action){
    Pair<Integer, EnvironmentAction> aa = new Pair<Integer, EnvironmentAction>(agentId, action);
    actionSafeguard.add(aa);
  }
  
  public boolean hasActionSafeguard(Integer agentId, EnvironmentAction action){
    Pair<Integer, EnvironmentAction> aa = new Pair<Integer, EnvironmentAction>(agentId, action);
    return actionSafeguard.contains(aa);
  }
  
  public void clearActionSafeguard(){
    actionSafeguard.clear();
  }
  
  public Object clone(){
    return new EMMASTraceInfo((HashSet<Pair<Integer, EnvironmentStimulus>>)stimulationSafeguard.clone(), 
        (HashSet<Pair<Integer, EnvironmentAction>>)actionSafeguard.clone());
  }
}
