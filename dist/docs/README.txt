FGS - Formally Guided Simulator
===============================================================================

FGS (together with related components) is the main software produced by
Paulo Salem da Silva for his doctoral thesis. The thesis is entitled

  "Verification of Behaviourist Multi-Agent Systems by means of Formally
  Guided Simulations"
  
and was developed both at the Universidade de São Paulo (in Brazil) and at
Université Paris-Sud 11 (in France). It was successfully defended in
November 28th, 2011.

This software is a proof-of-concept implementation of the theory presented 
in the thesis. As such, it is rather experimental as well, suitable for
research purposes but not for production use. It includes the
main features of the approach proposed in the thesis, and is capable of
running the examples shown there (in Chapter 9). Nevertheless, a number of
non-fundamental features were left out. Care was taken to only exclude
features that could be expressed in terms of those that were included.

FGS is a command-line tool. It takes some files (which specify the multi-agent 
system to simulate, as well as the experiment to run) and parameters as inputs, 
and produces an output according to the requested experiment. For the thesis,
the experiments are all verification procedures, and therefore the output
of FGS is a verdict, indicating whether the multi-agent system has or has not
a certain property. 

This software is best understood by referring to the thesis. For an overview of 
the implementation, see Chapter 8. For a concrete explanation of the input 
format and parameters, see Appendix C. The examples given in Chapter 9 are
provided in this distribution and can be used as templates for 
new specifications.

