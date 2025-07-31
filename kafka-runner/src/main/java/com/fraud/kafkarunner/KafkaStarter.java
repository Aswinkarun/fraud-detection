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


//package com.fraud.kafkarunner;
//
//import java.io.*;
//
//public class KafkaStarter {
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//        System.out.println("Starting Zookeeper...");
//
//        ProcessBuilder zookeeperBuilder = new ProcessBuilder(
//            "E:\\kpmg\\kafka_2.13-3.9.1\\bin\\windows\\zookeeper-server-start.bat",
//            "E:\\kpmg\\kafka_2.13-3.9.1\\config\\zookeeper.properties"
//        );
//        zookeeperBuilder.redirectErrorStream(true);
//        Process zookeeper = zookeeperBuilder.start();
//
//        printStream(zookeeper.getInputStream(), "ZOOKEEPER");
//
//        Thread.sleep(5000); // give zookeeper time to start
//
//        System.out.println("Starting Kafka...");
//
//        ProcessBuilder kafkaBuilder = new ProcessBuilder(
//            "E:\\kpmg\\kafka_2.13-3.9.1\\bin\\windows\\kafka-server-start.bat",
//            "E:\\kpmg\\kafka_2.13-3.9.1\\config\\server.properties"
//        );
//        kafkaBuilder.redirectErrorStream(true);
//        Process kafka = kafkaBuilder.start();
//
//        printStream(kafka.getInputStream(), "KAFKA");
//
//        // Block main thread until Kafka process terminates (keeps the JVM alive)
//        kafka.waitFor();
//        zookeeper.destroy();
//    }
//
//    private static void printStream(InputStream inputStream, String prefix) {
//        new Thread(() -> {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println("[" + prefix + "] " + line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//}
