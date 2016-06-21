#!/bin/bash

current_working_path=`pwd`
cd `dirname $0`
script_path=`pwd`
cd $current_working_path
java_home_path="$script_path/jre"

if [[ ! -d "$java_home_path" ]]; then
    echo ""
    echo "Folder \"$java_home_path\" does NOT exist, exit now!" 
    echo ""
    exit
fi


export JAVA_HOME=$java_home_path
export PATH="$JAVA_HOME/bin:$PATH"
#export CLASSPATH=".:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar"
export CLASSPATH=".:$JAVA_HOME/lib"

JAVA_RUN="$java_home_path/bin/java -classpath $CLASSPATH"

ALL_PARAMETERS="$*"


$JAVA_RUN -jar project100.jar $ALL_PARAMETERS





