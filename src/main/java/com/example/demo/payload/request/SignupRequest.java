package com.example.demo.payload.request;

import com.example.demo.annotations.PasswordMatches;
import com.example.demo.annotations.ValidEmail;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@PasswordMatches
public class SignupRequest {

    @Email(message = "It should have email format")
    @NotBlank(message = "User email is required")
    @ValidEmail
    private String email;
    @NotEmpty(message = "Please enter your name")
    private String firstname;
    @NotEmpty(message = "Please enter your lastname")
    private String lastname;
    @NotEmpty(message = "Please enter your username")
    private String username;
    @NotEmpty(message = "Password is required")
    @Size(min = 6)
    private String password;

    private String confirmPassword;

    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @NotEmpty(message = "RF Reader ID Token is required")
    private String rfReaderIdToken;

    @NotEmpty(message = "Position is required")
    private String position;

    @NotEmpty(message = "Responsibility is required")
    private String responsibility;
}
