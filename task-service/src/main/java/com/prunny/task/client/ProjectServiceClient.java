package com.prunny.task.client;

import com.prunny.task.service.dto.ProjectDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Service
public class ProjectServiceClient {

    private final RestTemplate restTemplate;

    public ProjectServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ProjectDTO> getProject(Long projectId) {
        String API_URL = "http://project/api/projects/" +projectId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getCurrentToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
//        ParameterizedTypeReference<Optional<ProjectDTO>> responseType =
//            new ParameterizedTypeReference<Optional<ProjectDTO>>() {};
//
//
//        ResponseEntity<Optional<ProjectDTO>> response = restTemplate.exchange(
//            API_URL,
//            HttpMethod.GET,
//            entity,
//            responseType
//        );
//
//        return response.getBody();
        try {
            ResponseEntity<ProjectDTO> response = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ProjectDTO>() {}
            );
            return Optional.ofNullable(response.getBody());

        } catch (HttpClientErrorException.NotFound ex) {
            // Project not found → return empty
            return Optional.empty();
        }
    }

    private String getCurrentToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }
}


