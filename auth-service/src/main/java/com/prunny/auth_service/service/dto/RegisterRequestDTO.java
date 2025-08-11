package com.prunny.auth_service.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one number"
    )
    private String password;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(0\\d{10}|\\+\\d{1,15})$",
        message = "Invalid phone number format (e.g., 08012345678 or +2348012345678)"
    )
    private String phoneNumber;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
