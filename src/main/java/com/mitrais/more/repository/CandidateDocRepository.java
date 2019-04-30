package com.mitrais.more.repository;

import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.CandidateDoc;

public interface CandidateDocRepository extends JpaRepository<CandidateDoc, Long>{
}
