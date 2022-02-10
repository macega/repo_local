@echo off
 
if NOT "%JDBC_HOME%" == "" goto checknextvar
echo You need to set JDBC_HOME before building the samples
goto end

:checknextvar
if NOT "%JAVA_HOME%" == "" goto build_all
echo You need to set JAVA_HOME before building the samples
goto end

:build_all
set JAVA_CMD=javac -d "%JDBC_HOME%\classes" -classpath "%JDBC_HOME%\classes\jconn4.jar;%JDBC_HOME%\classes"

REM build all files
%JAVA_CMD% *.java

REM re-build Address with a JDK 1.1 compatible class
%JAVA_CMD% Address.java

goto end 

:end

