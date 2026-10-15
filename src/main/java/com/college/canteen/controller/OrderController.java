package com.college.canteen.controller;

import com.college.canteen.dto.OrderConfirmation;
import com.college.canteen.dto.OrderResponse;
import com.college.canteen.dto.PlaceOrderRequest;
import com.college.canteen.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// For students: place an order and see their own orders
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderConfirmation> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(request));
    }

    @GetMapping("/student/{studentId}")
    public List<OrderResponse> myOrders(@PathVariable Long studentId) {
        return orderService.ordersOfStudent(studentId);
    }
}
