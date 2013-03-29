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
package simulator.agent.action;

import java.io.Serializable;

import simulator.util.Assert;

/**
 * Represents the action of an agent as it is transmitted through an environment. 
 * Objects from this class are immutable in order to facilitate
 * sharing.
 * 
 * @author Paulo Salem
 *
 */
public class EnvironmentAction implements Serializable{
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  
  private String type;

  public EnvironmentAction(String type) {
    super();
    
    Assert.notNull(type);
    
    this.type = type;
  }
  
  
  @Override
  public String toString(){
    return "[Action type='"+ type +"']";
  }
  
  
  
  public String getType() {
    return type;
  }


  @Override
  public boolean equals(Object obj){
    
    if(obj instanceof EnvironmentAction){
      EnvironmentAction a = (EnvironmentAction) obj;
      if(this.type.equals(a.type)){
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public int hashCode(){
    return type.hashCode();
  }

}
