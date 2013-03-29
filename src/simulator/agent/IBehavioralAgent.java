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
package simulator.agent;

import java.util.Collection;

import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;

/**
 * An interface particular to agents following the Behavioral Agent Architecture.
 * 
 * @author Paulo Salem
 *
 */
public interface IBehavioralAgent extends IAgent{
  
  public enum StimulationStatus {BEGINNING, ENDING, STABLE, ABSENT}
  
  public enum ActionStatus {EMITTING, NOT_EMITTING}

  /**
   * Sets the current status of a stimulus.
   * 
   * @param environmentStimulus
   * @param status
   */
  public void receiveStimulus(EnvironmentStimulus environmentStimulus, StimulationStatus status);

  
  /**
   * Each agent has a set of possible stimuli that it may receive. This
   * method returns this set.
   * 
   * @return The stimuli that the agent may receive.
   */
  public Collection<EnvironmentStimulus> possibleStimuli();
  
  
  /**
   * 
   * @param action An action which this agent is capable of performing.
   * 
   * @return The current status of the specified action.
   */
  public ActionStatus getActionStatus(EnvironmentAction action);
  
  
  /**
   * Each agent has a set of possible actions that it may perform. This
   * method returns this set.
   * 
   * @return The possible actions of this agent.
   */
  public Collection<EnvironmentAction> possibleActions();
  
}
