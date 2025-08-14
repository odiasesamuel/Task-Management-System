package com.prunny.task.client;

import com.prunny.task.service.dto.ProjectDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;


@Service
public class ProjectServiceClient {

    private final RestTemplate restTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceClient.class);

    public ProjectServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<ProjectDTO> getProject(Long projectId) {
        String API_URL = "http://project/api/projects/" +projectId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getCurrentToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<ProjectDTO> response = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ProjectDTO>() {}
            );
            return Optional.ofNullable(response.getBody());

        } catch (HttpClientErrorException.NotFound ex) {

            return Optional.empty();
        }
    }

    public boolean canAccessProject(Long projectId) {
        String API_URL = "http://project/api/projects/" + projectId + "/can-access";

        try {
            HttpHeaders headers = new HttpHeaders();
            String token = getCurrentToken();
            if (token != null) {
                headers.set("Authorization", "Bearer " + token);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Boolean> response = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                entity,
                Boolean.class
            );

            return response.getBody();

        } catch (Exception e) {
            LOG.error("Error checking team access for team {}: {}", projectId, e.getMessage());
            return false;
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


