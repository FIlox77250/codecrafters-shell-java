
@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-23.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo === Java utilisé pour Maven ===
java -version
mvn -v
mvn clean package
pause
