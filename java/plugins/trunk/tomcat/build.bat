rmdir /S /Q target

SETLOCAL

SET JAVA_HOME=e:\dev\java6_32
SET PATH=e:\dev\java6_32\bin;%PATH%

REM  build default
REM mvn 

REM  build the jar version
REM mvn compile jar:jar

REM resume a build after modifying project files
mvn clean install -rf :tomcatplugin

REM to skip tests add -DskipTests
REM mvn install -DskipTests

ENDLOCAL