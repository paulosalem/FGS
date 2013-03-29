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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import simulator.analysis.UndefinedPropertyException;
import simulator.engine.SimulationFossilizedState;
import simulator.engine.SimulationState;
import simulator.util.Assert;

/**
 * A simulation run containing the states that have been explored.
 * 
 * @author Paulo Salem
 */
public class SimulationRun {

  /**
   * How the states can to be saved.
   * 
   * @author Paulo Salem
   * 
   */
  public enum StorageMode {
    FULL, FOSSILIZED, NONE
  }

  /**
   * The sequence of successive states during a simulation run (i.e., the
   * simulation run's trace).
   */
  private ArrayList<SimulationState> trace = new ArrayList<SimulationState>();

  /**
   * The fossilized sequence of successive states during a simulation run (i.e.,
   * the simulation run's trace).
   */
  private ArrayList<SimulationFossilizedState> fossilizedTrace = new ArrayList<SimulationFossilizedState>();

  /**
   * How the states should be stored.
   */
  private StorageMode mode = StorageMode.FULL;

  private int currentPosition = -1;

  //
  // TODO store time to complete the run and other statistics?
  //

  public SimulationRun(StorageMode mode) {

    Assert.notNull(mode);

    this.mode = mode;
    
    // Setup some minimal initial capacity for the traces
    trace.ensureCapacity(1000);
    fossilizedTrace.ensureCapacity(1000);
  }

  public void append(SimulationState state) throws UndefinedPropertyException,
      SimulationRunException {
    Assert.notNull(state);

    if (state.getPosition() != currentPosition + 1) {
      throw new SimulationRunException(
          "The specified state's position must be the next one w.r.t. the current state's position.");
    }

    currentPosition++;
    
    if (mode == StorageMode.FULL) {
      trace.ensureCapacity(currentPosition + 2); 
      //trace.set(currentPosition, state);
      trace.add(state);
      
    } else if (mode == StorageMode.FOSSILIZED) {
      fossilizedTrace.ensureCapacity(currentPosition + 2);
      //fossilizedTrace.set(currentPosition, state.fossilize());
      fossilizedTrace.add(state.fossilize());
    }

  }
  

  /**
   * Reconfigures the simulation run so that the specified state becomes the current simulation state.
   * Notice that simulation states have associated positions, and this means that the specified
   * state's position must be either already present in this run or be the next one. That is
   * to say, if the current run's last state has position n, then the new one must have
   * a position less than or equal to n + 1. This guarantees the contiguity of the simulation run
   * (i.e., no missing intermediary states). 
   * 
   * @param state The state to become the current one.
   * 
   * @throws UndefinedPropertyException
   * @throws SimulationRunException
   */
  public void restartFrom(SimulationState state) throws UndefinedPropertyException, SimulationRunException{
    
    if(state.getPosition() > currentPosition + 1){
      throw new SimulationRunException(
          "The specified state's position ("+state.getPosition() +") cannot be greater than the current position + 1 ("+ currentPosition + 1 +").");
    }
    else if(state.getPosition() == currentPosition + 1){
      // Add as a next state of the run
      append(state);
    }
    else{
      // Substitute an exiting state
      replace(state);  
    }
    
    // Redefines the current position
    currentPosition = state.getPosition();
    
    // Note: it is not necessary to actually remove the states after the new current position
    // because they will naturally be replaced as new states are appended
    
  }

  /**
   * The specified state defines its on position on the simulation run. This
   * method replaces the state currently on that position with the one
   * specified.
   * 
   * @param state
   *          The state to be put in place.
   * @throws UndefinedPropertyException
   * @throws SimulationRunException
   */
  private void replace(SimulationState state)
      throws UndefinedPropertyException, SimulationRunException {

    if (state.getPosition() > currentPosition) {
      throw new SimulationRunException(
          "The specified state's position ("+state.getPosition() +") cannot be greater than the current position ("+currentPosition+").");
    }

    if (mode == StorageMode.FULL) {
      trace.set(state.getPosition(), state);
    } else if (mode == StorageMode.FOSSILIZED) {
      fossilizedTrace.set(state.getPosition(), state.fossilize());
    }
  }

  /**
   * Clear all information stored.
   */
  public void clear() {
    trace.clear();
    fossilizedTrace.clear();
  }

  public List<SimulationFossilizedState> getFossilizedTrace()
      throws SimulationRunException, UndefinedPropertyException {

    if (mode == StorageMode.FULL) {

      fossilizedTrace = new ArrayList<SimulationFossilizedState>();

      Iterator<SimulationState> it = trace.iterator();
      while (it.hasNext()) {
        fossilizedTrace.add(it.next().fossilize());
      }

    } else if (mode == StorageMode.NONE) {
      throw new SimulationRunException(
          "Cannot get a fossilized state because it was not being stored.");
    }

    return fossilizedTrace;
  }

  public ArrayList<SimulationState> getTrace() throws SimulationRunException {
    if (mode != StorageMode.FULL) {
      throw new SimulationRunException(
          "Cannot get a full trace because it was not being stored.");
    }

    return trace;
  }

  public StorageMode getMode() {
    return mode;
  }

}
