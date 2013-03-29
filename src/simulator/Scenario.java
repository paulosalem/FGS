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
package simulator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import simulator.agent.AgentProxy;
import simulator.agent.IAgent;
import simulator.agent.IAgentControl;
import simulator.agent.relation.IRelation;
import simulator.agent.relation.Relation;
import simulator.agent.relation.RelationProxy;
import simulator.analysis.IProperty;
import simulator.analysis.PropertyBearerWrapper;
import simulator.components.ComponentInstantiationException;
import simulator.components.ComponentParameters;
import simulator.components.ComponentsRegistry;
import simulator.engine.SimulationState;
import simulator.engine.alevos.EMMASTransitionSystem;
import simulator.environment.EMMASEnvironment;
import simulator.environment.IEnvironment;
import simulator.environment.SocialNetworkEnvironment;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import simulator.util.Assert;

/**
 * Defines abstractly the entities present in a simulation.  <code>Scenario</code> 
 * builds an internal representation of such entities and only translate them in 
 * components' instances when <code>getInitialState</code> method is called. This 
 * architecture enforces the separation of component definition and component use. 
 * As an additional benefit, it allows the same scenario to be used with different 
 * sets of components.
 * 
 * @author  Paulo Salem
 */
public class Scenario implements Serializable{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  /////////////////////////////////////////////////////////////////////////////
  // Scenario description information
  /////////////////////////////////////////////////////////////////////////////
  


  /**
   * A user friendly name for the scenario.
   */
  private String name = "(Unnamed scenario)";
  
  /**
   * A user friendly description of the scenario.
   */
  private String description = "(No description available)";
  

  /**
   * A map from agents' IDs to agents' parameters.
   */
  private Map<Integer, ComponentParameters> agentsParameters = new HashMap<Integer, ComponentParameters>();
  
  
  /**
   * A map from properties' IDs to properties' parameters.
   */
  private Map<Integer, ComponentParameters> propertiesParameters= new HashMap<Integer, ComponentParameters>();
  
  
  /**
   * A map from relations' IDs to relations.
   */
  private Map<Integer, IRelation<IAgent>> relationsMap = new HashMap<Integer, IRelation<IAgent>>();
  

  /**
   * A list of triples containing relationsMap' pairs. The first coordinate of 
   * a triple is the ID of the relation. The other two coordinates
   * are the agents' IDs that form the pair.
   */
  private List<Integer[]> relationsPairs = new LinkedList<Integer[]>();
  
  
  /**
   * A map from agents' IDs to lists of attached properties' IDs. Each entry in the map points from one agent ID to the IDs of properties attached to it.
   */
  private Map<Integer, List<Integer>> agents2Properties = new HashMap<Integer, List<Integer>>();

  /**
   * A list of propeties IDs attached to the environment.
   */
  private List<Integer> environmentProperties = new LinkedList();
  
  /**
   * A transition system denoting an EMMAS model, if any.
   */
  private transient EMMASTransitionSystem emmasTS = null; // TODO transient ??

  
  
  /////////////////////////////////////////////////////////////////////////////
  // Proxies support
  /////////////////////////////////////////////////////////////////////////////
  
  /**
   * A map from agents' IDs to proxies.
   */
  private Map<Integer, AgentProxy> agentProxies = new HashMap<Integer, AgentProxy>();
  
  /**
   * A map from relations' IDs to proxies.
   */
  private Map<Integer, RelationProxy> relationProxies = new HashMap<Integer, RelationProxy>();


  
  public Scenario(String name, String description){
    this.name = name;
    this.description = description;
  }
  
  /**
   * Creates a proxy associated with the specified agent. If such a proxy
   * already exists, the existing instance is returned.
   * 
   * @param agent The agent for whom a proxy is to be built.
   * 
   * @return A new <code>AgentProxy</code> associated with the specified simulator.agent, if
   *         none exist; the existing <code>AgentProxy</code> otherwise.
   */
  public AgentProxy createAgentProxy(IAgent agent){
    // If there is already a proxy for the specified agent
    if(agentProxies.containsKey(agent.getId())){
      return agentProxies.get(agent.getId());
    }

    AgentProxy proxy = new AgentProxy(agent);
    
    // Save the proxy
    agentProxies.put(agent.getId(), proxy);
    
    return proxy;
  }
  
  /**
   * Creates a proxy associated with the specified agent relation. If such a proxy
   * already exists, the existing instance is returned.
   * 
   * @param relation The relation for whom a proxy is to be built.
   * 
   * @return A new <code>RelationProxy</code> associated with the specified relation, if
   *         none exist; the existing <code>RelationProxy</code> otherwise.
   */  
  public RelationProxy createRelationProxy(IRelation<IAgent> relation){
    // If there is already a proxy for the specified relation
    if(relationProxies.containsKey(relation.getId())){
      return relationProxies.get(relation.getId());
    }

    RelationProxy proxy = new RelationProxy(relation, this);
    
    // Save the proxy
    relationProxies.put(relation.getId(), proxy);
    
    return proxy;  
  }
  
  public List<IAgent> getAgentProxies(){
    return new LinkedList<IAgent>(agentProxies.values());
  }

  public List<IRelation> getRelationProxies(){
    return new LinkedList<IRelation>(relationProxies.values());
  }
  
  
  
  /**
   * Creates an initial state for the simulation. This initial state consists
   * of new instances of all simulation components. However, all proxies
   * already associated with this scenario are updated, so that they
   * refer to the new instances of the components they serve. 
   *  
   * 
   * @param cr Where the components to instatiate are located.
   * 
   * @return An initial state based on the specified registry.
   */
  public SimulationState createInitialState(ComponentsRegistry cr) throws ComponentInstantiationException{
    Assert.notNull(cr);
    
    //  Create a new environment according to the available information 
    IEnvironment environment;
    if(emmasTS != null){
      // If there is an EMMAS transition system, this is the environment we need
      environment = new EMMASEnvironment(this, emmasTS);
    }
    else{
      // Else, we come up with a simple environment
      environment = new SocialNetworkEnvironment(this);
    }
    
    
    List<IAgentControl> agents = new LinkedList<IAgentControl>();
    Map<Integer, IAgentControl> ids2Agents = new HashMap<Integer, IAgentControl>(); 
     
    // Instantiate and configure relevant agents
    for(Integer i: agentsParameters.keySet()){
      
      // Instantiates the agent
      IAgentControl ac = cr.createAgentControl(agentsParameters.get(i), environment);
      agents.add(ac);
      ids2Agents.put(i, ac);
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Created the following agent while building an initial state:", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  " + ac.toString(), Messenger.NORMAL_MSG);
      
      // If there is no proxy for the agent
      if(!agentProxies.containsKey(i)){

        // Setup proxy
        ac.setAgentProxy(createAgentProxy(ac.getAgent()));  
      }
      
      // Else, if there is already a proxy (i.e., because this scenario has  
      // already generated an initial state before)
      else{
        
        // Use the existing proxies, but inform them that
        // the underlying agent instance has changed
        agentProxies.get(i).setAgent(ac.getAgent());
        ac.setAgentProxy(agentProxies.get(i));
        
      }
    }

    
    // Configure relationsMap
    for(Integer[] t: relationsPairs){
      IRelation<IAgent> r = relationsMap.get(t[0]);
      
      IAgent a1 = null;
      IAgent a2 = null;
      
      // Locate the agents the form the pair
      for(IAgentControl ac: agents){
        if(ac.getId() == t[1]){
          a1 = ac.getAgent(); 
        }
        
        // Note that the two agents can be the same
        
        if(ac.getId() == t[2]){
          a2 = ac.getAgent();
        }
      }
      
     
      r.add(a1, a2);
    }
    
    List<IRelation<IAgent>> relations = new LinkedList<IRelation<IAgent>>(relationsMap.values());
    
    
    // Setup relation proxies 
    for(IRelation<IAgent> r: relations){
      
      // If there is no proxy yet we create it
      if(!relationProxies.containsKey(r.getId())){
        createRelationProxy(r);  
      }
      
      
      // Else, if a proxy has already been put in place, we use it
      else{
        RelationProxy rp = relationProxies.get(r.getId());
        rp.setRelation(r);
      }
      
      
    }


    
    List<IProperty> properties = new LinkedList<IProperty>();
    Map<Integer, IProperty> ids2Properties = new HashMap<Integer, IProperty>();
    
    // Instantiate and configure relevant properties
    for(Integer i: propertiesParameters.keySet()){
    	IProperty p = cr.createProperty(propertiesParameters.get(i), environment);
    	properties.add(p);
    	ids2Properties.put(i, p);
    }
    
    List<PropertyBearerWrapper> pbWrappers = new LinkedList<PropertyBearerWrapper>();
    
    // Attach properties to agents
    for(Integer i: agents2Properties.keySet()){
    	
    	// We need a wrapper to attach the properties
    	PropertyBearerWrapper pbw = new PropertyBearerWrapper(ids2Agents.get(i), ids2Agents.get(i).getAgent().getName());

    	// Attach each property
    	for(Integer j: agents2Properties.get(i)){
    		pbw.attach(ids2Properties.get(j));
    	}
    	
    	// Save the wrapper
    	pbWrappers.add(pbw);
    	
    }
    

    
    // A property bearer wrapper for the environment
    PropertyBearerWrapper ew = new PropertyBearerWrapper(environment, "Environment");
    
    // Attach properties to environment
    for(Integer i: environmentProperties){   	
    	ew.attach(ids2Properties.get(i));
    }
    
    // Save the environment wrapper
	pbWrappers.add(ew);
    
    
    // Instantiate and configure the initial state
    SimulationState s = new SimulationState(0, environment, relations, agents, properties, pbWrappers);
    
    return s;
  }
  
  /**
   * Creates the agent controls of the scenario, disregarding everything else (including the environment).
   * This method should be used when one is interested in the isolated agents only.
   * 
   * @param cr
   * @return
   * @throws ComponentInstantiationException
   */
  public List<IAgentControl> createAgentControls(ComponentsRegistry cr) throws ComponentInstantiationException{
    Assert.notNull(cr);
    
    List<IAgentControl> agents = new LinkedList<IAgentControl>();
    
    // Instantiate and configure relevant agents
    for(Integer i: agentsParameters.keySet()){
      
      // Instantiates the agent
      IAgentControl ac = cr.createAgentControl(agentsParameters.get(i), null);
      agents.add(ac);
    }
    
    return agents;
  }
  
  /**
   * Add an agent to the scenario.
   * 
   * @param componentId The ID of the component that implements the agent.
   * @param agentId The ID to be assigned to the agent.
   * @param agentName A user friendly name for the agent.
   */
  public void addAgent(String componentId, int agentId, String agentName){
    
    Assert.notNull(componentId);
    Assert.notNull(agentName);
    
    ComponentParameters cp = new ComponentParameters(componentId);
    
    // Add standard parameters
    cp.addParameter("id", Integer.toString(agentId));
    cp.addParameter("name", agentName);
    
    // Add to the agents list
    agentsParameters.put(agentId, cp);
  }
  
  public void addAgentInitializer(int agentId, Element initializer){
    Assert.notNull(initializer);
    
    // If the relevant agent is not present
    if(!agentsParameters.containsKey(agentId)){
      throw new IllegalArgumentException("A valid agent ID must be specified.");
    }
    
    ComponentParameters cp = agentsParameters.get(agentId);
    
    cp.setInitializer(initializer);
  }

  
  /**
   * Configure an agent's parameter.
   * 
   * @param agentId The ID of the agent to be configured.
   * @param parameterName The name of the parameter.
   * @param parameterValue The value of the parameter.
   */
  public void addAgentParameter(int agentId, String parameterName, String parameterValue){
    
    Assert.notNull(parameterName);
    Assert.notNull(parameterValue);
    
    // If the relevant agent is not present
    if(!agentsParameters.containsKey(agentId)){
      throw new IllegalArgumentException("A valid agent ID must be specified.");
    }
    
    ComponentParameters cp = agentsParameters.get(agentId);
    
    // Add parameter
    cp.addParameter(parameterName, parameterValue);
  }
  
  /**
   * Configure an agent's list parameter.
   * 
   * @param agentId The ID of the agent to be configured.
   * @param parameterName The name of the parameter.
   * @param parameterValue The value of the parameter.
   */
  public void addAgentParameter(int agentId, String parameterName, List<String> parameterValue){
    
    Assert.notNull(parameterName);
    Assert.notNull(parameterValue);
    
    // If the relevant agent is not present
    if(!agentsParameters.containsKey(agentId)){
      throw new IllegalArgumentException("A valid agent ID must be specified.");
    }
    
    ComponentParameters cp = agentsParameters.get(agentId);
    
    // Add parameter
    cp.addParameter(parameterName, parameterValue);
  }  
  

  /**
   * Adds a relation to the scenario.
   * 
   * @param id The unique ID to be assigned to the relation.
   * @param name A user friendly name for the relation.
   * @param description A user friendly description of the relation.
   */
  public void addRelation(int id, String name, String description){
    
    Assert.notNull(name);
    Assert.notNull(description);
    
    IRelation<IAgent> relation = new Relation<IAgent>(id, name, description);
    
    // Check if the relation being added has a unique ID
    if(relationsMap.containsKey(id)){
      throw new IllegalArgumentException("No two relationsMap in a scenario can have the same ID.");
    }
    
    // Ok, we may add the relation
    relationsMap.put(id, relation);
  }

  
  /**
   * Adds a ordered pair of agents to a relation.
   * 
   * @param relationId The ID of the relation.
   * @param agent1Id The ID of the first agent.
   * @param agent2Id The ID of the second agent.
   */
  public void addRelatioPair(int relationId, int agent1Id, int agent2Id){
    Integer[] triple = new Integer[3];
    
    // Check if the relation is defined
    if(!relationsMap.containsKey(relationId)){
      throw new IllegalArgumentException("Pairs must be added to existing realtions.");
    }
    
    triple[0] = relationId;
    triple[1] = agent1Id;
    triple[2] = agent2Id;
    
    relationsPairs.add(triple);
  }
  
  public void addProperty(String componentId, int propertyId, String propertyName){
	    Assert.notNull(componentId);
	    Assert.notNull(propertyName);
	    
	    ComponentParameters cp = new ComponentParameters(componentId);
	    
	    // Add standard parameters
	    cp.addParameter("id", Integer.toString(propertyId));
	    cp.addParameter("name", propertyName);
	    
	    // Add to the agents list
	    propertiesParameters.put(propertyId, cp);
	  
  }

  
  /**
   * Configure a property's parameter.
   * 
   * @param propertyId The ID of the property to be configured.
   * @param parameterName The name of the parameter.
   * @param parameterValue The value of the parameter.
   */
  public void addPropertyParameter(int propertyId, String parameterName, String parameterValue){
    
    Assert.notNull(parameterName);
    Assert.notNull(parameterValue);
    
    // If the relevant property is not present
    if(!propertiesParameters.containsKey(propertyId)){
      throw new IllegalArgumentException("A valid property ID must be specified.");
    }
    
    ComponentParameters cp = propertiesParameters.get(propertyId);
    
    // Add parameter
    cp.addParameter(parameterName, parameterValue);
  }

  /**
   * Configure a property's list parameter.
   * 
   * @param propertyId The ID of the property to be configured.
   * @param parameterName The name of the parameter.
   * @param parameterValue The value of the parameter.
   */
  public void addPropertyParameter(int propertyId, String parameterName, List<String> parameterValue){
    Assert.notNull(parameterName);
    Assert.notNull(parameterValue);
    
    // If the relevant property is not present
    if(!propertiesParameters.containsKey(propertyId)){
      throw new IllegalArgumentException("A valid property ID must be specified.");
    }
    
    ComponentParameters cp = propertiesParameters.get(propertyId);
    
    // Add parameter
    cp.addParameter(parameterName, parameterValue);
  }
  
  public void setEMMAS(EMMASTransitionSystem ts){
    Assert.notNull(ts);
    
    this.emmasTS = ts;
  }
  
  public EMMASTransitionSystem getEMMAS() {
    return emmasTS;
  }
  
  /**
   * Attaches a property to an agent.
   * 
   * @param propertyId The ID of the desired property.
   * @param agentId The ID of the desired agent.
   */
  public void attachPropertyToAgent(int propertyId, int agentId){

	  List<Integer> properties = agents2Properties.get(agentId);
	  
	  // If the agent is not mapped yet, we map it now
	  if (properties == null){
		  properties = new LinkedList<Integer>();
	  }
	  
	  properties.add(propertyId);
	  
	  // Update the map
	  agents2Properties.put(agentId, properties);
  }
  
  /**
   * Attaches a property to the environment.
   * 
   * @param propertyId The ID of the desired property.
   */
  public void attachPropertyToEnvironment(int propertyId){
	  environmentProperties.add(propertyId);
  }

  

/**
 * @return  the name
 * @uml.property  name="name"
 */
public String getName(){
    return name;
  }
  
  /**
 * @return  the description
 * @uml.property  name="description"
 */
public String getDescription(){
    return description;
  }




  
}
