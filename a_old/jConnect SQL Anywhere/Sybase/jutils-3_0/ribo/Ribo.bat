@echo off
if "%RIBO_HOME%" == "" goto set_ribo

:buildargs
rem build the command line arguments
set riboParams=%1
:getparam
shift
if "%1" == "" goto doneWithParams
set riboParams=%riboParams% %1
goto getparam
:doneWithParams

cd "%RIBO_HOME%"

if not "%JAVA_HOME%" == "" goto java_vm
if exist "%SYSTEMROOT%\jview.exe" goto msnt_vm
if exist "%windir%\jview.exe" goto ms95_vm
:no_vm_found
    echo You need to install one of the following Java VMs:
    echo - the JDK, and set JAVA_HOME to the directory where java.exe is    
    echo - the JRE, and set JAVA_HOME to the directory where jre.exe is
    echo - the SDK, and be sure jview.exe is installed
    goto end
:java_vm
if exist "%JAVA_HOME%\lib\tools.jar" goto go_java12
if exist "%JAVA_HOME%\jre\bin\java.exe" goto go_jre2
if exist "%JAVA_HOME%\bin\java.exe" goto go_jdk 
if exist "%JAVA_HOME%\bin\jre.exe" goto go_jre
:error
    echo %JAVA_HOME% is not a recognized Java installation. Make sure JAVA_HOME is correctly set.    
    goto end
:go_java12
    "%JAVA_HOME%\bin\java" -classpath "%RIBO_HOME%\ribo.jar;." com.sybase.ribo.RiboMgr %riboParams%
    goto end
:go_jdk
    "%JAVA_HOME%\bin\java" -classpath "%RIBO_HOME%\ribo.jar;%JAVA_HOME%\lib\classes.zip;." com.sybase.ribo.RiboMgr %riboParams%
    goto end 
:go_jre    
    "%JAVA_HOME%\bin\jre" -classpath "%RIBO_HOME%\ribo.jar;%JAVA_HOME%\lib\rt.jar;%JAVA_HOME%\lib\i18n.jar" com.sybase.ribo.RiboMgr %riboParams%
    goto end
:go_jre2    
    "%JAVA_HOME%\jre\bin\java" -classpath "%RIBO_HOME%\ribo.jar;%JAVA_HOME%\jre\lib\rt.jar;%JAVA_HOME%\jre\lib\i18n.jar" com.sybase.ribo.RiboMgr %riboParams%
    goto end
:msnt_vm
    "%SYSTEMROOT%\jview" /cp "%RIBO_HOME%\ribo.jar"   com.sybase.ribo.RiboMgr %riboParams%
    goto end
:ms95_vm
    jview /cp "%RIBO_HOME%\ribo.jar" com.sybase.ribo.RiboMgr %riboParams%
    goto end
:set_ribo
    rem Try the jConnect directory if it exists.
    if "%JDBC_HOME%" == "" goto no_ribo
    set RIBO_HOME="%JDBC_HOME%\utils"
    if exist "%RIBO_HOME%\ribo.jar" goto buildargs
    rem No ribo-home, and the jdbc-home directory doesn't have ribo either, so
    rem just give up. We don't complain about the latter case, though.
    goto no_ribo
:no_ribo
    echo You must set RIBO_HOME variable in order to run Ribo
    goto end
:end
set riboParams=
