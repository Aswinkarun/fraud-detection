package com.fraud.engine.controller;

import com.fraud.engine.model.Rule;
import com.fraud.engine.repository.RuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rules")
@CrossOrigin (origins= "*")
public class RuleController 
{

	private final RuleRepository ruleRepository;
	
	@Autowired
	public RuleController (RuleRepository ruleRepository)
	{
		this.ruleRepository = ruleRepository;
	} 
	
	@GetMapping
	public List<Rule> getRules() 
	{
		return ruleRepository.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Rule> addRule(@RequestBody Rule rule) 
	{
		Rule savedRule = ruleRepository.save(rule);
		return new ResponseEntity<>(savedRule, HttpStatus.CREATED);
	} 
	
	@PutMapping("/{id}")
	public Rule updateRule(@PathVariable Long id, @RequestBody Rule updatedRule) 
	{
		Rule rule = ruleRepository.findById(id).orElseThrow(); 
		rule.setColumnName(updatedRule.getColumnName());
		rule.setOperator(updatedRule.getOperator());
		rule.setValue(updatedRule.getValue());
		rule.setActive(updatedRule.isActive());
		return ruleRepository.save(rule);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity <Void> deleteRule (@PathVariable Long id) 
	{
		ruleRepository.deleteById(id);
		System.out.println("Deleting rule with ID: "+ id);
		return ResponseEntity.noContent().build();
	}

}