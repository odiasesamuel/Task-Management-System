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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class NotificationClient {

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendTaskAssigned(Long taskId, String title, Long assignedToUserId, ZonedDateTime dueDate) {
        String API_URL = "http://notificationservice/api/notifications/task-assigned";

        var payload = Map.of(
            "taskId", taskId,
            "title", title,
            "assignedToUserId", assignedToUserId,
            "dueDate", dueDate
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getCurrentToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.exchange(
            API_URL,
            HttpMethod.POST,
            new HttpEntity<>(payload, headers),
            Void.class
        );
    }


    public void sendTaskCommented(Long taskId, Long commenterUserId,String comment, ZonedDateTime commentedAt) {
        String API_URL = "http://notificationservice/api/notifications/task-commented";
        var payload = Map.of(
            "taskId", taskId,
            "commenterUserId", commenterUserId,
            "comment",comment,
            "commentedAt", commentedAt
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getCurrentToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.exchange(
            API_URL,
            HttpMethod.POST,
            new HttpEntity<>(payload, headers),
            Void.class
        );
    }

    private String getCurrentToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }
}


