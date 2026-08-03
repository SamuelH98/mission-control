package com.missioncontrol.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectStatusTest {

    @Test
    void parsesLowercaseValues() {
        assertEquals(ProjectStatus.ACTIVE, ProjectStatus.from("active"));
        assertEquals(ProjectStatus.COMPLETED, ProjectStatus.from("completed"));
        assertEquals(ProjectStatus.PLANNED, ProjectStatus.from("planned"));
    }

    @Test
    void isCaseInsensitive() {
        assertEquals(ProjectStatus.ACTIVE, ProjectStatus.from("ACTIVE"));
        assertEquals(ProjectStatus.ACTIVE, ProjectStatus.from("Active"));
    }

    @Test
    void rejectsUnknownValues() {
        assertThrows(IllegalArgumentException.class, () -> ProjectStatus.from("on-hold"));
    }
}
