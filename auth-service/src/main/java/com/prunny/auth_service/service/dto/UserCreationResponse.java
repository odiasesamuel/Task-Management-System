package com.prunny.auth_service.service.dto;

public class UserCreationResponse {
    private String email;
    private String name;
    private String message;

    public UserCreationResponse() {}

    // Getters and setters

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "UserCreationResponse{" +
            ", email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
