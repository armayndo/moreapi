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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represent candidate documents
 * a candidate submit a vacancy with requirement documents
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CandidateDoc {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String filepath;
	
	@JsonIgnore
	@Column(name="created_by")
	private Long createdBy;
	
	@JsonIgnore
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_at")
	private Date createDate;
	
	@JsonIgnore
	@Column(name="updated_by")
	private Long updatedBy;
	
	@JsonIgnore
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private Date updateDate;
	
}
