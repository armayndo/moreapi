package com.mitrais.more.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.more.model.ReasonTemplate;

public interface ReasonTemplateRepository extends JpaRepository<ReasonTemplate, Long> {
	List<ReasonTemplate> findByDeleted(boolean deleted);
}
