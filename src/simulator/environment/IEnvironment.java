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
package simulator.environment;

import java.util.List;

import simulator.agent.IAgent;
import simulator.engine.SimulationState;
import simulator.engine.runner.ExecutionMode;

/**
 * An environment defines the elements that exist in a simulation and how they interact.
 * This interaction can be either endogenous or exogenous. In the endogenous case,
 * agents themselves choose how to communicate with other agents. In the exogenous case,
 * the environment chooses how agents shall communicate with each other.
 * 
 * @author Paulo Salem
 *
 */
public abstract interface IEnvironment {
  
  public void step(ExecutionMode mode);
  
  public List<IAgent> getAgents();

  public IAgent getAgent(int id);
  
  public void setSimulationState(SimulationState state);

}
