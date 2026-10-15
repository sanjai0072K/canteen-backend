package com.college.canteen.service;

import com.college.canteen.dto.OrderConfirmation;
import com.college.canteen.dto.OrderResponse;
import com.college.canteen.dto.OrderStatusResponse;
import com.college.canteen.entity.Order;
import com.college.canteen.entity.OrderStatus;
import com.college.canteen.entity.Student;
import com.college.canteen.repository.OrderRepository;
import com.college.canteen.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TokenService {

    private final OrderRepository orderRepository;
    private final StudentRepository studentRepository;

    public TokenService(OrderRepository orderRepository, StudentRepository studentRepository) {
        this.orderRepository = orderRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> pendingOrders(String applicationNumber) {
        return searchOrders(applicationNumber).stream()
                .filter(o -> o.status() == OrderStatus.PLACED)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> searchOrders(String applicationNumber) {
        Student student = studentRepository.findByApplicationNumber(applicationNumber.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No student found with this application number"
                ));

        return orderRepository.findByStudentIdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> uncollectedOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.PLACED
                        || o.getStatus() == OrderStatus.TOKEN_ISSUED)
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> collectedOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.COLLECTED)
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public OrderConfirmation issueToken(Long orderId) {
        Order order = findOrder(orderId);

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Token can be issued only for new orders. This order is " + order.getStatus()
            );
        }

        String token = String.format("T%04d", order.getId());
        order.setTokenNumber(token);
        order.setStatus(OrderStatus.TOKEN_ISSUED);
        orderRepository.save(order);

        return new OrderConfirmation(
                "Payment received. Token " + token + " issued to " + order.getStudent().getName(),
                OrderResponse.from(order)
        );
    }

    @Transactional
    public OrderConfirmation collectOrder(Long orderId) {
        Order order = findOrder(orderId);

        if (order.getStatus() == OrderStatus.COLLECTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This order is already collected"
            );
        }

        if (order.getStatus() != OrderStatus.TOKEN_ISSUED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Issue the token after receiving payment before collecting food"
            );
        }

        order.setStatus(OrderStatus.COLLECTED);
        orderRepository.save(order);

        return new OrderConfirmation(
                "Food collected by " + order.getStudent().getName()
                        + ". Order " + order.getId() + " completed.",
                OrderResponse.from(order)
        );
    }

    @Transactional
    public OrderConfirmation collect(String tokenNumber) {
        Order order = findByToken(tokenNumber);
        return collectOrder(order.getId());
    }

    @Transactional(readOnly = true)
    public OrderStatusResponse statusOf(Long orderId) {
        Order order = findOrder(orderId);

        String message = switch (order.getStatus()) {
            case PLACED -> "Order placed. Pay at the canteen counter to get your token.";
            case TOKEN_ISSUED -> "Token " + order.getTokenNumber()
                    + " issued. Show it at the counter to collect your food.";
            case COLLECTED -> "Food collected. Thank you!";
            case NO_SHOW -> "You did not collect this order.";
        };

        return new OrderStatusResponse(
                order.getId(),
                order.getStatus(),
                order.getTokenNumber(),
                message
        );
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found"
                ));
    }

    private Order findByToken(String tokenNumber) {
        String token = tokenNumber.trim().toUpperCase();

        if (!token.matches("T\\d+")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token");
        }

        Order order = orderRepository.findById(Long.parseLong(token.substring(1)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Invalid token"
                ));

        if (!token.equals(order.getTokenNumber())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token");
        }

        return order;
    }
}
