package com.prunny.auth_service.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    public @NotBlank(message = "Email is required") @Email(message = "Email should be a valid email address") String getEmail() {
        return email;
    }

    public @NotBlank(message = "Password is required") String getPassword() {
        return password;
    }

    public @NotBlank(message = "name is required") String getName() {
        return name;
    }

    public @NotBlank(message = "Phone number is required") String getPhoneNumber() {
        return phoneNumber;
    }
}
