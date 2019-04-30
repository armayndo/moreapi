package com.mitrais.more.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.more.exception.AppException;
import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.ApplicantReasonHistory;
import com.mitrais.more.model.EmailScheduler;
import com.mitrais.more.model.EmailSchedulerStatus;
import com.mitrais.more.model.EmailTemplate;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.payloads.ApplicantUpdateRequest;
import com.mitrais.more.repository.ApplicantRepository;
import com.mitrais.more.repository.EmailRepository;
import com.mitrais.more.repository.EmailSchedulerRepository;
import com.mitrais.more.security.CurrentUser;
import com.mitrais.more.security.UserPrincipal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;

/*
 * This class is operate applicant model process
 * operate read, create, update and delete
 */
@Api(description="Applicant API")
@RestController
@RequestMapping(ApplicantController.BASE_URL)
public class ApplicantController {
	/**
	 * Base API URL for applicant
	 */
	public static final String BASE_URL = "/api/v1/applicant";
	/**
	 * Applicant Repository. responsible in store candidate applied a job
	 */
	private ApplicantRepository applicantRepository;
	
	/** The email repository. */
	private EmailRepository emailRepository;
	
	/** The email scheduler repository. */
	private EmailSchedulerRepository emailSchedulerRepository;
	
	/**
	 * Instantiates a new applicant controller.
	 *
	 * @param applicantRepository the applicant repository
	 * @param applicantReasonHistoryRepo the applicant reason history repo
	 * @param emailRepository the email repository
	 * @param emailSchedulerRepository the email scheduler repository
	 */
	@Autowired
	public ApplicantController(ApplicantRepository applicantRepository, EmailRepository emailRepository,
								EmailSchedulerRepository emailSchedulerRepository) {
		this.applicantRepository = applicantRepository;
		this.emailRepository = emailRepository;
		this.emailSchedulerRepository = emailSchedulerRepository;
	}
	
	/**
	 * Update applicant status in certain vacancy
	 * 
	 * @param id Applicant ID
	 * 
	 * @return ApiResponse
	 */
	@ApiOperation(value="This API will update applicant status with Accept or Reject", notes="Only Admin allowed")
	@Secured("ROLE_ADMIN")
	@PutMapping("/{id}")
	public ApiResponse<Applicant> updateCandidateStatusOnVacancy(@CurrentUser UserPrincipal currentUser, @RequestBody Applicant updateApplicant, @PathVariable long id) {
		Applicant applicant = applicantRepository.findById(id)
								.orElseThrow(() -> new AppException("Applicant ID Not Found."));
		applicant.setStatus(updateApplicant.getStatus());
		applicant.setComment(updateApplicant.getComment());
		
		
		// insert a reason history
		ApplicantReasonHistory reason = new ApplicantReasonHistory();
		reason.setApplicant(applicant);
		reason.setReason(updateApplicant.getReason());
		reason.setCreatedBy(currentUser.getName());
		reason.setStatus(updateApplicant.getStatus());
		applicant.getReasons().add(reason);
		
		applicantRepository.save(applicant);
		
		List<Applicant> updatedApplicant = Collections.singletonList(applicant);
		return new ApiResponse<Applicant>(HttpStatus.OK.value(), "Applicant updated successfully",updatedApplicant);
	}
	
	/**
	 * Update Comment in applicant
	 * 
	 * @param id Applicant ID
	 * 
	 * @return ApiResponse
	 */
	@ApiOperation(value="This API will update applicant's comment", notes="Only Admin and User are allowed")
	@Secured("ROLE_ADMIN")
	@PutMapping("/comment/{id}")
	public ApiResponse<Applicant> updateCommentOnVacancy(@RequestBody Applicant updateApplicant, @PathVariable long id) {
		Applicant applicant = applicantRepository.findById(id)
								.orElseThrow(() -> new AppException("Applicant ID Not Found."));
		applicant.setComment(updateApplicant.getComment());
		applicantRepository.save(applicant);
		List<Applicant> updatedApplicant = Collections.singletonList(applicant);
		return new ApiResponse<Applicant>(HttpStatus.OK.value(), "Applicant updated successfully",updatedApplicant);
	}
	
	
	/**
	 * Get list of all applicant
	 * 
	 * @return ApiResponse with Applicant as the Object Type
	 * 
	 */
	@ApiOperation(value="This API will generate list of Applicant", notes="Only Admin allowed")
	@Secured("ROLE_ADMIN")
	@GetMapping
	public ApiResponse<Applicant> getApplicants() {
		List<Applicant> applicants = applicantRepository.findAll();
		return new ApiResponse<Applicant>(HttpStatus.OK.value(), "Success", applicants);
	}
	
	/**
	 * Get list of applicant by id
	 * 
	 * @return ApiResponse with Applicant as the Object Type
	 * 
	 */
	@ApiOperation(value="This API will generate Applicant by id", notes="Only Admin allowed")
	@Secured("ROLE_ADMIN")
	@GetMapping("/{id}")
	public ApiResponse<Applicant> getApplicantsById(@PathVariable long id) {
		List<Applicant> applicants = Collections.singletonList(applicantRepository.findById(id).get());
		return new ApiResponse<Applicant>(HttpStatus.OK.value(), "Success", applicants);
	}
	
	/**
	 * Check email schedule by applicant.
	 *
	 * @param currentUser the current user
	 * @param applicantUpdateReq the applicant update req
	 * @return the api response
	 */
	@ApiOperation(value="This API will schedule email notification for applicant", notes="Only Admin allowed")
	@Secured("ROLE_ADMIN")
	@PostMapping("/sendemail")
	@Transactional
	public ApiResponse<String> checkEmailScheduleByApplicant(@CurrentUser UserPrincipal currentUser, @RequestBody ApplicantUpdateRequest applicantUpdateReq) {
		try {
			// get email template by candidate status
			EmailTemplate email = emailRepository.findByName(applicantUpdateReq.getApplicant().getStatus().name())
									.orElseThrow(() -> new NotFoundException("Email not found"));
			
			// get emailScheduled if any pending scheduled email by candidate email
			List<EmailScheduler> oldScheduler = emailSchedulerRepository.findBySendTo(applicantUpdateReq.getApplicant().getCandidate().getEmail());
			
			// check if found then delete it
			if(oldScheduler.size() > 0) {
				for(EmailScheduler em : oldScheduler) {
					em.setDeleted(true);
					em.setStatus(EmailSchedulerStatus.CANCEL);
					em.setUpdatedBy(currentUser.getName());
					emailSchedulerRepository.save(em);
				}
			}
			
			// set email template and candidate email to email scheduler object
			EmailScheduler scheduleEmail = new EmailScheduler();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, email.getDaysSend());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			
			if ( email.getDaysSend() == 0 ) {
				scheduleEmail.setSendDate(sdf.parse(sdf.format(calendar.getTime())));
			} else {
				calendar.setTime(applicantUpdateReq.getSendNotificationDate());
				calendar.add(Calendar.DATE, 1);
				scheduleEmail.setSendDate(sdf.parse(sdf.format(calendar.getTime())));
			}
			
			scheduleEmail.setSendTo(applicantUpdateReq.getApplicant().getCandidate().getEmail());
			scheduleEmail.setSubject(email.getSubject());
			scheduleEmail.setMessage(email.getBody());
			scheduleEmail.setStatus(EmailSchedulerStatus.PENDING);
			scheduleEmail.setCreatedBy(currentUser.getName());
			scheduleEmail.setUpdatedBy(currentUser.getName());
			
			// save new scheduled email
			emailSchedulerRepository.save(scheduleEmail);

			return new ApiResponse<String>(HttpStatus.OK.value(),"Success");
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
}
