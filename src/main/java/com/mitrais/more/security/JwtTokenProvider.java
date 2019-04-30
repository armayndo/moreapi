package com.mitrais.more.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
/*
 * This class is used to manage the user token
 */
@Component
public class JwtTokenProvider {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	/*
	 * Secret key for the specified algorithm to produce a JWS
	 * the value is set in application.properties
	 */
	 @Value("${app.jwtSecret}")
	 private String jwtSecret;

	 /*
	  * Used for logged in expiration time
	  * the value is set in application.properties
	  */
	 @Value("${app.jwtExpirationInMs}")
	 private int jwtExpirationInMs;
	 
	 /*
	  * This method generate token for user when user logged in
	  * set subject with the userPrincipal id
	  * set issued time as now
	  * set expiration by now + jwtExpirationInMs
	  * set signWith with HSS12 algorithm and jwtSecret for the secret key
	  * builds the JWT and serializes it to a compact
	  */
	 public String generateToken(Authentication authentication) {
		 UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		 
		 Date now = new Date();
	     Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
	     Map<String, Object> header = new HashMap<>();
	     header.put("alg", "HS512");
	     header.put("typ", "JWT");
	     
		 return Jwts.builder()
				 	.setHeader(header)
				 	.setSubject(Long.toString(userPrincipal.getId()))
				 	.setIssuedAt(new Date())
				 	.setExpiration(expiryDate)
				 	.signWith(SignatureAlgorithm.HS512, jwtSecret)
				 	.compact();
	 }
	 
	 /*
	  * This method get user id from token
	  * set secret key and token
	  * and get body (user id)
	  * @return claims that parsed into id by token and secret key
	  */
	 public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

	 /*
	  * This method validate the token
	  * @return true if there is no signature exception else false
	  */
    public boolean validateToken(String authToken) {
    	Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(authToken)
                .getBody();
    	Header header = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(authToken)
                .getHeader();
    	
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            throw new SignatureException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            throw new MalformedJwtException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            throw new ExpiredJwtException(header, claims, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            throw new UnsupportedJwtException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
            throw new IllegalArgumentException("JWT claims string is empty.");
        }
    }
}
