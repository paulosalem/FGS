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

import simulator.agent.IBehavioralAgent.ActionStatus;
import simulator.agent.IBehavioralAgent.StimulationStatus;
import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import alevos.IllegalSemanticsException;
import alevos.expression.Expression;
import alevos.expression.picalculus.PiActionPrefix;
import alevos.expression.picalculus.PiProcess;
import alevos.expression.picalculus.PiTauAction;
import alevos.ts.DynamicTransitionSystem;
import alevos.ts.Event;
import alevos.ts.IOEvent.IOType;
import alevos.ts.PiEvent;
import alevos.ts.State;
import alevos.util.Pair;
import alevos.verification.TraceInfo;

public class EMMASTransitionSystem extends DynamicTransitionSystem {
  
  //
  // Action emission pi-calculus names
  //
  protected static final String NAME_EMIT = "emit";
  protected static final String NAME_STOP = "stop";
  
  //
  // Stimulation pi-calculus names
  //
  protected static final String NAME_BEGINNING = "beginning";
  protected static final String NAME_STABLE = "stable";
  protected static final String NAME_ENDING = "ending";
  protected static final String NAME_ABSENT = "absent";
  
  protected static final String NAME_COMMIT = "commit";
  
  public EMMASTransitionSystem() {
    super();
  }
  
  public EMMASTransitionSystem(State initial) {
    super(initial);
    printDebugMsg(initial.getExpression());
  }
  
  public EMMASTransitionSystem(PiProcess process){
    super(new State(process));
    printDebugMsg(process);
    
    
  }
  
  protected void printDebugMsg(Expression exp){
    SimulatorUI.instance().getMessenger().printDebugMsg("Using the following expression as the environment: ", Messenger.NORMAL_MSG);
    SimulatorUI.instance().getMessenger().printDebugMsg("  " + exp.toString(), Messenger.NORMAL_MSG);
  }
  
  @Override
  public Collection<Pair<Event, State>> translate(Collection<Pair<Event, State>> originalTransitions) throws IllegalSemanticsException{
    Collection<Pair<Event, State>> newTransitions = new LinkedList<Pair<Event, State>>();

    // Put the appropriate EMMAS events in the place of the original pi-calculus events.
    // To do so, we will get decorations present in the PiNames and then use this
    // to create new EMMAS events.
    for(Pair<Event, State> ot: originalTransitions){
      
      PiEvent piEvent = (PiEvent) ot.getFirst();
 
      // The pi-prefix determine the new event to be defined. If it is a TAU action,
      // we must discover the cause of this TAU action (another prefix) and use it
      // instead (because the TAU action itself contains no useful information).
      PiActionPrefix prefix = piEvent.getPrefix();
      if(prefix instanceof PiTauAction){ 
        
        PiActionPrefix cause = piEvent.getCause();
        
        // But the cause is only useful if it is meaningful. Otherwise, we want
        // to leave the prefix as a TAU (i.e., internal, meaningless to the exterior)
        if(isMeaningful(cause.getChannel().getIdentifier())){
          prefix = cause;
        }
      }
      

      // Now we get the relevant decorations present on the PiName
      String name = prefix.getChannel().getIdentifier();
      Integer agentId = (Integer) prefix.getChannel().getDecorator("agentId");
      EnvironmentAction action = (EnvironmentAction) prefix.getChannel().getDecorator("action"); 
      EnvironmentStimulus stimulus = (EnvironmentStimulus) prefix.getChannel().getDecorator("stimulus");
      
      //
      // Figure out i/o type in EMMAS (which may be different from the pi-calculus ones)
      //
      IOType ioType;
      
      if(prefix instanceof PiTauAction){
        // If the prefix is still a TAU, it means that we really wish to consider the
        // event as internal
        ioType = IOType.INTERNAL;
      }
      else{
        // If it it concerns agents actions, it is an input in the EMMAS transition system
        if(name.equals(NAME_EMIT) || name.equals(NAME_STOP)){
          ioType = IOType.INPUT;
        }
        // Otherwise, it is an output of the transition system (i.e., something that will come out
        // of the transition system and influence the agents)
        else{
          ioType = IOType.OUTPUT;
        }
      }
      
      
      // Create the new event
      Event emmasEvent = null;
      if(agentId == null && action == null && stimulus == null){
        emmasEvent = new EMMASEvent(name, ioType);
      }
      else if (agentId != null && action != null && stimulus == null){
        emmasEvent = new EMMASEvent(name, ioType, agentId, action);
      }
      else if (agentId != null && action == null && stimulus != null){
        StimulationStatus status = defineStatus(name);
        emmasEvent = new EMMASEvent(name, ioType, agentId, stimulus, status);
      }
      else{
        throw new IllegalSemanticsException("The underlying pi-calculus transition system " +
        		"cannot be converted into an EMMAS transition system.");
      }
      
      // Add the new transition (new event, same old state)
      newTransitions.add(new Pair<Event, State>(emmasEvent, ot.getSecond()));
    }
    
    return newTransitions;
  }
  
  
  private StimulationStatus defineStatus(String name) throws IllegalSemanticsException{
    if(name.equals(NAME_BEGINNING)){
      return StimulationStatus.BEGINNING;
    }
    else if(name.equals(NAME_STABLE)){
      return StimulationStatus.STABLE;
    }
    else if(name.equals(NAME_ENDING)){
      return StimulationStatus.ENDING;
    }
    else if(name.equals(NAME_ABSENT)){
      return StimulationStatus.ABSENT;
    }
    else{
      throw new IllegalSemanticsException("A name related to stimulation was expected.");
    }
  }
  
  private boolean isMeaningful(String name){
    
    // The name is meaningful iff it carries useful information w.r.t. the simulator
    if(name.equals(NAME_EMIT) ||  name.equals(NAME_STOP) || name.equals(NAME_BEGINNING) || name.equals(NAME_STABLE) ||
       name.equals(NAME_ENDING) || name.equals(NAME_ABSENT)){
      
      return true;
    }
    
    return false;
  }
  
  @Override
  protected Collection<Pair<Event, State>> restrict(State source, Collection<Pair<Event, State>> originalSuccs, TraceInfo ti)  throws IllegalSemanticsException{
    
    Collection<Pair<Event, State>> ess = new LinkedList<Pair<Event, State>>();
    
    for(Pair<Event, State> es: originalSuccs){
      if(!isForbidden(source, (EMMASEvent) es.getFirst(), es.getSecond(), originalSuccs, (EMMASTraceInfo)ti)){
        ess.add(es);
      }
    }
    
    return ess;
  }
  
  private boolean isForbidden(State s1, EMMASEvent e, State s2, Collection<Pair<Event, State>> originalSuccs, EMMASTraceInfo ti) {
    
    EMMASSimulatorContext context = (EMMASSimulatorContext) s1.getContext();
    
    //
    // Local transition constraints
    //
    
    
    EnvironmentAction action = e.getAction();
    Integer agentId = e.getAgentId();
    if(action != null && agentId != null){
      
      
      if(context.response(agentId, action) == ActionStatus.EMITTING){
        
        // Restriction 1
        if(e.getName().equals(EMMASTransitionSystem.NAME_STOP) && e.getIoType() == IOType.INPUT){
          return true;
        }
        
        // Restriction 3
        else if(e.getName().equals(EMMASTransitionSystem.NAME_COMMIT)){
          EMMASEvent emit = new EMMASEvent(EMMASTransitionSystem.NAME_EMIT, IOType.INPUT, agentId, action);
          if(contains(emit, originalSuccs)){
            return true;
          }
        }
        
      }
      else if(context.response(agentId, action) == ActionStatus.NOT_EMITTING){
        
        // Restriction 2
        if(e.getName().equals(EMMASTransitionSystem.NAME_EMIT) && e.getIoType() == IOType.INPUT){
          return true;
        }
        // Restriction 4
        else if(e.getName().equals(EMMASTransitionSystem.NAME_COMMIT)){
          EMMASEvent stop = new EMMASEvent(EMMASTransitionSystem.NAME_STOP, IOType.INPUT, agentId, action);
          if(contains(stop, originalSuccs)){
            return true;
          }
        }
      }
      
    }
    
    
    //
    // Trace constraints
    //

    // If the event concerns stimulation, we may check the appropriate safeguard
     if(e.getName().equals(NAME_ABSENT) || e.getName().equals(NAME_BEGINNING) || e.getName().equals(NAME_STABLE) || e.getName().equals(NAME_ENDING)){
       if(ti.hasStimulationSafeguard(e.getAgentId(), e.getStimulus())){
         return true;
       }
    }
    
    // If the event concerns action, we may check the appropriate safeguard
    else if(e.getName().equals(NAME_EMIT) || e.getName().equals(NAME_STOP)){
      if(ti.hasActionSafeguard(e.getAgentId(), e.getAction())){
        return true;
      }
    }
    
    return false;
  }
  
  public boolean contains(Event e, Collection<Pair<Event, State>> ess){
    
    for(Pair<Event, State> es: ess){
      if(es.getFirst().equals(es)){
        return true;
      }
    }
    
    return false;
  }

  @Override
  public TraceInfo eventScheduled(Event event, TraceInfo ti){
    
    //
    // Handle the trace constraints
    //
    
    EMMASEvent e = (EMMASEvent) event;
    
    // If the event is a clock, we can reset the safeguards
    if(e.getName().equals(NAME_COMMIT)){
      ((EMMASTraceInfo)ti).clearStimulationSafeguards();
      ((EMMASTraceInfo)ti).clearActionSafeguard();
    }
    
    // If the event concerns stimulation, we may trigger the appropriate safeguard
    else if(e.getName().equals(NAME_ABSENT) || e.getName().equals(NAME_BEGINNING) || e.getName().equals(NAME_STABLE) || e.getName().equals(NAME_ENDING)){
      ((EMMASTraceInfo)ti).putStimulationSafeguard(e.getAgentId(), e.getStimulus());
    }
   
   // If the event concerns action, we may trigger the appropriate safeguard
   else if(e.getName().equals(NAME_EMIT) || e.getName().equals(NAME_STOP)){
     ((EMMASTraceInfo)ti).putActionSafeguard(e.getAgentId(), e.getAction());
   }
    
    return ti;
    
  }
  
  @Override
  public TraceInfo getInitialTraceInfo(){
    return new EMMASTraceInfo();
  }
  
  
  
  
  // TODO delete this method when no longer used
  /*
  public static EMMASTransitionSystem createTestTS1(){
    EMMASTransitionSystem ts = null;
    
    EnvironmentStimulus s = new EnvironmentStimulus("s");
    EnvironmentAction a = new EnvironmentAction("Action 0");
    
    //
    // Define and decorate PiNames
    //
    PiName beg = new PiName(NAME_BEGINNING);
    beg.setDecorator("agentId", 0);
    beg.setDecorator("stimulus", s);
    
    PiName sta = new PiName(NAME_STABLE);
    sta.setDecorator("agentId", 0);
    sta.setDecorator("stimulus", s);
    
    PiName end = new PiName(NAME_ENDING);
    end.setDecorator("agentId", 0);
    end.setDecorator("stimulus", s);
    
    PiName abs = new PiName(NAME_ABSENT);
    abs.setDecorator("agentId", 0);
    abs.setDecorator("stimulus", s);
    
    PiName emi = new PiName(NAME_EMIT);
    emi.setDecorator("agentId", 0);
    emi.setDecorator("action", a);
    
    PiName sto = new PiName(NAME_STOP);
    sto.setDecorator("agentId", 0);
    sto.setDecorator("action",a);
    
    PiName x = new PiName("x"); // Some dummy parameter name
    
    PiName clock = new PiName(NAME_CLOCK); // The commit name
    
    //
    // Define actions
    //
    
    // The agent receives
    PiInputAction aBegI = new PiInputAction(beg, x);
    PiInputAction aStaI = new PiInputAction(sta, x);
    PiInputAction aEndI = new PiInputAction(end, x);
    PiInputAction aAbsI = new PiInputAction(abs, x);
    
    // The agent emits
    PiOutputAction aEmi = new PiOutputAction(emi, x);
    PiOutputAction aSto = new PiOutputAction(sto, x);
    
    // The environment stimulates
    PiOutputAction aBegO = new PiOutputAction(beg, x);
    PiOutputAction aStaO = new PiOutputAction(sta, x);
    
    // The commit event
    PiOutputAction aClock = new PiOutputAction(clock, x);
    
    //
    // Define the environment PiProcess
    //
    PiProcess stim = new PiPrefix(new PiNilProcess(), aBegI, aStaI, aEndI, aAbsI);
    PiProcess act = new PiPrefix(new PiNilProcess(), aEmi, aSto);
    
    PiProcess eb = new PiPrefix(new PiNilProcess(), aBegO, aStaO);
    
    PiProcess time = new PiReplication(new PiPrefix(aClock, new PiNilProcess()));

    PiProcess env = new PiRestriction(new PiParallel(time, new PiParallel(eb, new PiParallel(stim, act))), beg, sta, end, abs);
    

    // Define an initial state containing the appropriate pi-calculus process
    State init = new State(env);    
    
    // Create the transition system
    ts = new EMMASTransitionSystem(init);
    
    return ts;
  }
  
  //TODO delete this method when no longer used
  public static EMMASTransitionSystem createTestTS2(){
    
    //
    // Let us build an environment using our language (ESL) and have it automatically
    // converted into a PiProcess.
    //
    
    Set<Action> actions1 = new HashSet<Action>();
    
    Action a0 = new Action("Action 0");
    actions1.add(a0);
    
    Set<Stimulus> stimuli1 = new HashSet<Stimulus>();
    
    Stimulus s0 = new Stimulus("Stimulus 0");
    stimuli1.add(s0);
    
    Set<AgentProfile> agents = new HashSet<AgentProfile>();

    AgentProfile ag1 = new AgentProfile(0, actions1, stimuli1);
    agents.add(ag1);
    
    
    Set<ActionTransformer> actionTransformers = new HashSet<ActionTransformer>(); 
    
    ActionTransformer at0 = new ActionTransformer(ag1, a0, s0, ag1);
    actionTransformers.add(at0);
    
    
    Set<ESLOperation> behaviors = new HashSet<ESLOperation>();
    
    BeginStimulation eb1 = new BeginStimulation(s0, ag1);
    behaviors.add(eb1);    
    
    Environment environment = new Environment(agents, actionTransformers, behaviors);
    
    PiProcess proc = environment.toPiProcess(new Context());
    
    
    
    return new EMMASTransitionSystem(proc);
    
  }
  */

}
