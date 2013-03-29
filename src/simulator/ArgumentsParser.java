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
package simulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses and provides access to the parameters contained in a string.
 * Each argument must have the following form:
 * 
 *   -argname argvalue
 *   
 * The argument value might be empty, in which case the argument
 * becomes a boolean value.
 * 
 * 
 * @author Paulo Salem
 *
 */
public class ArgumentsParser {

  /**
   * A map from parameter names to their values.
   */
  Map<String, String> parameters = new HashMap<String, String>();
  
  public ArgumentsParser(String[] args, Collection<String> possibleParameters) throws InvalidArgumentException{

    
    for(int i = 0; i < args.length; i++){
      String a = args[i];
      
      String name = "";
      String value = null;
      
      // If the argument is a parameter name, we parse it
      if(isParameterName(a)){
        name = a.substring(1); // chops off the '-' character
        
        // Check if it is a valid name
        if(!possibleParameters.contains(name)){
          throw new InvalidArgumentException("A parameter named '" + name + "' is not allowed.");
        }
        
      }
      // If not, we throw an exception
      else{
        throw new InvalidArgumentException("Argument name expected, but instead found '" + a + "'.");
      }
      
      // If now there is a next value
      if(i < args.length - 1){
        
        a = args[i+1];
        
        // If it is not a parameter name, it must be the value of a parameter
        if(!isParameterName(a)){
          // Acknowledge that the value was read
          i++;
        
          value = a;
        }
      }
      
      // Add to the argument map
      parameters.put(name, value);
    }
  }

  
  /**
   * @param name The name of the desired parameter.
   * 
   * @return The value of the desired argument. 
   */
  public String getParameterValue(String name){
    return parameters.get(name);
  }

  /**
   * 
   * @param name The name of the desired parameter.
   * 
   * @return <code>true</code> if the parameter has a set value; <code>false</code> otherwise.
   */
  public boolean hasParameterValue(String name){
    
    if(parameters.get(name) != null){
      return true;
    }

    return false;
    
  }
  
  /**
   * 
   * @param name the name of the desired parameter.
   * 
   * @return <code>True</code> if the parameter is set;
   *         <code>False</code> otherwise.
   */
  public boolean isParameterSet(String name){
    return parameters.containsKey(name);
  }

  
  /**
   * 
   * @return The number of parameters available.
   */
  public int getNumberOfParameters(){
    return parameters.size();
  }
  
  
  public void enforceParameterAndValuePresence(String name) throws InvalidArgumentException{
    if(!isParameterSet(name)){
      throw new InvalidArgumentException("Parameter '" + name + "' must be present and have a value.");
    }
  }
  
  public void enforceValuePresence(String name) throws InvalidArgumentException {
    if(isParameterSet(name)){
      if(!hasParameterValue(name)){
        throw new InvalidArgumentException("If parameter '" + name + "' is present, it must have a value.");
      }
    }
  }

  
  private boolean isParameterName(String name){
    
    if(name.substring(0, 1).equals("-")){
      return true;
    }
    
    return false;
  }
  
  
}
