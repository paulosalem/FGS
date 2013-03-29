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
 package simulator.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import simulator.agent.IAgent;
import simulator.agent.IAgentControl;
import simulator.agent.relation.IRelation;
import simulator.analysis.IProperty;
import simulator.analysis.PropertyBearerWrapper;
import simulator.analysis.UndefinedPropertyException;
import simulator.environment.IEnvironment;
import simulator.util.Assert;
import simulator.util.Cloning;


/**
 * @author Paulo Salem
 */
public class SimulationState implements Serializable{
  

  /**
   * 
   */
  private static final long serialVersionUID = 5756431919893150971L;

  /**
   * Indicates the position of the state with respect to a sequence of states
   * (i.e., a simulation run).
   */
  private int position = 0;

  /**
   * The relations available in the state.
   */
  private List<IRelation<IAgent>> relations = new LinkedList<IRelation<IAgent>>();

  /**
   * The agents available in the state.
   */
  private List<IAgentControl> agentControls = new LinkedList<IAgentControl>();
  
  /**
   * The agents' environment.
   */
  private IEnvironment environment;
  
  /**
   * The <code>IProperty</code>s available in the state;
   */
  private List<IProperty> properties = new LinkedList<IProperty>();

  /**
   * The <code>PropertyBearerWrapper</code>s available in the state
   */
  private List<PropertyBearerWrapper> propertyBearerWrappers = new LinkedList<PropertyBearerWrapper>();

  // TODO Necessary?
  /**
   * A map from <code>IProperty</code> to its <code>String</code> value.
   * The simulator is responsible for inserting values in the map.
   */
  // private Map<IProperty, String> stringPropertyObservation = new HashMap<IProperty, String>();
  
  
  public SimulationState(int position, IEnvironment environment, List<IRelation<IAgent>> relations, List<IAgentControl> agentControls, List<IProperty> properties, List<PropertyBearerWrapper> propertyBearerWrappers){
	  Assert.notNull(relations);
	  Assert.notNull(agentControls);
	  Assert.notNull(properties);
	  Assert.notNull(propertyBearerWrappers);
	  Assert.notNull(environment);

	  this.position = position;
    this.relations = relations;
    this.agentControls = agentControls;
    this.properties = properties;
    this.propertyBearerWrappers = propertyBearerWrappers;
    
    environment.setSimulationState(this);
    this.environment = environment;
    
    
    
    // The environment is also a property bearer, so we must wrapp it
//    PropertyBearerWrapper pbw = new PropertyBearerWrapper(environment, "Environment");
//    this.propertyBearerWrappers.add(pbw);
    
    // TODO remove! ComponentsRegistry is doing this now
    // Inform the agents about the environment
    /*
    for(IAgentControl ac: agentControls){
      
      // Setup environment
      ac.setEnvironment(environment);
      
      // Setup proxies
      ac.setAgentProxy(environment.createAgentProxy(ac.getAgent()));
    }*/
    

  }
  

  
  public Object clone(){
    
    Object clone = null;
    
    // TODO This works, but it is probably very inefficient. We may try to optimize.
    
    try{
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      
      oos.writeObject(this);
      
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      SimulatorObjectInputStream ois = new SimulatorObjectInputStream(bais);
      
      clone = ois.readObject();
    
    } catch(Exception e){
      // TODO
      e.printStackTrace();
    }


    return clone;
  }
  
  public int getPosition() {
    return position;
  }
  
  

  public void setPosition(int position) {
    Assert.nonNegative(position);
    
    this.position = position;
  }

  /**
 * @return  The relations of the simulation state.
 * @uml.property  name="relations"
 */
  public List<IRelation<IAgent>> getRelations(){
    return relations;
  }
  
/**
 * @return  The agent controls of the simulation state.
 * @uml.property  name="agentControls"
 */
  public List<IAgentControl> getAgentControls(){
    return agentControls;
  }

  /**
   * 
   * @param id The ID of the desired agent.
   * 
   * @return The appropriate <code>IAgentControl</code>, if it exists;
   *         <code>null<code> otherwise.
   */
  public IAgentControl getAgentControl(int id){
    
    for(IAgentControl ac: agentControls){
      if(ac.getAgent().getId() == id){
        return ac;
      }
    }
    
    return null;
  }
  
  /**
   * 
   * @param id The ID of the desired property.
   * 
   * @return The appropriate <code>IProperty</code>, if it exists;
   *         <code>null<code> otherwise.
   */
  public IProperty getProperty(int id){
    
    for(IProperty p: properties){
      if(p.getId() == id){
        return p;
      }
    }
    
    return null;
  }
  
  /**
   * @return  The <code>PropertyBearerWrapper</code> of the simulation state.
   * @uml.property  name="propertyBearerWrappers"
   */
  public List<PropertyBearerWrapper> getPropertyBearerWrappers(){
	  return propertyBearerWrappers;
  }
  
  /**
   * 
   * @return The <code>IEnvironment</code> of this state.
   */
  public IEnvironment getEnvironment(){
    return environment;
  }
  
  /**
   * 
   * @return A fossilized version of the simulation state.
   */
  public SimulationFossilizedState fossilize() throws UndefinedPropertyException{
    SimulationFossilizedState sfs = null;
    
    Map<String, String> stringMap = new HashMap<String, String>();
    Map<String, Integer> integerMap = new HashMap<String, Integer>();
    Map<String, Boolean> booleanMap = new HashMap<String, Boolean>();
    
    // Inspect each property bearer
    for(PropertyBearerWrapper pb: propertyBearerWrappers){
    	for(IProperty p: pb.getProperties()){
    		stringMap.put(p.getName() + "@" + pb.getName(),p.getValueAsString(pb));
    	}
    	
    }
    
    sfs = new SimulationFossilizedState(stringMap, integerMap, booleanMap);
    
    return sfs;
  }
  
  
  public String toString(){
    
    String s = "Agents after simulation: \n";
    
    for(IAgentControl ac: agentControls){
      s = s + "  " + ac.toString() + "\n";
    }
    
    s = s + "\nProperties after simulation: \n";
    
    for(IProperty p: properties){
      s = s + "  " + p.toString() + "\n";
    }
    
    s = s + "\nProperties' values after simulation: \n";
    
    try {
      s = s + "  "+ fossilize().toString()+ "\n";
    } catch (UndefinedPropertyException e) {
      s = s + "  Error: Some property has been used in an inappropriate manner. \n";
    }
    
    return s;
  }
}
