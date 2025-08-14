package com.prunny.notification.service.impl;

import com.prunny.notification.client.TaskServiceClient;
import com.prunny.notification.client.UserServiceClient;
import com.prunny.notification.service.NotificationService;
import com.prunny.notification.service.dto.TaskCommentNoitificationDTO;
import com.prunny.notification.service.dto.TaskNotificationDTO;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;

@Service
public class NotificationServiceImpl {

    private final UserServiceClient userClient;
    private final TaskServiceClient taskServiceClient;
//    private final T taskClient;

    public NotificationServiceImpl(UserServiceClient userClient, TaskServiceClient taskServiceClient) {
        this.userClient = userClient;
//        this.taskClient = taskClient;
        this.taskServiceClient = taskServiceClient;
    }

    public TaskNotificationDTO onTaskAssigned(Map<String, Object> payload) {
        Long userId = ((Number) payload.get("assignedToUserId")).longValue(); //get the assigned user id from payload
        Long taskId = ((Number) payload.get("taskId")).longValue();
        String title = (String) payload.get("title");
        String dateTimeString = (String) payload.get("dueDate");
        ZonedDateTime dueDate = ZonedDateTime.parse(dateTimeString);


        var user = userClient.getCurrentUser(userId);
//        var task = taskClient.getTask(taskId);

        System.out.println("📢 Task Assigned Event Received");
        System.out.println("User: " + user);
        return new TaskNotificationDTO(taskId,title,user.getEmail(),dueDate);
    }

    public TaskCommentNoitificationDTO onTaskCommented(Map<String, Object> payload) {
        Long commenterId = ((Number) payload.get("commenterUserId")).longValue();
        Long taskId = ((Number) payload.get("taskId")).longValue();
        String comment = (String) payload.get("comment");
        String dateTimeString = (String) payload.get("commentedAt");
        ZonedDateTime commentedAt = ZonedDateTime.parse(dateTimeString);

        var user = userClient.getCurrentUser(commenterId);
        var task = taskServiceClient.getTask(taskId);

        System.out.println("💬 Task Commented Event Received");
        System.out.println("Commenter: " + user);
        return new TaskCommentNoitificationDTO(user.getEmail(),task.getTitle(),comment,commentedAt);

    }
}

