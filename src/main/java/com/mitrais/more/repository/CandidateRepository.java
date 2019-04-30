package com.mitrais.more.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mitrais.more.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long>{
	List<Candidate> findByActive(boolean active);

	Optional<Candidate> findByEmail(String email);
	
	@Modifying(clearAutomatically=true)
	@Query("UPDATE Candidate c SET c.active = :active WHERE c.id = :candidateId")
	int softDeleteById(@Param("candidateId") Long candidateId, @Param("active") boolean active);

}
