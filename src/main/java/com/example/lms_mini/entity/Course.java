package com.example.lms_mini.entity;

import com.example.lms_mini.enums.CourseLanguage;
import com.example.lms_mini.enums.CourseLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "courses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course extends BaseEntity {

    @Column(nullable = false)
    String name;

    @Column(unique = true, length = 50, nullable = false)
    String code;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(precision = 19, scale = 4)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    CourseLevel level;

    @Column(length = 100)
    String duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_language", length = 20)
    CourseLanguage language;

    @Column(name = "instructor_name", length = 100)
    String instructorName;

    // Relationships

    // Relationship with Lesson
    @OneToMany(
        mappedBy = "course",
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    Set<Lesson> lessons = new HashSet<>();

    // Relationship with Enrollment
    @OneToMany(
        mappedBy = "course",
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    Set<Enrollment> enrollments = new HashSet<>();
}