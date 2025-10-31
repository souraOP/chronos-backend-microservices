package com.chronos.common.exception;

import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime timestamp,
        String status,
        String message,
        String path
) {
}
