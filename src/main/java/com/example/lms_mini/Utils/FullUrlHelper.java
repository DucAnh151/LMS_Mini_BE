package com.example.lms_mini.Utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class FullUrlHelper {
    public static String getFullUrl(String resourceUri) {
        if (resourceUri != null) {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/")
                    .path(resourceUri)
                    .toUriString();
        }
        return null;
    }
}
