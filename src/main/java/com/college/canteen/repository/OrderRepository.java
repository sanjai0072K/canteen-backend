
package com.college.canteen.repository;

import com.college.canteen.entity.Order;
import com.college.canteen.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findAllByOrderByCreatedAtAsc();

    // used by the scheduler to find orders that were never collected
    List<Order> findByStatusInAndPickupTimeBefore(List<OrderStatus> statuses, LocalDateTime time);
}
