package com.college.canteen.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Department is required") String department,
        @NotBlank(message = "Application number is required") String applicationNumber,
        @NotBlank(message = "College name is required") String collegeName
) {
}
