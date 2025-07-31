package com.fraud.engine.repository;

import com.fraud.engine.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, Long>
{
	List<Rule> findByActiveTrue();
}
