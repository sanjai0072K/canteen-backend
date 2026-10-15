package com.college.canteen.dto;

import com.college.canteen.entity.Student;

public record StudentResponse(
        Long id,
        String name,
        String department,
        String applicationNumber,
        String collegeName
) {
    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getName(),
                s.getDepartment(),
                s.getApplicationNumber(),
                s.getCollegeName()
        );
    }
}
