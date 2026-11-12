package com.example.lms_mini.dto.response.course;

import com.example.lms_mini.dto.response.ResourceResponseDTO;
import com.example.lms_mini.enums.CourseLanguage;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsDTO {

    Long id;
    String name;
    String code;
    String description;
    BigDecimal price;
    CourseLevel level;
    String duration;
    CourseLanguage language;
    String instructorName;
    Status status;
    List<ResourceResponseDTO> resources;
}
