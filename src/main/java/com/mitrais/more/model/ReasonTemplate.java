package com.mitrais.more.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Represent Reason data
 * used as an example for reason text to candidate 
 * that has been rejected
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReasonTemplate {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message="Please provide a name")
	private String name;
	
	@NotNull(message="Please fill the reason content")
	@Column(columnDefinition="TEXT")
	private String content;
	
	@JsonIgnore
	private String createdBy;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date createdAt;
	
	@JsonIgnore
	private String updatedBy;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date updatedAt;
	
	@Column(columnDefinition="tinyint(1) default 0")
	private boolean deleted;
}
