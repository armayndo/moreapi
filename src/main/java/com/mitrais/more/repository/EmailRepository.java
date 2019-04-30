package com.mitrais.more.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.more.model.EmailTemplate;

public interface EmailRepository extends JpaRepository<EmailTemplate, Long>{
	Optional<EmailTemplate> findByName(String name);
}
