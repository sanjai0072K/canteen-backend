package com.college.canteen.repository;

import com.college.canteen.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByAvailableTrue();

    // items for one day + items served every day (day is null)
    @Query("select m from MenuItem m where m.available = true and (m.day = :day or m.day is null)")
    List<MenuItem> findAvailableForDay(@Param("day") DayOfWeek day);
}
