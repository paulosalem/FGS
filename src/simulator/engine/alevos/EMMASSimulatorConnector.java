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

import simulator.agent.IBehavioralAgent.ActionStatus;
import simulator.agent.action.EnvironmentAction;
import simulator.analysis.UndefinedPropertyException;
import simulator.engine.SimulationState;
import simulator.engine.runner.ExecutionMode;
import simulator.engine.runner.SimulationRun.StorageMode;
import simulator.engine.runner.SimulationRunException;
import simulator.engine.runner.SimulationRunner;
import simulator.environment.EMMASEnvironment;
import simulator.ui.SimulatorUI;
import simulator.util.Assert;
import alevos.simulation.InvalidSimulatorRequest;
import alevos.simulation.SimulatorConnector;
import alevos.simulation.SimulatorContext;
import alevos.ts.Event;
import alevos.ts.IOEvent.IOType;
import alevos.ts.State;

/**
 * Provides a way to control a simulation in which the environment is based on an EMMAS specification.
 */
public class EMMASSimulatorConnector extends SimulatorConnector {
  
  private SimulationRunner runner;
  
  
  //
  // Variables for controlling pre- and post-simulation restrictions.
  //
  // TODO
  
  
  
  public EMMASSimulatorConnector(SimulationRunner runner){
    super(new EMMASEvent("commit", IOType.OUTPUT));
    
    Assert.notNull(runner);
    
    this.runner = runner;
  }
  
  
  @Override
  public void setup() throws InvalidSimulatorRequest{
    try {
      runner.setupRun(StorageMode.FULL, ExecutionMode.VERIFICATION);
      
    } catch (UndefinedPropertyException e) {
      throw new InvalidSimulatorRequest("An undefined property was referenced.", e);
    } catch (SimulationRunException e) {
      throw new InvalidSimulatorRequest("There was an error during the simulation run.", e);
    }
    
  }



  @Override
  public void reset() throws InvalidSimulatorRequest {
    try {
      runner.resetRun();
    } catch (UndefinedPropertyException e) {
      throw new InvalidSimulatorRequest("An undefined property was referenced.", e);
    } catch (SimulationRunException e) {
      throw new InvalidSimulatorRequest("There was an error during the simulation run.", e);
    }
    
  }
  
  @Override
  public void scheduleStep(Event event) throws InvalidSimulatorRequest {
    if(!(event instanceof EMMASEvent)){
      throw new IllegalArgumentException("An EMMAS was expected.");
    }
    
    scheduleStep((EMMASEvent) event);
  }
  
  
  
  public void scheduleStep(EMMASEvent event) throws InvalidSimulatorRequest {
    
    // If the event is an input to the simulator (and therefore an output of the transition system)
    if(event.isOutput()){

      // We shall consider only the delivery of stimuli to agents
      if(event.getAgentId() != null && event.getStimulus() != null && event.getStimulationStatus() != null){
        ((EMMASEnvironment) runner.getCurrentEnvironment()).deliverStimulation(event.getAgentId(), event.getStimulus(), event.getStimulationStatus());
        
      }
      else if(event.equals(commitEvent)){
        // If anything special is required to handle this event, put it here.
      }
      else{
        throw new InvalidSimulatorRequest("A stimulation delivery to an agent or a commit event was expected. Instead, the following event was found: " + event.toString());
      }
      
    }
    
    // In the other cases there is nothing to be done, for they have already been 
    // accounted for by the canStep method.
    
    
  }

  
  @Override
  public void step() throws InvalidSimulatorRequest{
    try {
      runner.runStep();
      
      
    } catch (Exception e) {
      throw new InvalidSimulatorRequest();
    }
  }

  @Override
  public long getCurrentPosition() {
    return runner.getCurrentState().getPosition();
  }


  @Override
  public void goToState(Object state) throws InvalidSimulatorRequest {
    try {
      runner.goToState((SimulationState) state);
    } catch (Exception e) {
      throw new InvalidSimulatorRequest("The simulator is unable to return to the specified state.", e);
    }
  }

  @Override
  public Object currentState() {
    return runner.getCurrentState().clone();
  }


  @Override
  public SimulatorContext getCurrentContext() {
    return new EMMASSimulatorContext((EMMASEnvironment)runner.getCurrentEnvironment(), runner.getCurrentState());
  }


  @Override
  public void printDebugMsg(String msg, int msgImportance) {
    SimulatorUI.instance().getMessenger().printDebugMsg(msg, msgImportance);
    
  }


  @Override
  public void printMsg(String msg, int msgImportance) {
    SimulatorUI.instance().getMessenger().printMsg(msg, msgImportance);
  }

  
  // TODO remove all of this?
/*
  @Override
  protected boolean preSimulationRestriction(State s1, Event e, State s2) {
    if(!(e instanceof EMMASEvent)){
      throw new IllegalArgumentException("An EMMAS event was expected.");
    }
    else if(s1.getContext() == null){
      throw new IllegalArgumentException("The source state must have a context.");
    }
    
    return preSimulationRestriction(s1, (EMMASEvent) e, s2);
  }
  
  // TODO this is still a mess
  private boolean preSimulationRestriction(State s1, EMMASEvent e, State s2) {
    
    EMMASSimulatorContext context = (EMMASSimulatorContext) s1.getContext();
    
    //
    // Action constraints
    //
    
    
    EnvironmentAction action = e.getAction();
    Integer agentId = e.getAgentId();
    if(action != null && agentId != null){
      
      
      if(context.response(agentId, action) == ActionStatus.EMITTING){
        
        // Restriction 1
        if(e.getName().equals(EMMASTransitionSystem.NAME_STOP) && e.getIoType() == IOType.INPUT){
          return true;
        }
        
        // Restriction 3
        else if(e.getName().equals(EMMASTransitionSystem.NAME_CLOCK)){
          // TODO
          EMMASEvent emit = new EMMASEvent(EMMASTransitionSystem.NAME_EMIT, IOType.INPUT, agentId, action);
          
        }
        
      }
      else if(context.response(agentId, action) == ActionStatus.NOT_EMITTING){
        
        // Restriction 2
        if(e.getName().equals(EMMASTransitionSystem.NAME_EMIT) && e.getIoType() == IOType.INPUT){
          return true;
        }
        // Restriction 4
        else if(e.getName().equals(EMMASTransitionSystem.NAME_CLOCK)){
          // TODO
        }
      }
      
    }
    
    
    //
    // Clock constraints
    //
    
    // TODO
    
    return false;
  }


  // TODO this might be unnecessary...
  @Override
  protected boolean postSimulationRestriction(State s) {
    // TODO Auto-generated method stub
    return false;
  }

  */





  

}
