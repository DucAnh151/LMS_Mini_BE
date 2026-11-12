package com.example.lms_mini.dto.response.enrollment;

import com.example.lms_mini.enums.Gender;
import com.example.lms_mini.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollmentDTO {

    Long enrollmentId;
    LocalDate enrollmentDate;
    Status status; // Enrollment status

    Long studentId;
    String studentName;
    Gender gender;
    String studentEmail;
    String studentPhone;

}
