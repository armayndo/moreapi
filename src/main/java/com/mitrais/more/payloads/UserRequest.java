package com.mitrais.more.payloads;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * This class is used to register a new user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank
    @Size(min = 4, max = 40)
    public String name;

    @NotBlank
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    private boolean sendMeEmail;
}