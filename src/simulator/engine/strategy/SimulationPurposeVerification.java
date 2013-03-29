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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import simulator.analysis.UndefinedPropertyException;
import simulator.engine.runner.ExecutionMode;
import simulator.engine.runner.SimulationRun.StorageMode;
import simulator.engine.runner.SimulationRunException;
import simulator.environment.IALEVOSEnvironment;
import simulator.environment.IEnvironment;
import simulator.environment.InvalidEnvironmentException;
import simulator.util.Assert;
import alevos.IllegalSemanticsException;
import alevos.simulation.InvalidSimulatorRequest;
import alevos.ts.AnnotatedTransitionSystem;
import alevos.ts.sp.SimulationPurpose;
import alevos.verification.SynchState;
import alevos.verification.VerificationAlgorithm;
import alevos.verification.VerificationAlgorithm.Verdict;

public class SimulationPurposeVerification extends ALEVOSSimulationStrategy {

  
  /**
   * The simulation purpose to be verified.
   */
  private SimulationPurpose sp;
  
  /**
   * The verification algorithm to be used.
   */
  private VerificationAlgorithm va;
  
  /**
   * The result of the verification. 
   */
  private VerificationAlgorithm.Verdict verdict = VerificationAlgorithm.Verdict.INCONCLUSIVE; 
  
  /**
   * Trace found during verification, if any.
   */
  private List<SynchState> trace = new LinkedList<SynchState>();
  
  /**
   * If <code>true</code>, it means that this strategy found the desired solution.
   */
  private boolean iFoundTheSolution = false;

  
  public SimulationPurposeVerification(SimulationPurpose sp, VerificationAlgorithm va, String group) {
    super(group);
    Assert.notNull(sp);
    Assert.notNull(va);

    this.name = "Simulation Purpose Verification";
    this.sp = sp;
    this.va = va;
  }

  @Override
  public void execute() throws InvalidEnvironmentException, UndefinedPropertyException, SimulationRunException{
    
    // The strategy will only take place if the group is not finished yet
    if(!this.isGroupFinished(group)){
    
      // Setup the runner for verification
      //runner.setupRun(StorageMode.FULL, ExecutionMode.VERIFICATION);
      runner.setupRun(StorageMode.NONE, ExecutionMode.VERIFICATION); // TODO no sim. trace storage?
      
      IEnvironment environment = runner.getInitialEnvironment();
  
      // Check if it is the right kind of environment
      if(!(environment instanceof IALEVOSEnvironment)){
        throw new InvalidEnvironmentException("This strategy requires an ALEVOS-enabled environment.");
      }
      
      
      
      // Get the transition system that specifies the environment coordination
      AnnotatedTransitionSystem ats = ((IALEVOSEnvironment) environment).getATS();
      
      try {
        
        verdict = va.verify(sp, ats, simulatorConnector);
        
      } catch (IllegalSemanticsException e) {
        throw new SimulationRunException("The underlying transition systems used for the simulation are incorrect.", e);
        
      } catch (InvalidSimulatorRequest e) {
        e.printStackTrace();
        throw new SimulationRunException("The verification was canceled because an invalid simulation request was made.", e);
      }
      
      // get trace provided by the verification, if any
      trace = va.traceFound();

      this.va = null; // No longer needed
      this.sp = null; // No longer needed
      this.runner = null; // No longer needed

      // If the algorithm was successful, the group of strategies is finished.
      if(verdict == Verdict.SUCCESS){
        addFinishedGroup(group);
        iFoundTheSolution = true;
      }
    }
    
  }

  @Override
  public String toString() {
    String s = "";
    
    if(iFoundTheSolution || !isGroupFinished(group)){
      s = name + " strategy (group = " + group + ")\n";
      s = s +    "====================================================== \n";
      
      s = s +    "Result = " + verdict + "\n";
      s = s +    "Running time = " + this.runningTime() / 1000000000 + "s\n\n";
      
      s = s +    "Run found:" + traceToString() + " \n\n";
  
      s = s +    "Result = " + verdict + "\n";
      s = s +    "Running time = " + this.runningTime() / 1000000000 + "s\n\n";
    }
    
    return s;
  }
  
  
  
  private String traceToString(){
    
    String str = "\n";
    
    ListIterator<SynchState> it = trace.listIterator();
    
    while(it.hasNext()){

      SynchState ss = it.next();
      
      //
      // Synch. information
      //
      
      if(ss.getEventSP() != null && ss.getEventATS() != null &&
          ss.getStateSP() != null && ss.getStateATS() != null){
        
        String eventSP = ss.getEventSP().toString();
        String eventATS = ss.getEventATS().toString();
        String stateSP = ss.getStateSP().getLiterals().toString();
        String stateATS = ss.getStateATS().getLiterals().toString();
        
        /*
        str = str + "    [depth = "+ ss.getDepth() +"] Events synch'd: <" + eventSP + ",     " + eventATS + ">; " +
                          "State annotations synch'd: <" + stateSP + ",     " + stateATS + "> ";
                          */
        
        // Spaces top tabulate the result presented
        String tab1 = spaces(("    [depth = "+ ss.getDepth() +"] ").length()); 
        String tab2 = spaces(("    [depth = "+ ss.getDepth() +"] Events synch'd: <").length());
        
        str = str + "    [depth = "+ ss.getDepth() +"] Events synch'd: <" + eventSP + ",\n" +
        		tab2 + eventATS + ">; \n" +
        tab1+ "State annotations synch'd: <" + stateSP + ",     " + stateATS + "> ";
      }
      else{
        str = str + "    [depth = "+ ss.getDepth() +"]";
      }
      
      
      //
      // Position in the simulation purpose
      //
      
      String stateSP = ss.getStateSP().getName().toString();

      str = str + " State (in SP): " + stateSP + "\n";
      
    }
    
    str = str + "\n";
    
    return str;
  }
  
  /**
   * Calculates a String of n spaces.
   * 
   * @param n The number of spaces.
   * 
   * @return A string with n spaces.
   */
  private String spaces(int n){
    String s = "";
    for(int i = 0; i < n; i++){
      s = s + " ";
    }
    return s;
  }

}
