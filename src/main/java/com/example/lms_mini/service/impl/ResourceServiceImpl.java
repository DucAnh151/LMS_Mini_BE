package com.example.lms_mini.service.impl;

import com.example.lms_mini.entity.Resource;
import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import com.example.lms_mini.enums.Status;
import com.example.lms_mini.exception.InvalidDataException;
import com.example.lms_mini.exception.ResourceNotFoundException;
import com.example.lms_mini.repository.ResourceRepository;
import com.example.lms_mini.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final FileStorageServiceImpl fileStorageService;

    public ResourceServiceImpl(ResourceRepository resourceRepository, FileStorageServiceImpl fileStorageService) {
        this.resourceRepository = resourceRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void handleResourceUpdate(Long objectId, ResourceType type, ObjectType objectType, List<MultipartFile> newFiles, Long chosenOldId) {
        boolean hasNewFiles = (newFiles != null && !newFiles.isEmpty());
        boolean hasChosenOld = (chosenOldId != null);

        if (hasNewFiles || hasChosenOld) {
            resourceRepository.removeAllPrimaryResources(objectId, objectType, type);
        }

        if (hasChosenOld) {
            com.example.lms_mini.entity.Resource oldRes = resourceRepository.findById(chosenOldId)
                    .orElseThrow(() -> new ResourceNotFoundException("resource.notfound"));

            if (oldRes.getObjectId().equals(objectId) && oldRes.getResourceType() == type && oldRes.getStatus() == Status.ACTIVE) {
                oldRes.setIsPrimary(true);
                resourceRepository.save(oldRes);
            } else {
                throw new InvalidDataException("resource.invalid");
            }

            // Nếu có file MỚI -> Lưu tất cả làm phụ (isPrimary = false)
            if (hasNewFiles) {
                saveResourceList(objectId, objectType, type, newFiles, false);
            }
        }
        else if (hasNewFiles) {
            saveResourceList(objectId, objectType, type, newFiles, true);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveResourceList(Long objectId, ObjectType objectType, ResourceType resourceType, List<MultipartFile> files, boolean autoPickFirstAsPrimary) {
        if(files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String filename = fileStorageService.storeFile(file);

                Resource resource = new Resource();
                resource.setObjectId(objectId);
                resource.setObjectType(objectType);
                resource.setResourceType(resourceType);
                resource.setUrl(filename);
                resource.setFileName(file.getOriginalFilename());

                resource.setIsPrimary(autoPickFirstAsPrimary && i == 0);

                resourceRepository.save(resource);
            }
        }
    }
}
