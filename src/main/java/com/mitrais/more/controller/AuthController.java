package com.mitrais.more.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.mitrais.more.payloads.JwtAuthenticationResponse;
import com.mitrais.more.payloads.LoginRequest;
import com.mitrais.more.security.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/*
 * This class is used for user authentication.
 * It contains only on method.
 * The method operate user authentication and return unique token
 * throw an exception if the user unauthorized
 * 
 */
@Api(description = "User Authentication Api")
@RestController
@RequestMapping(AuthController.BASE_URL)
public class AuthController {
	/**
	 * Base URL for Authentication API
	 */
	public static final String BASE_URL = "/api/v1/auth";
	/**
	 * AuthenticationManager class used to authenticate user and password
	 * and set them to UserPrincipal if passed
	 */
    private AuthenticationManager authenticationManager;
    /**
     * Used to generate a token, if user has authorized
     */
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}

    /**
     * This method is used to authenticate the user
     * 
     * @param loginRequest contains user name and password {@link LoginRequest} 
     * authenticate the user name and password with AuthenticationManager by UsernamePasswordAuthenticationToken
     * generate token from JwtTokenProvider
     * 
     * @return JwtAuthenticationResponse whether it's success or not
     */
	@ApiOperation(value = "This API will generate access token.")
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public JwtAuthenticationResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    	try {
    		Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserParam(),
                            loginRequest.getPassParam()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            
            return new JwtAuthenticationResponse(200,jwt,"Bearer", authentication.getName(), authentication.getAuthorities().toArray()[0].toString());
		} catch (BadCredentialsException e) {
            return new JwtAuthenticationResponse(401,null);
		}
        
    }
}
