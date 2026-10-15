package com.college.canteen.repository;

import com.college.canteen.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByApplicationNumber(String applicationNumber);

    Optional<Student> findByNameIgnoreCaseAndApplicationNumber(String name, String applicationNumber);

    boolean existsByApplicationNumber(String applicationNumber);
}
