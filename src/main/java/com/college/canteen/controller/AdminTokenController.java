package com.college.canteen.controller;

import com.college.canteen.dto.OrderConfirmation;
import com.college.canteen.dto.OrderResponse;
import com.college.canteen.service.TokenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tokens")
public class AdminTokenController {

    private final TokenService tokenService;

    public AdminTokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/pending")
    public List<OrderResponse> pending(@RequestParam String applicationNumber) {
        return tokenService.pendingOrders(applicationNumber);
    }

    @GetMapping("/search")
    public List<OrderResponse> search(@RequestParam String applicationNumber) {
        return tokenService.searchOrders(applicationNumber);
    }

    @GetMapping("/uncollected")
    public List<OrderResponse> uncollected() {
        return tokenService.uncollectedOrders();
    }

    @GetMapping("/collected")
    public List<OrderResponse> collected() {
        return tokenService.collectedOrders();
    }

    @PostMapping("/issue/{orderId}")
    public OrderConfirmation issue(@PathVariable Long orderId) {
        return tokenService.issueToken(orderId);
    }

    @PostMapping("/collect-order/{orderId}")
    public OrderConfirmation collectOrder(@PathVariable Long orderId) {
        return tokenService.collectOrder(orderId);
    }

    @PostMapping("/collect/{tokenNumber}")
    public OrderConfirmation collect(@PathVariable String tokenNumber) {
        return tokenService.collect(tokenNumber);
    }
}
