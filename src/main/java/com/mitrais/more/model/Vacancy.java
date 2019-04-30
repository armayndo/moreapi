package com.mitrais.more.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a vacancy in the office.
 * recruiter(User) has authority to create vacancy.
 * And a vacancy can be enrolled by many candidate
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vacancy {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message ="Please provide a name")
	private String position;
	
	@NotEmpty(message = "Please provide a location")
	private String location;
	
	@Column(columnDefinition="TEXT")
	private String description;
	
	@ManyToOne
	private JobFunction jobFunction;
	
	private boolean active;
	
	@JsonIgnore
	@Column(name="created_by")
	private String createdBy;
	
	@JsonIgnore
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private Date updatedDate;
	
	@Column(columnDefinition="tinyint(1) default 0")
	private boolean deleted;
}
