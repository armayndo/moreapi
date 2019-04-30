package com.mitrais.more.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.more.model.ReasonTemplate;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.repository.ReasonTemplateRepository;
import com.mitrais.more.security.CurrentUser;
import com.mitrais.more.security.UserPrincipal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;

/**
 * 
 * Perform reason template management system.
 * such as create, read, update and delete 
 *
 */
@Api(description="Reason Template API")
@RestController
@RequestMapping(ReasonTemplateController.BASE_URL)
public class ReasonTemplateController {
	
	/** Initial BASE URL for Reason Template API. */
	public static final String BASE_URL = "/api/v1/reason";

	/** Perform resource management operation for reason template model. */
	private ReasonTemplateRepository reasonTemplateRepository;

	
	/**
	 * Instantiates a new reason template controller.
	 *
	 * @param reasonTemplateRepository the reason template repository
	 */
	@Autowired
	public ReasonTemplateController(ReasonTemplateRepository reasonTemplateRepository) {
		this.reasonTemplateRepository = reasonTemplateRepository;
	}
	
	
	/**
	 * Gets the reasons.
	 *
	 * @return the reasons
	 */
	@ApiOperation(value="This API will genereate list of reason template", notes="Only admin and recruiter allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@GetMapping
	public ApiResponse<ReasonTemplate> getReasons() {
		List<ReasonTemplate> reasons = reasonTemplateRepository.findByDeleted(false);
		return new ApiResponse<ReasonTemplate>(HttpStatus.OK.value(),"Reason Template", reasons);
	}
	
	/**
	 * Creates the reason template.
	 *
	 * @param currentUser the current user
	 * @param reasonTemplateBody the reason template body
	 * @return the api response
	 */
	@ApiOperation(value="This API create a new reason template", notes="Only admin and recruiter allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@PostMapping
	public ApiResponse<Long> createReasonTemplate(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody ReasonTemplate reasonTemplateBody) {
		reasonTemplateBody.setCreatedBy(currentUser.getUsername());
		ReasonTemplate newTemplate = reasonTemplateRepository.save(reasonTemplateBody);
		return new ApiResponse<Long>(HttpStatus.OK.value(),"Template created succesfully", Arrays.asList(newTemplate.getId()));
	}
	
	
	/**
	 * Update reason template.
	 *
	 * @param currentUser the current user logged in
	 * @param reasonTemplateBody the reason template body
	 * @param id the id
	 * 
	 * @return the api response
	 */
	@ApiOperation(value="This API update the old reason template with new values", notes="Only admin and recruiter allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@PutMapping("/{id}")
	public ApiResponse<ReasonTemplate> updateReasonTemplate(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody ReasonTemplate reasonTemplateBody, @PathVariable long id) {
		try {
			ReasonTemplate oldTemplate = reasonTemplateRepository.findById(id).orElseThrow(() -> new NotFoundException("Reason template not found"));
			reasonTemplateBody.setCreatedBy(oldTemplate.getCreatedBy());
			reasonTemplateBody.setCreatedAt(oldTemplate.getCreatedAt());
			reasonTemplateBody.setUpdatedBy(currentUser.getUsername());
			reasonTemplateBody.setUpdatedAt(new Date());
			ReasonTemplate updatedTemplate = reasonTemplateRepository.save(reasonTemplateBody);
			
			return new ApiResponse<ReasonTemplate>(HttpStatus.OK.value(), "Template updated successfully", Arrays.asList(updatedTemplate));
		} catch (NotFoundException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
	
	/**
	 * Delete reason template.
	 * Soft delete strategy
	 *
	 * @param currentUser the current user
	 * @param id the id
	 * @return the api response
	 */
	@ApiOperation(value="This API will delete reason template ", notes="Only admin and recruiter allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@DeleteMapping("/{id}")
	public ApiResponse<String> deleteReasonTemplate(@CurrentUser UserPrincipal currentUser, @PathVariable long id) {
		try {
			ReasonTemplate template = reasonTemplateRepository.findById(id).orElseThrow(() -> new NotFoundException("Template not found"));
			
			template.setDeleted(true);
			template.setUpdatedBy(currentUser.getUsername());
			template.setUpdatedAt(new Date());
			reasonTemplateRepository.save(template);
			
			return new ApiResponse<>(HttpStatus.OK.value(), "Template Deleted succesfully");
		} catch (NotFoundException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		}
	}
}
