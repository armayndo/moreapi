package com.mitrais.more.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mitrais.more.model.User;
import com.mitrais.more.repository.UserRepository;
/*
 * This class is used for authentication by checking the user details
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
	/**
     * UserRepository interface implemented from JpaRepository
     */
	private UserRepository userRepository;

	@Autowired
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/*
	 * This method load UserDetails by user name
	 * find user by user name with UserRepository
	 * then create UserPrincipal by the user's found by user name
	 * @return UserPrincipal created by user's information
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException("Username not found"));
		
		if (user == null) {
	        throw new BadCredentialsException("Bad Credentials");
	    }
	    if (!user.isActive()) {
	        throw new DisabledException("Username not active");
	    }
	    
		return UserPrincipal.create(user);
	}
	
	/*
	 * This method load UserDetails by user id
	 * find user by user id with UserRepository
	 * then create UserPrincipal by the user's found by user id
	 * @return UserPrincipal created by user's information
	 */
	public UserDetails loadUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(
				() -> new UsernameNotFoundException("User not found with id : " + id));
		
		if (user == null) {
	        throw new BadCredentialsException("Bad Credentialsssss");
	    }
	    if (!user.isActive()) {
	        throw new DisabledException("Username not active");
	    }
	    
		return UserPrincipal.create(user);
	}
}
