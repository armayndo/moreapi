package com.mitrais.more.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a user enrolled in the office.
 * each user has a role.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NonNull
	@NotEmpty(message = "*Please provide a Username")
	private String username;
	
	@NonNull
	@Length(min = 5, message = "*Your password must have at least 5 characters")
	@NotEmpty(message = "*Please provide a Password")
	@JsonIgnore
	private String password;
	
	@NonNull
	@Email(message = "*Please provide a valid Email")
	@NotEmpty(message = "*Please provide an email")
	private String email;
	
	@NonNull
	@NotEmpty(message = "*Please provide a Name")
	private String name;
	
	@JsonIgnore
	@Column(name = "created_by")
	private Long createdBy;
	
	@JsonIgnore
	@Column(name = "updated_by")
	private Long updatedBy;
	
	@JsonIgnore
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdDate;
	
	@JsonIgnore
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private Date updatedDate;
	
	/*
	 * Used to indicate whether the user is active or inactive for ....
	 */
	private boolean active;
	
	/** Set user whether the user wants to receive email notification when applicant submit a vacancy. */
	@Column(columnDefinition="tinyint(1) default 0")
	private boolean sendMeEmail;

	/*
	 * Used to give permission for user to perform a group of tasks
	 */
	@Nullable
	@OneToOne
	private Role role;
}
