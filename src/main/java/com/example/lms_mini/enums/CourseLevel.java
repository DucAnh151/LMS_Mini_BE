package com.example.lms_mini.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CourseLevel {
    @JsonProperty("Beginner")
    BEGINNER,
    @JsonProperty("Intermediate")
    INTERMEDIATE,
    @JsonProperty("Advanced")
    ADVANCED
}
