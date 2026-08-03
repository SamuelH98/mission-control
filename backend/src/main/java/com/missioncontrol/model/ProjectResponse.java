package com.missioncontrol.model;

public record ProjectResponse(
        int id,
        String title,
        String description,
        ProjectStatus status,
        Integer managerId,
        String managerName
) {
}
