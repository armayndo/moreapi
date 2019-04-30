package com.mitrais.more.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * This class is represents user's role 
 * to give permission for user to perform a group of tasks
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

    @Column(length = 60)
    private String name;
}
