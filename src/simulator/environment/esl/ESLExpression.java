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

import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiProcess;

/**
 * An abstract Environment Specification Language experession.
 * 
 * @author Paulo Salem
 *
 */
public abstract class ESLExpression {
  
  //
  // Action emission pi-calculus names
  //
  public static final String NAME_EMIT = "emit";
  public static final String NAME_STOP = "stop";
  
  //
  // Stimulation pi-calculus names
  //
  public static final String NAME_BEGINNING = "beginning";
  public static final String NAME_STABLE = "stable";
  public static final String NAME_ENDING = "ending";
  public static final String NAME_ABSENT = "absent";
  
  public static final String NAME_CREATE = "ccn";
  public static final String NAME_DESTROY = "destroy";
  
  public static final String NAME_START = "start";
  public static final String NAME_DONE = "done";
  
  public static final String NAME_COMMIT = "commit";
  
  //
  // Markers
  //
  
  public static final String MARKER_AGENT = "agent";
  public static final String MARKER_ACTION_TRANSFORMER = "at";
  
  
  public abstract PiProcess toPiProcess(Context context) throws IllegalSemanticsException;

}
