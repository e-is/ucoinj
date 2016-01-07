@echo off

REM Comment out this line to specify your JAVA path:
REM SET JAVA_HOME=<path_to_java>


set OLDDIR=%CD%
cd /d %~dp0%

set APP_BASEDIR=%CD%
set JAVA_COMMAND=%JAVA_HOME%\bin\java
set APP_LOG_FILE=%APP_BASEDIR%\data\${project.artifactId}-${project.version}.log
set JAVA_OPTS=-Xmx1G

if not exist "%JAVA_HOME%" goto no_java

echo ===============================================================================
echo .
echo   ${project.name}
echo .
echo   JAVA: %JAVA_COMMAND%
echo .
echo   JAVA_OPTS: %JAVA_OPTS%
echo .
echo   log file: %APP_LOG_FILE%
echo .
echo ===============================================================================
echo .


set OLDDIR=%CD%
cd /d %~dp0%

call "%JAVA_COMMAND%" %JAVA_OPTS% "-Ducoinj.log.file=%APP_LOG_FILE%" -jar ${project.build.finalName}.${project.packaging} %1 %2 %3 %4 %5 %6 %7 %8 %9
set exitcode=%ERRORLEVEL%
echo Stop with exitcode: %exitcode%
cd %OLDDIR%
exit /b %exitcode%
goto end

no_java:
echo "Java not detected ! Please set environment variable JAVA_HOME before launching,"
echo "or edit the file 'launch.bat' and insert this line :"
echo " SET JAVA_HOME=<path_to_java>"

:end
