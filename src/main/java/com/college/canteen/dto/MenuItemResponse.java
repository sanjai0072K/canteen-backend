package com.college.canteen.dto;

import com.college.canteen.entity.MenuItem;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public record MenuItemResponse(Long id, String name, BigDecimal price, boolean available, DayOfWeek day) {

    public static MenuItemResponse from(MenuItem m) {
        return new MenuItemResponse(m.getId(), m.getName(), m.getPrice(), m.isAvailable(), m.getDay());
    }
}
