package com.mitrais.more.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a job seeker.
 * called candidate after he/she submit to a vacancy.
 * a candidate can be submitted to many vacancy
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Candidate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String email;
	
	@Column(name="birth_date")
	private String birthDate;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	private boolean active;

	@JsonIgnore
	@Column(name="created_by")
	private Long createdBy;
	
	@JsonIgnore
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdDate;
	
	@JsonIgnore
	@Column(name="updated_by")
	private Long updatedBy;
	
	@JsonIgnore
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private Date updatedDate;
	
	@Nullable
	private Long originalCandidateId;
}
