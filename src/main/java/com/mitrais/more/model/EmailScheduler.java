package com.mitrais.more.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmailScheduler {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "Please provide a send date time")
	private Date sendDate;
	
	@Nullable
	private String sendTo;
	
	@NotEmpty(message ="Please provide a subject")
	private String subject;
	
	@NotEmpty(message ="Please provide a message")
	@Column(columnDefinition="TEXT")
	private String message;
	
	@Nullable
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private EmailSchedulerStatus status = null;
	
	@JsonIgnore
	@Nullable
	private String createdBy;
	
	@JsonIgnore
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@JsonIgnore
	@Nullable
	private String updatedBy;
	
	@JsonIgnore
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	@Column(columnDefinition="tinyint(1) default 0")
	private boolean deleted;
}
