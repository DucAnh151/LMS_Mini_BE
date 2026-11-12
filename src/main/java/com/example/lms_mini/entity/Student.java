package com.example.lms_mini.entity;

import com.example.lms_mini.enums.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "students")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "identity_number", unique = true, nullable = false)
    String identityNumber;

    @Column(name = "birth_date")
    LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    Gender gender;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "address")
    String address;

    // Relationships

    // Relationship with Enrollment
    @OneToMany(
        mappedBy = "student",
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    Set<Enrollment> enrollments = new HashSet<>();
}
