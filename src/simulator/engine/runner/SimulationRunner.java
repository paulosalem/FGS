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
package simulator.engine.runner;

import simulator.agent.IAgentControl;
import simulator.agent.relation.IRelation;
import simulator.analysis.IProperty;
import simulator.analysis.PropertyBearerWrapper;
import simulator.analysis.UndefinedPropertyException;
import simulator.engine.SimulationState;
import simulator.engine.runner.SimulationRun.StorageMode;
import simulator.environment.IEnvironment;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import simulator.util.Assert;

/**
 * Implements the mechanisms necessary for controlling the execution of the simulation. Simulation strategies
 * must use instances of this class to implement their simulation algorithms. 
 * 
 * @author    Paulo Salem
 */
public class SimulationRunner {
  
  /**
   * The simulation's initial state.
   */
  private SimulationState initialState;
  
  /**
   * Simulation's current state.
   */
  private SimulationState currentState;
  
  /**
   * Simulation's current run.
   */
  private SimulationRun currentRun;

  /**
   * The desired execution mode.
   */
  private ExecutionMode executionMode;
  
  /**
   * Current step in the simulation. The position -1 indicates that not even the initial state has been
   * correctly setup, whereas the position 0 indicates that the initial state is setup and ready,
   * but no simulation step has been given yet..
   */
  private int currentPosition = -1;

  
  public SimulationRunner(SimulationState initialState){
    this(initialState, StorageMode.NONE);
  }

  
  public SimulationRunner(SimulationState initialState, SimulationRun.StorageMode mode){
    Assert.notNull(initialState);
    Assert.notNull(mode);
    
    this.initialState = initialState; 
    this.currentRun = new SimulationRun(mode);
  }
  
  public SimulationRun run(SimulationState initialState, int steps)  throws UndefinedPropertyException, SimulationRunException{
    this.initialState = initialState;
    return runSteps(steps);
  }
  
  /**
   * Runs the simulation for <code>steps</code> iterations, beginning
   * at the initial state defined in this runner.
   * 
   * @param steps How many simulation steps shall be executed.
   * 
   * @return The resulting <code>SimulationRun</code>
   * @throws SimulationRunException 
   */
  public SimulationRun runSteps(int steps)  throws UndefinedPropertyException, SimulationRunException{
    
    // Check parameters
    Assert.nonNegativeNonZero(steps);
    
    // Simulation loop
    for(int i = 0; i < steps; i++){
      runStep();
    }
    
    return currentRun;
    
  }
  
  
  /**
   * Runs one step of the simulation.
   * @throws UndefinedPropertyException 
   * @throws SimulationRunException 
   */
  public void runStep() throws UndefinedPropertyException, SimulationRunException{
    
    // If the run has not been properly setup (i.e., position -1), we do it now.
    if(currentPosition <= -1){
      setupRun();
    }
    
    // Increments the current position
    currentPosition++;
    currentState.setPosition(currentPosition);

    
    // Update the environment. This must come here, before all other updates, because
    // the environment may be responsible for synchronizing things needed for the
    // other simulation elements (e.g., it may deliver some stimuli to agents).
    currentState.getEnvironment().step(executionMode);
    
    
    // Supervise agents
    for(IAgentControl ac: currentState.getAgentControls()){
      ac.step();
      
    }
 
    
    // Supervise relations
    for(IRelation r: currentState.getRelations()){
      // TODO
    }

    
    // Supervise property bearers
    for(PropertyBearerWrapper pbw: currentState.getPropertyBearerWrappers()){

      String msg = "Properties (for  " + pbw.getName() + "): ";
      for(IProperty p: pbw.getProperties()){
        
        // TODO represent the property somehow if required
        
        msg = msg + " [" + p.getName() + " = " + p.getValueAsString(pbw) + "]";
      }
      
      SimulatorUI.instance().getMessenger().printDebugMsg(msg, Messenger.IMPORTANT_MSG);
    }

    // Store current state after the modifications
    currentRun.append((SimulationState) currentState.clone());
    
  }
  
  /**
   * If no simulation step has been given yet, it is necessary to setup the initial conditions
   * for it. This method handles this task, and, in particular, defines that the simulation
   * run shall keep no states in its trace.
   * @throws UndefinedPropertyException 
   * @throws SimulationRunException 
   */
  public void setupRun() throws UndefinedPropertyException, SimulationRunException{
    setupRun(SimulationRun.StorageMode.NONE, ExecutionMode.EXPLORATION);
  }
  
  /**
   * If no simulation step has been given yet, it is necessary to setup the initial conditions
   * for it. This method handles this task.
   * 
   * @param storageMode The desired storage mode for the traces produced during the simulation run.
   * @throws UndefinedPropertyException 
   * @throws SimulationRunException 
   */
  public void setupRun(StorageMode storageMode, ExecutionMode executionMode) throws UndefinedPropertyException, SimulationRunException{
    Assert.notNull(storageMode);
    Assert.notNull(executionMode);
    
    // We need a new run
    currentRun = new SimulationRun(storageMode);
    
    // Define how to execute the simulation
    this.executionMode = executionMode;
    
    
    // Clone the initial state to become the current one
    currentState = (SimulationState) initialState.clone();
    
    // Store a initial state clone as well
    currentRun.append((SimulationState) initialState.clone());
    
    
    // Define that the initial state has been properly set by specifying 
    // the 0 position as the current one.
    currentPosition = 0;

  }
  
  /**
   * Configures the simulation to have the specified state as the current one.
   * The specified state must have a position such that it is at most
   * the next one in the simulatio run.
   * 
   * @param state The state to go to.
   * @throws SimulationRunException 
   * @throws UndefinedPropertyException 
   */
  public void goToState(SimulationState state) throws UndefinedPropertyException, SimulationRunException{
    
    // Clone the state and make it the current one
    currentState = (SimulationState) state.clone();

    currentRun.restartFrom(currentState);
    currentPosition = currentState.getPosition();
  }
  
  
  /**
   * Replaces the current simulation run with a new one. There must be a current simulation run.
   * 
   * @throws SimulationRunException If no current simulation run exists.
   * @throws UndefinedPropertyException 
   */
  public void resetRun() throws SimulationRunException, UndefinedPropertyException{
    
    if(currentRun == null){
      throw new SimulationRunException("The simulation run must already exist in order to be reset.");
    }
    
    setupRun(currentRun.getMode(), executionMode);
  }
  
  
  /**
   *
   * @return A clone of the initial state.
   */
  public SimulationState getInitialStateClone() {
    return (SimulationState) initialState.clone();
  }


  /**
   * @return  The current state of the simulation.
   */
  public SimulationState getCurrentState(){
    return currentState;
  }
  
  public IEnvironment getCurrentEnvironment(){
    return currentState.getEnvironment(); 
  }
  
  public IEnvironment getInitialEnvironment(){
    return initialState.getEnvironment();
  }
  

}
