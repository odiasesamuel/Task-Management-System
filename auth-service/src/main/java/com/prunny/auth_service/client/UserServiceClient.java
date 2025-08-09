package com.prunny.auth_service.client;

import com.prunny.auth_service.service.dto.CreateUserRequest;
import com.prunny.auth_service.service.dto.CreateUserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    public UserServiceClient(RestTemplate restTemplate, @Value("${user.service.url:http://localhost:8081}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public CreateUserResponse createUserProfile(CreateUserRequest userProfile) {
        try {
            String url = userServiceUrl + "/api/users/internal";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateUserRequest> entity = new HttpEntity<>(userProfile, headers);

            ResponseEntity<CreateUserResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                CreateUserResponse.class
            );

            logger.info("Successfully created user profile for auth user email: {}", response.getBody().getEmail());
            return response.getBody();

        } catch (Exception e) {
            logger.error("Error while creating user for auth user {}: {}",
                userProfile.getEmail(), e.getMessage());
            CreateUserResponse response = new CreateUserResponse();
            response.setMessage("Unexpected response from user service");
            return response;
        }
    }
}
