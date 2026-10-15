package com.college.canteen.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Application number is required") String applicationNumber
) {
}
