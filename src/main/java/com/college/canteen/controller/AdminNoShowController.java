package com.college.canteen.controller;

import com.college.canteen.service.NoShowService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Runs the no-show check right now (it also runs automatically every minute).
// Useful for testing.
@RestController
@RequestMapping("/api/admin/no-show")
public class AdminNoShowController {

    private final NoShowService noShowService;

    public AdminNoShowController(NoShowService noShowService) {
        this.noShowService = noShowService;
    }

    @PostMapping("/run")
    public Map<String, Integer> run() {
        return Map.of("markedNoShow", noShowService.markNoShows());
    }
}
