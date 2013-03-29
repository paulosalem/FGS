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
package simulator.engine.strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import simulator.util.Assert;

/**
 * Given a sequence with N elements and a number K < N,
 * this class generates all possible sequences containing
 * K of the N elements. Notice that the generated sequences might
 * have repeated elements.
 * 
 * @author Paulo Salem
 *
 */
public class CombinationIterator<T> implements Iterator<List<T>>{

  /**
   * The elements whose combination is desired.
   */
  private List<T> elements;
  
  /**
   * How many members each combinations should have.
   */
  private int k;
  
  /**
   * The current combination.
   */
  private List<Integer> combination;
  
  /**
   * The current position that works as a pivot.
   */
  private int pivot;
  

  
  /**
   * 
   * @param elements The elements whose combination is desired.
   * @param k How many members each combinations should have. 
   */
  public CombinationIterator(List<T> elements, int k){
    Assert.notNull(elements);
    
    if(elements.size() < k){
      throw new IllegalArgumentException ("The combination size must be less than or equal to the number of elements in the sequence.");
    }
    
    this.elements = elements;
    this.k = k;
    
    // Setup the pivot to start at the last element
    pivot = k - 1;
    
    // Setup the initial combination to contain the first element K times
    combination = new ArrayList<Integer>();
    for(int i = 0; i < k; i++){
      combination.add(i, 0);
    }
    
  }

  
  
  /////////////////////////////////////////////////////////////////////////////
  // Iterator methods
  /////////////////////////////////////////////////////////////////////////////
  
  public boolean hasNext() {
    
    if(pivot >= 0){
      return true;
    }

    return false;
  }

  // TODO there is a problem somewhere here...
  public List<T> next() {
    
    if(hasNext()){
      
      int n = elements.size();
      
      for(int i = k - 1; i >= pivot; i-- ){
        
        // Increase current position, if possible
        if(combination.get(i) < n - 1){
          combination.set(i, combination.get(i) + 1);
          
          // We are done!
          break;
        }
        // If not possible and if we are at the pivot...
        else if (i == pivot){
          // ...decrease the pivot...
          pivot--;
         
          
          // ...and reset the positions modified so far (including the previous pivot).
          for(int j = pivot + 1; j < k; j++){
            combination.set(j, 0);
          }
          
          // If the pivot is non negative, we increase its value
          if(pivot > 0){
            combination.set(pivot, combination.get(pivot) + 1);
            
          } 
          
          // No more elements need to be examined
          break;

        }
      }
      
      // Process the result
      List<T> result = new ArrayList<T>();
      for(int i = 0; i < k; i++){
        result.add(i, elements.get(combination.get(i)));
      }
      
      return result;
      
    }
    else{
      throw new NoSuchElementException("There are no more combinations.");
    }
  }

  
  public void remove() {
    throw new UnsupportedOperationException ("Elements cannot be removed from the sequence.");
  }
  
  
}
