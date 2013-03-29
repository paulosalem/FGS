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
package simulator.components;

import org.jdom.Element;

/**
 * Defines an interface by which a component can load its configuration through a
 * XML specification. This is particularly useful when it makes sense to delegate the control
 * over the input format to the component itself. For example, in adapting existing software
 * to work as a component, or when there is no good reason to provide a general
 * parameter setter for something which is very specific to a component.
 * 
 * @author Paulo Salem
 *
 */
public interface IXMLComponentInitializer {
  
  /**
   * Initializes the component with the specified XML structure.
   * 
   * @param domElement The XML structure that the component is capable of loading.
   */
  public void initialize(Element domElement); 
  // TODO would it be possible to load a Document object instead?

}
