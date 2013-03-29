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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import simulator.agent.AgentProxy;
import simulator.agent.IAgent;




/**
 * Provides a mechanism to access state inspectors. 
 * 
 * @author Paulo Salem
 */
public class StateInspectorHelper{
  
  /**
   * Inspects the specified state of the specified agent.
   * 
   * @param agent The agent whose state we wish to inspect.
   * @param state The name of the state to be inspected.
   * 
   * @return The value of the inspected state.
   */
  public static String inspect(IAgent agent, String state) throws NoSuchStateInspectorException{
    
    try{
      
      // The value to be returned
      String value = "";
      
      // Check if we are dealing with a proxy or with a real agent
      if(agent instanceof AgentProxy){
        // Get the real agent!
        agent = ((AgentProxy) agent).getAgent();
      }
      
      // Search for the correct method
      for(Method m: agent.getClass().getMethods()){

        AStateInspector si = m.getAnnotation(AStateInspector.class);
      
        if(si != null){

          //  If we have found the state inspector we wanted
          if(si.name().equals(state)){
        
            // Call the method
            Object r = m.invoke(agent, null);
            
            // Extract the output
            value = r.toString();
        
            // We are done
            break;
          }
        }
      }
    
      return value;
      
    }
    catch(IllegalAccessException exception){
      throw new NoSuchStateInspectorException(exception.getMessage(), exception);
    }
    catch(InvocationTargetException exception){
      throw new NoSuchStateInspectorException(exception.getMessage(), exception);
    }
  }
  
}
