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
package simulator.analysis.formal;

import simulator.analysis.IProperty;
import simulator.analysis.PropertyBearerWrapper;
import simulator.analysis.UndefinedPropertyException;
import simulator.util.Assert;

// TODO: Not only properties, but also values must be comparable

/**
 * Compares two properties and becomes true if the first one is greater than the second one.
 * @author  Paulo Salem
 */
public class GreaterThan extends Predicate {

  /**
   * The property that has to be greater.
   */
  private IProperty p1;

  /**
   * The property bearer wrapper that the first property should be applied to.
   */
  private PropertyBearerWrapper pbw1;
  
  /**
   * The property that has to be smaller.
   */
  private IProperty p2;
  
  /**
   * The property bearer wrapper that the second property should be applied to.
   */  
  private PropertyBearerWrapper pbw2;
  
  /**
   * Builds a new instance so that it is true if, and only if,
   * <code>p1</code> > <code>p2<code> and they are both
   * of a numerical type.
   * 
   * @param p1 The property that has to be greater.
   * @param pbw1 The property bearer wrapper that the first property should be applied to.
   * @param p2 The property that has to be smaller.
   * @param pbw1 The property bearer wrapper that the second property should be applied to.
   */
  public GreaterThan(IProperty p1, PropertyBearerWrapper pbw1, IProperty p2, PropertyBearerWrapper pbw2){
    Assert.notNull(p1);
    Assert.notNull(pbw1);
    Assert.notNull(p2);
    Assert.notNull(pbw2);
    
    this.p1 = p1;
    this.pbw1 = pbw1;
    this.p2 = p2;
    this.pbw2 = pbw2;
  }
  
  
  @Override
  public boolean isTrue() throws UndefinedPropertyException{
    
    try{
      // Convert both properties' values to a number.
      double v1 = Double.valueOf(p1.getValueAsString(pbw1));
      double v2 = Double.valueOf(p2.getValueAsString(pbw2));
      
      // Apply the desired semantics
      if(v1 > v2){
        return true;
      }
      else{
        return false;
      }
      
    }
    catch(NumberFormatException e){
      // If the specified values are not numbers, the predicate
      // is false
      return false;
    }
  }

}
