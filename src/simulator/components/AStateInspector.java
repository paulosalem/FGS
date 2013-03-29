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
 * Marks a method as a state inspector. State inspectors allow the simulator
 * to differ methods that are supposed to be monitored from methods
 * that should be ignored (e.g., because they are only important
 * for the agent's internal implementation).
 * 
 * State inspectors differ from parameter inspector in that state
 * can change during simulation, while parameters, once setup, should remain
 * fixed. Hence, state inspectors monitor changes to the underlying component,
 * while parameter inspectors merely present to the user the 
 * parameters in effect.
 * 
 * @author Paulo Salem
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AStateInspector {

  /**
   * @return A user friendly name to the inspector.
   */
  String name() default "(un-named)";

  
  /**
   * @return A user friendly description to the inspector.
   */
  String description() default "(none)";
  
}
