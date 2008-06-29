#!/bin/bash

# JMX options
JMX_OPTS="-Djava.security.manager -Djava.security.policy=conf/red5.policy"

# Debug options
JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y"

# Jython options
JYTHON="-Dpython.home=lib"

for JAVA in "$JAVA_HOME/bin/java" "/usr/bin/java" "/usr/local/bin/java"
do
  if [ -x $JAVA ]
  then
    break
  fi
done

if [ ! -x $JAVA ]
then
  echo "Unable to locate java. Please set JAVA_HOME environment variable."
  exit
fi

# Start Red5
exec $JAVA $JMX_OPTS $JAVA_OPTS $JYTHON -cp red5.jar:conf:$CLASSPATH org.red5.server.Standalone
