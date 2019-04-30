package com.mitrais.more.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.ApplicantStatus;
import com.mitrais.more.model.Candidate;

public interface ApplicantRepository extends JpaRepository<Applicant, Long>{
	List<Applicant> findByCandidate(Candidate candidate);
	List<Applicant> findByStatus(ApplicantStatus status);
	Optional<Applicant> findByCandidateEmailAndVacancyId(String candidateEmail, Long vacancyId);
}
