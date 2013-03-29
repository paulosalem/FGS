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

import java.util.LinkedList;
import java.util.List;

import simulator.Scenario;
import simulator.agent.AgentProxy;
import simulator.agent.IAgent;
import simulator.agent.relation.IRelation;
import simulator.engine.SimulationState;
import simulator.engine.runner.ExecutionMode;
import simulator.util.Assert;


/**
 * Provides resources to the simulation's agents, according to the <code>ISocialNetworkEnvironment</code> 
 * interface contract. This implementation is further responsible for creating the necessary proxies and 
 * enforcing the relevant access policies to fulfill agents requests. Proxies are created on demand.
 * 
 * @author   Paulo Salem
 */
public class SocialNetworkEnvironment extends AbstractEnvironment implements ISocialNetwork{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an environment associated to the specified scenario.
   * 
   * @param scenario The scenario associated with the new envioronment.
   */
  public SocialNetworkEnvironment(Scenario scenario){
    super(scenario);
  }


  /////////////////////////////////////////////////////////////////////////////
  // IEnvironment methods
  /////////////////////////////////////////////////////////////////////////////

  @Override
  public void step(ExecutionMode mode) {
    // Nothing to be done
    
  }

  
  /////////////////////////////////////////////////////////////////////////////
  // ISocialNetworkEnvironment methods
  /////////////////////////////////////////////////////////////////////////////
  
  public List<IAgent> getNeighbors(IAgent agent) {
    
    List<IAgent> neighbors = new LinkedList<IAgent>();
    
    // Create a proxy for each neighbor
    for(IRelation<IAgent> r: currentState.getRelations()){
      for(IAgent a: r.relationalImage(agent)){
        AgentProxy proxy = scenario.createAgentProxy(a);
        neighbors.add(proxy);
      }
    }
    
    
    return neighbors;
  }
  
  public List<IAgent> getNeighbors(IAgent agent, int relationId) {
    
    List<IAgent> neighbors = new LinkedList<IAgent>();
    
    // Create a proxy for each neighbor
    for(IRelation<IAgent> r: currentState.getRelations()){
      
      // Check the ID
      if(r.getId() == relationId){
        for(IAgent a: r.relationalImage(agent)){
          AgentProxy proxy = scenario.createAgentProxy(a);
          neighbors.add(proxy);
        }
      }
      
    }
    
    
    return neighbors;
  }
  
  
  public List<IRelation> getRelations(){
    return scenario.getRelationProxies(); 
  }
  
  public IRelation<IAgent> getRelation(int id){
    
    for(IRelation r: scenario.getRelationProxies()){
      if(r.getId() == id){
        return r;
      }
    }
    
    return null;
  }
  
  
}
