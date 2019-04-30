package com.mitrais.more.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represent a candidate who has been submitted to a vacancy
 * with a status ONPROCESS, PASSED or REJECTED
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Applicant {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Nullable
	@ManyToOne
	private Vacancy vacancy;
	
	@Nullable
	@ManyToOne
	private Candidate candidate;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "apply_date")
	private Date applyDate;
	
	@Nullable
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private ApplicantStatus status = ApplicantStatus.IN_PROGRESS;
	
	private Long currentSalary;
	
	private Long expectedSalary;
	
	private ArrayList<String> competencies;
	
	@Nullable
	@Column(columnDefinition="TEXT")
	private String comment;
	
	@Nullable
	private String reason;
	
	@Nullable
	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "applicant")
	private List<ApplicantReasonHistory> reasons = new ArrayList<ApplicantReasonHistory>();
	
	@Nullable
	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="applicant")
	private List<CandidateDoc> documents = new ArrayList<CandidateDoc>();
}
