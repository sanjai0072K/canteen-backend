package com.college.canteen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "Menu item id is required") Long menuItemId,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity
) {
}
