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
package simulator.engine.alevos;

import java.util.Collection;
import java.util.LinkedList;

import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import alevos.IllegalSemanticsException;
import alevos.expression.Expression;
import alevos.expression.picalculus.PiActionPrefix;
import alevos.process.semantics.Rule;
import alevos.ts.Event;
import alevos.ts.PiEvent;
import alevos.util.Pair;

public class PiEMMASAgentProfileRule extends Rule {

  @Override
  public Collection<Pair<Event, Expression>> succ(Expression exp) throws IllegalSemanticsException {
    
    Collection<Pair<Event, Expression>> ees = new LinkedList<Pair<Event, Expression>>();
    
    if(exp instanceof PiEMMASAgentProfile){
      PiEMMASAgentProfile ap = (PiEMMASAgentProfile)exp;
    
      for(EnvironmentAction ea: ap.getActions()){
        PiActionPrefix prefix = ap.nextClonedPrefixFor(ea);
        PiEMMASAgentProfile nextAgent = ap.clonedAgentAfter(ea);
        
        ees.add(new Pair<Event, Expression>(new PiEvent((PiActionPrefix)prefix.clone()), nextAgent));
      }
      
      for(EnvironmentStimulus es: ap.getStimuli()){
        PiActionPrefix prefix = ap.nextClonedPrefixFor(es);
        PiEMMASAgentProfile nextAgent = ap.clonedAgentAfter(es);
        
        ees.add(new Pair<Event, Expression>(new PiEvent((PiActionPrefix)prefix.clone()), nextAgent));
      }
      
    
      return ees;
    }
    else{
      throw new IllegalArgumentException("This rule is not defined for the specified expression.");
    }
  }

}
