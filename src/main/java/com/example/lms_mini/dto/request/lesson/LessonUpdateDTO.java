package com.example.lms_mini.dto.request.lesson;

import com.example.lms_mini.enums.LessonType;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateDTO {
    String title;
    String lessonCode;
    String summary;
    String content;
    @Min(value = 1)
    Integer orderIndex;
    Integer durationInSeconds;
    LessonType lessonType;
}
