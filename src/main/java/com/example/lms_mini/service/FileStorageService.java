package com.example.lms_mini.service;

import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    String storeFile(MultipartFile file);

    Resource loadFileAsResource(String filename);
}
