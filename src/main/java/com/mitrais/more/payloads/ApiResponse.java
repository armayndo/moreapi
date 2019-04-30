package com.mitrais.more.payloads;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import com.mitrais.more.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*
 * This class used to be the response of the application
 * status to return the response status (e.g. 200, 401, etc)
 * message to return the response message (e.g. success, Invalid Username or Password!, etc)
 * and use JWTAuthenticationResponse for authentication response
 */

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	/**
	 * HTTP Status value
	 */
	@NonNull
	private int status;
	/**
	 * Response Message 
	 */
	@NonNull
	private String message;
	/**
	 * If any return with a List type
	 * Used to display a list of model result
	 */
	@Nullable
	private List<T> result;
}
