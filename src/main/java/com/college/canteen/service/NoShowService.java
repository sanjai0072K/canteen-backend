package com.college.canteen.service;

import com.college.canteen.entity.Order;
import com.college.canteen.entity.OrderStatus;
import com.college.canteen.entity.Student;
import com.college.canteen.repository.OrderRepository;
import com.college.canteen.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoShowService {

    private static final Logger log = LoggerFactory.getLogger(NoShowService.class);

    // 2 no-shows -> blocked for 1 week
    private static final int MAX_NO_SHOWS = 2;
    private static final int BLOCK_DAYS = 7;

    private final OrderRepository orderRepository;
    private final StudentRepository studentRepository;
    private final long graceMinutes;

    public NoShowService(OrderRepository orderRepository,
                         StudentRepository studentRepository,
                         @Value("${canteen.no-show.grace-minutes:60}") long graceMinutes) {
        this.orderRepository = orderRepository;
        this.studentRepository = studentRepository;
        this.graceMinutes = graceMinutes;
    }

    // runs automatically every 1 minute
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void scheduledCheck() {
        int count = markNoShows();
        if (count > 0) {
            log.info("No-show check: {} order(s) marked as NO_SHOW", count);
        }
    }

    // Orders whose pickup time is over (plus grace time) and food was never collected
    @Transactional
    public int markNoShows() {
        LocalDateTime now = LocalDateTime.now();

        List<Order> overdue = orderRepository.findByStatusInAndPickupTimeBefore(
                List.of(OrderStatus.PLACED, OrderStatus.TOKEN_ISSUED),
                now.minusMinutes(graceMinutes));

        for (Order order : overdue) {
            order.setStatus(OrderStatus.NO_SHOW);

            Student student = order.getStudent();
            student.setNoShowCount(student.getNoShowCount() + 1);

            if (student.getNoShowCount() >= MAX_NO_SHOWS) {
                student.setBlockedUntil(now.plusDays(BLOCK_DAYS));
                student.setNoShowCount(0);
                log.info("Student {} ({}) blocked until {}",
                        student.getName(), student.getApplicationNumber(), student.getBlockedUntil());
            }
            studentRepository.save(student);
        }
        orderRepository.saveAll(overdue);
        return overdue.size();
    }
}
