package com.prunny.project.client;


import com.prunny.project.service.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TeamServiceClient {

    private final RestTemplate restTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(TeamServiceClient.class);

    public boolean canAccessTeam(Long teamId) {
        String API_URL = "http://userservice/api/teams/" + teamId + "/can-access";

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
            LOG.error("Error checking team access for team {}: {}", teamId, e.getMessage());
            return false;
        }
    }

    public TeamServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getCurrentToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }
}

