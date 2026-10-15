package com.college.canteen.service;

import com.college.canteen.dto.OrderConfirmation;
import com.college.canteen.dto.OrderItemRequest;
import com.college.canteen.dto.OrderResponse;
import com.college.canteen.dto.PlaceOrderRequest;
import com.college.canteen.entity.MenuItem;
import com.college.canteen.entity.Order;
import com.college.canteen.entity.OrderItem;
import com.college.canteen.entity.Student;
import com.college.canteen.repository.MenuItemRepository;
import com.college.canteen.repository.OrderRepository;
import com.college.canteen.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class OrderService {

    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(12, 30);
    private static final int MAX_DAYS_AHEAD = 1;

    private final OrderRepository orderRepository;
    private final StudentRepository studentRepository;
    private final MenuItemRepository menuItemRepository;
    private final long minHoursBefore;

    public OrderService(OrderRepository orderRepository,
                        StudentRepository studentRepository,
                        MenuItemRepository menuItemRepository,
                        @Value("${canteen.order.min-hours-before:3}") long minHoursBefore) {
        this.orderRepository = orderRepository;
        this.studentRepository = studentRepository;
        this.menuItemRepository = menuItemRepository;
        this.minHoursBefore = minHoursBefore;
    }

    @Transactional
    public OrderConfirmation placeOrder(PlaceOrderRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (student.getBlockedUntil() != null && student.getBlockedUntil().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You cannot place orders until " + student.getBlockedUntil()
                            + " because you did not collect your earlier orders");
        }

        if (request.pickupTime().isBefore(now.plusHours(minHoursBefore))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Orders must be placed at least " + minHoursBefore + " hours before the pickup time");
        }

        LocalDate pickupDate = request.pickupTime().toLocalDate();
        if (pickupDate.isAfter(now.toLocalDate().plusDays(MAX_DAYS_AHEAD))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Orders can only be placed for today or tomorrow");
        }

        LocalTime pickupTimeOfDay = request.pickupTime().toLocalTime();
        if (pickupTimeOfDay.isBefore(LUNCH_START) || pickupTimeOfDay.isAfter(LUNCH_END)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pickup time must be between 12:00 PM and 12:30 PM (lunch only)");
        }

        DayOfWeek pickupDay = request.pickupTime().getDayOfWeek();

        Order order = new Order();
        order.setStudent(student);
        order.setPickupTime(request.pickupTime());

        for (OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Menu item " + itemRequest.menuItemId() + " not found"));

            if (!menuItem.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        menuItem.getName() + " is not available");
            }

            if (menuItem.getDay() != null && menuItem.getDay() != pickupDay) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        menuItem.getName() + " is not served on " + pickupDay);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.quantity());
            order.getItems().add(orderItem);
        }

        Order saved = orderRepository.save(order);

        String message = "Order placed successfully. Order number: " + saved.getId()
                + ". Go to the canteen counter, tell your application number, pay and collect your token.";

        return new OrderConfirmation(message, OrderResponse.from(saved));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> ordersOfStudent(Long studentId) {
        return orderRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> allOrders() {
        return orderRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(OrderResponse::from)
                .toList();
    }
}