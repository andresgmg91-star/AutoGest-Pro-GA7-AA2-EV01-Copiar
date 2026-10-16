@echo off
title AutoGest Pro - Gestion de Clientes

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "MAVEN_HOME=C:\Users\JULIAN\Downloads\apache-maven-3.9.16"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo Iniciando AutoGest Pro...
mvn exec:java

echo.
echo El programa ha finalizado.
pause