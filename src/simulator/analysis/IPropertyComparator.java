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

/**
 * Provides a mechanism to compare the value of a property.
 * This interface should be implemented only by property components.
 * 
 * @author Paulo Salem
 *
 */
public interface IPropertyComparator {

  public enum Comparison {EQUAL, LESS, GREATER};
  
  /**
   * Compares the specified value.
   * 
   * @param value1 The first value.
   * @param value2 The second value.
   * 
   * @return The comparison that relates the first to the second values.
   */
  public Comparison compare(String value1, String value2);
  
  
  /**
   * 
   * @return The minimun value the property might have.
   */
  public String minimumValue();
  
  
  /**
   * 
   * @return The maximum value the property might have.
   */
  public String maximumValue();
  
  // TODO remove?
  /**
   * 
   * @return The property to which this comparator can be applied to.
   */
  //public IProperty getProperty();
}
