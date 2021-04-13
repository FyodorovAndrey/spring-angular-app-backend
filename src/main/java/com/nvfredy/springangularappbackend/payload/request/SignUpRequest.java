package com.nvfredy.springangularappbackend.payload.request;

import com.nvfredy.springangularappbackend.annotations.PasswordMatches;
import com.nvfredy.springangularappbackend.annotations.ValidEmail;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@PasswordMatches
public class SignUpRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "It should be email format")
    @ValidEmail
    private String email;
    @NotEmpty(message = "Username is required")
    private String username;
    @NotEmpty(message = "Name is required")
    private String firstName;
    @NotEmpty(message = "Last name is required")
    private String lastName;
    @NotEmpty(message = "Password is required")
    @Size(min = 8)
    private String password;
    private String confirmPassword;
}
