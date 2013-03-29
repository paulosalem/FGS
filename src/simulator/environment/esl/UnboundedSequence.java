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
package simulator.environment.esl;

import java.util.LinkedList;

import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiDefinition;
import alevos.expression.picalculus.PiIdentifier;
import alevos.expression.picalculus.PiInputAction;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiOutputAction;
import alevos.expression.picalculus.PiParallel;
import alevos.expression.picalculus.PiPrefix;
import alevos.expression.picalculus.PiProcess;
import alevos.expression.picalculus.PiRestriction;

public class UnboundedSequence extends ESLOperator {
  
  ESLOperation operation;

  public UnboundedSequence(ESLOperation operation) {
    super();
    this.operation = operation;
  }

  @Override
  public PiProcess toPiProcess(Context context) throws IllegalSemanticsException {

    PiName start = new PiName("start");
    PiName done = new PiName("done");
    
    PiName x = new PiName("x"); // Some dummy parameter
    
    PiInputAction aStart = new PiInputAction(start, x);
    
    PiProcess op1 = operation.toPiProcess(context);
    op1.substitute(done, start);
    
    PiIdentifier forever = new PiIdentifier("Forever");
    
    PiProcess seq = new PiRestriction(
        start, 
        new PiParallel(
            op1, 
            new PiPrefix(
                aStart, 
                forever)));
    
    PiDefinition foreverDef = new  PiDefinition(forever.getName(), new LinkedList<PiName>(), seq);
    forever.setDefinition(foreverDef);
    
    return seq;
  }

}
