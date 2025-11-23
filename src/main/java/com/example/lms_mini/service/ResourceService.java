package com.example.lms_mini.service;

import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    void handleResourceUpdate(Long objectId, ResourceType resourceType, ObjectType objectType, List<MultipartFile> newFiles, Long chosenOldId);

    void saveResourceList(Long objectId, ObjectType objectType, ResourceType resourceType, List<MultipartFile> files, boolean autoPickFirstAsPrimary);

}
