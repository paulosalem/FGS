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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import simulator.util.Assert;

/**
 * @author  user
 */
public class Relation<T> implements IRelation<T>{

  /**
   * @uml.property  name="id"
   */
  private int id;
  
  /**
   * @uml.property  name="name"
   */
  private String name;
  
  /**
   * @uml.property  name="description"
   */
  private String description;

  /**
   * A map from first coordinates to lists of second coordinates.
   * @uml.property  name="ties"
   * @uml.associationEnd  qualifier="key:java.lang.Object java.util.List<T>"
   */
  private Map<T, List<T>> ties = new HashMap<T, List<T>>();
  
  
  public Relation(int id, String name, String description){

    // Check parameters
    if(name == null || description == null){
      throw new IllegalArgumentException();
    }
    
    this.id = id;
    this.name = name;
    this.description = description;
    
  }

  
  /////////////////////////////////////////////////////////////////////////////
  // IRelation methods
  /////////////////////////////////////////////////////////////////////////////

  public void add(T a, T b) {
    Assert.notNull(a);
    Assert.notNull(b);
    
    List<T> ss = ties.get(a);
    
    // If there is no such list, we add the first coordinate
    // and create a list of second coordinates
    if(ss == null){
      ss = new LinkedList<T>();
      ss.add(b);
      ties.put(a, ss);
    }
    
    // If the list exist and it does not already contain the desired
    // second coordinate, we add it
    else{
      if(!ss.contains(b)){
        ss.add(b);
      }
    }
    
  }



  public List<T> relationalImage(T a) {
    
    List<T> ss = ties.get(a);
    
    // If there is no such list, we create an empty onew
    if(ss == null){
      ss = new LinkedList<T>();
    }
        
    return ss;
  }
  
  
  public List<T> inverseRelationalImage(T b){
    
    List<T> image = new LinkedList<T>();
    
    for(T a: ties.keySet()){
      
      // If b is in the second cordinate of a, then a
      // must be included in the inverse image
      if(ties.get(a).contains(b)){
        image.add(a);
      }
    }
    
    return image;
    
  }



  public void remove(T a, T b) {
    // TODO Auto-generated method stub
    
  }
  
  
  public List<ITie<T>> ties(){
	  
	  List<ITie<T>> ts = new LinkedList<ITie<T>>();
	  
	  for(T a: ties.keySet()){
		  for(T b: ties.get(a)){
			  ts.add(new Tie<T>(a, b));
		  }
	  }
	  
	  return ts;
  }
  
  public List<T> members(){
    
    Set<T> elements = new HashSet<T>();
    
    for(T t: ties.keySet()){
      elements.add(t);
      elements.addAll(ties.get(t));
    }
    
    return new LinkedList<T>(elements);
    
  }

  /**
 * @return  the id
 * @uml.property  name="id"
 */
public int getId(){
    return id;
  }

  /**
 * @return  the name
 * @uml.property  name="name"
 */
public String getName(){
    return name;
  }
  
  /**
 * @return  the description
 * @uml.property  name="description"
 */
public String getDescription(){
    return description;
  }
  
  public String toString(){
    return "[Relation " + name + ": id = " + id + "]";
  }



  

  
  
}
