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
package simulator.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * This class merely overrides the <code>resolveClass()</code> method of 
 * <code>ObjectInputStream</code> in order to employ for the correct
 * class loader for deserialization. This is necessary because simulation components
 * are loaded in a special way, and as a result are not available to the 
 * usual deserialization process.
 * 
 * @author Paulo Salem
 *
 */
public class SimulatorObjectInputStream extends ObjectInputStream {

  public SimulatorObjectInputStream(InputStream in) throws IOException {
    super(in);
    
  }
  
  /**
   * Resolves the necessary classes w.r.t. the class loader that manages all the components
   * and libraries.
   */
  protected Class<?> resolveClass(ObjectStreamClass desc)  throws IOException, ClassNotFoundException {
    
    // Get the current class loader
    ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
    
    Class<?> c = Class.forName(desc.getName(), false, currentCL);
    
    return c;
  }



}
