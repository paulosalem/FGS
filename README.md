FGS - Formally Guided Simulator
===============================================================================

Formally Guided Simulator's (FGS) sources and executables. 

FGS (together with related components) is the main software produced by Paulo Salem da Silva for his doctoral thesis. The thesis is entitled

    "Verification of Behaviourist Multi-Agent Systems by means of Formally Guided Simulations"
  
and was developed both at the Universidade de São Paulo (in Brazil) and at Université Paris-Sud 11 (in France). It was successfully defended in November 28th, 2011. You can find downloadable copies of the thesis at the folowing links:

  
  http://tel.archives-ouvertes.fr/tel-00656809/
  
  http://www.teses.usp.br/teses/disponiveis/45/45134/tde-10042012-100539/pt-br.php


This software is a proof-of-concept implementation of the theory presented in the thesis. As such, it is rather experimental as well, suitable for research purposes but not for production use. It includes the main features of the approach proposed in the thesis, and is capable of running the examples shown there (in Chapter 9). Nevertheless, a number of non-fundamental features were left out. Care was taken to only exclude features that could be expressed in terms of those that were included.

FGS is a command-line tool. It takes some files (which specify the multi-agent system to simulate, as well as the experiment to run) and parameters as inputs, and produces an output according to the requested experiment. For the thesis, the experiments are all verification procedures, and therefore the output of FGS is a verdict, indicating whether the multi-agent system has or has not
a certain property. 

This software is best understood by referring to the thesis. For an overview of the implementation, see Chapter 8. For a concrete explanation of the input format and parameters, see Appendix C. The examples given in Chapter 9 are provided in this distribution and can be used as templates for new specifications.

If you need help, would like to make comments or even built upon my programs, please feel free to contact me (at paulosalem@paulosalem.com). Since this is an experimental system, built irregularly over years, and subject to various changes of mind at different moments, you are sure to find loose ends, strange design decisions and a few bugs that still need to be fixed. I'd be glad to help you try to get through them. However, I am quite tired of _programming_ this system, so I can only offer advice! Also notice that you can replicate my main algorithms independently by following the descriptions given in the thesis. In fact, if you do so, I advise you to choose a non-garbage collected language, as FGS has some memory issues, possibly because of GC (it is implemented in Java).

This is the overall proof-of-concept software that goes along with the thesis. If you wish to run the examples given there this is the only thing you need. However, FGS itself is built from more fundamental parts, whose sources have been given independent repositores for the sake of organization:

  https://github.com/paulosalem/behaviorist_agent_architecture
  
  https://github.com/paulosalem/behaviorist_agent_architecture_component
  
  https://github.com/paulosalem/ALEVOS

In particular, the verification algorithms are implemented in the ALEVOS library.



Installation Instructions
-------------------------------------------------------------------------------


### System Requirements ###


FGS is developed entirely in Java SE 6, and therefore depends only on the presence of an appropriate JRE. It can be obtained, for example, here:
  
  http://www.oracle.com/technetwork/java/javase/downloads/index.html
     


### Setup Components Repository and Libraries Folder ###


FGS expects two special folders to be defined in the user's machine, namely:
    
  * The Components Repository: Where simulation model components can be
    dropped for later use in simulations. By default, it is set to
    (user's home folder)/simulation-components/
        
  * The Libraries Folder: Where any libraries required by simulation
    model components can be dropped. By default, is set to
    (user's home folder)/simulation-libraries/
        

Both locations can be changed by editing the appropriate entries in the preferences.xml file, located in the root of the FGS distribution.



### Run FGS ###

FGS' distribution can be found under the /dist folder. The main executable comes packed in a JAR file, which means it can be run by simply invoking


  ```shell  
    java -jar simulator.jar
  ```
    
This distribution, however, comes with (Unix) shell scripts to facilitate the customization of this invocation (e.g., to change the virtual machine's  parameters):
  
  * fgs.sh: Invokes FGS with custom JVM parameters. You may tweak these parameters to suit your needs.
  * examples.sh: Invokes particular FGS simulation examples that come with the distribution.
  
  


License
-------------------------------------------------------------------------------

FGS - Formally Guided Simulator
 
Copyright (c) 2008 - 2012, Paulo Salem da Silva.
All rights reserved.
  
This software may be used, modified and distributed freely, provided that the 
following rules are followed:
  
  (i)   this copyright notice must be maintained in any redistribution, in both 
          original and modified form,  of this software;
          
  (ii)  this software must be provided free of charge, although services which 
          require the software may be charged;
          
  (iii) for non-commercial purposes, this software may be used, modified and 
          distributed free of charge;
          
  (iv)  for commercial purposes, only the original, unmodified, version of this 
          software may be used.
  
For other uses of the software, please contact the author (at paulosalem@paulosalem.com).
