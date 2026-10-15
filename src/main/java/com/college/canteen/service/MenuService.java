package com.college.canteen.service;

import com.college.canteen.dto.MenuItemRequest;
import com.college.canteen.dto.MenuItemResponse;
import com.college.canteen.entity.MenuItem;
import com.college.canteen.repository.MenuItemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    // students see only available items. If a day is given, only that day's items
    // (plus items served every day).
    public List<MenuItemResponse> listAvailable(DayOfWeek day) {
        List<MenuItem> items = (day == null)
                ? menuItemRepository.findByAvailableTrue()
                : menuItemRepository.findAvailableForDay(day);
        return items.stream().map(MenuItemResponse::from).toList();
    }

    // admin sees everything
    public List<MenuItemResponse> listAll() {
        return menuItemRepository.findAll().stream()
                .map(MenuItemResponse::from)
                .toList();
    }

    public MenuItemResponse create(MenuItemRequest request) {
        MenuItem item = new MenuItem();
        item.setName(request.name().trim());
        item.setPrice(request.price());
        item.setAvailable(request.available() == null || request.available());
        item.setDay(request.day());
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    public MenuItemResponse update(Long id, MenuItemRequest request) {
        MenuItem item = find(id);
        item.setName(request.name().trim());
        item.setPrice(request.price());
        if (request.available() != null) {
            item.setAvailable(request.available());
        }
        item.setDay(request.day());
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    public void delete(Long id) {
        MenuItem item = find(id);
        try {
            menuItemRepository.delete(item);
        } catch (DataIntegrityViolationException e) {
            // item is already used in some student's order
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This item is used in existing orders. Mark it as unavailable instead of deleting.");
        }
    }

    private MenuItem find(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
    }
}
