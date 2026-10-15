package com.college.canteen.controller;

import com.college.canteen.dto.OrderStatusResponse;
import com.college.canteen.service.TokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// For students: the website checks this to show confirmation messages
@RestController
@RequestMapping("/api/orders")
public class OrderStatusController {

    private final TokenService tokenService;

    public OrderStatusController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/{orderId}/status")
    public OrderStatusResponse status(@PathVariable Long orderId) {
        return tokenService.statusOf(orderId);
    }
}
