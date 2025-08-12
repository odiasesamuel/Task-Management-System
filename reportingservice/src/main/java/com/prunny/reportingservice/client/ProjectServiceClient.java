package com.prunny.reportingservice.client;

import com.prunny.reportingservice.service.dto.ProjectDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProjectServiceClient {
private final RestTemplate restTemplate;

    public ProjectServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProjectDTO> getAllProjects() {
        String API_URL = "http://project/api/projects";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getCurrentToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<ProjectDTO>> responseType =
            new ParameterizedTypeReference<List<ProjectDTO>>() {};

        ResponseEntity<List<ProjectDTO>> response = restTemplate.exchange(
            API_URL,
            HttpMethod.GET,
            entity,
            responseType
        );

        return response.getBody();
    }

    private String getCurrentToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }
}
