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
package simulator.util;

/**
 * Provides general assertion capabilities. 
 * 
 * @author Paulo Salem
 *
 */
public class Assert {


  public static void notNull(Object o){
    if(o == null){
      throw new IllegalArgumentException("A non-null parameter must be specified.");
    }
  }

  public static void notNull(Object o, String msg){
    if(o == null){
      throw new IllegalArgumentException(msg);
    }
  }
  
  public static void nonNegative(int i){
    if(i < 0){
      throw new IllegalArgumentException("A non-negative parameter must be specified.");
    }
  }
  
  public static void nonNegativeNonZero(int i){
    if(i <= 0){
      throw new IllegalArgumentException("A non-negative parameter must be specified.");
    }
  }
  
  public static void isInInterval(int i, int min, int max){
    if(i < min || i > max){
      throw new IllegalArgumentException("The specified number must be in the specified interval.");
    }
  }
  
  public static void isInInterval(double i, double min, double max){
    if(i < min || i > max){
      throw new IllegalArgumentException("The specified number must be in the specified interval.");
    }
  }

}
