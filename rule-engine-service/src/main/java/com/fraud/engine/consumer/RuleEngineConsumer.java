package com.fraud.engine.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class RuleEngineConsumer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public RuleEngineConsumer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "transaction-topic", groupId = "fraud-group")
    public void consume(String message) throws JsonProcessingException 
    {
    	System.out.println("Received transaction: " + message);
    	
        Map<String, Object> transaction = mapper.readValue(message, Map.class);

        int rulesViolated = 0;
        boolean isFraud = false;
        List<String> violatedRules = new ArrayList<>();

        double amount = Double.parseDouble(transaction.get("amount").toString());
        double cardUsage = Double.parseDouble(transaction.get("cardUsage").toString());
        double age = Double.parseDouble(transaction.get("age").toString());

        if (amount > 50000) {
            rulesViolated++;
            violatedRules.add("Amount > 50000");
        }
        if (cardUsage > 10) {
            rulesViolated++;
            violatedRules.add("Card Usage > 10");
        }
        if (age > 60) {
            rulesViolated++;
            violatedRules.add("Age > 60");
        }

        if (rulesViolated >= 2) isFraud = true;

        System.out.println("Transaction: " + transaction.get("transactionID"));
        System.out.println("Fraud Detected: " + isFraud);
        System.out.println("Rules Violated: " + rulesViolated);
        System.out.println("Violated Rules: " + violatedRules);
        System.out.println("--------------");

        Map<String, Object> result = new HashMap<>();
        result.put("transactionID", transaction.get("transactionID"));
        result.put("isFraud", isFraud);
        result.put("rulesViolated", rulesViolated);
        result.put("violatedRules", violatedRules);  // NEW FIELD

        kafkaTemplate.send("fraud-result", mapper.writeValueAsString(result));
    }
}
