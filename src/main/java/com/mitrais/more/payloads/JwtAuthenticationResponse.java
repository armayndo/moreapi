package com.mitrais.more.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * This class is used for the authentication response
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {
	/**
	 * HTPP Status Value
	 */
	private int status;
	/**
	 * The Token generated
	 */
	private String accessToken;
	/**
	 * The Token Type
	 */
    private String tokenType;
    /**
     * THe user credentials information
     */
    private String userinfo;
    /**
     * user's role name
     *  
     */
    private String roleName;
    
    public JwtAuthenticationResponse(int status,String accessToken) {
    	this.status = status;
        this.accessToken = accessToken;
    }

}
