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

import simulator.Scenario;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;

/**
 * Implements a SAX content handler for reading XML scenario files. This implementation expects the file to be well-formed. Such requirement can be easily fulfilled by checking the document with an appropriate DTD prior to parsing, for example.
 * @author  Paulo Salem
 */
@Deprecated
public class SAXScenarioHandler extends DefaultHandler{

	
	/*
	 * Constants for the XML tag names.
	 */
	
	private final String TAG_SCENARIO = "scenario";
	private final String TAG_AGENT = "agent";
	private final String TAG_PROPERTY = "property";
	
	private final String TAG_RELATION = "relation";
	private final String TAG_TIE = "tie";
	
	private final String TAG_PPARAMETER = "primitive-parameter";
  private final String TAG_LPARAMETER = "list-parameter";
  private final String TAG_PVALUE = "parameter-value";

	private final String TAG_ATARGET = "agent-target";
	private final String TAG_ETARGET = "environment-target";
	
	/**
	 * The scenario being built.
	 */
	private Scenario scenario = null;
	
	
	/**
	 * What the parser is currently loading.
	 */
	private Stack<String> parserState = new Stack<String>();
	
	/**
	 * The ID of the last parsed agent.
	 */
	private int lastAgentId;
	
	/**
	 * The ID of the last parsed relation.
	 */
	private int lastRelationId;

	/**
	 * The ID of the last parsed property.
	 */
	private int lastPropertyId;
  

  /**
   * The parameter list currently being loaded.
   */
  private List<String> currentParameterList;
  
  /**
   * The name of the parameter list currently being loaded.
   */
  private String currentParameterListName;

	/**
   * @return  The <code>Scenario</code> that has been read.
   * @uml.property  name="scenario"
   */
	public Scenario getScenario(){
		return scenario;
	}
	
	@Override
	public void endDocument() throws SAXException {
	  SimulatorUI.instance().getMessenger().printDebugMsg("Closing scenario file...", Messenger.NORMAL_MSG);
	}

	@Override
	public void startDocument() throws SAXException {
	  SimulatorUI.instance().getMessenger().printDebugMsg("Opening scenario file...", Messenger.NORMAL_MSG);
		
		// Reset all content handling variables
		scenario = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

		// Everytime we find a star element, we push it into a stack so that
		// the parser knows what it is supposed to do later on.
		
		if(qName.equals(TAG_SCENARIO)){
			
			
			String name = atts.getValue("name");
			String description = atts.getValue("description");
			
			scenario = new Scenario(name, description);
			
			SimulatorUI.instance().getMessenger().printDebugMsg("Found scenario declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Description = " + description, Messenger.NORMAL_MSG);

			parserState.push(TAG_SCENARIO);

		}
		else if(qName.equals(TAG_AGENT)){
			
			String id = atts.getValue("id");
			String name = atts.getValue("name");
			String componentId = atts.getValue("component-id");
			
			lastAgentId = Integer.parseInt(id);
			scenario.addAgent(componentId, lastAgentId, name);
			
			SimulatorUI.instance().getMessenger().printDebugMsg("Found agent declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Component ID = " + componentId, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + id, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);

			parserState.push(TAG_AGENT);

		}
		else if(qName.equals(TAG_PROPERTY)){
			
			String id = atts.getValue("id");
			String name = atts.getValue("name");
			String componentId = atts.getValue("component-id");	
			
			lastPropertyId = Integer.parseInt(id);
			scenario.addProperty(componentId, lastPropertyId, name);

			SimulatorUI.instance().getMessenger().printDebugMsg("Found property declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Component ID = " + componentId, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + id, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);
			
			parserState.push(TAG_PROPERTY);
			
		}
		else if(qName.equals(TAG_PPARAMETER)){
		
			String name = atts.getValue("name");
			String value = atts.getValue("value");
			
			// Check wich entity the parameter refers to
			if(parserState.peek().equals(TAG_AGENT)){
				scenario.addAgentParameter(lastAgentId, name, value);
			}
			else if (parserState.peek().equals(TAG_PROPERTY)){
				scenario.addPropertyParameter(lastPropertyId, name, value);
			}

			
			SimulatorUI.instance().getMessenger().printDebugMsg("Found primitive parameter declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);			
			SimulatorUI.instance().getMessenger().printDebugMsg("  Value = " + value, Messenger.NORMAL_MSG);

			parserState.push(TAG_PPARAMETER);
		
		}
    // TODO TAG_LPARAMETER
    else if(qName.equals(TAG_LPARAMETER)){
      
      currentParameterListName = atts.getValue("name");
      currentParameterList = new LinkedList<String>();

      parserState.push(TAG_LPARAMETER);
    
    }    
    // TODO TAG_VALUE
    else if(qName.equals(TAG_PVALUE)){
      
      String value = atts.getValue("value");

      currentParameterList.add(value);
            
      SimulatorUI.instance().getMessenger().printDebugMsg("Found parameter value declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Value = " + value, Messenger.NORMAL_MSG);
      
      parserState.push(TAG_PVALUE);
    
    }
		else if(qName.equals(TAG_RELATION)){
		
			String id = atts.getValue("id");
			String name = atts.getValue("name");
			String description = atts.getValue("description");
			
			lastRelationId = Integer.parseInt(id);
			scenario.addRelation(Integer.parseInt(id), name, description);
						
			SimulatorUI.instance().getMessenger().printDebugMsg("Found relation declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + id, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);			
			SimulatorUI.instance().getMessenger().printDebugMsg("  Description = " + description, Messenger.NORMAL_MSG);

			parserState.push(TAG_RELATION);
		}
		else if(qName.equals(TAG_TIE)){
	
			String id1 = atts.getValue("id1");
			String id2 = atts.getValue("id2");
			
			scenario.addRelatioPair(lastRelationId, Integer.parseInt(id1), Integer.parseInt(id2));
						
			SimulatorUI.instance().getMessenger().printDebugMsg("Found relational tie declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the first agent = " + id1, Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the second agent = " + id2, Messenger.NORMAL_MSG);
			
			parserState.push(TAG_TIE);
		
		}
		else if(qName.equals(TAG_ATARGET)){
			String id = atts.getValue("id");
			
			scenario.attachPropertyToAgent(lastPropertyId, Integer.parseInt(id));

			SimulatorUI.instance().getMessenger().printDebugMsg("Found property agent target declaration: ", Messenger.NORMAL_MSG);
			SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the agent = " + id, Messenger.NORMAL_MSG);
			
			parserState.push(TAG_ATARGET);
		}
		else if(qName.equals(TAG_ETARGET)){
			
			scenario.attachPropertyToEnvironment(lastPropertyId);
			
			SimulatorUI.instance().getMessenger().printDebugMsg("Found property attached to environment. ", Messenger.NORMAL_MSG);
			
			parserState.push(TAG_ETARGET);
		}
		
	}
	

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		parserState.pop();
	
    // Finalize operations
    if(qName.equals(TAG_LPARAMETER)){
      
      String name = currentParameterListName;
      
      // Check wich entity the parameter refers to
      if(parserState.peek().equals(TAG_AGENT)){
        scenario.addAgentParameter(lastAgentId, name, currentParameterList);
      }
      else if (parserState.peek().equals(TAG_PROPERTY)){
        scenario.addPropertyParameter(lastPropertyId, name, currentParameterList);
      }

      
      SimulatorUI.instance().getMessenger().printDebugMsg("Found list parameter declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + currentParameterListName, Messenger.NORMAL_MSG);      
      SimulatorUI.instance().getMessenger().printDebugMsg("  Value = " + currentParameterList.toString(), Messenger.NORMAL_MSG);    
    }
	}

	
	
}
