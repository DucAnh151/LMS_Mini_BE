package com.example.lms_mini.dto.request.course;

import com.example.lms_mini.enums.CourseLanguage;
import com.example.lms_mini.enums.CourseLevel;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateDTO {

    String name;

    String code;

    String description;

    @Min(value = 0, message = "{course.price.min}")
    BigDecimal price;

    CourseLevel level;

    String duration;

    CourseLanguage language;

    String instructorName;
}
