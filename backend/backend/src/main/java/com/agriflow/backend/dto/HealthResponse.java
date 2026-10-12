package com.agriflow.backend.dto;

import java.time.Instant;
import java.util.Map;

public record HealthResponse(
    String status,
    String application,
    Instant timestamp,
    Map<String, Object> database
) {}
