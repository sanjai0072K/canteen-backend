package com.college.canteen.dto;

import com.college.canteen.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(String name, BigDecimal price, int quantity, BigDecimal subtotal) {

    public static OrderItemResponse from(OrderItem oi) {
        BigDecimal price = oi.getMenuItem().getPrice();
        return new OrderItemResponse(
                oi.getMenuItem().getName(),
                price,
                oi.getQuantity(),
                price.multiply(BigDecimal.valueOf(oi.getQuantity()))
        );
    }
}
