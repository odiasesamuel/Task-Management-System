package com.prunny.reportingservice.client;

import com.prunny.reportingservice.service.dto.TaskDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TaskServiceClient {

    private final RestTemplate restTemplate;

    public List<TaskDTO> getAllTasks(Long projectId) {
        String API_URL = "http://taskservice/api/tasks/" +projectId +"/tasks";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getCurrentToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<TaskDTO>> responseType =
            new ParameterizedTypeReference<List<TaskDTO>>() {};

        ResponseEntity<List<TaskDTO>> response = restTemplate.exchange(
            API_URL,
            HttpMethod.GET,
            entity,
            responseType
        );

        return response.getBody();
    }

    public TaskServiceClient(RestTemplate restTemplate) {
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


