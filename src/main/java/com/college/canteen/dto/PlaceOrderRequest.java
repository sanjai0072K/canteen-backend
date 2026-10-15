package com.college.canteen.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PlaceOrderRequest(
        @NotNull(message = "Student id is required") Long studentId,
        @NotNull(message = "Pickup time is required") LocalDateTime pickupTime,
        @NotEmpty(message = "Select at least one item") @Valid List<OrderItemRequest> items
) {
}
