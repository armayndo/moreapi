package com.mitrais.more.payloads;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * This class is used to do authentication from user's username and password
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	@NotBlank
	private String userParam;

	@NotBlank
	private String passParam;

}
