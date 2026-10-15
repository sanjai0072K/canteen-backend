package com.college.canteen.service;

import com.college.canteen.dto.AuthResponse;
import com.college.canteen.dto.LoginRequest;
import com.college.canteen.dto.RegisterRequest;
import com.college.canteen.dto.StudentResponse;
import com.college.canteen.entity.Student;
import com.college.canteen.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final StudentRepository studentRepository;

    public AuthService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        String appNo = request.applicationNumber().trim();

        if (studentRepository.existsByApplicationNumber(appNo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This application number is already registered");
        }

        Student student = new Student();
        student.setName(request.name().trim());
        student.setDepartment(request.department().trim());
        student.setApplicationNumber(appNo);
        student.setCollegeName(request.collegeName().trim());

        Student saved = studentRepository.save(student);
        return new AuthResponse("Registration successful", StudentResponse.from(saved));
    }

    public AuthResponse login(LoginRequest request) {
        Student student = studentRepository
                .findByNameIgnoreCaseAndApplicationNumber(
                        request.name().trim(), request.applicationNumber().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Name or application number is wrong"));

        return new AuthResponse("Login successful", StudentResponse.from(student));
    }
}
