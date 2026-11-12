package com.example.lms_mini.service.impl;

import com.example.lms_mini.Utils.FullUrlHelper;
import com.example.lms_mini.dto.request.lesson.LessonRequestDTO;
import com.example.lms_mini.dto.request.lesson.LessonUpdateDTO;
import com.example.lms_mini.dto.response.ResourceResponseDTO;
import com.example.lms_mini.dto.response.lesson.LessonBasicResponseDTO;
import com.example.lms_mini.dto.response.lesson.LessonDetailsDTO;
import com.example.lms_mini.entity.Course;
import com.example.lms_mini.entity.Lesson;
import com.example.lms_mini.entity.Resource;
import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import com.example.lms_mini.enums.Status;
import com.example.lms_mini.exception.InvalidDataException;
import com.example.lms_mini.exception.ResourceAlreadyExistsException;
import com.example.lms_mini.exception.ResourceNotFoundException;
import com.example.lms_mini.mapper.LessonMapper;
import com.example.lms_mini.mapper.ResourceMapper;
import com.example.lms_mini.repository.CourseRepository;
import com.example.lms_mini.repository.LessonRepository;
import com.example.lms_mini.repository.ResourceRepository;
import com.example.lms_mini.service.FileStorageService;
import com.example.lms_mini.service.LessonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final FileStorageService fileStorageService;
    private final ResourceRepository resourceRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;
    private final ResourceMapper resourceMapper;

    public LessonServiceImpl(LessonRepository lessonRepository, FileStorageService fileStorageService, ResourceRepository resourceRepository, CourseRepository courseRepository, LessonMapper lessonMapper, ResourceMapper resourceMapper) {
        this.lessonRepository = lessonRepository;
        this.fileStorageService = fileStorageService;
        this.resourceRepository = resourceRepository;
        this.courseRepository = courseRepository;
        this.lessonMapper = lessonMapper;
        this.resourceMapper = resourceMapper;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createLesson(Long courseId,
                             LessonRequestDTO request,
                             List<MultipartFile> videos,
                             List<MultipartFile> thumbnails,
                             List<MultipartFile> documents) {

        Course course = courseRepository.findByIdAndStatus(courseId, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("course.notfound"));

        if (lessonRepository.existsByLessonCode(request.getLessonCode())) {
            throw new ResourceAlreadyExistsException("lesson.code.exists");
        }

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setCourse(course);

        lesson = lessonRepository.save(lesson);

        saveLessonResources(videos, lesson.getId(), ResourceType.VIDEO, true);

        saveLessonResources(thumbnails, lesson.getId(), ResourceType.THUMBNAIL, true);

        saveLessonResources(documents, lesson.getId(), ResourceType.DOCUMENT, false);

    }

    @Override
    public List<LessonBasicResponseDTO> getLessonsByCourseId(Long courseId) {
        // 1. Validate Course tồn tại (Optional: Check thêm Status ACTIVE nếu cần)
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("course.notfound");
        }

        // 2. Query DB
        List<LessonBasicResponseDTO> lessons = lessonRepository.getLessonsByCourseId(courseId, Status.ACTIVE);

        lessons.forEach(dto -> {
            if (dto.getPrimaryVideoUrl() != null) {
                dto.setPrimaryVideoUrl(FullUrlHelper.getFullUrl(dto.getPrimaryVideoUrl()));
            }
            if (dto.getPrimaryThumbnailUrl() != null) {
                dto.setPrimaryThumbnailUrl(FullUrlHelper.getFullUrl(dto.getPrimaryThumbnailUrl()));
            }
        });
        return lessons;
    }

    @Override
    public LessonDetailsDTO getLessonDetails(Long id) {
        // 1. Tìm Lesson
        Lesson lesson = lessonRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("lesson.notfound"));

        // 2. Map Info
        LessonDetailsDTO response = lessonMapper.toDetailsDTO(lesson);

        // 3. Lấy TOÀN BỘ Resource
        List<Resource> allResources = resourceRepository.findByObjectIdAndObjectTypeAndStatus(id, ObjectType.LESSON, Status.ACTIVE);

        // 4. Phân loại & Build URL
        List<ResourceResponseDTO> videos = mapResourcesToDTOs(allResources, ResourceType.VIDEO);
        List<ResourceResponseDTO> thumbnails = mapResourcesToDTOs(allResources, ResourceType.THUMBNAIL);
        List<ResourceResponseDTO> documents = mapResourcesToDTOs(allResources, ResourceType.DOCUMENT);

        // 5. Set vào Response
        response.setVideos(videos);
        response.setThumbnails(thumbnails);
        response.setDocuments(documents);

        return response;
    }

    private List<ResourceResponseDTO> mapResourcesToDTOs(List<Resource> allResources, ResourceType type) {
        List<ResourceResponseDTO> dtos = resourceMapper.toResponseDTOList(allResources);
        return dtos.stream()
                .filter(r -> r.getResourceType() == type)
                .peek(r -> r.setUrl(FullUrlHelper.getFullUrl(r.getUrl())))
                .toList();
    }

    @Override
    public LessonBasicResponseDTO updateLesson(Long lessonId, LessonUpdateDTO dto,
                                               List<MultipartFile> thumbnails,
                                               List<MultipartFile> videos,
                                               List<MultipartFile> documents,
                                               Long chosenPrimaryVideoId,
                                               Long chosenPrimaryThumbnailId,
                                               List<Long> deletedResourceIds) {
        // Kiểm tra lesson tồn tại
        Lesson lesson = lessonRepository.findByIdAndStatus(lessonId, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("lesson.notfound"));

        // Kiểm tra lessonCode trùng lặp
        if (dto.getLessonCode() != null && !dto.getLessonCode().equals(lesson.getLessonCode())) {
            if (lessonRepository.existsByLessonCode(dto.getLessonCode())) {
                throw new ResourceAlreadyExistsException("lesson.code.exists");
            }
        }

        // Update partial
        lessonMapper.updateLessonFromDto(lesson, dto);
        lesson = lessonRepository.save(lesson);

        // Xử lí resources

        // Xóa resources nếu có
        if(deletedResourceIds != null && !deletedResourceIds.isEmpty()) {
            resourceRepository.softDeleteResources(deletedResourceIds, lessonId, ObjectType.LESSON, Status.DELETE);
        }

        // Thêm mới resources nếu có
        handleResourceUpdate(lessonId, ResourceType.VIDEO, videos, ObjectType.LESSON, chosenPrimaryVideoId);
        handleResourceUpdate(lessonId, ResourceType.THUMBNAIL, thumbnails, ObjectType.LESSON, chosenPrimaryThumbnailId);

        if (documents != null && !documents.isEmpty()) {
            saveLessonResources(documents, lessonId, ResourceType.DOCUMENT, false);
        }

        // Trả về LessonBasicResponseDTO
        return lessonMapper.toBasicResponseDTO(lesson);
    }

    @Override
    public void softDeteletionLesson(Long lessonId) {
        // Kiểm tra lesson tồn tại
        Lesson lesson = lessonRepository.findByIdAndStatus(lessonId, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("lesson.notfound"));
        lesson.setStatus(Status.DELETE);
        lessonRepository.save(lesson);
    }

    private String saveLessonResources(List<MultipartFile> files, Long lessonId, ResourceType type, boolean autoPickFirstAsPrimary) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        String primaryFilename = null;

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String filename = fileStorageService.storeFile(file);

            Resource resource = new Resource();
            resource.setObjectId(lessonId);
            resource.setObjectType(ObjectType.LESSON); // Object là LESSON
            resource.setResourceType(type);
            resource.setUrl(filename);

            // LOGIC CỦA BRO: Auto pick first as primary
            boolean isPrimary = autoPickFirstAsPrimary && (i == 0);
            resource.setIsPrimary(isPrimary);

            // Capture lại tên file nếu nó là primary để trả về response
            if (isPrimary) {
                primaryFilename = filename;
            }

            resourceRepository.save(resource);
        }

        return primaryFilename;
    }

    private void handleResourceUpdate(Long lessonId, ResourceType type,
                                      List<MultipartFile> newFiles,
                                      ObjectType objectType,
                                      Long chosenOldId) {
        boolean hasNewFiles = (newFiles != null && !newFiles.isEmpty());
        boolean hasChosenOld = (chosenOldId != null);

        // Bước 1: Nếu có bất kỳ thay đổi nào về Primary -> Reset tất cả về false trước
        if (hasNewFiles || hasChosenOld) {
            resourceRepository.removeAllPrimaryResources(lessonId, objectType, type);
        }

        // Bước 2: Xử lý chọn Resource CŨ làm Primary (Ưu tiên 1)
        if (hasChosenOld) {
            Resource oldRes = resourceRepository.findById(chosenOldId)
                    .orElseThrow(() -> new ResourceNotFoundException("resource.notfound"));

            // Validate kỹ: Phải thuộc Lesson này, đúng Loại, và chưa bị Xóa
            if (oldRes.getObjectId().equals(lessonId) && oldRes.getResourceType() == type && oldRes.getStatus() == Status.ACTIVE) {
                oldRes.setIsPrimary(true);
                resourceRepository.save(oldRes);
            } else {
                // Nếu ID gửi lên tào lao -> Bỏ qua hoặc ném lỗi tùy bro (ở đây tôi chọn ném lỗi cho chặt)
                throw new InvalidDataException("resource.invalid");
            }

            // Nếu có file MỚI -> Lưu tất cả làm phụ (isPrimary = false)
            if (hasNewFiles) {
                saveLessonResources(newFiles, lessonId, type, false);
            }
        }
        // Bước 3: Không chọn cũ, nhưng có MỚI (Ưu tiên 2)
        else if (hasNewFiles) {
            // Lưu list mới, file ĐẦU TIÊN (index 0) tự động làm Primary
            saveLessonResources(newFiles, lessonId, type, true);
        }
        // Bước 4: Không làm gì -> Giữ nguyên trạng thái cũ
    }
}
