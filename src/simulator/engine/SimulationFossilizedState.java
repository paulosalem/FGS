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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A compact representation of a simulation state.
 * 
 * @author Paulo Salem
 *
 */
public class SimulationFossilizedState {
	
	
	// NOTE: If maps are changed, most of the operations in this class must
	// be changed as well.
	

	/**
   * @uml.property  name="string"
   * @uml.associationEnd  qualifier="key:java.lang.Object java.lang.String"
   */
	private Map<String, String> stringMap;
	
	/**
   * @uml.property  name="integer"
   * @uml.associationEnd  qualifier="key:java.lang.Object java.lang.Integer"
   */
	private Map<String, Integer> integerMap;
	
	/**
   * @uml.property  name="boolean"
   * @uml.associationEnd  qualifier="key:java.lang.Object java.lang.Boolean"
   */
	private Map<String, Boolean> booleanMap;
	
	
	public SimulationFossilizedState(Map<String, String> stringMap, Map<String, Integer> integerMap, Map<String, Boolean> booleanMap){
		
		this.stringMap = stringMap;
		this.integerMap = integerMap;
		this.booleanMap = booleanMap;
		
	}
	
	
	public String toString(){
		String s = "[";
		
		// Convert string map
		for(String k: stringMap.keySet()){
			s = s + k + ": " + stringMap.get(k) + ", ";
		}
		
		// Convert integer map
		for(String k: integerMap.keySet()){
			s = s + k + ": " + integerMap.get(k) + ", ";
		}		
		
		// Convert boolean map
		for(String k: booleanMap.keySet()){
			s = s + k + ": " + booleanMap.get(k) + ", ";
		}		
		
		s = s + "]";
		
		return s;
	}
	
	
	
	  /////////////////////////////////////////////////////////////////////////////
	  // Comparison methods
	  /////////////////////////////////////////////////////////////////////////////
	  
	  public boolean equals(Object o){
	    return equals((SimulationFossilizedState) o);
	  }

	  
	  /**
	   * Two fossilized states are equal iff they contain the same
	   * data.
	   * 
	   * @param s The fossilized state to be compared to this one.
	   * 
	   * @return <code>true</code> if they are equal; 
	   *         <code>false</code> otherwise.
	   */
	  private boolean equals(SimulationFossilizedState s){
		  
		  // Check for non-equality
		  if(!equalMaps(stringMap, s.stringMap)){
			  return false;
		  }
		  else if(!equalMaps(integerMap, s.integerMap)){
			  return false;
		  }
		  else if(!equalMaps(booleanMap, s.booleanMap)){
			  return false;
		  }
		  
		  // Passed all tests, must be equal
		  return true;

	  }
	  
	  /**
	   * Compare two maps for equaliy. They are considerer equal iff they contain
	   * exactly the same keyset and the same value for each key.
	   * 
	   * @param m1 One of the maps.
	   * @param m2 The other map.
	   * 
	   * @return <code>true</code> if the maps are equal; 
	   *         <code>false</code> otherwise.
	   */
	  private boolean equalMaps(Map m1, Map m2){

		  // If we order the maps' keys, we can compare the maps
		  // in O(n) time. Otherwise, it becomes O(n^2).
		  
		  ArrayList<String> keys1;
		  ArrayList<String> keys2;
		  
		  keys1 = new ArrayList<String>(m1.keySet());
		  Collections.sort(keys1);
		  keys2 = new ArrayList<String>(m2.keySet());
		  Collections.sort(keys2);
		
		  // If sizes doesn't match, we have an error
		  if(keys1.size() != keys2.size()){
			  return false;
		  }
		  
		  for(int i = 0, size = keys1.size(); i < size; i++){
			  
			  // If keys don't match, we have an error
			  if(!keys1.get(i).equals(keys2.get(i))){
				  return false;
			  }
			  
			  // If content don't match, we have an error
			  else if(!m1.get(keys1.get(i)).equals(m2.get(keys1.get(i)))){
				  return false;
			  }
		  }	  

		  return true;
	  }
	  
}
