package com.college.canteen.service;

import com.college.canteen.entity.Order;
import com.college.canteen.entity.OrderItem;
import com.college.canteen.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

// Makes the admin "sheet" as a CSV file. Excel opens CSV files directly.
@Service
public class ExportService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final OrderRepository orderRepository;

    public ExportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // pickupDate = null -> all orders, otherwise only orders to be picked up on that day
    @Transactional(readOnly = true)
    public byte[] ordersCsv(LocalDate pickupDate) {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(o -> pickupDate == null || o.getPickupTime().toLocalDate().equals(pickupDate))
                .toList();

        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF'); // makes Excel read the file as UTF-8
        csv.append(row("Order ID", "Student Name", "Department", "Application Number", "College",
                "Items", "Total (Rs)", "Pickup Time", "Status", "Token", "Ordered At"));

        for (Order o : orders) {
            String items = o.getItems().stream()
                    .map(i -> i.getMenuItem().getName() + " x" + i.getQuantity())
                    .collect(Collectors.joining("; "));

            BigDecimal total = o.getItems().stream()
                    .map(this::lineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            csv.append(row(
                    String.valueOf(o.getId()),
                    o.getStudent().getName(),
                    o.getStudent().getDepartment(),
                    o.getStudent().getApplicationNumber(),
                    o.getStudent().getCollegeName(),
                    items,
                    total.toPlainString(),
                    o.getPickupTime().format(TIME_FORMAT),
                    o.getStatus().name(),
                    o.getTokenNumber(),
                    o.getCreatedAt().format(TIME_FORMAT)));
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private BigDecimal lineTotal(OrderItem item) {
        return item.getMenuItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private String row(String... columns) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(escape(columns[i]));
        }
        return line.append("\r\n").toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        // a text starting with = + - @ would be run as a formula by Excel. Make it plain text.
        char first = value.isEmpty() ? ' ' : value.charAt(0);
        if (first == '=' || first == '+' || first == '-' || first == '@' || first == '\t' || first == '\r') {
            value = "'" + value;
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
