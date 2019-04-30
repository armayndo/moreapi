package com.mitrais.more.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Represent a template email 
 * for sending to candidate or recruiter 
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EmailTemplate {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String subject;
	@Column(columnDefinition="TEXT")
	private String body;
	
	@Builder.Default
	private Integer daysSend = 0;
	
	@JsonIgnore
	private Long createdBy;
	@JsonIgnore
	private Long updatedBy;
	
	@JsonIgnore
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@JsonIgnore
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	
}
