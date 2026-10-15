package com.college.canteen.dto;

import com.college.canteen.entity.OrderStatus;

public record OrderStatusResponse(Long orderId, OrderStatus status, String tokenNumber, String message) {
}
