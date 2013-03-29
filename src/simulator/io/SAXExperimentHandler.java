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

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import simulator.Experiment;
import simulator.agent.IAgent;
import simulator.agent.stimuli.EnvironmentStimulus;
import simulator.analysis.PropertyBearerWrapper;
import simulator.environment.IEnvironment;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import simulator.util.Assert;



/**
 * Implements a SAX content handler for reading XML experiment files. This 
 * implementation expects the file to be well-formed. Such requirement can 
 * be easily fulfilled by checking the document with an appropriate DTD prior 
 * to parsing, for example.
 * 
 * @author  Paulo Salem
 */
@Deprecated
public class SAXExperimentHandler extends DefaultHandler{

	
	/*
	 * Constants for the XML tag names.
	 */
	
	private final String TAG_EXPERIMENT = "experiment";
  
  private final String TAG_STANDARD_SIM = "standard-simulation";
  
  private final String TAG_STIMULUS_DELIVERY_OPT = "stimulus-delivery-optimization";
  private final String TAG_STIMULUS = "stimulus";
  
  private final String TAG_REFERENCED_AGENTS = "referenced-agents";
  private final String TAG_AGENT = "agent";
	
  private final String TAG_ATARGET = "agent-target";
  private final String TAG_ETARGET = "environment-target";
  
  
  private IEnvironment environment;
  
	/**
	 * The experiment being loaded.
	 */
	private Experiment experiment = null;
	
	
	/**
	 * What the parser is currently loading.
	 */
	private Stack<String> parserState = new Stack<String>();
	

  /**
   * A stack that contains objects to be used during
   * the endElement() operation.
   */
  private Stack<Object> stateInfo = new Stack<Object>();
  
  /**
   * The agents refered to by the current stimulus.
   */
  private List<IAgent> referencedAgents = new LinkedList<IAgent>();

  /**
   * The agents targeted to by the current strategy.
   */
  private List<IAgent> targetAgents = new LinkedList<IAgent>();
  
  /**
   * If the environment is being targeted by the current strategy.
   */
  private boolean targetEnvironment = false;

  
  public SAXExperimentHandler(IEnvironment environment){
    Assert.notNull(environment);
    
    this.environment = environment;
  }
  
	/**
	 * 
	 * @return The <code>Experiment</code> that has been read.
	 */
	public Experiment getExperiment(){
		return experiment;
	}
	
	@Override
	public void endDocument() throws SAXException {
	  SimulatorUI.instance().getMessenger().printDebugMsg("Closing experiment file...", Messenger.NORMAL_MSG);
	}

	@Override
	public void startDocument() throws SAXException {
	  SimulatorUI.instance().getMessenger().printDebugMsg("Opening experiment file...", Messenger.NORMAL_MSG);
		
		// Reset all content handling variables
		experiment = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

		// Everytime we find a star element, we push it into a stack so that
		// the parser knows what it is supposed to do later on.
		
		if(qName.equals(TAG_EXPERIMENT)){
			
			
			String name = atts.getValue("name");
			String description = atts.getValue("description");
			
			experiment = new Experiment(name, description);
			
			SimulatorUI.instance().getMessenger().printDebugMsg("Found experiment declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Description = " + description, Messenger.NORMAL_MSG);

			parserState.push(TAG_EXPERIMENT);

		}
    else if(qName.equals(TAG_STANDARD_SIM)){
      
      String name = atts.getValue("name");
      String runs = atts.getValue("runs");
      String iterations = atts.getValue("iterations-per-run");

      SimulatorUI.instance().getMessenger().printDebugMsg("Found standard simulation strategy declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Runs = " + runs, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Iterations per Run = " + iterations, Messenger.NORMAL_MSG);

      experiment.addStandardSimulationStrategy(Integer.parseInt(runs), Integer.parseInt(iterations));
        
      parserState.push(TAG_STANDARD_SIM);
    }
    else if(qName.equals(TAG_STIMULUS_DELIVERY_OPT)){
      
      targetAgents = new LinkedList<IAgent>();
      targetEnvironment = false;
      
      String name = atts.getValue("name");
      String runs = atts.getValue("runs");
      String iterations = atts.getValue("iterations-per-run");
      String propertyId = atts.getValue("property-id");
      String targets = atts.getValue("targets");

      SimulatorUI.instance().getMessenger().printDebugMsg("Found stimulus delivery optimization strategy declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Runs = " + runs, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Iterations per Run = " + iterations, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Objective property ID = " + propertyId, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Targets = " + targets, Messenger.NORMAL_MSG);
      
      // Push data to be used in endElement() operation.
      stateInfo.push(name);
      stateInfo.push(runs);
      stateInfo.push(iterations);
      stateInfo.push(propertyId);
      stateInfo.push(targets);

        
      parserState.push(TAG_STIMULUS_DELIVERY_OPT);
    }
    else if(qName.equals(TAG_STIMULUS)){
      
      
      String type = atts.getValue("type");
      String content = atts.getValue("content");
      
      stateInfo.push(type);
      stateInfo.push(content);
        
      parserState.push(TAG_STIMULUS);
    }
    else if(qName.equals(TAG_REFERENCED_AGENTS)){
      
      referencedAgents = new LinkedList<IAgent>();

      SimulatorUI.instance().getMessenger().printDebugMsg("Found referenced agents list declaration: ", Messenger.NORMAL_MSG);
      
      parserState.push(TAG_REFERENCED_AGENTS);
    }
    else if(qName.equals(TAG_AGENT)){
      
      String id = atts.getValue("id");
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Found agent reference declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + id, Messenger.NORMAL_MSG);
      
      // Add agent to the referenced agents list
      referencedAgents.add(environment.getAgent(Integer.parseInt(id)));
      
      parserState.push(TAG_AGENT);
    }
    else if(qName.equals(TAG_ATARGET)){
      String id = atts.getValue("id");
      
      IAgent a = environment.getAgent(Integer.valueOf(id));
      
      targetAgents.add(a);
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Found agent target declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the agent = " + id, Messenger.NORMAL_MSG);
      
      parserState.push(TAG_ATARGET);
    }
    else if(qName.equals(TAG_ETARGET)){
      
      targetEnvironment = true;
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Found targeted environment declaration. ", Messenger.NORMAL_MSG);
      
      parserState.push(TAG_ETARGET);
    }

    // TODO Read more strategies
		
	}
	

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
    // Pop parser state first
		parserState.pop();
		
    // Finalize operations
    if(qName.equals(TAG_STIMULUS_DELIVERY_OPT)){
      
      // Be careful with the order we pop things! Must be in the inverse
      // order that they have been pushed.
      EnvironmentStimulus environmentStimulus = (EnvironmentStimulus) stateInfo.pop();
      String targets = (String) stateInfo.pop();
      String propertyId = (String) stateInfo.pop();
      String iterations = (String) stateInfo.pop();
      String runs = (String) stateInfo.pop();
      String name = (String) stateInfo.pop();
      
      IAgent aTarget = null;
      
      // Get target agents, if any
      if(targetAgents.size() == 1){
        aTarget = targetAgents.get(0);
      }
      else if(targetAgents.size() > 1){
        throw new SAXException("Stimulus delivery optimization must be target at 0 or 1 agents only.");
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
        throw new SAXException("Stimulus delivery optimization must be target either at an agent or at the environment, but not both.");
      }

       
      
      try {
        experiment.addStimulusDeliveryOptimization(Integer.parseInt(runs), Integer.parseInt(iterations), environmentStimulus, Integer.parseInt(propertyId), pbw, Integer.parseInt(targets));
        
      } catch (NumberFormatException e) {
        throw new SAXException("Integer value required.");
        
      } 
            
      
    }
    else if(qName.equals(TAG_STIMULUS)){

      // Be careful with the order we pop things! Must be in the inverse
      // order that they have been pushed.
      String content = (String) stateInfo.pop();
      String type = (String) stateInfo.pop();

      SimulatorUI.instance().getMessenger().printDebugMsg("Found stimulus declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Type = " + type, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Content = " + content, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Referenced agents = " + referencedAgents.toString(), Messenger.NORMAL_MSG);
      

      // Push the stimulus to be read later on
      EnvironmentStimulus environmentStimulus = new EnvironmentStimulus(type, content, null, referencedAgents);
      stateInfo.push(environmentStimulus);
    }


	}

	
	
}
