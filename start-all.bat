@echo off
title Fraud Detection System Starter

echo Starting Zookeeper and Kafka...
start "Kafka Service" java -cp kafka-runner\target\classes com.fraud.kafkarunner.KafkaStarter
timeout /t 5 /nobreak > NUL

echo Starting Excel Service...
if exist "excel-service\target\excel-service-0.0.1-SNAPSHOT.jar" (
    start "Excel Service" java -jar excel-service\target\excel-service-0.0.1-SNAPSHOT.jar
) else (
    echo [ERROR] Excel service JAR not found.
)

timeout /t 2 /nobreak > NUL

echo Starting Rule Engine Service...
if exist "rule-engine-service\target\rule-engine-service-0.0.1-SNAPSHOT.jar" (
    start "Rule Engine Service" java -jar rule-engine-service\target\rule-engine-service-0.0.1-SNAPSHOT.jar
) else (
    echo [ERROR] Rule Engine service JAR not found.
)

timeout /t 2 /nobreak > NUL

echo Starting UI Service...
if exist "ui-service\target\ui-service-0.0.1-SNAPSHOT.jar" (
    start "UI Service" java -jar ui-service\target\ui-service-0.0.1-SNAPSHOT.jar
) else (
    echo [ERROR] UI service JAR not found.
)

echo.
echo ✅ All services launched in new windows.
pause
