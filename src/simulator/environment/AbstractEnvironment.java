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

import java.io.Serializable;
import java.util.List;

import simulator.Scenario;
import simulator.agent.IAgent;
import simulator.engine.SimulationState;
import simulator.util.Assert;

/**
 * Implements the most general methods from the <code>IEnvironment</code> interface.
 * 
 * @author Paulo Salem
 *
 */
public abstract class AbstractEnvironment implements IEnvironment, Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * The scenario that defines which elements shall be present in the simulation.
   */
  protected Scenario scenario = null;
  
  /**
   * The simulation state that this environment gives access to.
   */
  protected SimulationState currentState = null;
  // TODO update currentState during the simulation...

  
  public AbstractEnvironment(Scenario scenario){
    Assert.notNull(scenario);
    
    this.scenario = scenario;
  }

  /////////////////////////////////////////////////////////////////////////////
  // IEnvironment methods
  /////////////////////////////////////////////////////////////////////////////

  @Override
  public List<IAgent> getAgents(){
    return scenario.getAgentProxies(); 
  }
  
  @Override
  public IAgent getAgent(int id){
    
    for(IAgent a: scenario.getAgentProxies()){
      if(a.getId() == id){
        return a;
      }
    }
    
    return null;
  }

  @Override
  public void setSimulationState(SimulationState state){
      Assert.notNull(state);
      
      this.currentState = state;   
  }

}
