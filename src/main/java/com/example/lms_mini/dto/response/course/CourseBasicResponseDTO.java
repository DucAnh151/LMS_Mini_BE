package com.example.lms_mini.dto.response.course;

import com.example.lms_mini.Utils.FullUrlHelper;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
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

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = FullUrlHelper.getFullUrl(thumbnailUrl);
    }

    public CourseBasicResponseDTO(Long id, String name, String code, BigDecimal price, CourseLevel level, String instructorName, Status status, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.price = price;
        this.level = level;
        this.instructorName = instructorName;
        this.status = status;
        this.thumbnailUrl = FullUrlHelper.getFullUrl(thumbnailUrl);
    }
}