package com.example.lms_mini.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file);

    Resource loadFileAsResource(String filename);
}
