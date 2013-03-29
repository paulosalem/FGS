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

import java.util.List;

import simulator.agent.relation.ISocialNetworkAgent;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.components.AComponentInfo;
import simulator.environment.ISocialNetwork;



/**
 * Provides a base implementation of all agent's interfaces. It takes care of most general implementation issues. By extending this class, concrete subclasses may focus on their model, rather than work on infrastrucural details.  This class is provided merely as a convinience for agents implementers. There is no need to actually extend it in order to build an agent.
 * @author   Paulo Salem
 */
@AComponentInfo(
   id = "simulator.agent.SocialNetworkAgent",
   version = 1,
   name = "Standard Social Network Agent",
   description = "A base and abstract implementation of a social network agent.",
   type = AComponentInfo.ComponentType.AGENT
)
abstract public class StandardSocialNetworkAgent implements IAgent, 
                                                   IAgentControl,
                                                   ISocialNetworkAgent{


  /**
   * The agent's unique ID. Not to be confused with the component's ID.
   * @uml.property  name="id"
   */
  protected int id = 0;
  
  /**
   * A user friendly name.
   * @uml.property  name="name"
   */
  protected String name = "Annonymous";
  
  
  /**
   * A proxy that represents this agent in the social network.
   */
  private AgentProxy proxy = null;

  
  /**
   * The agent's environment.
   */
  protected ISocialNetwork environment = null;
  
  
  public StandardSocialNetworkAgent(){

  }

  /////////////////////////////////////////////////////////////////////////////
  // IAgentControl methods.
  /////////////////////////////////////////////////////////////////////////////

  abstract public void step();
  
  abstract public String toString();

  /**
 * @param id  the id to set
 * @uml.property  name="id"
 */
public void setId(int id){
    this.id = id;
  }
  
  /**
 * @param name  the name to set
 * @uml.property  name="name"
 */
public void setName(String name){
    this.name = name;
  }
  
  /**
 * @param environment  the environment to set
 * @uml.property  name="environment"
 */
public void setEnvironment(ISocialNetwork environment){
    this.environment = environment;
  }

  public IAgent getAgent() {
    return this;
  }
  
  public IAgent me() {
    return ((IAgent) proxy);
  }

  public void setAgentProxy(AgentProxy proxy) {
    this.proxy = proxy;
  }  

  
  
  
  /////////////////////////////////////////////////////////////////////////////
  // ISocialNetworkAgent methods.
  /////////////////////////////////////////////////////////////////////////////
  
  public List<IAgent> getNeighbors() {
    return environment.getNeighbors(me());
  }
  
  public List<IAgent> getNeighbors(int relationId) {
    return environment.getNeighbors(me(), relationId);
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // IAgent methods.
  /////////////////////////////////////////////////////////////////////////////
  

  /**
 * @return  the id
 * @uml.property  name="id"
 */
public int getId() {
    return id;
  }

  /**
 * @return  the name
 * @uml.property  name="name"
 */
public String getName() {
    return name;
  }


// TODO remove
/*
  public ISocialNetworkAgent getSocialNetworkAgent() {
    return ((ISocialNetworkAgent) this);
  }

  public IStimuliReceiver getStimuliReceiver() {
    return ((IStimuliReceiver) this);
  }

  */
  
  
  /////////////////////////////////////////////////////////////////////////////
  // IStimuliReceiver methods.
  /////////////////////////////////////////////////////////////////////////////

  abstract public void receiveStimulus(EnvironmentStimulus environmentStimulus);
  

  
  /////////////////////////////////////////////////////////////////////////////
  // Comparison methods
  /////////////////////////////////////////////////////////////////////////////
  
  public boolean equals(Object o){
    return equals((IAgent) o);
  }

  public int hashCode(){
    return id;
  }
  
  /**
   * Two agents are equal iff their IDs are equal.
   * 
   * @param a The agent to be compared to this one.
   * 
   * @return <code>true</code> if the IDs are equal; 
   *         <code>false</code> otherwise.
   */
  private boolean equals(IAgent a){
    
    if (this.id == a.getId()){
      return true;
    }
    
    return false;
  }
  


}
