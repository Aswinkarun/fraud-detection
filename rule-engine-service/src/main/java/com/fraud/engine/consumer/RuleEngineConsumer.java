package com.fraud.engine.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.engine.model.Rule;
import com.fraud.engine.repository.RuleRepository;
import com.fraud.engine.service.RuleEngineService;

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
    private final RuleRepository ruleRepository;
    private final RuleEngineService ruleEngineService;

    public RuleEngineConsumer(KafkaTemplate<String, String> kafkaTemplate, RuleRepository ruleRepository, RuleEngineService ruleEngineService) 
    {
        this.kafkaTemplate = kafkaTemplate;
        this.ruleRepository = ruleRepository;
        this.ruleEngineService = ruleEngineService;
    }

    @KafkaListener(topics = "transaction-topic", groupId = "fraud-group")
    public void consume(String message) throws JsonProcessingException 
    {
    	System.out.println("Received transaction: " + message);
    	
        Map<String, Object> transaction = mapper.readValue(message, Map.class);
        
        List<Rule> dbRules = ruleRepository.findByActiveTrue();

        List<Map<String, String>> ruleList = new ArrayList<>();
        for(Rule r : dbRules)
        {
        	Map<String, String> ruleMap = new HashMap<>();
        	ruleMap.put("columnName", r.getColumnName());
        	ruleMap.put("operator", r.getOperator());
        	ruleMap.put("value", r.getValue());
        	ruleList.add(ruleMap);
        }
        Map<String, Object> result = ruleEngineService.evaluateRules(transaction, ruleList);
        
        kafkaTemplate.send("fraud-result", mapper.writeValueAsString(result));
    }
}
