package com.prunny.notification.service.dto;

import java.time.ZonedDateTime;

public record TaskCommentNoitificationDTO(String email, String title, String comment, ZonedDateTime commentedAt) {
}
