package com.mitrais.more.controller;

import java.util.Arrays; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.more.model.Lookup;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.security.CurrentUser;
import com.mitrais.more.security.UserPrincipal;
import com.mitrais.more.service.SettingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;

/**
 * 
 * SettingController
 * Manage value of CONSTANT named SETTING in Lookup model
 *
 */
@Api(description="Application Settings API")
@RestController
@RequestMapping(SettingController.BASE_URL)
public class SettingController {
	/**
	 * Base URL for Settings API
	 */
	public static final String BASE_URL = "/api/v1/settings";
	
	/** The setting service. */
	private SettingService settingService;

	
	/**
	 * Instantiates a new setting controller.
	 *
	 * @param settingService the setting service
	 */
	@Autowired
	public SettingController(SettingService settingService) {
		this.settingService = settingService;
	}

	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	@ApiOperation(value="This API will generate setting list", notes="Only Admin Allowed to access")
	@Secured("ROLE_ADMIN")
	@GetMapping
	public ApiResponse<Lookup> getSettings() {
		return new ApiResponse<Lookup>(HttpStatus.OK.value(), "Succes", settingService.getAll());
	}
	
	/**
	 * Update setting.
	 *
	 * @param lookup the lookup
	 * @param id the lookup id
	 * 
	 * @return the api response
	 */
	@ApiOperation(value="This API will generate update setting value", notes="Only Admin Allowed to access")
	@Secured("ROLE_ADMIN")
	@PutMapping("/{id}")
	public ApiResponse<Lookup> updateSetting(@RequestBody String value,  @PathVariable Long id, @CurrentUser UserPrincipal currentUser) {
		try {
	    	Lookup setting = settingService.updateSetting(id, value, currentUser);
	    	
	    	return new ApiResponse<Lookup>(HttpStatus.OK.value(),"Setting Updated successfully", Arrays.asList(setting));
		} catch (NotFoundException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
	
}
