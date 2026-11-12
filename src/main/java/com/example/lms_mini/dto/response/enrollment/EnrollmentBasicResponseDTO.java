package com.example.lms_mini.dto.response.enrollment;

import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.student.StudentBasicResponseDTO;
import com.example.lms_mini.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentBasicResponseDTO {
    Long id;
    StudentBasicResponseDTO student;
    CourseBasicResponseDTO course;
    Status status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate createdDate;
}