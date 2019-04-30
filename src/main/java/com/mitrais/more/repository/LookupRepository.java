package com.mitrais.more.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.more.model.Lookup;

public interface LookupRepository extends JpaRepository<Lookup, Long> {
	List<Lookup> findByName(String name);
	Lookup findByNameAndKeyword(String name, String keyword);
}
