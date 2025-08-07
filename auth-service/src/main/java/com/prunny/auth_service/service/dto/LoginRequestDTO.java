package com.prunny.auth_service.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public @NotBlank(message = "Email is required") @Email(message = "Email should be a valid email address") String getEmail() {
        return email;
    }

    public @NotBlank(message = "Password is required") String getPassword() {
        return password;
    }
}
