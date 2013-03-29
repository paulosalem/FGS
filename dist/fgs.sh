#!/bin/bash 
#
# Runs FSG with the given arguments using appropriate JVM parameters.

#
# VM options
#
VM_OPTS="-Xms1000m -Xmx2000m -XX:+AggressiveHeap"

# We are assuming that the classpath is set within the simulator's JAR manifest file.

java $VM_OPTS -jar simulator.jar "$@"

