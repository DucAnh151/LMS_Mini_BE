package com.example.lms_mini.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LessonType {
    @JsonProperty("Video")
    VIDEO,
    @JsonProperty("Document")
    DOCUMENT,
    @JsonProperty("Quiz")
    QUIZ
}
