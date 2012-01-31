@echo off

SETLOCAL

SET JAVA_HOME=e:\dev\java6
SET PATH=e:\dev\java6\bin;%PATH%

ant clean dist

ENDLOCAL

pause
