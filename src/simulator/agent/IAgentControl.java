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

import org.jdom.Element;

import simulator.environment.IEnvironment;


/**
 * Provides the infrastructural interface that allows the simulator to control the agent.
 * 
 * @author   Paulo Salem
 */
public interface IAgentControl {
  
  /**
   * Instructs the agent to update its internal state as if
   * one time unit has passed.
   */
  public void step();
  
  /**
   * Sets the proxy for the agent. Each simulator.agent must know its proxy
   * in order to make self references (i.e., agents cannot use
   * the <code>this</code> keyword in order to refer to themselves).
   * 
   * @param proxy
   */
  public void setAgentProxy(AgentProxy proxy);
  
  /**
   * Returns a reference to this agent's proxy. All self-references 
   * should be made using this method.
   * 
   * @return A reference to this agent's proxy.
   */
  public IAgent me();
  
  public IAgent getAgent();
  
  /**
   * @param  id
   */
public void setId(int id);
  
  /**
   * @return
   */
public int getId();
  
  public void setName(String name);
  
  public void setEnvironment(IEnvironment environment);
  
  
  /**
   * 
   * @return A textual representation of the agent's state.
   */
  public String toString();
  

  // TODO substitute for a more generic affiliation mechanism
 /**
  * 
  * @return The affiliation of the agent.
  */  
  public String affiliation ();
  

}
