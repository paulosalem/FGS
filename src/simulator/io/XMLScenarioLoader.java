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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import simulator.Scenario;
import simulator.components.ComponentsRegistry;
import simulator.io.esl.XMLESLLoader;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;
import simulator.util.Assert;

/**
 * @author  Paulo Salem
 */
public class XMLScenarioLoader extends ScenarioLoader {

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
  
  private final String TAG_INITIALIZER = "initializer";
  private final String ATTR_FILE = "file";

  private final String TAG_ATARGET = "agent-target";
  private final String TAG_ETARGET = "environment-target";
  
  private final String TAG_EMMAS = "emmas";
  
  // TODO tags for EMMAS related things
  
	SAXScenarioHandler handler = new SAXScenarioHandler();
	
	ComponentsRegistry cr = null;
	
	
	
	public XMLScenarioLoader(ComponentsRegistry cr) {
    super();
    Assert.notNull(cr);
    
    this.cr = cr;
  }



  public  Scenario loadScenario(File f) throws ScenarioLoadingException{

	  SAXBuilder builder = new SAXBuilder();
	  
	  try{
	    
	    Document doc = builder.build(f);
	    
	    SimulatorUI.instance().getMessenger().printDebugMsg("Loading scenario file...", Messenger.NORMAL_MSG);
	    
	    Scenario scenario = load(f, doc);
	    
	    SimulatorUI.instance().getMessenger().printDebugMsg("Finsihed loading scenario file.", Messenger.NORMAL_MSG);
	    
	    return scenario;
	  
	  } catch (Exception e) {
	    
	    SimulatorUI.instance().getMessenger().printDebugMsg("Exception while loading scenario:" + e.getMessage(), Messenger.NORMAL_MSG);
	    
	    throw new ScenarioLoadingException(e.getMessage(), e);
	  } 
	  
	
	}
	
	
	
	
	private Scenario load(File mainFile, Document doc) throws ScenarioLoadingException{
	  
	  Scenario scenario = null;
	  
	  // Get the scenario element
	  Element eScenario = doc.getRootElement();//.getChild(TAG_SCENARIO);
	  if(!eScenario.getName().equals(TAG_SCENARIO)){
	    throw new ScenarioLoadingException("Root element must be <" + TAG_SCENARIO +">");
	  }
	  
	  scenario = new Scenario(eScenario.getAttributeValue("name", ""),
	                          eScenario.getAttributeValue("description", ""));
	  
	  SimulatorUI.instance().getMessenger().printDebugMsg("Found scenario declaration: ", Messenger.NORMAL_MSG);
    SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + eScenario.getAttributeValue("name", ""), Messenger.NORMAL_MSG);
    SimulatorUI.instance().getMessenger().printDebugMsg("  Description = " + eScenario.getAttributeValue("description", ""), Messenger.NORMAL_MSG);

	  
	  
	  // Get agents
	  List<Element> leAgent = eScenario.getChildren(TAG_AGENT);
	  loadAgents(mainFile, scenario, leAgent);
	  
	  // Get properties
	  List<Element> leProperty = eScenario.getChildren(TAG_PROPERTY);
	  loadProperties(scenario, leProperty);
	  
	  // Get relations
	  List<Element> leRelation = eScenario.getChildren(TAG_RELATION);
	  loadRelations(scenario, leRelation);
	  
	  // Get formal environment description (EMMAS), if any
	  // This has to come after the agents definitions, since they are used
	  // here.
	  Element eEMMAS = eScenario.getChild(TAG_EMMAS);
	  if(eEMMAS != null){
	    loadEMMAS(scenario, eEMMAS);
	  }
	  
	  return scenario;
	}

	
	private void loadAgents(File mainFile, Scenario scenario, List<Element> leAgent) throws ScenarioLoadingException{
	
	  for(Element eAgent: leAgent){
	    
	    // Load agent info
	    String id = eAgent.getAttributeValue("id");
	    String name = eAgent.getAttributeValue("name");
	    String componentId = eAgent.getAttributeValue("component-id");
	    
	    int agentId = Integer.parseInt(id);
	    
	    SimulatorUI.instance().getMessenger().printDebugMsg("Found agent declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Component ID = " + componentId, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + id, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);
	    
	    scenario.addAgent(componentId, agentId, name);
	    
	    // Load custom initializer, if any
	    Element eInitializer = eAgent.getChild(TAG_INITIALIZER);
	    if (eInitializer != null){
	      
	      SimulatorUI.instance().getMessenger().printDebugMsg("Found custom agent initializer declaration: ", Messenger.NORMAL_MSG);
	      
	      Element eInitContent;
	      
	      // If a file is included, we load it
	      if(eInitializer.getAttribute(ATTR_FILE) != null){
	        String fileName = eInitializer.getAttributeValue(ATTR_FILE);
	        eInitContent = getRootElementFromIncludedFile(mainFile, fileName);
	        
	        SimulatorUI.instance().getMessenger().printDebugMsg("  in included file: " + fileName, Messenger.NORMAL_MSG);
	      }
	      else{
	        // If it is not an attribute, it must be in the tag's content
	        eInitContent = (Element) eInitializer.getChildren().iterator().next();
	        
	        SimulatorUI.instance().getMessenger().printDebugMsg("  in the main file. ", Messenger.NORMAL_MSG);
	      }
	      
	      scenario.addAgentInitializer(agentId, eInitContent);
	      
	    }
	    
	    // Load primitive parameters
	    List<Element> lePParameter = eAgent.getChildren(TAG_PPARAMETER);
	    for(Element ePParameter: lePParameter){
	      String attrName = ePParameter.getAttributeValue("name");
	      String attrValue = ePParameter.getAttributeValue("value");
	      
	      SimulatorUI.instance().getMessenger().printDebugMsg("Found agent primitive parameter declaration: ", Messenger.NORMAL_MSG);
	      SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + attrName, Messenger.NORMAL_MSG);      
	      SimulatorUI.instance().getMessenger().printDebugMsg("  Value = " + attrValue, Messenger.NORMAL_MSG);
	      
	      scenario.addAgentParameter(agentId, attrName, attrValue);
	    }
	    
	    // Load list parameters
	    List<Element> leLParameter = eAgent.getChildren(TAG_LPARAMETER);
	    List<String> currentParameterList = new LinkedList<String>();
      for(Element eLParameter: leLParameter){
        String attrName = eLParameter.getAttributeValue("name");
        
        
        // Read list elements
        List<Element> lePValue = eLParameter.getChildren(TAG_PVALUE);
        for(Element ePValue: lePValue){
          
          SimulatorUI.instance().getMessenger().printDebugMsg("Found parameter value declaration: ", Messenger.NORMAL_MSG);
          SimulatorUI.instance().getMessenger().printDebugMsg("  Value = " + ePValue.getAttributeValue("value"), Messenger.NORMAL_MSG);
          
          currentParameterList.add(ePValue.getAttributeValue("value"));
        }
        
        // Save the list
        scenario.addAgentParameter(agentId, attrName, currentParameterList);
      }
	    
	  }
	}
	
	private void loadRelations(Scenario scenario, List<Element> leRelation){
    
	  for(Element eRelation: leRelation){
	    
	    // Load relation info
	    String relName = eRelation.getAttributeValue("name", "");
	    int relId = Integer.parseInt(eRelation.getAttributeValue("id"));
	    String relDescription = eRelation.getAttributeValue("description", "");
	    
	    SimulatorUI.instance().getMessenger().printDebugMsg("Found relation declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + relId, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + relName, Messenger.NORMAL_MSG);      
      SimulatorUI.instance().getMessenger().printDebugMsg("  Description = " + relDescription, Messenger.NORMAL_MSG);
	    
	    scenario.addRelation(relId, relName, relDescription);
	    
	    // Load relational ties
	    List<Element> leTies = eRelation.getChildren(TAG_TIE);
	    for(Element eTie: leTies){
	      
	      int id1 = Integer.parseInt(eTie.getAttributeValue("id1"));
	      int id2 = Integer.parseInt(eTie.getAttributeValue("id2"));
	      
	      SimulatorUI.instance().getMessenger().printDebugMsg("Found relational tie declaration: ", Messenger.NORMAL_MSG);
	      SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the first agent = " + id1, Messenger.NORMAL_MSG);
	      SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the second agent = " + id2, Messenger.NORMAL_MSG);
	      
	      scenario.addRelatioPair(relId, id1, id2);
	      
	    }
	  }
  }
	
	
	private void loadProperties(Scenario scenario, List<Element> leProperty){
	  
	  for(Element eProperty: leProperty){
	    String id = eProperty.getAttributeValue("id");
      String name = eProperty.getAttributeValue("name");
      String componentId = eProperty.getAttributeValue("component-id");
      
      int propertyId = Integer.parseInt(id);
      
      SimulatorUI.instance().getMessenger().printDebugMsg("Found property declaration: ", Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Component ID = " + componentId, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  ID = " + id, Messenger.NORMAL_MSG);
      SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + name, Messenger.NORMAL_MSG);
      
      scenario.addProperty(componentId, propertyId, name);
      
      // Load primitive parameters
      List<Element> lePParameter = eProperty.getChildren(TAG_PPARAMETER);
      for(Element ePParameter: lePParameter){
        String attrName = ePParameter.getAttributeValue("name");
        String attrValue = ePParameter.getAttributeValue("value");
        
        SimulatorUI.instance().getMessenger().printDebugMsg("Found property primitive parameter declaration: ", Messenger.NORMAL_MSG);
        SimulatorUI.instance().getMessenger().printDebugMsg("  Name = " + attrName, Messenger.NORMAL_MSG);      
        SimulatorUI.instance().getMessenger().printDebugMsg("  Value = " + attrValue, Messenger.NORMAL_MSG);
        
        scenario.addPropertyParameter(propertyId, attrName, attrValue);
      }
      
      // Load list parameters
      List<Element> leLParameter = eProperty.getChildren(TAG_LPARAMETER);
      List<String> currentParameterList = new LinkedList<String>();
      for(Element eLParameter: leLParameter){
        String attrName = eLParameter.getAttributeValue("name");
        
        
        // Read list elements
        List<Element> lePValue = eLParameter.getChildren(TAG_PVALUE);
        for(Element ePValue: lePValue){
          
          SimulatorUI.instance().getMessenger().printDebugMsg("Found parameter value declaration: ", Messenger.NORMAL_MSG);
          SimulatorUI.instance().getMessenger().printDebugMsg("  Value = " + ePValue.getAttributeValue("value"), Messenger.NORMAL_MSG);
          
          currentParameterList.add(ePValue.getAttributeValue("value"));
        }
        
        // Save the list
        scenario.addPropertyParameter(propertyId, attrName, currentParameterList);
      }
      
      // Load agent targets
      List<Element> leATarget = eProperty.getChildren(TAG_ATARGET);
      for(Element eATarget: leATarget){
       
        int agentId = Integer.parseInt(eATarget.getAttributeValue("id"));
        
        SimulatorUI.instance().getMessenger().printDebugMsg("Found property agent target declaration: ", Messenger.NORMAL_MSG);
        SimulatorUI.instance().getMessenger().printDebugMsg("  ID of the agent = " + agentId, Messenger.NORMAL_MSG);
        
        scenario.attachPropertyToAgent(propertyId, agentId);
      }
      
      // Load environment target
      Element eETarget = eProperty.getChild(TAG_ETARGET);
      if(eETarget != null){
        SimulatorUI.instance().getMessenger().printDebugMsg("Found property attached to environment. ", Messenger.NORMAL_MSG);
        scenario.attachPropertyToEnvironment(propertyId);
      }
      
      
      
	  }
	  
  }
	
	
	private void loadEMMAS(Scenario scenario, Element eEMMAS) throws ScenarioLoadingException{
	  
	  // We have a class just for this task, which is rather laborious
	  XMLESLLoader loader = new XMLESLLoader(cr);
	  
	  scenario.setEMMAS(loader.readEMMAS(scenario, eEMMAS));
	}
	
	/**
	 * Retrieves the root element from an included XML file.
	 * 
	 * @param mainFile A main file in which another is included.
	 * 
	 * @param includedFileName The name of the file to be included.
	 * 
	 * @return The root element of the included file.
	 * 
	 * @throws ScenarioLoadingException
	 */
	private Element getRootElementFromIncludedFile(File mainFile, String includedFileName) throws ScenarioLoadingException{
	  
	  // Check whether the included file is absolute or not
	  File includedFile = new File(includedFileName);
	  
	  if(!includedFile.isAbsolute()){
	    // If it is not absolute, it must be relative w.r.t. the main file's folder
	    includedFile = new File(mainFile.getParent() + File.separator + includedFileName);
	  }
	  
	  SAXBuilder builder = new SAXBuilder();
	  
	  try {
      Document doc = builder.build(includedFile);
      
      return doc.getRootElement();
      
    } catch (JDOMException e) {
      throw new ScenarioLoadingException("The file " + includedFileName + " is malformed.", e);
    } catch (IOException e) {
      throw new ScenarioLoadingException("The file " + includedFileName + " could not be opened.", e);
    }
	}
	
	
	
}
