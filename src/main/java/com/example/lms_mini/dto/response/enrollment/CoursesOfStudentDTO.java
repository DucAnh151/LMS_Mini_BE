package com.example.lms_mini.dto.response.enrollment;

import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CoursesOfStudentDTO {

    Long studentId;
    String studentName;

    List<CourseBasicResponseDTO> courses;
}
