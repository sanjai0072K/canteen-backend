package com.college.canteen.service;

import com.college.canteen.dto.BlockedStudentResponse;
import com.college.canteen.entity.Student;
import com.college.canteen.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

// Admin tools for students who are blocked (2 uncollected orders -> 7 days block)
@Service
public class StudentAdminService {

    private final StudentRepository studentRepository;

    public StudentAdminService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // students who are blocked right now, the one who gets free first on top
    @Transactional(readOnly = true)
    public List<BlockedStudentResponse> blockedStudents() {
        LocalDateTime now = LocalDateTime.now();
        return studentRepository.findAll().stream()
                .filter(s -> s.getBlockedUntil() != null && s.getBlockedUntil().isAfter(now))
                .sorted(Comparator.comparing(Student::getBlockedUntil))
                .map(BlockedStudentResponse::from)
                .toList();
    }

    // removes the block and clears the no-show count, so the student can order again
    @Transactional
    public String unblock(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        student.setBlockedUntil(null);
        student.setNoShowCount(0);
        studentRepository.save(student);

        return student.getName() + " (" + student.getApplicationNumber() + ") can place orders again.";
    }
}
