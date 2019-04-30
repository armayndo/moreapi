package com.mitrais.more.controller;

import java.text.SimpleDateFormat; 
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.more.exception.AppException;
import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.ApplicantStatus;
import com.mitrais.more.model.Candidate;
import com.mitrais.more.model.EmailScheduler;
import com.mitrais.more.model.EmailSchedulerStatus;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.repository.ApplicantRepository;
import com.mitrais.more.repository.EmailSchedulerRepository;
import com.mitrais.more.security.CurrentUser;
import com.mitrais.more.security.UserPrincipal;
import com.mitrais.more.service.CandidateService;
import com.mitrais.more.service.EmailService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * This class operate email scheduler process
 * used to define get, create, update and delete API for schedule email
 */
@Api(description="Schdule an email broadcast API")
@RestController
@RequestMapping(EmailSchedulerController.BASE_URL)
public class EmailSchedulerController {
	/**
	 * Set BASE_URL to access this API 
	 */
	public static final String BASE_URL = "/api/v1/email/schedule";
	/**
	 * Set email schedule repository.
	 * This will be used to operate read, create, update and delete email schedule
	 * 
	 */
	private EmailSchedulerRepository emailSchedulerRepository;
	/**
	 * send email class
	 */
	private EmailService emailService;
	/**
	 * Set dependency for candidate model
	 */
	private CandidateService candidateService;
	/**
	 * Set applicant dependency. Get accept or reject applicant
	 */
	private ApplicantRepository applicantRepository;
	
	/**
	 * Set dependencies to initiate this class
	 * 
	 * @param emailSchedulerRepository
	 */
	@Autowired
	public EmailSchedulerController(EmailSchedulerRepository emailSchedulerRepository, 
			EmailService emailService, CandidateService candidateService, ApplicantRepository applicantRepository) {
		this.emailSchedulerRepository = emailSchedulerRepository;
		this.emailService = emailService;
		this.candidateService = candidateService;
		this.applicantRepository = applicantRepository;
	}
	
	/**
	 * Get list of email schedulers. where deleted status is false
	 * Only administrator and recruiter are allowed
	 * 
	 * @return ApiResponse {@link ApiResponse} with list of {@link EmailScheduler}
	 */
	@ApiOperation(value="This API will generate list of email schedulers", notes="Only Admin and Recruiter are allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@GetMapping
	public ApiResponse<EmailScheduler> getSchedulers() {
		List<EmailScheduler> result = emailSchedulerRepository.findByDeleted(false);
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", result);
	}
	
	/**
	 * Create a new email scheduler.
	 * Only administrator and recruiter are allowed
	 * 
	 * @param emailScheduler {@link EmailScheduler}
	 * @return ApiResponse {@link ApiResponse} with created email scheduler ID
	 * 
	 */
	@ApiOperation(value="This API will create a new email scheduler", notes="Only Admin and Recruiter are allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@PostMapping
	public ApiResponse<Long> createScheduler(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody EmailScheduler emailScheduler) {
		try {
			// save valid emailScheduler to repository
			// emailScheduler.setSendDate(new Date());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(emailScheduler.getSendDate());
			calendar.add(Calendar.DATE, 1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			emailScheduler.setSendDate(sdf.parse(sdf.format(calendar.getTime())));
			emailScheduler.setStatus(EmailSchedulerStatus.PENDING);
			emailScheduler.setCreatedBy(currentUser.getName());
			emailScheduler.setUpdatedBy(currentUser.getName());
			EmailScheduler newEmailScheduler =  emailSchedulerRepository.save(emailScheduler);	
			return new ApiResponse<Long>(HttpStatus.OK.value(), "Schedule created successfully", Arrays.asList(newEmailScheduler.getId()));
		} catch (NullPointerException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (IllegalArgumentException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		}
	}
	
	/**
	 * Update a email scheduler with a new email scheduler object
	 * Only administrator and recruiter are allowed
	 * 
	 * @param id
	 * @param emailScheduler {@link EmailScheduler}
	 * 
	 * @return ApiResponse {@link ApiResponse} with updated {@link EmailScheduler}
	 */
	@ApiOperation(value="This API will update a email scheduler with a new email scheduler", notes="Only Admin and Recruiter are allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@PutMapping("/{id}")
	public ApiResponse<EmailScheduler> updateScheduler(@PathVariable Long id, @Valid @RequestBody EmailScheduler emailScheduler, @CurrentUser UserPrincipal currentUser) {
		try {
			// get old email schedule object from repository and throw exception if not found
			EmailScheduler oldSchedule = emailSchedulerRepository.findById(id)
											.orElseThrow(() -> new AppException("Schedule Not Found"));
			oldSchedule.setSubject(emailScheduler.getSubject());
			oldSchedule.setSendDate(emailScheduler.getSendDate());
			oldSchedule.setSendTo(emailScheduler.getSendTo());
			oldSchedule.setMessage(emailScheduler.getMessage());
			oldSchedule.setUpdatedBy(currentUser.getUsername());
			oldSchedule.setUpdatedAt(new Date());
			
			emailSchedulerRepository.save(oldSchedule);
			return new ApiResponse<EmailScheduler>(HttpStatus.OK.value(),"Schedule updated successfully", Arrays.asList(oldSchedule));
		} catch (AppException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (NullPointerException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (IllegalArgumentException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		}
	}
	
	/**
	 * Delete a schedule with soft delete strategy
	 * @param id
	 * 
	 * @return ApiResponse {@link ApiResponse}
	 */
	@ApiOperation(value="This API will delete a email scheduler", notes="Only Admin and Recruiter are allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@DeleteMapping("/{id}")
	public ApiResponse<String> deleteSchduler(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
		try {
			// get old email schedule object from repository and throw exception if not found
			EmailScheduler schedule = emailSchedulerRepository.findById(id)
											.orElseThrow(() -> new AppException("Schedule Not Found"));
			schedule.setDeleted(true);
			schedule.setUpdatedBy(currentUser.getUsername());
			schedule.setUpdatedAt(new Date());
			emailSchedulerRepository.save(schedule);
			return new ApiResponse<>(HttpStatus.OK.value(),"Schedule deleted successfully");
		} catch (AppException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (NullPointerException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (IllegalArgumentException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		}
	}
	
	/**
	 * Update status of email scheduler to be canceled
	 * @param id
	 * 
	 * @return ApiResponse {@link ApiResponse}
	 */
	@ApiOperation(value="This API will update schedule status to be cancelled", notes="Only Admin and Recruiter are allowed")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	@DeleteMapping("/cancel/{id}")
	public ApiResponse<String> cancelSheduler(@PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
		try {
			// get old email schedule object from repository and throw exception if not found
			EmailScheduler schedule = emailSchedulerRepository.findById(id)
											.orElseThrow(() -> new AppException("Schedule Not Found"));
			schedule.setStatus(EmailSchedulerStatus.CANCEL);;
			schedule.setUpdatedBy(currentUser.getUsername());
			schedule.setUpdatedAt(new Date());
			emailSchedulerRepository.save(schedule);
			return new ApiResponse<>(HttpStatus.OK.value(),"Schedule canceled");
		} catch (AppException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (NullPointerException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (IllegalArgumentException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		}
	}
	
	/**
	 * Send email in email scheduler data automatically based on send date
	 * Updated email scheduler status to complete after sent successfully
	 */
	@Scheduled(cron = "0 * * * * ?")
	public void sendEmailInScheculerAutomatically() {
		try {
			Date currentDate = new Date(); // Current System Date and time is assigned to objDate
			SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // standard format for compare date
			
			List<EmailScheduler> list = emailSchedulerRepository.findByStatusAndDeleted(EmailSchedulerStatus.PENDING, false);
			
			for(EmailScheduler job: list) {
				// compare job send date with server current date. execute send email if match
				if(dbDateFormat.format(currentDate).compareTo(dbDateFormat.format(job.getSendDate())) == 0) {
					String sendDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(job.getSendDate());
					if (job.getSendTo().equals("ALL")) {
						List<Candidate> candidateList = candidateService.getAllCandidate();
						for(Candidate c: candidateList) {
							List<Applicant> applyList = applicantRepository.findByCandidate(c);
							for(Applicant apply: applyList) {
								emailService.sendEmail(c.getEmail(), job.getSubject(), job.getMessage(), apply, sendDate);
							}
						}
					} else if ( job.getSendTo().equals(ApplicantStatus.HIRED.toString()) ) {
						List<Applicant> acceptApplicantList = applicantRepository.findByStatus(ApplicantStatus.HIRED);
						for(Applicant accept: acceptApplicantList) {
							emailService.sendEmail(accept.getCandidate().getEmail(), job.getSubject(), job.getMessage(), accept, sendDate);
						}
					} else if ( job.getSendTo().equals(ApplicantStatus.REJECT.toString()) ) {
						List<Applicant> rejectApplicantList = applicantRepository.findByStatus(ApplicantStatus.REJECT);
						for(Applicant reject: rejectApplicantList) {
							emailService.sendEmail(reject.getCandidate().getEmail(), job.getSubject(), job.getMessage(), reject, sendDate);
						}
					} else {
						Candidate candidate = candidateService.getCandidateByEmail(job.getSendTo());
						List<Applicant> applyList = applicantRepository.findByCandidate(candidate);
						for(Applicant apply: applyList) {
							String subject = job.getSubject();
							if(subject.toLowerCase().contains(apply.getStatus().name().toLowerCase())) {
								emailService.sendEmail(job.getSendTo(), job.getSubject(), job.getMessage(), apply, sendDate);
							}
						}
						
					}
					
					job.setStatus(EmailSchedulerStatus.COMPLETE);
					emailSchedulerRepository.save(job);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
