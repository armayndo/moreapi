package com.mitrais.more.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.more.model.Vacancy;

public interface VacancyRepository extends JpaRepository<Vacancy, Long>{
	List<Vacancy> findByActive(boolean active); 
	List<Vacancy> findByIdIn(List<Long> vacancyIdList);
	List<Vacancy> findByDeleted(boolean deleted); 
}
