package com.mitrais.more.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mitrais.more.exception.AppException;
import com.mitrais.more.model.Lookup;
import com.mitrais.more.repository.LookupRepository;
import com.mitrais.more.security.UserPrincipal;

import javassist.NotFoundException;

/**
 * 
 * SettingService
 * provide services of CONSTANT named SETTING in Lookup model
 *
 */
@Service
public class SettingService {
	/** The lookup repository. */
	private LookupRepository lookupRepository;

	/**
	 * Instantiates a new setting service.
	 *
	 * @param lookupRepository the lookup repository
	 */
	@Autowired
	public SettingService(LookupRepository lookupRepository) {
		this.lookupRepository = lookupRepository;
	}
	
	/**
	 * Gets the all settings.
	 *
	 * @return Settings List
	 */
	public List<Lookup> getAll() {
		try {
			return lookupRepository.findByName("SETTING");
		} catch (Exception e) {
			throw new AppException("Failed to generate all settings lookup");
		}
	}
	
	/**
	 * Gets the setting by id.
	 *
	 * @param id the id
	 * @return the setting
	 */
	public Lookup getSettingById(Long id) {
		try {
			return lookupRepository.findById(id).orElseThrow(() -> new NotFoundException("Setting not found"));
		} catch (NotFoundException e) {
			throw new AppException(e.getMessage());
		}
	}
	
	/**
	 * Gets the setting by keyword.
	 *
	 * @param keyword the keyword
	 * @return the setting by keyword
	 */
	public Lookup getSettingByKeyword(String keyword) {
		try {
			return lookupRepository.findByNameAndKeyword("SETTING", keyword);
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
	}
	
	/**
	 * Update setting.
	 *
	 * @param id the id
	 * @param lookup the lookup
	 * @param currentUser the current user
	 * @return the lookup
	 * @throws NotFoundException 
	 */
	@Transactional
	public Lookup updateSetting(Long id, String value, UserPrincipal currentUser) throws NotFoundException {
		try {
	    	Lookup setting = lookupRepository.findById(id).orElseThrow(() -> new NotFoundException("Setting not found"));
	    	
	    	setting.setValue(value);
	    	setting.setUpdatedBy(currentUser.getName());
	    	lookupRepository.save(setting);
	    	
	    	return setting;
		} catch (NotFoundException e) {
			throw new NotFoundException("Setting not found");
		}
	}
}
