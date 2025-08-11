package com.prunny.user_service.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/**
 * A DTO for the {@link com.prunny.user_service.domain.User} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserRequestDTO implements Serializable {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(0\\d{10}|\\+\\d{1,15})$",
        message = "Invalid phone number format (e.g., 08012345678 or +2348012345678)"
    )
    private String phoneNumber;

    private String profilePictureUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDTO{" +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", profilePictureUrl='" + getProfilePictureUrl() + "'" +
            "}";
    }
}
