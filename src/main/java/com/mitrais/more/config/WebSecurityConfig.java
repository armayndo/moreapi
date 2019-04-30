package com.mitrais.more.config;

 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mitrais.more.security.CustomUserDetailsService;
import com.mitrais.more.security.JwtAuthenticationEntryPoint;
import com.mitrais.more.security.JwtAuthenticationFilter;
import com.mitrais.more.security.JwtTokenProvider;
/*
 * This class is used to configure Spring Security in the application
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 * The CustomUserDetail extended from UserDetail Class
	 * with custom load user by Id
	 */
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	/**
	 * JSON Web Token authorization handler class
	 */
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	/**
	 * Provide generate token process and authorized token to access API
	 */
	@Autowired
	private JwtTokenProvider jwtProvider;
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtProvider, customUserDetailsService);
	}

	/*
	 * This method configure the AuthenticationManagerBuilder
	 * set the userDetailsService with CustomUserDetailsService
	 * and set the passwordEncoder
	 */
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

	/*
	 * This method set the password encoder
	 * the password encoder is set with BCryptPasswordEncoder
	 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
    /*
     * This method configure the HttpSecurity
     * set corsFilter and CSRF support disabled
     * set exception handling with JwtAuthenticationEntryPoint 
     * set session creation policy with STATELESS
     * and permit all authorize requests
     */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
        .cors()
            .and()
        .csrf()
            .disable()
        .exceptionHandling()
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
        .authorizeRequests()
            .antMatchers("/",
                "/favicon.ico",
                "/**/*.png",
                "/**/*.gif",
                "/**/*.svg",
                "/**/*.jpg",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js")
                .permitAll()
            .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**")
                .permitAll()
            .antMatchers("/api/v1/auth/**","/api/v1/vacancy/active","/api/v1/candidate/**","/api/v1/vacancy/{\\d+}", "/api/v1/email/schedule/autosend", "/api/v1/vacancy/jobFunctions")
                .permitAll()
            .anyRequest()
                .authenticated();

		// Add our custom JWT security filter
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/*
	 * This method configure the WebSecurity
	 * set to ignoring some URL
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");
		
	}
}
