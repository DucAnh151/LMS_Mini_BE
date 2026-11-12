package com.example.lms_mini.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResourceType {
    @JsonProperty("VIDEO")
    VIDEO,
    @JsonProperty("AVATAR")
    AVATAR,
    @JsonProperty("THUMBNAIL")
    THUMBNAIL,
}
