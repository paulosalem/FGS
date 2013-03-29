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
package simulator.agent.relation;

import java.util.List;

public interface IRelation<T> {

  
  public void add(T a, T b);
  
  public void remove(T a, T b);
  
  /**
   * 
   * @return A list of all ordered pairs that belong to this relation.
   */
  public List<ITie<T>> ties();

  
  /**
   * 
   * @return A list of all the elements that are related in this relation. 
   */
  public List<T> members();
  
  /**
   * Let <code>R: A x B</code> be the relation controlled by this interface
   * and <code>a</code> the specified element. 
   * Then the relational image of <code>R</code> with respect to <code>a</code>
   * is the set of all elements 
   * <code>b</code> of <code>B</code> such that <code>(a, b)</code> 
   * belongs to <code>R</code>.
   * 
   * @param a The element with respct to wich the relational image is to be calculated.
   * 
   * @return The inverse relational image with respect to the specified element.
   */
  public List<T> relationalImage(T a);
  

  /**
   * Let <code>R: A x B</code> be the relation controled by this interface
   * and <code>b</code> the specified element. 
   * Then the inverse relational image of <code>R</code> with respect to <code>b</code>
   *  is the set of all elements 
   * <code>a</code> of <code>A</code> such that <code>(a, b)</code> 
   * belongs to <code>R</code>.
   * 
   * @param b The element with respct to wich the relational image is to be calculated.
   * 
   * @return The inverse relational image with respect to the specified element.
   */
  public List<T> inverseRelationalImage(T b);
  
  /**
   * Every relation has a unique ID withing a simulation.
   * 
   * @return The relation's unique ID.
   */
  public int getId();

  
  /**
   * 
   * @return A user friendly name.
   */
  public String getName();
  
  
  /**
   * 
   * @return A user friendly description.
   */
  public String getDescription();
  
  public String toString();
}
