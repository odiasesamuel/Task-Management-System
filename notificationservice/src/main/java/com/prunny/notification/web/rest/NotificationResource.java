package com.prunny.notification.web.rest;

import com.prunny.notification.service.dto.TaskCommentNoitificationDTO;
import com.prunny.notification.service.dto.TaskNotificationDTO;
import com.prunny.notification.service.impl.NotificationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {

    private final NotificationServiceImpl notificationService;

    public NotificationResource(NotificationServiceImpl notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/task-assigned")
    public ResponseEntity<TaskNotificationDTO> taskAssigned(@RequestBody Map<String, Object> payload) {
        TaskNotificationDTO taskNotificationDTO= notificationService.onTaskAssigned(payload);
        return ResponseEntity.ok(taskNotificationDTO);
    }

    @PostMapping("/task-commented")
    public ResponseEntity<TaskCommentNoitificationDTO> taskCommented(@RequestBody Map<String, Object> payload) {
        TaskCommentNoitificationDTO commentNoitificationDTO= notificationService.onTaskCommented(payload);
        notificationService.onTaskCommented(payload);
        return ResponseEntity.ok(commentNoitificationDTO);
    }
}

