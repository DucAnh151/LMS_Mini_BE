package com.example.lms_mini.dto.request.course;

import com.example.lms_mini.enums.CourseLanguage;
import com.example.lms_mini.enums.CourseLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequestDTO {

    @NotBlank(message = "{course.name.notblank}")
    String name;

    @NotBlank(message = "{course.code.notblank}")
    String code;

    String description;

    @NotNull(message = "{course.price.notnull}")
    @Min(value = 0, message = "{course.price.min}")
    BigDecimal price;

    @NotNull(message = "{course.level.notnull}")
    CourseLevel level;

    @NotBlank(message = "{course.duration.notnull}")
    String duration;

    @NotNull(message = "{course.language.notnull}")
    CourseLanguage language;

    @NotBlank(message = "{course.instructorName.notblank}")
    String instructorName;
}
