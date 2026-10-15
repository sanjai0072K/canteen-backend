package com.college.canteen.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public record MenuItemRequest(
        @NotBlank(message = "Item name is required") String name,
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price cannot be negative") BigDecimal price,
        Boolean available,
        DayOfWeek day
) {
}
