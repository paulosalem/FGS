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

import simulator.agent.IBehavioralAgent.StimulationStatus;
import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.util.Assert;
import alevos.ts.IOEvent;

public class EMMASEvent extends IOEvent {
  
  /**
   * The ID of the agent concerned with the event, if any.
   */
  private Integer agentId = null;
  
  /**
   * The action associated with this event, if any.
   */
  private EnvironmentAction action = null;
  
  /** 
   * The stimulus associated with this event, if any.
   */
  private EnvironmentStimulus stimulus = null;
  
  /**
   * What the stimulation status is supposed to become.
   */
  private StimulationStatus stimulationStatus = null;

  
  /**
   * Builds a new event unrelated to agents.
   * 
   * @param name
   * @param ioType
   */
  public EMMASEvent(String name, IOType ioType){
    this(name, ioType, null, null, null, null);
  }
  
  /**
   * Builds a new event indicating the performance of an action by an agent.
   * @param name
   * @param ioType
   * @param agentId
   * @param action
   */
  public EMMASEvent(String name, IOType ioType, Integer agentId, EnvironmentAction action){
    this(name, ioType, agentId, action, null, null);
  }
  
  /**
   * Builds a new event indicating the stimulation of an agent.
   * @param name
   * @param ioType
   * @param agentId
   * @param stimulus
   * @param stimulationStatus
   */
  public EMMASEvent(String name, IOType ioType, Integer agentId, EnvironmentStimulus stimulus, StimulationStatus stimulationStatus){
    this(name, ioType, agentId, null, stimulus, stimulationStatus);
  }
  
  
  /**
   * Builds a new EMMAS event. A number of meanings can be attached to such an event. More precisely,
   * an EMMAS event can denote an input or an output of exactly one of the following:
   * 
   *   - An action performed by an agent;
   *   - A stimulus, with an associated stimulation status, to be applied in an agent.
   *   - An event unrelated to any agents, actions or stimuli.
   *   
   * The parameters of this constructor, therefore, may contain <code>null</code> values as long
   * as the values w.r.t. the desired meaning are not <code>null</code>.
   * 
   * @param name The event identifier.
   * @param ioType The I/O type.
   * @param agentId The id of the agent concerned by the event. May be <code>null</code>.
   * @param action The action represented by this event, if any. May be <code>null</code>.
   * @param stimulus The stimulus represented by this event. May be <code>null</code>.
   * @param stimulationStatus The stimulation status represented by this event. May be <code>null</code>.
   */
  protected EMMASEvent(String name, IOType ioType, Integer agentId, EnvironmentAction action, EnvironmentStimulus stimulus, StimulationStatus stimulationStatus){
    super(name, ioType);
    
    Assert.notNull(name);
    Assert.notNull(ioType);
    // The other parameters may be null!!
    
    this.agentId = agentId;
    this.action = action;
    this.stimulus = stimulus;
    this.stimulationStatus = stimulationStatus; 
  }

  

  @Override
  public boolean isComplementary(IOEvent e){
    
    // First try the criteria for the superclass
    if(!super.isComplementary(e)){
      return false;
    }
    
    //
    // Now check with the particular characteristics of this class
    //
    
    EMMASEvent ee = null;
    
    if(e instanceof EMMASEvent){
      ee = (EMMASEvent) e;
    }
    else{
      return false;
    }
    
    // Check agent id
    if(agentId != null && ee.getAgentId() != null){
      if(!agentId.equals(ee.getAgentId())){
        return false;
      }
    }
    else if(agentId != null && ee.getAgentId() == null){
      return false;
    }
    else if(agentId == null && ee.getAgentId() != null){
      return false;
    }
    
    
    // Check action
    if(action != null && ee.getAction() != null){
      if(!action.equals(ee.getAction())){
        return false;
      }
    }
    else if(action != null && ee.getAction() == null){
      return false;
    }
    else if(action == null && ee.getAction() != null){
      return false;
    }
    
    // Check stimulus
    if(stimulus != null && ee.getStimulus() != null){
      if(!stimulus.equals(ee.getStimulus())){
        return false;
      }
    }
    else if(stimulus != null && ee.getStimulus() == null){
      return false;
    }
    else if(stimulus == null && ee.getStimulus() != null){
      return false;
    }
    
    // Check stimulation status
    if(stimulationStatus != null && ee.getStimulationStatus() != null){
      if(!stimulationStatus.equals(ee.getStimulationStatus())){
        return false;
      }
    }
    else if(stimulationStatus != null && ee.getStimulationStatus() == null){
      return false;
    }
    else if(stimulationStatus == null && ee.getStimulationStatus() != null){
      return false;
    }
    
    
    // If it has not been falsified so far, it must be true
    return true;
    
  }
  
  
  public Integer getAgentId() {
    return agentId;
  }

  


  public EnvironmentAction getAction() {
    return action;
  }



  public EnvironmentStimulus getStimulus() {
    return stimulus;
  }

  


  public StimulationStatus getStimulationStatus() {
    return stimulationStatus;
  }



  @Override
  public boolean equals(Object obj) {
    
    if(obj instanceof EMMASEvent){
      EMMASEvent event = (EMMASEvent) obj;
      if(event.getName().equals(this.getName()) && event.getIoType().equals(this.getIoType())){
        
        //
        // We shall check now the three possibilities of EMMAS events, and their corresponding
        // equality criteria.
        //
        
        if(agentId != null){
          if(action != null){
            
            // An action w.r.t. an agent
            if(agentId.equals(event.getAgentId()) && action.equals(event.getAction())){
              return true;
            }
          }
          else if(stimulus != null && stimulationStatus != null){
            
            // A stimulus, with its status, w.r.t. an agent
            if(agentId.equals(event.getAgentId()) && stimulus.equals(event.getStimulus()) && stimulationStatus.equals(event.getStimulationStatus())){
              return true;
            }
            
          }
        }
        else{
          
          // An event independent of any agent, action or stimulus
          return true;
        }
        
      }
    }
    
    
    // No condition of equality was met
    return false;
  }



  @Override
  public int hashCode() {
    return this.getName().hashCode() + this.getIoType().hashCode() + agentId;
  }



  @Override
  public String toString() {
    String s = "";
    
    if(getIoType().equals(IOType.INPUT)){
      s = "?";
    }
    else if(getIoType().equals(IOType.OUTPUT)){
      s = "!";
    }
    else if(getIoType().equals(IOType.OTHER)){
      s = "(*)";
    }
    
    s = s + getName(); // + "[agentId = "+ agentId +"]_" + action + "_" + stimulus;
    
    if(agentId != null){
      s = s + "[agentId = "+ agentId +"]";
    }
    
    if(action != null){
      s = s + "_" + action;
    }
    
    if(stimulus != null){
      s = s +"_" + stimulus;
    }
    
    
    
    return s;
  }


}
