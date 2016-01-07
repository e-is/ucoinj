#!/bin/bash

#Comment out this line to specify your JAVA path:
export JAVA_HOME=/usr/lib/jvm/default-java

export APP_BASEDIR=$(pwd)
export JAVA_COMMAND=$JAVA_HOME/bin/java
export APP_LOG_FILE=$APP_BASEDIR/data/${project.artifactId}-${project.version}.log

cd $APP_BASEDIR

if [ -d $JAVA_HOME ]; then
	echo "${project.name}"
	echo "  basedir:  $APP_BASEDIR"
	echo "  jre home: $JAVA_HOME"
	echo "  log file: $APP_LOG_FILE"
	
	MEMORY="-Xmx1G"
	#APP_JVM_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000"
	
	REP=$(dirname $0)
	
	cd $REP
	
	echo "launch java"
	echo "java command: $JAVA_COMMAND"
	
	$JAVA_COMMAND $MEMORY $APP_JVM_OPTS -Ducoinj.log.file=$APP_LOG_FILE -jar ${project.build.finalName}.${project.packaging} $*
	exitcode=$?
	echo "Stop ${project.name} with exitcode: $exitcode"
	exit $exitcode
	
else
	echo "Java not detected ! Please set environment variable JAVA_HOME before launching,"
	echo "or edit the file 'launch.sh' and insert this line :"
	echo "                                                     export JAVA_HOME=<path_to_java>"
fi

