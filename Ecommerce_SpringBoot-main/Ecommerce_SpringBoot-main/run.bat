@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Cleaning previous build...
call mvn clean

echo Building the project...
call mvn package -DskipTests

echo Running the application...
java --module-path "%JAVA_HOME%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar target/demo-0.0.1-SNAPSHOT.jar
