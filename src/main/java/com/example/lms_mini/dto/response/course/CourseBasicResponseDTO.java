package com.example.lms_mini.dto.response.course;

import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseBasicResponseDTO {

    Long id;
    String name;
    String code;
    BigDecimal price;
    CourseLevel level;
    String instructorName;
    Status status;
    String thumbnailUrl;
}