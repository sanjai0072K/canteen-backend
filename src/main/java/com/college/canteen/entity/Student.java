package com.college.canteen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department;

    @Column(name = "application_number", nullable = false, unique = true)
    private String applicationNumber;

    @Column(name = "college_name", nullable = false)
    private String collegeName;

    // student cannot order until this time (null = not blocked)
    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;

    @Column(name = "no_show_count", nullable = false)
    private int noShowCount = 0;
}
