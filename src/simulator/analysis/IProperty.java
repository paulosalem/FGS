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

import simulator.environment.IEnvironment;


/**
 * A property that can be attached to a <code>PropertyBearerWrapper</code>.
 * @author  Paulo Salem
 */
public interface IProperty {
  
	/**
   * @return  A unique ID for the property instace.
   */
	public int getId();
	
	
	/**
   * Sets the ID of the property to be the speficied one.
   * @param id  The new ID.
   */
	public void setId(int id);
	

	/**
   * @return  A user friendly name.
   */
	public String getName();
	
	
	/**
   * Sets the name of the property to be the specified one.
   * @param name  The new name.
   */
	public void setName(String name);
	
  
  /**
   * Sets the environment to be analyzed by the property. Concrete implementations
   * might use the set environment directly.
   * 
   * @param environment The environment to be analyzed.
   */
  public void setEnvironment(IEnvironment environment);
  
  
	/**
	 * Calculates the value of the property concerning the
	 * specified <code>PropertyBearerWrapper</code>.
	 * 
	 * 
	 * @param pb The property bearer to be assessed.
	 * 
	 * @return The value of the property converted to a String.
	 */
	public String getValueAsString(PropertyBearerWrapper pb) throws UndefinedPropertyException;

  /**
   * Calculates the value of the property concerning the
   * specified <code>Object</code>.
   * 
   * 
   * @param o The object to be assessed.
   * 
   * @return The value of the property converted to a String.
   */
  public String getValueAsString(Object o) throws UndefinedPropertyException;
	
	
	
	// TODO Check whether the following methods are necessary or not.
	
	
	/**
	 * Attaches the specified <code>PropertyBearerWrapper<code> to this
	 * property, so that it can be analyzed.
	 * 
	 * @param pb The <code>PropertyBearerWrapper<code> to be attached.
	 */
	//public void attach(PropertyBearerWrapper pb);
	

	/**
	 * Detaches the specified <code>PropertyBearerWrapper<code> from this
	 * property, so that it is no longer analyzed.
	 * 
	 * @param pb The <code>PropertyBearerWrapper<code> to be detached.
	 */
	//public void detach(PropertyBearerWrapper pb);
	
	
}
