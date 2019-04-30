package com.mitrais.more.controller;

import java.io.ByteArrayInputStream;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mitrais.more.exception.AppException;
import com.mitrais.more.model.Applicant;
import com.mitrais.more.model.Candidate;
import com.mitrais.more.model.CandidateDoc;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.payloads.UploadFileResponse;
import com.mitrais.more.repository.ApplicantRepository;
import com.mitrais.more.service.CandidateService;
import com.mitrais.more.service.FileStorageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * This class is operate candidate model process
 * operate read, create, update and delete
 */
@Api(description = "Candidate API")
@RestController
@RequestMapping(CandidateController.BASE_URL)
public class CandidateController {
	/**
	 * Base URL for this User API
	 */
	public static final String BASE_URL = "/api/v1/candidate";
	
	/**
	 * Candidate Repository implemented from JpaRepository
	 */
	private CandidateService candidateService;
	/**
	 * File storage service. responsible in upload and store file to server
	 */
	private FileStorageService fileStorageService;
	/**
	 * Applicant Repository. responsible in store candidate applied a job
	 */
	private ApplicantRepository applicantRepository;
	
	/**
	 * 
	 * @param candidateService
	 * @param fileStorageService
	 * @param applicantRepository
	 * 
	 * Candidate Controller Constructor
	 */
	@Autowired
	public CandidateController(CandidateService candidateService, FileStorageService fileStorageService, ApplicantRepository applicantRepository) {
		this.candidateService = candidateService;
		this.fileStorageService = fileStorageService;
		this.applicantRepository = applicantRepository;
	}
	/**
	 * Get list of vacancy
	 * 
	 * @return ApiResponse with Vacancy as the Object Type
	 * 
	 */
	@ApiOperation(value = "This API will genereate list of candidate", notes = "All Roles Allowed")
	@GetMapping
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public ApiResponse<Candidate> getAllCandidate(){
		return new ApiResponse<>(
				HttpStatus.OK.value(), 
				"Success", 
				candidateService.getAllCandidate());
	}
	
	/**
	 * Get list candidate by ID
	 * @param id
	 * @return ApiResponse with Vacancy as the Object Type
	 */
	@ApiOperation(value="This API will generate list candidate by ID")
	@GetMapping("/{id}")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public ApiResponse<Candidate> getCandidateById(@PathVariable long id){
		if (!candidateService.checkCandidate(id).isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found");
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", candidateService.getCandidateById(id));
	}
	
	/**
	 * Gets the candidate by email.
	 *
	 * @param email the email
	 * @return the candidate by email
	 */
	@ApiOperation(value="This API will generate candidate by Email")
	@GetMapping("/email/{email}")
	public ApiResponse<Candidate> getCandidateByEmail(@PathVariable String email) {
		try {
			Candidate candidate = candidateService.getCandidateByEmail(email);
			return new ApiResponse<Candidate>(HttpStatus.OK.value(), "Success", Arrays.asList(candidate));
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
	
	/**
	 * Create candidate
	 * @param candidateRequest
	 * @param currentUser
	 * @return ApiResponse with Vacancy as the Object Type
	 */
	@ApiOperation(value = "This API will create candidate", notes ="All roles Allowed")
	@PostMapping
	public ApiResponse<String> createCandidate(@Valid @RequestBody Applicant applicant){
		if (candidateService.checkApplicant(applicant.getCandidate().getEmail(), applicant.getVacancy().getId()).isPresent()) {
			return new ApiResponse<String>(HttpStatus.CONFLICT.value(), "Your data has been submited before, you cannot apply with same vacancy", null);
		} else {
			try {
				return new ApiResponse<String>(
						HttpStatus.OK.value(), 
						"Successfully apply to this vacancy", candidateService.createCandidate(applicant));
			} catch (SendFailedException e) {
				throw new AppException(e.getMessage());
			}
		}
	}
	
	/**
	 * delete candidate by ID
	 * @param id
	 * @return ApiResponse with Vacancy as the Object Type
	 */
	@ApiOperation(value="This API will delete candidate by ID",notes="Admin and Recruiter are Allowed")
	@DeleteMapping("/{id}")
	@Secured({"ROLE_ADMIN","ROLE_RECRUITER"})
	public ApiResponse<String> deleteCandidate(@PathVariable long id){
		
		if (!candidateService.checkCandidate(id).isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found");
		}
		
		int res = candidateService.deleteCandidate(id);
		
		if( res > 0) {
			return new ApiResponse<>(HttpStatus.OK.value(), "Candidate deleted ");
		} else {
			return new ApiResponse<>(HttpStatus.OK.value(), "failed to delete candidate");
		}
	}
	
	
	/** 
	 * Upload candidate document
	 * @param file
	 * 
	 * @return UploadFileResponse
	 */
	@ApiOperation(value="This API will upload files when candidate applied a vacancy")
	@PostMapping("/{applicantId}/uploadFile")
	public ResponseEntity<ApiResponse<UploadFileResponse>> uploadFile(@PathVariable long applicantId, @RequestParam("file") MultipartFile file) {
		Applicant applicant = applicantRepository.findById(applicantId).orElseThrow(() -> new AppException("Applicant not found"));
		
		try {
			CandidateDoc doc = new CandidateDoc();
			
			String result = fileStorageService.storeFile(file);
			
			doc.setName(file.getOriginalFilename());
			doc.setFilepath(result);
			applicant.getDocuments().add(doc);
			applicantRepository.save(applicant);
			
			List<UploadFileResponse> res = new ArrayList<>();
			res.add(new UploadFileResponse(file.getOriginalFilename(), file.getContentType(), file.getSize()));
			return new ResponseEntity<ApiResponse<UploadFileResponse>>(new ApiResponse<UploadFileResponse>(HttpStatus.OK.value(), "File uploaded", res), HttpStatus.CREATED);
		} catch (AppException e) {
			applicantRepository.deleteById(applicantId);
			candidateService.hardDeleteCandidate(applicant.getCandidate().getId());;
			return new ResponseEntity<ApiResponse<UploadFileResponse>>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (NullPointerException e) {
			return new ResponseEntity<ApiResponse<UploadFileResponse>>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<ApiResponse<UploadFileResponse>>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Get Candidate applied vacancies
	 * @param candidateId
	 * 
	 * @return ApiResponse shows vacancies list
	 */
	@ApiOperation(value="This api will provide vacancies based on candidate ID",notes="Only Admin and Recruiter are Allowed")
	@GetMapping("/{id}/vacancies")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public ApiResponse<Applicant> getCandidateApplied(@PathVariable long id) {
		Candidate candidate = candidateService.getCandidateById(id).get(0);
		List<Applicant> applicantList = applicantRepository.findByCandidate(candidate);
		//List<Vacancy> vancanciesIdList = applicantRepository.findVacanciesByCandidateId(candidate);
		return new ApiResponse<Applicant>(HttpStatus.OK.value(), "Success", applicantList);
	}
	
	
	/**
	 * Provide download URL for file that stored in server
	 * 
	 * @param fileName
	 * @param request
	 * @return file requested as a Resource {@link Resource}
	 */
	@ApiOperation(value="This api will export candidate's document file to client side")
	@GetMapping("/downloadFile/{fileName:.+}")
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
	
	/**
	 * Export to excel.
	 *
	 * @param fileName the file name
	 * @param request the request
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@ApiOperation(value="This api will export applicants to excel file")
	@PostMapping("/exportFile/{fileName:.+}")
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	public ResponseEntity<InputStreamResource> exportToExcel(@RequestBody List<Applicant> filteredApplicants, HttpServletRequest request) throws IOException {
		String fileName = "applicants";
		
		String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		
		ByteArrayInputStream in = candidateService.exportCandidateApplicantToExcel(filteredApplicants);
	    // return IOUtils.toByteArray(in);
	    
	     return ResponseEntity.ok()
	    		 	.contentType(MediaType.parseMediaType(contentType))
	    		 	.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
	    		 	.body(new InputStreamResource(in));
	}

	/**
	 * Duplicate candidate.
	 *
	 * @param applicant the applicant
	 * @return the api response
	 */
	@ApiOperation(value="This api will duplicate the candidate",notes="Only Admin Allowed")
	@Secured("ROLE_ADMIN")
	@PostMapping("/duplicateCandidate")
	public ApiResponse<String> duplicateCandidate(@Valid @RequestBody Applicant applicant){
		try {
			return new ApiResponse<String>(
					HttpStatus.OK.value(), 
					"Successfully duplicate the candidate", candidateService.duplicateCandidate(applicant));
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
	}
}
