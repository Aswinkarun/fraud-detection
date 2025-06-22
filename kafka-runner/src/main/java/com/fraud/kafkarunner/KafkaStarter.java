package com.fraud.kafkarunner;

import java.io.IOException;

public class KafkaStarter {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting Zookeeper and Kafka...");

        ProcessBuilder zookeeper = new ProcessBuilder(
            "cmd.exe", "/c",
            "start", "E:\\kpmg\\kafka_2.13-3.9.1\\bin\\windows\\zookeeper-server-start.bat",
            "E:\\kpmg\\kafka_2.13-3.9.1\\config\\zookeeper.properties"
        );
        zookeeper.start();

        Thread.sleep(5000); // wait for Zookeeper to start

        ProcessBuilder kafka = new ProcessBuilder(
            "cmd.exe", "/c",
            "start", "E:\\kpmg\\kafka_2.13-3.9.1\\bin\\windows\\kafka-server-start.bat",
            "E:\\kpmg\\kafka_2.13-3.9.1\\config\\server.properties"
        );
        kafka.start();

        System.out.println("Kafka and Zookeeper started.");
    }
}
