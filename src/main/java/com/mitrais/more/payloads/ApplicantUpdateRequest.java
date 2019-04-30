package com.mitrais.more.payloads;

import java.util.Date;

import com.mitrais.more.model.Applicant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantUpdateRequest {
	private Applicant applicant;
	private Date sendNotificationDate;
}
