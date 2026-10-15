package com.college.canteen.dto;

import com.college.canteen.entity.Order;
import com.college.canteen.entity.OrderStatus;
import com.college.canteen.entity.Student;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        LocalDateTime pickupTime,
        LocalDateTime createdAt,
        String tokenNumber,
        String studentName,
        String department,
        String applicationNumber,
        List<OrderItemResponse> items,
        BigDecimal totalAmount
) {
    public static OrderResponse from(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        BigDecimal total = items.stream()
                .map(OrderItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Student s = o.getStudent();
        return new OrderResponse(
                o.getId(),
                o.getStatus(),
                o.getPickupTime(),
                o.getCreatedAt(),
                o.getTokenNumber(),
                s.getName(),
                s.getDepartment(),
                s.getApplicationNumber(),
                items,
                total
        );
    }
}
