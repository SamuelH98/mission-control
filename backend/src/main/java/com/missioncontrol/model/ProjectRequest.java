package com.missioncontrol.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "title is required")
        @Size(max = 50, message = "title must be at most 50 characters")
        String title,

        @Size(max = 500, message = "description must be at most 500 characters")
        String description,

        @NotNull(message = "status is required")
        ProjectStatus status,

        @NotNull(message = "manager is required")
        Integer managerId
) {
}
