#!/bin/bash 
#
# Runs examples using FGS.



###############################################################################
# Script functions
###############################################################################

run()
{
  echo ./fgs.sh -s "\"$1\"" -e "\"$2\"" 
  ./fgs.sh -s "$1" -e "$2" -verbose 2 -max-depth 100 -max-synch-steps 300
}

# Example
run_example_dog()
{
  scenario="examples/EMMAS Dog/dog.emmas.scenario.xml"
  experiment="examples/EMMAS Dog/dog.simpurpverif.experiment.xml"
  run "$scenario" "$experiment"
  
}

# Example
run_example_osn()
{
  scenario="examples/EMMAS Online Social Network/osn.emmas.scenario.xml"
  experiment="examples/EMMAS Online Social Network/osn.simpurpverif.experiment.xml"
  run "$scenario" "$experiment"
}


# Example
run_example_factory()
{
  scenario="examples/EMMAS Factory/factory.emmas.scenario.xml"
  experiment="examples/EMMAS Factory/factory.simpurpverif.experiment.xml"
  run "$scenario" "$experiment"
}

# Example
run_example_behavior_elimination()
{
  scenario="examples/EMMAS Behavior Elimination/elimination.emmas.scenario.xml"
  experiment="examples/EMMAS Behavior Elimination/elimination.simpurpverif.experiment.xml"
  run "$scenario" "$experiment"
}

# Example
run_example_operant_chaining()
{
  scenario="examples/EMMAS Operant Chaining/worker.emmas.scenario.xml"
  experiment="examples/EMMAS Operant Chaining/worker.simpurpverif.experiment.xml"
  run "$scenario" "$experiment"
}

# Example
run_example_school()
{
  scenario="examples/EMMAS School/school.emmas.scenario.xml"
  experiment="examples/EMMAS School/school.simpurpverif.experiment.xml"
  run "$scenario" "$experiment"

}


print_usage()
{
  echo "FGS - Formally Guided Simulator"
  echo "Examples Script"
  echo ""
  echo "Usage: $0 EXAMPLE"
  echo ""
  echo "   where the EXAMPLE parameter is one of the following:"
  echo ""
  echo "   dog          A classical conditioning example involving just one dog."
  echo "   chaining     Operant chaining in a worker."
  echo "   elimination  Behavior elimination in a problem child."
  echo "   factory      How to setup managers and workers so that production achieves" 
  echo "                desirable level?"
  echo "   school       Investigates discipline in school children."
  echo "   osn          Marketing in an online social network."
  echo ""
  echo ""
  echo "Note: You may wish to modify this script in order to employ different parameters"
  echo        "when calling FGS. In particular, it may be necessary to edit the function"
  echo        "run() to define larger values for parameters -max-depth and" 
  echo        "-max-synch-steps in order to avoid inconclusive verdicts."
  echo ""
}


###############################################################################
# Main body of the script
###############################################################################

if [[ $# != 1 ]]; then
  print_usage
else

  case "$1" in
    'dog')
    run_example_dog
    ;;
    'chaining')
    run_example_operant_chaining
    ;;
    'elimination')
    run_example_behavior_elimination
    ;;
    'factory')
    run_example_factory
    ;;
    'school')
    run_example_school
    ;;
    'osn')
    run_example_osn
    ;;
    *)
    echo "Invalid choice."
    print_usage
    ;;
  esac
fi

