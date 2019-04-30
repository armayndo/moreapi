package com.mitrais.more.payloads;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class for create vacancy
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancyRequest {
	@NotEmpty(message ="Please provide a name")
	private String position;
	
	private String description;
	
	private boolean active;

}
