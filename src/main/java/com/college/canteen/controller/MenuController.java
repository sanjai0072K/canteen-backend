package com.college.canteen.controller;

import com.college.canteen.dto.MenuItemResponse;
import com.college.canteen.service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.util.List;

// For students: see the available menu
//   GET /api/menu               -> all available items
//   GET /api/menu?day=MONDAY    -> Monday's items (+ items served every day)
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuItemResponse> availableMenu(@RequestParam(required = false) DayOfWeek day) {
        return menuService.listAvailable(day);
    }
}
