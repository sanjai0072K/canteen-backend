package com.college.canteen.controller;

import com.college.canteen.dto.MenuItemRequest;
import com.college.canteen.dto.MenuItemResponse;
import com.college.canteen.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// For canteen admin: add, edit, delete menu items
// (admin login will be added later; for now these are open for testing)
@RestController
@RequestMapping("/api/admin/menu")
public class AdminMenuController {

    private final MenuService menuService;

    public AdminMenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuItemResponse> allItems() {
        return menuService.listAll();
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.create(request));
    }

    @PutMapping("/{id}")
    public MenuItemResponse update(@PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return menuService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Map.of("message", "Menu item deleted");
    }
}
