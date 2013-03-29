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
package simulator.ui;

import simulator.util.Assert;

public class SimulatorUI {

  private static SimulatorUI instance;
  
  private Messenger messenger;
  
  private SimulatorUI(Messenger messenger){
    Assert.notNull(messenger);
    this.messenger = messenger;
  }
  
  public static void initialize(Messenger messenger){
    Assert.notNull(messenger);
    
    if(instance == null){
      instance = new SimulatorUI(messenger);
    }
    else{
      instance.messenger = messenger;
    }
    
  }
  
  
  public static SimulatorUI instance(){
    
    if(instance == null){
      throw new RuntimeException("Please initialize the SimulatorUI before using it.");
    }
    
    return instance;
  }

  public Messenger getMessenger() {
    return messenger;
  }
  
  
  
}
