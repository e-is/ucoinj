@echo off
SET MAVEN_OPTS=-Xms128m -Xmx256m -XX:PermSize=64M -XX:MaxPermSize=128M
SET JAVA_HOME=C:\Program Files\Java\jdk1.7.0_45

REM CALL mvn install -f batch/pom.xml -Dmaven.test.skip=true -Plocalhost-gepeto -Ppgsql -e -Denv=pgsql

CALL mvn jetty:stop -f ui/pom.xml

CALL mvn install --non-recursive

CALL mvn install -f core/pom.xml -DskipTests

SET MAVEN_OPTS=-Xms128m -Xmx256m -XX:PermSize=64M -XX:MaxPermSize=128M -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n
CALL mvn jetty:run -f ui/pom.xml -DskipTests -e
