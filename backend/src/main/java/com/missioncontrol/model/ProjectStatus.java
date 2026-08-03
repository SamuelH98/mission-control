package com.missioncontrol.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStatus {
    ACTIVE("active"),
    COMPLETED("completed"),
    PLANNED("planned");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProjectStatus from(String value) {
        for (ProjectStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }

    public static ProjectStatus fromDb(String value) {
        return from(value);
    }
}
