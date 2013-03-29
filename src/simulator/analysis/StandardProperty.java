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
package simulator.analysis;

import java.io.Serializable;

import simulator.components.AComponentInfo;
import simulator.environment.IEnvironment;

/**
 * @author  Paulo Salem
 */
@AComponentInfo(
  id = "simulator.analysis.StandardProperty",
  version = 1,
  name = "Standard Property",
  description = "A base and abstract implementation of a property.",
  type = AComponentInfo.ComponentType.PROPERTY
)
abstract public class StandardProperty implements IProperty, Serializable{
  
  private static final long serialVersionUID = 1L;
  

	
	/**
	 * The unique identificator of the property's instance.
	 */
	private int id;
  
	/**
	 * A user friendly name to the propertie's instance.
	 */
	private String name="(Unnamed property instance)";
	
  /**
   * The environment to be analyzed. Subclasses may access this directly.
   */
  protected IEnvironment environment;
	
	///////////////////////////////////////////////////////////////////////////
	// IProperty methods
	///////////////////////////////////////////////////////////////////////////
	
	
	/**
   * @return  the id
   */
	public int getId(){
		return id;
	}
	
	/**
   * @param id  the id to set
   */
	public void setId(int id){
		this.id = id;
	}
	
	/**
   * @return  the name
   */
	public String getName(){
		return name;
	}
	
	/**
   * @param name  the name to set
   */
	public void setName(String name){
		this.name = name;
	}
  
  public void setEnvironment(IEnvironment environment){
    this.environment = environment;
  }
	
	abstract public String getValueAsString(PropertyBearerWrapper pb) throws UndefinedPropertyException;
	
  abstract public String getValueAsString(Object o) throws UndefinedPropertyException;
	
	
	  /////////////////////////////////////////////////////////////////////////////
	  // Comparison methods
	  /////////////////////////////////////////////////////////////////////////////
	  
	  public boolean equals(Object o){
	    return equals((IProperty) o);
	  }

	  public int hashCode(){
	    return id;
	  }
	  
	  /**
	   * Two properties are equal iff their IDs are equal.
	   * 
	   * @param p The property to be compared to this one.
	   * 
	   * @return <code>true</code> if the IDs are equal; 
	   *         <code>false</code> otherwise.
	   */
	  private boolean equals(IProperty p){
	    
	    if (this.id == p.getId()){
	      return true;
	    }
	    
	    return false;
	  }

}
