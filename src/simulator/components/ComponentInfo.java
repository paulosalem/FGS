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
package simulator.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * General information about a particular component.
 * 
 * @author Paulo Salem
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentInfo {
  
/**
 * The type of the component.
 */
public enum ComponentType{AGENT, PROPERTY}
  
  /**
   * Each component has a unique identifier.
   * 
   * @return A unique identifier.
   */
  String id();
  
  /**
   * Each component has a version. The same component id
   * may be associated with several versions.
   * 
   * @return The version of the component.
   */
  int version();
  
  /**
   * 
   * @return A user friendly name.
   */
  String name();
  
  /**
   * 
   * @return A user friendly description.
   */
  String description();
  

  /**
   * 
   * @return The type of this component.
   */
  ComponentType type();

}
