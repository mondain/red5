@echo off

set JMX_OPTS=-Djava.security.manager -Djava.security.policy=conf/red5.policy

set JYTHON_OPTS=-Dpython.home=lib

if not "%JAVA_HOME%" == "" goto launchRed5

:launchRed5
"%JAVA_HOME%/bin/java" %JYTHON_OPTS% %JMX_OPTS% -cp red5.jar;conf;bin org.red5.server.Standalone
goto finaly

:err
echo JAVA_HOME environment variable not set! Take a look at the readme.
pause

:finaly
pause
