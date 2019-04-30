package com.mitrais.more.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.more.model.ApplicantReasonHistory;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.repository.ApplicantReasonHistoryRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * This class is provide applicant reason
 * Each applicant could have more than one reason
 */
@Api(description="Applicant Reason History API")
@RestController
@RequestMapping(ApplicantReasonHistoryController.BASE_URL)
public class ApplicantReasonHistoryController {
	/**
	 * Base API URL for applicant
	 */
	public static final String BASE_URL = "/api/v1/applicantreason";
	
	/** The reason history repository. */
	private ApplicantReasonHistoryRepository reasonHistoryRepository;

	/**
	 * Instantiates a new applicant reason history controller.
	 *
	 * @param reasonHistoryRepository the reason history repository
	 */
	@Autowired
	public ApplicantReasonHistoryController(ApplicantReasonHistoryRepository reasonHistoryRepository) {
		this.reasonHistoryRepository = reasonHistoryRepository;
	}
	
	/**
	 * Gets the reason history list.
	 *
	 * @return the reason history
	 */
	@ApiOperation(value="This API will generate list of reason history", notes="Only Admin and Recruiter are allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@GetMapping
	public ApiResponse<ApplicantReasonHistory> getReasonHistory() {
		List<ApplicantReasonHistory> result = reasonHistoryRepository.findAll();
		return new ApiResponse<ApplicantReasonHistory>(HttpStatus.OK.value(), "Success", result);
	}
}
