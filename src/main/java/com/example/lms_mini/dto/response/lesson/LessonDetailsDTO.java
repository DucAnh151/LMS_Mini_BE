package com.example.lms_mini.dto.response.lesson;

import com.example.lms_mini.dto.response.ResourceResponseDTO;
import com.example.lms_mini.enums.LessonType;
import com.example.lms_mini.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class LessonDetailsDTO {
    Long id;
    String title;
    String lessonCode;
    String summary;
    String content;
    Integer durationInSeconds;
    Integer orderIndex;
    LessonType lessonType;
    Status status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate updatedDate;

    List<ResourceResponseDTO> videos;
    List<ResourceResponseDTO> thumbnails;
    List<ResourceResponseDTO> documents;
}