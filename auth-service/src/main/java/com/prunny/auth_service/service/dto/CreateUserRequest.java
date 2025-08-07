package com.prunny.auth_service.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String name;

    private String phoneNumber;
    private String profilePictureUrl;

    public CreateUserRequest() {}

    public CreateUserRequest(String email, String name, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
