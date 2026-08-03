package com.missioncontrol.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManagerRequest(
        @NotBlank(message = "first name is required")
        @Size(max = 25, message = "first name must be at most 25 characters")
        String firstName,

        @NotBlank(message = "last name is required")
        @Size(max = 25, message = "last name must be at most 25 characters")
        String lastName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid address")
        @Size(max = 64, message = "email must be at most 64 characters")
        String email
) {
}
