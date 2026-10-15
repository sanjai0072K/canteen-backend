package com.college.canteen.controller;

import com.college.canteen.dto.BlockedStudentResponse;
import com.college.canteen.service.StudentAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// Admin only (needs the admin Bearer token, like every /api/admin/** API)
//   GET  /api/admin/students/blocked          -> students blocked right now
//   POST /api/admin/students/{id}/unblock     -> allow the student to order again
@RestController
@RequestMapping("/api/admin/students")
public class AdminStudentController {

    private final StudentAdminService studentAdminService;

    public AdminStudentController(StudentAdminService studentAdminService) {
        this.studentAdminService = studentAdminService;
    }

    @GetMapping("/blocked")
    public List<BlockedStudentResponse> blocked() {
        return studentAdminService.blockedStudents();
    }

    @PostMapping("/{id}/unblock")
    public Map<String, String> unblock(@PathVariable Long id) {
        return Map.of("message", studentAdminService.unblock(id));
    }
}
