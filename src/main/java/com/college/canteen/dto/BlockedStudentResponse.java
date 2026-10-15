package com.college.canteen.dto;

import com.college.canteen.entity.Student;

import java.time.LocalDateTime;

public record BlockedStudentResponse(
        Long id,
        String name,
        String department,
        String applicationNumber,
        String collegeName,
        LocalDateTime blockedUntil
) {
    public static BlockedStudentResponse from(Student s) {
        return new BlockedStudentResponse(
                s.getId(),
                s.getName(),
                s.getDepartment(),
                s.getApplicationNumber(),
                s.getCollegeName(),
                s.getBlockedUntil()
        );
    }
}
