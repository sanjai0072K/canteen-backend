package com.college.canteen.controller;

import com.college.canteen.dto.OrderResponse;
import com.college.canteen.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// For canteen admin: the "sheet" of all student orders
// (admin login will be added later; for now this is open for testing)
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> allOrders() {
        return orderService.allOrders();
    }
}
