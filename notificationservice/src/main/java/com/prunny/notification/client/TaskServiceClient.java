package com.prunny.notification.client;
import com.prunny.notification.service.dto.TaskDTO;
import com.prunny.notification.service.dto.UserResponseDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TaskServiceClient {
    private final RestTemplate restTemplate;

    public TaskServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TaskDTO getTask(Long taskId) {
        String API_URL = "http://taskservice/api/tasks/" +taskId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getCurrentToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);


        ResponseEntity<TaskDTO> response = restTemplate.exchange(
            API_URL,
            HttpMethod.GET,
            entity,
            TaskDTO.class
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

