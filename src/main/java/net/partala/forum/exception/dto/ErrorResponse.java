package net.partala.forum.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String detailedMessage,
        LocalDateTime time
) {
}
