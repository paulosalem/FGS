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
package simulator.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import simulator.Experiment;
import simulator.Simulator;
import simulator.agent.IAgent;
import simulator.agent.IBehavioralAgent.StimulationStatus;
import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.analysis.PropertyBearerWrapper;
import simulator.engine.alevos.EMMASEvent;
import simulator.environment.IEnvironment;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import alevos.ts.Event;
import alevos.ts.IOEvent.IOType;
import alevos.ts.Literal;
import alevos.ts.State;
import alevos.ts.sp.FailureState;
import alevos.ts.sp.SimulationPurpose;
import alevos.ts.sp.SuccessState;
import alevos.verification.VerificationAlgorithm;
import alevos.verification.VerifyCertainty;
import alevos.verification.VerifyFeasibility;
import alevos.verification.VerifyWeakFeasibility;

/**
 * @author  Paulo Salem
 */
public class XMLExperimentLoader extends ExperimentLoader {
  
  /*
   * Constants for the XML tag names.
   */
  
  private final String TAG_EXPERIMENT = "experiment";
  
  private final String TAG_STANDARD_SIM = "standard-simulation";
  
  private final String TAG_SIM_PURP_VERIF = "simulation-purpose-verification";
  private final String TAG_ATTR_SAT_REL = "relation";
  
  private final String TAG_ALEVOS_RANDOM_EXPLORATION = "random-exploration";
  
  private final String TAG_STIMULUS_DELIVERY_OPT = "stimulus-delivery-optimization";
  private final String TAG_STIMULUS = "stimulus";
  
  private final String TAG_REFERENCED_AGENTS = "referenced-agents";
  private final String TAG_AGENT = "agent";
  
  private final String TAG_ATARGET = "agent-target";
  private final String TAG_ETARGET = "environment-target";
  
  private final String ATTR_TRIALS = "trials";
  private final String ATTR_GROUP = "group";
  
  private final String TAG_STATES = "states";
  private final String TAG_STATE = "state";
  private final String ATTR_ID = "id";
  private final String TAG_LITERAL = "literal";
  private final String ATTR_PROPOSITION = "proposition";
  private final String ATTR_TYPE = "type";
  private final String VALUE_TYPE_POSITIVE = "positive";
  private final String VALUE_TYPE_NEGATIVE = "negative";
  private final String VALUE_ID_INITIAL = "initial";
  private final String VALUE_ID_SUCCESS = "success";
  private final String VALUE_ID_FAILURE = "failure";
  
  private final String TAG_EVENTS = "events";
  private final String TAG_EMMAS_EVENT = "emmas-event";
  private final String ATTR_AGENT_ID = "agent-id";
  private final String ATTR_NAME = "name";
  private final String ATTR_STIMULUS = "stimulus";
  private final String ATTR_ACTION = "action";
  private final String VALUE_TYPE_INPUT = "input";
  private final String VALUE_TYPE_OUTPUT = "output";
  private final String VALUE_ID_OTHER = "other";
  private final String VALUE_ID_COMMIT = "?commit";
  private final String VALUE_ID_INTERNAL = "internal";
  
  
  private final String TAG_TRANSITIONS = "transitions";
  private final String TAG_TRANSITION = "transition";
  private final String ATTR_STATE_ID1 = "state-id1";
  private final String ATTR_STATE_ID2 = "state-id2";
  private final String ATTR_EVENT_ID = "event-id";
  
  /**
   * Used to generate fresh and unique group names by the method <code>freshGroupName()</code>.
   */
  private static int groupCounter = 0;

  
  SAXExperimentHandler handler;

  public XMLExperimentLoader(IEnvironment environment){
    super(environment);
  }
  
  public  Experiment loadExperiment(File f) throws ExperimentLoadingException{

    SAXBuilder builder = new SAXBuilder();
    
    Experiment experiment = null;
    
    try{
      
      Document doc = builder.build(f);
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Loading experiment file...", Messenger.NORMAL_MSG);
      
      experiment = load(doc);
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Finsihed loading experiment file.", Messenger.NORMAL_MSG);
      
    } 
    catch(IOException e){
      SimulatorUI.instance().getMessenger().printDebugMsg("IOException while loading experiment:" + e.getMessage(), Messenger.NORMAL_MSG);
      
      throw new ExperimentLoadingException(e.getMessage());
    }
    catch(JDOMException e){
      SimulatorUI.instance().getMessenger().printDebugMsg("JDOMException while loading experiment:" + e.getMessage(), Messenger.NORMAL_MSG);
      
      throw new ExperimentLoadingException(e.getMessage()); 
    }
    catch (Exception e) {
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Exception while loading experiment:" + e.getMessage(), Messenger.NORMAL_MSG);
      
      throw new ExperimentLoadingException(e.getMessage());
    }

    return experiment;
    
  }
  
  
  private Experiment load(Document doc) throws ExperimentLoadingException, FileNotFoundException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException{
    
    Experiment experiment = null;
    
    // Get the scenario element
    Element eExperiment = doc.getRootElement();
    if(!eExperiment.getName().equals(TAG_EXPERIMENT)){
      throw new ExperimentLoadingException("Root element must be <" + TAG_EXPERIMENT +">");
    }
    
    String expName = eExperiment.getAttributeValue("name", "");
    String expDescription = eExperiment.getAttributeValue("description", "");
    
    SimulatorUI.instance().getMessenger().printDebugMsg("Found experiment declaration: ", Messenger.NORMAL_MSG);
    SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + expName, Messenger.NORMAL_MSG);
    SimulatorUI.instance().getMessenger().printDebugMsg("  Description = " + expDescription, Messenger.NORMAL_MSG);

    
    experiment = new Experiment(expName, expDescription);
    
    
    // Get standard simulation strategies
    List<Element> es = eExperiment.getChildren(TAG_STANDARD_SIM);
    loadStandardSimStrategies(experiment, es);
    
    // Get simulation purpose verification strategies
    es = eExperiment.getChildren(TAG_SIM_PURP_VERIF);
    loadSimPurpVerifStrategies(experiment, es);
    
    // Get random exploration strategies
    es = eExperiment.getChildren(TAG_ALEVOS_RANDOM_EXPLORATION);
    loadALEVOSRandomExplorationStrategies(experiment, es);
    
    
    // Get stimulus delivery optimization strategies
    es = eExperiment.getChildren(TAG_STIMULUS_DELIVERY_OPT);
    loadStimulusDeliveryOptStrategies(experiment, es);
    
    return experiment;
    
  }
  
  
  private void loadStandardSimStrategies(Experiment experiment, List<Element> es){
    
    for(Element e: es){
      
      String runs = e.getAttributeValue("runs", "1");
      String iterations = e.getAttributeValue("iterations-per-run", "1");

      SimulatorUI.instance().getMessenger().printDebugMsg("Found standard simulation strategy declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Runs = " + runs, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Iterations per Run = " + iterations, Messenger.NORMAL_MSG);

      experiment.addStandardSimulationStrategy(Integer.parseInt(runs), Integer.parseInt(iterations));
      
    }
    
  }
  
  private void loadStimulusDeliveryOptStrategies(Experiment experiment, List<Element> es) throws ExperimentLoadingException{
    
    for(Element e: es){
      
      List<IAgent> targetAgents = new LinkedList<IAgent>();
      List<IAgent> referencedAgents = new LinkedList<IAgent>();
      boolean targetEnvironment = false;
      
      // Get general info
      String runs = e.getAttributeValue("runs", "1");
      String iterations = e.getAttributeValue("iterations-per-run", "1");
      String propertyId = e.getAttributeValue("property-id");
      String targets = e.getAttributeValue("targets", "1");

      SimulatorUI.instance().getMessenger().printDebugMsg("Found stimulus delivery optimization strategy declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Runs = " + runs, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Iterations per Run = " + iterations, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Objective property ID = " + propertyId, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Targets = " + targets, Messenger.NORMAL_MSG);
      
      
      // Get the stimulus to deliver
      Element eStimulus = e.getChild(TAG_STIMULUS);
      
      String type = eStimulus.getAttributeValue("type");
      String content = eStimulus.getAttributeValue("content", "");
      
      // Get the referenced agents concerning this stimulus
      Element eRA = eStimulus.getChild(TAG_REFERENCED_AGENTS);
      if(eRA != null){
        List<Element> eas = eRA.getChildren(TAG_AGENT);
        for(Element ea: eas){
          String id = ea.getAttributeValue("id");
          
          SimulatorUI.instance().getMessenger().printDebugMsg("Found agent reference declaration: ", Messenger.NORMAL_MSG);
          SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + id, Messenger.NORMAL_MSG);
          
       // Add agent to the referenced agents list
          referencedAgents.add(environment.getAgent(Integer.parseInt(id)));
        }
      }
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Found stimulus declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Type = " + type, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Content = " + content, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Referenced agents = " + referencedAgents.toString(), Messenger.NORMAL_MSG);
      
      
      // Get agent target
      List<Element> leAT = e.getChildren(TAG_ATARGET);
      for(Element eAT: leAT){
        String agentId = eAT.getAttributeValue("id");
        
        IAgent a = environment.getAgent(Integer.parseInt(agentId));
        
        targetAgents.add(a);
        
        SimulatorUI.instance().getMessenger().printDebugMsg("Found agent target declaration: ", Messenger.NORMAL_MSG);
        SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the agent = " + agentId, Messenger.NORMAL_MSG);
      }
      
      
      // Get environment target
      Element eET = e.getChild(TAG_ETARGET);
      if(eET != null){
        targetEnvironment = true;
      }
        
      //
      // Create the strategy!
      //
      
      EnvironmentStimulus environmentStimulus = new EnvironmentStimulus(type, content, null, referencedAgents);
      
      IAgent aTarget = null;
      
      // Get target agents, if any
      if(targetAgents.size() == 1){
        aTarget = targetAgents.get(0);
      }
      else if(targetAgents.size() > 1){
        throw new ExperimentLoadingException("Stimulus delivery optimization must be target at 0 or 1 agents only.");
      }
      
      // There must be either an agent, or the environment as targets.
      // If both are set as targets, there is an error.
      PropertyBearerWrapper pbw;
      if(targetEnvironment == false && aTarget != null){
        pbw = new PropertyBearerWrapper(aTarget, "The agent targeted by the property being optimized.");
      }
      else if(targetEnvironment == true && aTarget == null){
        pbw = new PropertyBearerWrapper(environment, "The environment targeted by the property being optimized.");
      }
      else{
        throw new ExperimentLoadingException("Stimulus delivery optimization must be target either at an agent or at the environment, but not both.");
      }

       
      
      try {
        experiment.addStimulusDeliveryOptimization(Integer.parseInt(runs), Integer.parseInt(iterations), environmentStimulus, Integer.parseInt(propertyId), pbw, Integer.parseInt(targets));
        
      } catch (NumberFormatException ex) {
        throw new ExperimentLoadingException("Integer value required.");
        
      } 
      
      
    }
    
    
  }
  
  
  private void loadSimPurpVerifStrategies(Experiment experiment, List<Element> es) throws ExperimentLoadingException, FileNotFoundException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    
    Map<String, State> id2State = new HashMap<String, State>();
    Map<String, Event> id2Event = new HashMap<String, Event>();
    
    for(Element e: es){
      
      // Read verification task
      String relation = e.getAttributeValue(TAG_ATTR_SAT_REL, "feasibility");
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Found simulation purpose verification strategy declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Satisfiability relation = " + relation, Messenger.NORMAL_MSG);

      VerificationAlgorithm va = null;
      
      // By default, feasibility verification uses weak feasibility
      if(relation.equals("feasibility") || relation.equals("weak feasibility")){
        va = new VerifyFeasibility(Simulator.instance().getMaxDepth(), Simulator.instance().getRandomize(), Simulator.instance().getMaxSynchSteps(), VerifyFeasibility.Variant.Weak);
      }
      else if(relation.equals("strong feasibility")){
        va = new VerifyFeasibility(Simulator.instance().getMaxDepth(), Simulator.instance().getRandomize(), Simulator.instance().getMaxSynchSteps(), VerifyFeasibility.Variant.Strong);
      }
      else if(relation.equals("certainty")){
        va = new VerifyCertainty(Simulator.instance().getMaxDepth(), Simulator.instance().getRandomize(), Simulator.instance().getMaxSynchSteps());
      }
      else{
        throw new ExperimentLoadingException("Ther requested verification relation, " + relation + ", does not exist.");
      }
      

      id2State.clear();
      
      // Put the verdict states on the mapping
      id2State.put(VALUE_ID_SUCCESS, SuccessState.instance());
      id2State.put(VALUE_ID_FAILURE, FailureState.instance());
      
      // Read states of the simulation purpose with their literals
      List<Element> leState = e.getChild(TAG_STATES).getChildren(TAG_STATE);
      for(Element eState: leState){
        
        // Read id
        String id = eState.getAttributeValue(ATTR_ID);

        // Check if it is an illegal id
        if(id.equals(VALUE_ID_SUCCESS.toLowerCase()) || id.equals(VALUE_ID_FAILURE.toLowerCase())){
          throw new ExperimentLoadingException("The state identifiers '" + VALUE_ID_SUCCESS+"' and '" + VALUE_ID_FAILURE + 
              "' are reserved and cannot be redefined.");
        }
        
        State state = new State();
        id2State.put(id, state); // Store the state for later reference
        
        // Set a user friendly name 
        state.setName(id);
        
        // Read literals
        List<Element> leLiteral = eState.getChildren(TAG_LITERAL);
        for(Element eLiteral: leLiteral){
          String type = eLiteral.getAttributeValue(ATTR_TYPE, VALUE_TYPE_POSITIVE);
          String proposition = eLiteral.getAttributeValue(ATTR_PROPOSITION);
          
          Literal l;
          if(type.equals(VALUE_TYPE_POSITIVE)){
            l = new Literal(proposition, Literal.Type.POSITIVE);
          }
          else if(type.equals(VALUE_TYPE_NEGATIVE)){
            l = new Literal(proposition, Literal.Type.NEGATIVE);
          }
          else{
            throw new ExperimentLoadingException("The specified literal type, " + type + ", is invalid.");
          }
          
          state.addLiteral(l);
        }
      }
      
      id2Event.clear();
      
      // Put special events on the mapping
      id2Event.put(VALUE_ID_OTHER, new EMMASEvent(VALUE_ID_OTHER, IOType.OTHER));
      id2Event.put(VALUE_ID_COMMIT, new EMMASEvent("commit", IOType.INPUT));
      id2Event.put(VALUE_ID_INTERNAL, new EMMASEvent(VALUE_ID_INTERNAL, IOType.INTERNAL));
      
      // Read EMMAS events
      List<Element> leEvent = e.getChild(TAG_EVENTS).getChildren(TAG_EMMAS_EVENT);
      for(Element eEvent: leEvent){
        String id = eEvent.getAttributeValue(ATTR_ID);
        String type = eEvent.getAttributeValue(ATTR_TYPE);
        String name = eEvent.getAttributeValue(ATTR_NAME);
        String agentId = eEvent.getAttributeValue(ATTR_AGENT_ID);
        
        String action = eEvent.getAttributeValue(ATTR_ACTION);
        String stimulus = eEvent.getAttributeValue(ATTR_STIMULUS);
        
        
        
        IOType ioType;
        if(type.equals(VALUE_TYPE_INPUT)){
          ioType = IOType.INPUT;
        }
        else if(type.equals(VALUE_TYPE_OUTPUT)){
          ioType = IOType.OUTPUT;
        }
        else{
          throw new ExperimentLoadingException("The specified i/o type, " + type + ", is invalid.");
        }

        // An 'other' event cannot be redefined
        if(id.equals(VALUE_ID_OTHER.toLowerCase())){
          throw new ExperimentLoadingException("The event identifier '" + VALUE_ID_OTHER + "' is reserved and cannot be redefined.");
        }
        
        
        EMMASEvent event;
        // A non-decorated event 
        if(action == null && stimulus == null){
          event = new EMMASEvent(name, ioType);
        }
        
        // An action event
        else if(action != null){
          event = new EMMASEvent(name, ioType, Integer.parseInt(agentId), new EnvironmentAction(action));
        }
        
        // A stimulation event
        else if(stimulus != null){
          
          StimulationStatus status;
          if(name.equals(StimulationStatus.ABSENT.toString().toLowerCase())){
            status = StimulationStatus.ABSENT;
          }
          else if(name.equals(StimulationStatus.BEGINNING.toString().toLowerCase())){
            status = StimulationStatus.BEGINNING;
          }
          else if(name.equals(StimulationStatus.STABLE.toString().toLowerCase())){
            status = StimulationStatus.STABLE;
          }
          else if(name.equals(StimulationStatus.ENDING.toString().toLowerCase())){
            status = StimulationStatus.ENDING;
          }
          else{
            throw new ExperimentLoadingException("The specified stimulus, " + stimulus + ", is invalid.");
          }
          
          event = new EMMASEvent(name, ioType, Integer.parseInt(agentId), new EnvironmentStimulus(stimulus), status);
        }
        else{
          throw new ExperimentLoadingException("The specified event, " + name + ", is invalid.");
        }
        
        
        // Store the event for later reference
        id2Event.put(id, event);
      }

      // Create the simulation purpose in order to add the transitions
      SimulationPurpose sp;
      if(id2State.get(VALUE_ID_INITIAL) != null){
        sp = new SimulationPurpose(id2State.get(VALUE_ID_INITIAL));
      }
      else{
        throw new ExperimentLoadingException("A state identified by '" + VALUE_ID_INITIAL + "' must be specified.");
      }
      
      // Read transitions
      List<Element> leTransition = e.getChild(TAG_TRANSITIONS).getChildren(TAG_TRANSITION);
      for(Element eTransition: leTransition){
        String stateId1 = eTransition.getAttributeValue(ATTR_STATE_ID1);
        String stateId2 = eTransition.getAttributeValue(ATTR_STATE_ID2);
        String eventId = eTransition.getAttributeValue(ATTR_EVENT_ID);
        
        // Add the transition
        sp.addTransition(id2State.get(stateId1), id2Event.get(eventId), id2State.get(stateId2));
      }

      
      // How many times to repeat this experiment?
      int trials = Integer.parseInt(e.getAttributeValue(ATTR_TRIALS, "1"));
      String group = e.getAttributeValue(ATTR_GROUP, freshGroupName());
      
      for(int i = 0; i < trials; i++){
        experiment.addSimPurpVerifStrategy(sp, va, group);  
      }
      
      
    }
    
  }
  
  // TODO erase this when no longer needed
  /*
  private SimulationPurpose createTestSP1(){
    
    State init = new State();
    
    SimulationPurpose sp = new SimulationPurpose(init);

    IOEvent otherEvent = new IOEvent("other", IOEvent.IOType.OTHER);
    State anyState = new State();
    
    // init --other--> any --other--> Success
    sp.addTransition(init, otherEvent, anyState);
    sp.addTransition(anyState, otherEvent, SuccessState.instance());
    
    return sp;
  }
  
  // TODO erase this when no longer needed
  private SimulationPurpose createTestSP2(){
    
    State init = new State();
    
    SimulationPurpose sp = new SimulationPurpose(init);

    IOEvent otherEvent = new IOEvent("other", IOEvent.IOType.OTHER);
    
    int agentId = 0;
    
    EnvironmentStimulus wine = new EnvironmentStimulus("wine");
    EMMASEvent wineEvent = new EMMASEvent("beginning", IOEvent.IOType.INPUT, agentId, wine, StimulationStatus.BEGINNING);

    EnvironmentAction a_verbal_joke = new EnvironmentAction("a_verbal_joke");
    EMMASEvent jokeEvent = new EMMASEvent("emit", IOEvent.IOType.OUTPUT, agentId, a_verbal_joke);

    
    State anyState = new State();
    
    //                      |<--------------------------|
    // init --wineEvent--> anyState ---- otherEvent ----| --jokeEvent--> Success
    sp.addTransition(init, wineEvent, anyState);
    sp.addTransition(anyState, otherEvent, anyState);
    sp.addTransition(anyState, jokeEvent, SuccessState.instance());
    
    return sp;
  }
  */
  
  
  private void loadALEVOSRandomExplorationStrategies(Experiment experiment, List<Element> es) throws ExperimentLoadingException {
    
    for(Element e: es){
      
      String runs = e.getAttributeValue("runs", "1");
      String iterationsPerRun = e.getAttributeValue("iterations-per-run", "100");

      SimulatorUI.instance().getMessenger().printDebugMsg("Found random exploration strategy declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Runs = " + runs, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Iterations per Run = " + iterationsPerRun, Messenger.NORMAL_MSG);
      
      String group = e.getAttributeValue(ATTR_GROUP, freshGroupName());
      
      experiment.addALEVOSRandomExplorationStrategy(Integer.parseInt(runs), Integer.parseInt(iterationsPerRun), group);
      
    }
    
  }
  
  
  private String freshGroupName(){
    String name = "GROUP_" + groupCounter;
    groupCounter++;
    
    return name;
  }
    
  
  
}
