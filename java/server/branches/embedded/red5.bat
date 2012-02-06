@echo off

SETLOCAL

if NOT DEFINED RED5_HOME set RED5_HOME=%~dp0

if NOT DEFINED RED5_MAINCLASS set RED5_MAINCLASS=org.red5.server.Bootstrap

if NOT DEFINED JAVA_HOME goto err

REM Set up logging options
set LOGGING_OPTS=-Dlogback.ContextSelector=org.red5.logging.LoggingContextSelector -Dcatalina.useNaming=true
REM Set up security options
set SECURITY_OPTS=-Djava.security.debug=failure
REM JAVA options
set JAVA_OPTS=%LOGGING_OPTS% %SECURITY_OPTS% -Dred5.root=%RED5_HOME%

set RED5_CLASSPATH=%RED5_HOME%boot.jar;%RED5_HOME%conf;.

if NOT DEFINED RED5_OPTS set RED5_OPTS= 

goto launchRed5

:launchRed5
echo Starting Red5
"%JAVA_HOME%\bin\java" %JAVA_OPTS% -cp "%RED5_CLASSPATH%" %RED5_MAINCLASS% %RED5_OPTS%
goto finally

:err
echo JAVA_HOME environment variable not set! Take a look at the readme.
pause

:finally
ENDLOCAL
