package com.fraud.engine.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RuleEngineService {

    public Map<String, Object> evaluateRules(Map<String, Object> transaction, List<Map<String, String>> rulesFromDb) {
        int rulesViolated = 0;
        boolean isFraud = false;
        List<String> violatedRules = new ArrayList<>();
        
        System.out.println("Transaction Map: " + transaction);

        for (Map<String, String> rule : rulesFromDb) 
        {
        	System.out.println("Evaluating Rule: " + rule);
        	
            String column = rule.get("columnName");
            String operator = rule.get("operator");
            String value = rule.get("value");
            try 
            {
	            Object transactionObj = transaction.get(column);
	            if (transactionObj == null) continue;
	            
	            String transactionValueStr = transactionObj.toString();
	            
	            boolean violated;
	
	            try {
	                violated = false;
	
	                    double transactionValue = Double.parseDouble(transactionValueStr);
	                    double ruleValue = Double.parseDouble(value);
	
	                    violated = switch (operator) {
	                        case ">" -> transactionValue > ruleValue;
	                        case "<" -> transactionValue < ruleValue;
	                        case ">=" -> transactionValue >= ruleValue;
	                        case "<=" -> transactionValue <= ruleValue;
	                        case "==" -> transactionValue == ruleValue;
	                        default -> false;
	                    };
	                } catch(NumberFormatException e) {
	                    // Handle string comparison
	                	violated = switch(operator)
	                			{
	                	case "==" -> transactionValueStr.equalsIgnoreCase(value);
	                	case "!=" -> !transactionValueStr.equalsIgnoreCase(value);
	                	default -> false;
	                };
                }

                if (violated) 
                {
                    rulesViolated++;
                    violatedRules.add(column + " " + operator + " " + value);
                }

            } catch (Exception e) {
                System.out.println("Error processing rule: " + rule + " - " + e.getMessage());
            }
        }


        if (rulesViolated >= 2) {
            isFraud = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("transactionID", transaction.get("transactionID"));
        result.put("isFraud", isFraud);
        result.put("rulesViolated", rulesViolated);
        result.put("violatedRules", violatedRules);
        result.put("name", transaction.get("name"));
        result.put("accountNumber", transaction.get("accountNumber"));
        result.put("amount", transaction.get("amount"));
        result.put("mode", transaction.get("mode"));
        result.put("cardUsage", transaction.get("cardUsage"));
        result.put("age", transaction.get("age"));

        return result;
    }

}