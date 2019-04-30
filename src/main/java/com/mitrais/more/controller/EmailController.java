package com.mitrais.more.controller;

import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.more.security.CurrentUser;
import com.mitrais.more.security.UserPrincipal;
import com.mitrais.more.model.EmailTemplate;
import com.mitrais.more.model.Vacancy;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.payloads.UserRequest;
import com.mitrais.more.repository.EmailRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * This class is operate user model process
 * operate read, create, update and delete
 */
@Api(description = "Vacancy API")
@RestController
@RequestMapping(EmailController.BASE_URL)
public class EmailController {
	/**
	 * Base URL for this User API
	 */
	public static final String BASE_URL = "/api/v1/email";
	/**
	 * EmailRepository implemented from JpaRepository
	 */
	private EmailRepository emailRepository;

	@Autowired
	public EmailController(EmailRepository emailRepository) {
		this.emailRepository = emailRepository;
	}

	/**
	 * Get list of email
	 * 
	 * @return ApiResponse with EmailTemplates as the Object Type
	 * 
	 */
	@ApiOperation(value = "This API will genereate list of email template", notes = "All Roles Allowed")
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<EmailTemplate> getEmails() {
		List<EmailTemplate> emails = emailRepository.findAll();
		return new ApiResponse<EmailTemplate>(HttpStatus.OK.value(), "Success", emails);
	}

	/**
	 * Get list of email template by ID
	 * 
	 * @Return ApiResponse with Email Template as the Object
	 * 
	 */

	@ApiOperation(value = "This API will generate list email template filter by ID", notes = "All Roles Allowed")
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<EmailTemplate> getVacancyById(@PathVariable long id) {
		Optional<EmailTemplate> checkEmail = emailRepository.findById(id);

		if (!checkEmail.isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Email Template Not Found!");
		}

		List<EmailTemplate> email = Collections.singletonList(checkEmail.get());
		return new ApiResponse<EmailTemplate>(HttpStatus.OK.value(), "Success", email);
	}

	/**
	 * Create Email This API allowed to ADMIN and USERS
	 * 
	 * @param userRequest {@link UserRequest}
	 * 
	 * @return ApiResponse object {@link ApiResponse}
	 */
	@ApiOperation(value = "This API will create an email template.", notes = "Admin and Users Allowed to access")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@PostMapping
	public ApiResponse<String> createEmailTemplate(@CurrentUser UserPrincipal currentUser,
			@Valid @RequestBody EmailTemplate emailRequest) {
		
		emailRequest.setCreatedBy(currentUser.getId());
		emailRepository.save(emailRequest);

		List<String> emailList = new ArrayList<>();
		emailList.add(emailRequest.getId().toString());
		return new ApiResponse<String>(HttpStatus.OK.value(), "Email template created successfully", emailList);
	}

	/**
	 * Update email template with a new email template object This API allowed to ADMIN and USERS
	 * 
	 * @param email represent new Vacancy object {@link Vacancy}
	 * @param id     represent vacancyId being updated
	 * 
	 * @return ApiResponse object
	 */
	@ApiOperation(value = "This API will update vacancy with a new vacancy object.", notes = "Admin and users are allowed to access")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@PutMapping("/{id}")
	public ApiResponse<EmailTemplate> updateEmailTemplate(@CurrentUser UserPrincipal currentUser, @RequestBody EmailTemplate email,
			@PathVariable long id) {
		Optional<EmailTemplate> checkEmail = emailRepository.findById(id);
		if (!checkEmail.isPresent()) {
			return new ApiResponse<EmailTemplate>(HttpStatus.NOT_FOUND.value(), "Email template Not Found!");
		}
		email.setCreatedBy(checkEmail.get().getCreatedBy());
		email.setCreatedDate(checkEmail.get().getCreatedDate());
		email.setUpdatedBy(currentUser.getId());
		email.setId(id);

		emailRepository.save(email);
		List<EmailTemplate> listEmail = Collections.singletonList(checkEmail.get());
		return new ApiResponse<EmailTemplate>(HttpStatus.OK.value(), "Email template updated successfully", listEmail);
	}

	/**
	 * Delete an Email Template This API allowed to ADMIN and USER
	 * 
	 * @param id represent the email id
	 * 
	 * 
	 * @return ApiResponse object
	 */
	@ApiOperation(value = "This API will delete an email template.", notes = "Admin and Users are allowed to access")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@DeleteMapping("/{id}")
	public ApiResponse<String> deleteEmailTemplate(@PathVariable long id) {
		Optional<EmailTemplate> checkEmail = emailRepository.findById(id);
		if (!checkEmail.isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Email template Not Found!");
		}

		emailRepository.deleteById(id);
		return new ApiResponse<>(HttpStatus.OK.value(), "Email template deleted successfully");
	}

}
