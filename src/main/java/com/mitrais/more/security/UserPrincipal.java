package com.mitrais.more.security;

import java.util. ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mitrais.more.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
/*
 * this class is used for authorization
 * 
 */
@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
	private Long id;
    private String name;
    private String username;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    /*
     * This method create the UserPrincipal by User object
     * @param user user's information that will be used to create UserPrincipal
     * set authorities by user role
     * set user information into UserPrincipal
     * @return the UserPrincipal with user information
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        
        return new UserPrincipal(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
