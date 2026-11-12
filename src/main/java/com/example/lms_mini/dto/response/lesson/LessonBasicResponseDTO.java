package com.example.lms_mini.dto.response.lesson;

import com.example.lms_mini.enums.LessonType;
import com.example.lms_mini.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonBasicResponseDTO {
    Long id;
    Long courseId;
    String title;
    String lessonCode;
    String summary;
    Integer durationInSeconds;
    Integer orderIndex;
    LessonType lessonType;
    Status status;

    String primaryVideoUrl;
    String primaryThumbnailUrl;
}
