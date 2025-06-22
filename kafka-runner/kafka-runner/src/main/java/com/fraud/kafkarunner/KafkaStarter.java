package com.fraud.kafkarunner;

import java.io.IOException;

public class KafkaStarter {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting Zookeeper and Kafka...");

        ProcessBuilder zookeeper = new ProcessBuilder(
            "cmd.exe", "/c", 
            "start", "C:\\kafka\\bin\\windows\\zookeeper-server-start.bat", 
            "C:\\kafka\\config\\zookeeper.properties"
        );
        zookeeper.start();

        Thread.sleep(5000); // wait for Zookeeper

        ProcessBuilder kafka = new ProcessBuilder(
            "cmd.exe", "/c", 
            "start", "C:\\kafka\\bin\\windows\\kafka-server-start.bat", 
            "C:\\kafka\\config\\server.properties"
        );
        kafka.start();

        System.out.println("Kafka and Zookeeper started.");
    }
}
