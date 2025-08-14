package com.prunny.notification.service.dto;

import java.time.ZonedDateTime;

public record TaskNotificationDTO(Long taskId, String title, String email, ZonedDateTime dueDate) {
}
