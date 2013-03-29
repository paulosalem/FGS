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

import java.util.List;

import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiInputAction;
import alevos.expression.picalculus.PiName;
import alevos.expression.picalculus.PiNilProcess;
import alevos.expression.picalculus.PiOutputAction;
import alevos.expression.picalculus.PiParallel;
import alevos.expression.picalculus.PiPrefix;
import alevos.expression.picalculus.PiProcess;
import alevos.expression.picalculus.PiRestriction;

public class Parallel extends ESLOperator {
  
  List<ESLOperation> operations;

  
  
  public Parallel(List<ESLOperation> operations) {
    super();
    this.operations = operations;
  }



  @Override
  public PiProcess toPiProcess(Context context) throws IllegalSemanticsException {
    
    PiName done = new PiName("done");
    PiName start = new PiName("start");

    PiName x = new PiName("x"); // Some dummy parameter
    
    PiOutputAction aDone = new PiOutputAction(done, x);
    PiInputAction aStart = new PiInputAction(start, x);
    
    
    
    
    // The operations put in parallel
    PiProcess par = buildPart(operations, context);
    
    // The final component
    PiPrefix fin = new PiPrefix(aDone, new PiNilProcess());
    for(int i = 0; i < operations.size(); i++){
      fin = new PiPrefix(aStart, fin);
    }
    
    // Put the final component in the parallel composition
    par = new PiParallel(par, fin);
    
    // Add the restriction and we are done
    PiProcess res = new PiRestriction(start, par);
        
    return res;
  }
  
  private PiProcess buildPart(List<ESLOperation> ops, Context context) throws IllegalSemanticsException{

    PiName done = new PiName("done");
    PiName start = new PiName("start");

    if(ops.size() == 0){
      return new PiNilProcess();
    }
    else if(ops.size() == 1){
      PiProcess p = ops.get(0).toPiProcess(context);
      p.substitute(done, start);
      return p;
    }
    else{
      
      PiProcess op1 = ops.get(0).toPiProcess(context);
      op1.substitute(done, start);
      
       
      PiParallel par = new PiParallel(
                                op1, 
                                buildPart(ops.subList(1,ops.size()), context));
    
      return par;
    }
    
  }

}
