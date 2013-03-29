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

/**
 * Allows information to be presented to the user. Concrete subclasses define the particular
 * method of presentation.
 * 
 * @author Paulo Salem
 *
 */
public abstract class Messenger {
  
  protected boolean showDebugMsgs = false;
  
  protected int minMsgImportance = NORMAL_MSG;
  
  public final static int TRIVIAL_MSG = 0;
  public final static int UNINPORTANT_MSG = 1;
  public final static int NORMAL_MSG = 2;
  public final static int IMPORTANT_MSG = 3;
  public final static int CRUCIAL_MSG = 4;
  
  
  public Messenger(){
   
  }
  
  public Messenger(int minMsgImportance, boolean showDebugMsgs){
    Assert.isInInterval(minMsgImportance, TRIVIAL_MSG, CRUCIAL_MSG);
    
    this.minMsgImportance = minMsgImportance;
    this.showDebugMsgs = showDebugMsgs;
  }
  
  
  public void printMsg(String msg, int msgImportance){
    Assert.isInInterval(msgImportance, TRIVIAL_MSG, CRUCIAL_MSG);
    
    if (msgImportance >= minMsgImportance){
      printMsg(msg);
    }
  }
  
  abstract protected void printMsg(String msg);
  
  
  public void printDebugMsg(String msg, int msgImportance){
    Assert.isInInterval(msgImportance, TRIVIAL_MSG, CRUCIAL_MSG);
    
    if(showDebugMsgs && msgImportance >= minMsgImportance){
      printDebugMsg(msg);
    }
  }
  
  abstract protected void printDebugMsg(String msg);


  public void setShowDebugMsgs(boolean showDebugMsgs) {
    this.showDebugMsgs = showDebugMsgs;
  }

  public void setMinMsgImportance(int minMsgImportance) {
    this.minMsgImportance = minMsgImportance;
  }

  
  
}
