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
package simulator.io.esl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import simulator.Scenario;
import simulator.agent.IAgentControl;
import simulator.agent.IBehavioralAgent;
import simulator.agent.action.EnvironmentAction;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.components.ComponentInstantiationException;
import simulator.components.ComponentsRegistry;
import simulator.engine.alevos.EMMASTransitionSystem;
import simulator.environment.esl.Action;
import simulator.environment.esl.ActionTransformer;
import simulator.environment.esl.AgentProfile;
import simulator.environment.esl.BeginStimulation;
import simulator.environment.esl.Choice;
import simulator.environment.esl.Context;
import simulator.environment.esl.Create;
import simulator.environment.esl.Destroy;
import simulator.environment.esl.ESLOperation;
import simulator.environment.esl.EndStimulation;
import simulator.environment.esl.Environment;
import simulator.environment.esl.EnvironmentResponse;
import simulator.environment.esl.Nop;
import simulator.environment.esl.Parallel;
import simulator.environment.esl.Sequence;
import simulator.environment.esl.SequentialComposition;
import simulator.environment.esl.Stimulate;
import simulator.environment.esl.Stimulus;
import simulator.environment.esl.UnboundedSequence;
import simulator.io.ScenarioLoadingException;
import simulator.util.Assert;
import alevos.IllegalSemanticsException;
import alevos.expression.picalculus.PiProcess;

public class XMLESLLoader {
  
  private final String TAG_ACTION_TRANSFORMERS = "action-transformers";
  private final String TAG_ACTION_TRANSFORMER = "action-transformer";
  private final String ATTR_AGENT_ID1 = "agent-id1";
  private final String ATTR_AGENT_ID2 = "agent-id2";
  private final String ATTR_ACTION = "action";
  private final String ATTR_STIMULUS = "stimulus";
  
  private final String TAG_BEHAVIORS = "behaviors";
  private final String TAG_BEHAVIOR = "behavior";
  
  private final String TAG_CHOICE = "choice";
  private final String TAG_PARALLEL = "parallel";
  private final String TAG_SEQUENTIAL_COMPOSITION = "sequential-composition";
  private final String TAG_SEQUENCE = "sequence";
  private final String ATTR_TIMES = "times";
  private final String TAG_UNBOUNDED_SEQUENCE = "unbounded-sequence";
  
  private final String TAG_ENVIRONMENT_RESPONSE = "environment-response";
  
  private final String TAG_OP_NOP = "nop";
  
  private final String TAG_OP_CREATE = "create";
  private final String TAG_OP_DESTROY = "destroy";
  private final String TAG_OP_BEGIN_STIM = "begin-stimulation";
  private final String TAG_OP_END_STIM = "end-stimulation";
  
  private final String TAG_OP_STIMULATE = "stimulate";
  private final String ATTR_AGENT_ID = "agent-id";
  
  
  private ComponentsRegistry cr = null;
  
  private Map<Integer, AgentProfile> id2AgentProfile = new HashMap<Integer, AgentProfile>();
  
  

  public XMLESLLoader(ComponentsRegistry cr) {
    super();
    Assert.notNull(cr);
    
    this.cr = cr;
  }


  public EMMASTransitionSystem readEMMAS(Scenario scenario, Element eEMMAS) throws ScenarioLoadingException{
    
    // Read agent profiles
    List<AgentProfile> agentProfiles = readAgentProfiles(scenario);
    
    // Read action transformers
    List<Element> leActionTransformers = eEMMAS.getChild(TAG_ACTION_TRANSFORMERS).getChildren(TAG_ACTION_TRANSFORMER);
    List<ActionTransformer> actionTransformers = readActionTransformers(leActionTransformers);
    
    // Read environment behaviors
    List<Element> leBehaviors = eEMMAS.getChild(TAG_BEHAVIORS).getChildren(TAG_BEHAVIOR);
    List<ESLOperation> behaviors = readEnvironmentBehaviors(leBehaviors);
    
    // Setup the ESL environment
    Environment environment = new Environment(agentProfiles, actionTransformers, behaviors);

    PiProcess proc;
    
    try {
      proc = environment.toPiProcess(new Context());
    } catch (IllegalSemanticsException e) {
      throw new ScenarioLoadingException(e);
    }
    
    return new EMMASTransitionSystem(proc);
    
  }
  
  
  //
  // Main structures
  //
  
  private List<AgentProfile> readAgentProfiles(Scenario scenario) throws ScenarioLoadingException{
    
    // We assume that the agents have already been read. So now we get them
    // and query for their properties.
    
    List<AgentProfile> agentProfiles = new LinkedList<AgentProfile>();
    
    List<IAgentControl> agents;
    try {
      
      agents = scenario.createAgentControls(cr);
      
    } catch (ComponentInstantiationException e) {
      
      throw new ScenarioLoadingException("Cannot access the agents in order to determine their profiles.", e);
    }
    
    
    for(IAgentControl agent: agents){
      if(agent instanceof IBehavioralAgent){
         IBehavioralAgent ba = (IBehavioralAgent) agent;
         
         Integer id = ba.getId();
         Collection<EnvironmentAction> envActions = ba.possibleActions();
         Collection<EnvironmentStimulus> envStimuli = ba.possibleStimuli();
         
         Set<Action> actions = new HashSet<Action>();
         for(EnvironmentAction ea: envActions){
           actions.add(new Action(ea.getType()));
         }
         
         Set<Stimulus> stimuli = new HashSet<Stimulus>();
         for(EnvironmentStimulus es: envStimuli){
           stimuli.add(new Stimulus(es.getType()));
         }
         
         AgentProfile ap = new AgentProfile(id, actions, stimuli);
         agentProfiles.add(ap);
         
         // Store the agent in a map so that we may find it by its ID later in the loading process
         id2AgentProfile.put(id, ap);
         
      }
      else{
        throw new ScenarioLoadingException("All agents in an EMMAS model must implement IBehavioralAgent.");
      }
    }
    
    return agentProfiles;
  }
  
  private List<ActionTransformer> readActionTransformers(List<Element> leAT) throws ScenarioLoadingException{
    List<ActionTransformer> ats = new LinkedList<ActionTransformer>();
    
    for(Element eAT: leAT){
      
      Integer id1 = Integer.parseInt(eAT.getAttributeValue(ATTR_AGENT_ID1));
      String actionType = eAT.getAttributeValue(ATTR_ACTION);
      String stimulusType = eAT.getAttributeValue(ATTR_STIMULUS);
      Integer id2 = Integer.parseInt(eAT.getAttributeValue(ATTR_AGENT_ID2));
      
      AgentProfile ap1 = getAgentProfile(id1);
      AgentProfile ap2 = getAgentProfile(id2);
      Action action = new Action(actionType);
      Stimulus stimulus = new Stimulus(stimulusType);
      
      ActionTransformer at = new ActionTransformer(ap1, action, stimulus, ap2);
      ats.add(at);
    }
    
    return ats;
  }
  
  private List<ESLOperation> readEnvironmentBehaviors(List<Element> leBehavior) throws ScenarioLoadingException{
    
    List<ESLOperation> ops = new LinkedList<ESLOperation>();
    
    for(Element eBehavior: leBehavior){
      if(!eBehavior.getChildren().isEmpty()){
        Element element = (Element)eBehavior.getChildren().iterator().next();
        ESLOperation op = readOperation(element);
        ops.add(op);
      }
    }
    
    return ops;
  }
  
  
  //
  // Connectives, primitive operations
  //
  
  private List<ESLOperation> readOperations(List<Element> leOperation) throws ScenarioLoadingException{
    
    List<ESLOperation> ops = new LinkedList<ESLOperation>();
    
    for(Element eOperation: leOperation){
      ops.add(readOperation(eOperation));
    }
    
    return ops;
  }
  
  
  
  private ESLOperation readOperation(Element eOperation) throws ScenarioLoadingException{
    
    ESLOperation op = null;
    
    if(eOperation.getName().equals(TAG_CHOICE)){
      op = readChoice(eOperation);
    }
    else if (eOperation.getName().equals(TAG_PARALLEL)){
      op = readParallel(eOperation);
    }
    else if (eOperation.getName().equals(TAG_SEQUENTIAL_COMPOSITION)){
      op = readSequentialComposition(eOperation);
    }
    else if (eOperation.getName().equals(TAG_SEQUENCE)){
      op = readSequence(eOperation);
    }
    else if (eOperation.getName().equals(TAG_UNBOUNDED_SEQUENCE)){
      op = readUnboundedSequence(eOperation);
    }
    else if (eOperation.getName().equals(TAG_ENVIRONMENT_RESPONSE)){
      op = readEnvironmentResponse(eOperation);
    }
    else if (eOperation.getName().equals(TAG_OP_NOP)){
      op = readNop(eOperation);
    }    
    else if (eOperation.getName().equals(TAG_OP_CREATE)){
      op = readCreate(eOperation);
    }
    else if (eOperation.getName().equals(TAG_OP_DESTROY)){
      op = readDestroy(eOperation);
    }
    else if (eOperation.getName().equals(TAG_OP_BEGIN_STIM)){
      int id = Integer.parseInt(eOperation.getAttributeValue(ATTR_AGENT_ID));
      Stimulus stimulus = new Stimulus(eOperation.getAttributeValue(ATTR_STIMULUS));
      op = new BeginStimulation(stimulus, getAgentProfile(id));
    }
    else if (eOperation.getName().equals(TAG_OP_END_STIM)){
      int id = Integer.parseInt(eOperation.getAttributeValue(ATTR_AGENT_ID));
      Stimulus stimulus = new Stimulus(eOperation.getAttributeValue(ATTR_STIMULUS));
      op = new EndStimulation(stimulus, getAgentProfile(id));
    }
    else if (eOperation.getName().equals(TAG_OP_STIMULATE)){
      int id = Integer.parseInt(eOperation.getAttributeValue(ATTR_AGENT_ID));
      Stimulus stimulus = new Stimulus(eOperation.getAttributeValue(ATTR_STIMULUS));
      op = new Stimulate(stimulus, getAgentProfile(id));
    }
    else{
      throw new ScenarioLoadingException("The operation identified by the tag <" + eOperation.getName() + "> is undefined.");
    }
    
    // TODO other types ?
    
    
    
    return op;
    
  }
  
  private Choice readChoice(Element eChoice) throws ScenarioLoadingException{
    List<ESLOperation> operands = readOperations(eChoice.getChildren());
    return new Choice(operands);
  }
  
  private Parallel readParallel(Element eParallel) throws ScenarioLoadingException{
    List<ESLOperation> operands = readOperations(eParallel.getChildren());
    return new Parallel(operands);
  }
  
  private SequentialComposition readSequentialComposition(Element eSeqComp) throws ScenarioLoadingException{
    List<ESLOperation> operands = readOperations(eSeqComp.getChildren());
    return new SequentialComposition(operands);
  }

  private Sequence readSequence(Element eSequence) throws ScenarioLoadingException{
    ESLOperation operand = readOperation((Element) eSequence.getChildren().iterator().next());
    int n = Integer.parseInt(eSequence.getAttributeValue(ATTR_TIMES, "1"));

    return new Sequence(operand, n);
  }
  
  private UnboundedSequence readUnboundedSequence(Element eForever) throws ScenarioLoadingException{
    ESLOperation operand = readOperation((Element) eForever.getChildren().iterator().next());
    return new UnboundedSequence(operand);
  }
  
  private EnvironmentResponse readEnvironmentResponse(Element eER) throws ScenarioLoadingException{
    int id = Integer.parseInt(eER.getAttributeValue(ATTR_AGENT_ID));
    Action action = new Action(eER.getAttributeValue(ATTR_ACTION));
    
    ESLOperation operand = readOperation((Element) eER.getChildren().iterator().next());
    
    return new EnvironmentResponse(action, getAgentProfile(id), operand);
  }

  private Nop readNop(Element eCreate) throws ScenarioLoadingException{
    return new Nop();
  }
  
  private Create readCreate(Element eCreate) throws ScenarioLoadingException{
    
    Integer id1 = Integer.parseInt(eCreate.getAttributeValue(ATTR_AGENT_ID1));
    String actionType = eCreate.getAttributeValue(ATTR_ACTION);
    String stimulusType = eCreate.getAttributeValue(ATTR_STIMULUS);
    Integer id2 = Integer.parseInt(eCreate.getAttributeValue(ATTR_AGENT_ID2));
    
    AgentProfile ap1 = getAgentProfile(id1);
    AgentProfile ap2 = getAgentProfile(id2);
    Action action = new Action(actionType);
    Stimulus stimulus = new Stimulus(stimulusType);
    
    return new Create(ap1, action, stimulus, ap2);
  }
  
private Destroy readDestroy(Element eDestroy) throws ScenarioLoadingException{
    
    Integer id1 = Integer.parseInt(eDestroy.getAttributeValue(ATTR_AGENT_ID1));
    String actionType = eDestroy.getAttributeValue(ATTR_ACTION);
    String stimulusType = eDestroy.getAttributeValue(ATTR_STIMULUS);
    Integer id2 = Integer.parseInt(eDestroy.getAttributeValue(ATTR_AGENT_ID2));
    
    AgentProfile ap1 = getAgentProfile(id1);
    AgentProfile ap2 = getAgentProfile(id2);
    Action action = new Action(actionType);
    Stimulus stimulus = new Stimulus(stimulusType);
    
    return new Destroy(ap1, action, stimulus, ap2);
  }
  
  
  
  private AgentProfile getAgentProfile(Integer id) throws ScenarioLoadingException{
    AgentProfile ap = id2AgentProfile.get(id);
    
    if(ap == null){
      throw new ScenarioLoadingException("The agent identified by " + id + " was refered in the environment specification, but it was not defined.");
    }
    
    return ap;
  }
  
}
