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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

import simulator.components.ComponentsRegistry;
import simulator.ui.Messenger;
import simulator.ui.SimulatorUI;


/**
 * The mechanism to find and load components.
 * 
 * @author   Paulo Salem
 */
public class ComponentsLoader {

  /**
   * The path to the directory where components are located.
   */
  protected File componentsFolder;
  
  /**
   * The path to the directory where libraries used by the components are located.
   * This is necessary because we cannot nest the libraries in the components' jars
   * themselves.
   */
  protected File librariesFolder;
  
  /**
   * Where the components should be loaded.
   */
  protected ComponentsRegistry registry;
  
  
  public ComponentsLoader(ComponentsRegistry registry, File componentsFolder, File librariesFolder){
    
    // Check parameters
    if(registry == null || componentsFolder == null){
      throw new IllegalArgumentException();
    }
    
    this.registry = registry;
    this.componentsFolder = componentsFolder;
    this.librariesFolder = librariesFolder;
    
    
  }
  

  public void loadComponents() throws FileNotFoundException, 
                                      IOException, 
                                      ClassNotFoundException, 
                                      IllegalAccessException, 
                                      InstantiationException  {
    
    
    // First, load dependencies
    loadLibraries();
    
    File[] files = componentsFolder.listFiles();
    
    // Check if we really have a directory
    if(files == null){
      throw new IOException("The specified components' location cannot be accessed.");
    }
    
    List<URL> jars = new LinkedList<URL>();
    List<String> classNames = new LinkedList<String>();
    

    //
    // Scan each file and store useful information.
    //
    
    for(int i = 0; i < files.length; i++){
      
      File f = files[i];
      
      // Components are stored in jar files
      if(f.getName().endsWith("jar")){
        
        // Load jar
        JarFile jf = new JarFile(f);
        
        // Fetch the component's class
        String className = jf.getManifest().getMainAttributes().getValue("Main-Class");
        classNames.add(className);
        SimulatorUI.instance().getMessenger().printDebugMsg("Found component class: "  + className, Messenger.NORMAL_MSG);
        
        // Remember the Jar's URL
        URL url = f.toURI().toURL();
        jars.add(url);
        
      }
    }

    
    //
    // Setup an appropriate class loader based on the information found.
    //
    
    // We need an array, not a list
    URL[] urls = new URL[jars.size()];
    for(int i = 0; i < jars.size(); i++){
      urls[i] = jars.get(i);
    }
    
    // Create a new class loader, with the current one as the parent
    ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
    URLClassLoader newCL = new URLClassLoader(urls, currentCL);
    
    for(String className: classNames){
     // Register the component
      Class classDefinition = Class.forName(className, true, newCL);
      registry.registerComponent(classDefinition);
    }
    
    // Set the new class loader as the one to be used from now on
    Thread.currentThread().setContextClassLoader(newCL);

  }

  
  public void loadLibraries() throws IOException{
    
    File[] files = librariesFolder.listFiles();
    
    // Check if we really have a directory
    if(files == null){
      throw new IOException("The specified libraries' location cannot be accessed.");
    }
    
    List<URL> jars = new LinkedList<URL>();
    
    // Scan each file
    for(int i = 0; i < files.length; i++){
      
      File f = files[i];
      
      // Components are stored in jar files
      if(f.getName().endsWith("jar")){
        
        SimulatorUI.instance().getMessenger().printDebugMsg("Found library jar: "  + f.getName(), Messenger.NORMAL_MSG);
        
        // Collect the URLs of the jars
        URL url = f.toURI().toURL();
        jars.add(url);
      }
    }
    
    // We need an array, not a list
    URL[] urls = new URL[jars.size()];
    for(int i = 0; i < jars.size(); i++){
      urls[i] = jars.get(i);
    }
    
    // Create a new class loader, with the current one as the parent
    ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
    URLClassLoader newCL = new URLClassLoader(urls, currentCL);
    
    
    
    // Set the new class loader as the one to be used from now on
    Thread.currentThread().setContextClassLoader(newCL);

  }

}
