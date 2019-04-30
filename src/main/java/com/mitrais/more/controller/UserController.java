package com.mitrais.more.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;  
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mitrais.more.exception.AppException;
import com.mitrais.more.model.Role;
import com.mitrais.more.model.User;
import com.mitrais.more.payloads.ApiResponse;
import com.mitrais.more.payloads.UserRequest;
import com.mitrais.more.repository.RoleRepository;
import com.mitrais.more.repository.UserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;

/*
 * This class is operate user model process
 * operate read, create, update and delete
 */
@Api(description="User API")
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {
	/**
	 * Base URL for this User API
	 */
	public static final String BASE_URL = "/api/v1/users";
	/**
	 * BcryptPasswordEncoder encoded user password
	 * when registering a new user
	 */
    private PasswordEncoder passwordEncoder;
    /**
     * UserRepository implemented from JpaRepository
     */
    private UserRepository userRepository;
    /**
     * RoleRepository implemented from JpaRepository
     */
    private RoleRepository roleRepository;
    
	@Autowired
	public UserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}

	/**
	 * Get list of users
 	 * 
 	 * @return ApiResponse with User as the Object Type 
 	 * 
	 */
	@ApiOperation(value="This API will genereate list of user", notes="Only Admin Allowed to access")
	@Secured("ROLE_ADMIN")
	@GetMapping
	public ApiResponse<User> getUser() {
		List<User> users = userRepository.findAll();
		return new ApiResponse<User>(HttpStatus.OK.value(),"Success", users);
	}
	
	/**
	 * Get list of user by ID
	 * @Return ApiResponse with User as the Object
	 * 
	 */
	
	@ApiOperation(value="This API will generate list user filter by ID", notes="Only Admin Allowed to access")
	@Secured("ROLE_ADMIN")
	@GetMapping("/{id}")
	public ApiResponse<User> getUserById(@PathVariable long id){
		Optional<User> checkUser = userRepository.findById(id);
/*
		if (checkUser.isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "user Not Found!");
		}
		*/
		
		List<User> user = checkUser.isPresent()?Collections.singletonList(checkUser.get()): Collections.emptyList();
		return new ApiResponse<User>(HttpStatus.OK.value(), "Success", user);
	}
	/**
	 * Create Users
	 * this API only allowed to ADMIN
	 * 
	 * @param userRequest {@link UserRequest}
	 * 
	 * @return ApiResponse object {@link ApiResponse}
	 */
	@ApiOperation(value="This API will create a user.", notes="Only Admin Allowed to access")
	@Secured("ROLE_ADMIN")
    @PostMapping
    public ApiResponse<String> registerUser(@Valid @RequestBody UserRequest userRequest) {
        if(userRepository.existsByUsername(userRequest.getUsername())) {
            return new ApiResponse<>(HttpStatus.CONFLICT.value(), "Username Already Registered!");
        }

        if(userRepository.existsByEmail(userRequest.getEmail())) {
            return new ApiResponse<>(HttpStatus.CONFLICT.value(), "Email Already Registered!");
        }

        // Creating user's account
        User user = new User(userRequest.getUsername(), userRequest.getPassword(), userRequest.getEmail(), userRequest.getName());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRole(userRole);
        user.setSendMeEmail(userRequest.isSendMeEmail());

        userRepository.save(user);
        List<String> userList = new ArrayList<>();
        userList.add(user.getId().toString());
        return new ApiResponse<String>(HttpStatus.OK.value(), "User registered successfully", userList);
    }
    
	/**
	 * Update user with a new user object
	 * this API only allowed to ADMIN
	 * 
	 * @param user represent new User object {@link User}
	 * @param id represent userId being updated
	 * 
	 * @return ApiResponse object
	 */
	@ApiOperation(value="This API will update user with a new user object.", notes="Only Admin Allowed to access")
	@Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@RequestBody User user,@PathVariable long id){
    	try {
    		User oldUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        	
        	oldUser.setName(user.getName());
        	oldUser.setEmail(user.getEmail());
        	oldUser.setActive(user.isActive());
        	oldUser.setSendMeEmail(user.isSendMeEmail());
        	userRepository.save(oldUser);
        	List<User> listUser = Collections.singletonList(oldUser);
        	return new ApiResponse<User>(HttpStatus.OK.value(), "User updated successfully",listUser);
		} catch (NotFoundException e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
    	
    }
    
	/**
	 * Delete a User
	 * this API only allowed to ADMIN
	 * 
	 * @param id represent the userId
	 * 
	 * @return ApiResponse object
	 */
	@ApiOperation(value="This API will delete a user.", notes="Only Admin Allowed to access")
	@Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable long id) {
    	Optional<User> checkUser = userRepository.findById(id);
    	if (!checkUser.isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User Not Found!");
		}
    	userRepository.deleteById(id);
    	return new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully");
    }
    
}
