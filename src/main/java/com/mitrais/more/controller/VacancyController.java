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
import com.mitrais.more.model.JobFunction;
import com.mitrais.more.model.Vacancy;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.payloads.UserRequest;
import com.mitrais.more.repository.JobFunctionRepository;
import com.mitrais.more.repository.VacancyRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * This class is operate user model process
 * operate read, create, update and delete
 */
@Api(description = "Vacancy API")
@RestController
@RequestMapping(VacancyController.BASE_URL)
public class VacancyController {
	/**
	 * Base URL for this User API
	 */
	public static final String BASE_URL = "/api/v1/vacancy";
	/**
	 * VacancyRepository implemented from JpaRepository
	 */
	private VacancyRepository vacancyRepository;
	
	private JobFunctionRepository jobFunctionRepo;

	@Autowired
	public VacancyController(VacancyRepository vacancyRepository, JobFunctionRepository jobFunctionRepo) {
		this.vacancyRepository = vacancyRepository;
		this.jobFunctionRepo = jobFunctionRepo;
	}

	/**
	 * Get list of vacancy
	 * 
	 * @return ApiResponse with Vacancy as the Object Type
	 * 
	 */
	@ApiOperation(value = "This API will genereate list of vacancy", notes = "All Roles Allowed")
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<Vacancy> getVacancy() {
		List<Vacancy> vacant = vacancyRepository.findByDeleted(false);
		return new ApiResponse<Vacancy>(HttpStatus.OK.value(), "Success", vacant);
	}
	
	/**
	 * Get list of active vacancy
	 * 
	 * @return ApiResponse with Vacancy as the Object Type
	 * 
	 */
	@ApiOperation(value = "This API will genereate list of active vacancy", notes = "All Roles Allowed")
	@GetMapping("/active")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<Vacancy> getActiveVacancy() {
		List<Vacancy> vacant = vacancyRepository.findByActive(true);
		return new ApiResponse<Vacancy>(HttpStatus.OK.value(), "Success", vacant);
	}

	/**
	 * Get list of vacancy by ID
	 * 
	 * @Return ApiResponse with Vacancy as the Object
	 * 
	 */

	@ApiOperation(value = "This API will generate list vacancy filter by ID", notes = "All Roles Allowed")
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<Vacancy> getVacancyById(@PathVariable long id) {
		Optional<Vacancy> checkVacancy = vacancyRepository.findById(id);

		if (!checkVacancy.isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "vacancy Not Found!");
		}

		List<Vacancy> vacant = Collections.singletonList(checkVacancy.get());
		return new ApiResponse<Vacancy>(HttpStatus.OK.value(), "Success", vacant);
	}

	/**
	 * Create Vacancy This API allowed to ADMIN and USERS
	 * 
	 * @param userRequest {@link UserRequest}
	 * 
	 * @return ApiResponse object {@link ApiResponse}
	 */
	@ApiOperation(value = "This API will create a vacancy.", notes = "Admin and Users Allowed to access")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@PostMapping
	public ApiResponse<String> createVacancy(@CurrentUser UserPrincipal currentUser,
			@Valid @RequestBody Vacancy vacancyRequest) {

		// Creating vacancy
		Vacancy vacant = new Vacancy();
		vacant.setPosition(vacancyRequest.getPosition());
		vacant.setDescription(vacancyRequest.getDescription());
		vacant.setJobFunction(vacancyRequest.getJobFunction());
		vacant.setLocation(vacancyRequest.getLocation());
		vacant.setCreatedBy(currentUser.getName());
		vacant.setUpdatedBy(currentUser.getName());
		vacancyRepository.save(vacant);

		List<String> vacantList = new ArrayList<>();
		vacantList.add(vacant.getId().toString());
		return new ApiResponse<String>(HttpStatus.OK.value(), "Vacancy created successfully", vacantList);
	}

	/**
	 * Update vacancy with a new vacancy object This API allowed to ADMIN and USERS
	 * 
	 * @param vacant represent new Vacancy object {@link Vacancy}
	 * @param id     represent vacancyId being updated
	 * 
	 * @return ApiResponse object
	 */
	@ApiOperation(value = "This API will update vacancy with a new vacancy object.", notes = "Admin and users are allowed to access")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@PutMapping("/{id}")
	public ApiResponse<Vacancy> updateVacancy(@CurrentUser UserPrincipal currentUser, @RequestBody Vacancy vacant,
			@PathVariable long id) {
		Optional<Vacancy> checkVacant = vacancyRepository.findById(id);
		if (!checkVacant.isPresent()) {
			return new ApiResponse<Vacancy>(HttpStatus.NOT_FOUND.value(), "Vacancy Not Found!");
		}
		
		vacant.setUpdatedBy(currentUser.getName());
		vacant.setId(id);

		vacancyRepository.save(vacant);
		List<Vacancy> listVacant = Collections.singletonList(checkVacant.get());
		return new ApiResponse<Vacancy>(HttpStatus.OK.value(), "Vacancy updated successfully", listVacant);
	}

	/**
	 * Delete a Vacancy This API allowed to ADMIN and USER
	 * 
	 * @param id represent the vacancyId
	 * 
	 * 
	 * @return ApiResponse object
	 */
	@ApiOperation(value = "This API will delete a vacancy.", notes = "Admin and Users are allowed to access")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@DeleteMapping("/{id}")
	public ApiResponse<String> deleteVacancy(@PathVariable long id) {
		Optional<Vacancy> checkvacant = vacancyRepository.findById(id);
		if (!checkvacant.isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Vacancy Not Found!");
		}
		Vacancy vacancy = checkvacant.get();
		vacancy.setDeleted(true);
		vacancyRepository.save(vacancy);
		return new ApiResponse<>(HttpStatus.OK.value(), "Vacancy deleted successfully");
	}

	/**
	 * This API for activate vacancy
	 * 
	 * @param id       represent the vacancyId
	 * @param active value with boolean type (true or false)
	 * 
	 * @return ApiResponse object
	 */

	@ApiOperation(value = "This API will activate a vacancy with boolean value (true or false)")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@PutMapping("/activate/{id}")
	public ApiResponse<Vacancy> activateVacancy(@CurrentUser UserPrincipal currentUser, @RequestBody Vacancy vacant,
			@PathVariable long id) {
		Optional<Vacancy> checkVacant = vacancyRepository.findById(id);
		if (!checkVacant.isPresent()) {
			return new ApiResponse<Vacancy>(HttpStatus.NOT_FOUND.value(), "Vacancy Not Found!");
		}
		vacant.setPosition(checkVacant.get().getPosition());
		vacant.setDescription(checkVacant.get().getDescription());
		vacant.setCreatedBy(checkVacant.get().getCreatedBy());
		vacant.setCreatedDate(checkVacant.get().getCreatedDate());
		vacant.setUpdatedBy(currentUser.getName());
		vacant.setId(id);

		vacancyRepository.save(vacant);
		List<Vacancy> listVacant = Collections.singletonList(checkVacant.get());
		return new ApiResponse<Vacancy>(HttpStatus.OK.value(), "Activated successfully", listVacant);
	}
	
	/**
	 * Get list of job function
	 * 
	 * @return ApiResponse with JobFunction as the Object Type
	 * 
	 */
	@ApiOperation(value = "This API will genereate list of job function", notes = "All Roles Allowed")
	@GetMapping("/jobFunctions")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<JobFunction> getJobFunctions() {
		List<JobFunction> jobFunctions = jobFunctionRepo.findAll();
		return new ApiResponse<JobFunction>(HttpStatus.OK.value(), "Success", jobFunctions);
	}
}
