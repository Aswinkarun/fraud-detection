package com.fraud.engine.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class RuleEngineConsumer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public RuleEngineConsumer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "transaction-topic", groupId = "fraud-group")
    public void consume(String message) throws JsonProcessingException {
        Map<String, Object> transaction = mapper.readValue(message, Map.class);

        int satisfiedRules = 0;
        boolean isFraud = false;

        double amount = Double.parseDouble(transaction.get("amount").toString());
        double cardUsage = Double.parseDouble(transaction.get("cardUsage").toString());
        double age = Double.parseDouble(transaction.get("age").toString());

        if (amount > 50000) satisfiedRules++;
        if (cardUsage > 10) satisfiedRules++;
        if (age > 60) satisfiedRules++;

        if (satisfiedRules >= 2) isFraud = true;

        // Log to console (optional)
        System.out.println("User: " + transaction.get("name"));
        System.out.println("Fraud Detected: " + isFraud);
        System.out.println("Rules Passed: " + satisfiedRules);
        System.out.println("--------------");

        // Create output map
        Map<String, Object> result = new HashMap<>();
        result.put("name", transaction.get("name"));
        result.put("isFraud", isFraud);
        result.put("rulesPassed", satisfiedRules);

        // Send result to Kafka topic "fraud-result"
        kafkaTemplate.send("fraud-result", mapper.writeValueAsString(result));
    }
}
