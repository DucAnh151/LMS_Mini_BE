package com.example.lms_mini.dto.request.lesson;

import com.example.lms_mini.enums.LessonType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRequestDTO {

    @NotBlank(message = "{lesson.title.notblank}")
    String title;

    @NotBlank(message = "{lesson.code.notblank}")
    String lessonCode;

    @NotBlank(message = "{lesson.summary.notblank}")
    String summary;

    @NotBlank(message = "{lesson.content.notblank}")
    String content;

    @Min(value = 1, message = "{lesson.order.min}")
    @NotNull(message = "{lesson.order.notnull}")
    Integer orderIndex;

    @Min(value = 1, message = "{lesson.duration.min}")
    @NotNull(message = "{lesson.duration.notnull}")
    Integer durationInSeconds;

    @NotNull(message = "{lesson.type.notnull}")
    LessonType lessonType;
}