package com.mitrais.more.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateRequest {
	private String name;
	private String email;
	private String birthDate;
	private String phoneNumber;
	private String address;
	
}
