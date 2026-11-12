package com.example.lms_mini.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ObjectType {
    @JsonProperty("STUDENT")
    STUDENT,
    @JsonProperty("COURSE")
    COURSE,
    @JsonProperty("LESSON")
    LESSON
}
