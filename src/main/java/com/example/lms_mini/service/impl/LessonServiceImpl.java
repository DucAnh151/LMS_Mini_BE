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
import com.example.lms_mini.exception.ResourceAlreadyExistsException;
import com.example.lms_mini.exception.ResourceNotFoundException;
import com.example.lms_mini.mapper.LessonMapper;
import com.example.lms_mini.mapper.ResourceMapper;
import com.example.lms_mini.repository.CourseRepository;
import com.example.lms_mini.repository.LessonRepository;
import com.example.lms_mini.repository.ResourceRepository;
import com.example.lms_mini.service.LessonService;
import com.example.lms_mini.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ResourceRepository resourceRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;
    private final ResourceMapper resourceMapper;
    private final ResourceService resourceService;

    public LessonServiceImpl(LessonRepository lessonRepository, ResourceRepository resourceRepository, CourseRepository courseRepository, LessonMapper lessonMapper, ResourceMapper resourceMapper, ResourceService resourceService) {
        this.lessonRepository = lessonRepository;
        this.resourceRepository = resourceRepository;
        this.courseRepository = courseRepository;
        this.lessonMapper = lessonMapper;
        this.resourceMapper = resourceMapper;
        this.resourceService = resourceService;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long restoreLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("lesson.notfound"));
        lesson.setStatus(Status.ACTIVE);
        lessonRepository.save(lesson);
        return lesson.getId();
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

        // Kiểm tra orderIndex không trùng lặp trong cùng một Course
        boolean existsOrderIndex = lessonRepository.existsByCourseIdAndOrderIndexAndStatus(
                courseId, request.getOrderIndex(), Status.ACTIVE);
        if (existsOrderIndex) {
            throw new ResourceAlreadyExistsException("lesson.order-index.exists");
        }

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setCourse(course);

        lesson = lessonRepository.save(lesson);

        if(videos!=null && !videos.isEmpty()) {
            resourceService.saveResourceList(lesson.getId(), ObjectType.LESSON, ResourceType.VIDEO, videos, true);
        }
        // Thumbnails là bắt buộc
        resourceService.saveResourceList(lesson.getId(), ObjectType.LESSON, ResourceType.THUMBNAIL, thumbnails, true);

        if (documents != null && !documents.isEmpty()) {
            resourceService.saveResourceList(lesson.getId(), ObjectType.LESSON, ResourceType.DOCUMENT, documents, false);
        }
    }

    @Override
    public List<LessonBasicResponseDTO> getLessonsByCourseId(Long courseId) {
        // 1. Validate Course tồn tại
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
    @Transactional(rollbackFor = Throwable.class)
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

        // Kiểm tra orderIndex không trùng lặp trong cùng một Course
        if (dto.getOrderIndex() != null && !dto.getOrderIndex().equals((lesson.getOrderIndex()))) {
            boolean existsOrderIndex = lessonRepository.existsByCourseIdAndOrderIndexAndStatus(
                    lesson.getCourse().getId(), dto.getOrderIndex(), Status.ACTIVE);
            if (existsOrderIndex) {
                throw new ResourceAlreadyExistsException("lesson.order-index.exists");
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
        if(videos!=null && !videos.isEmpty()) {
            resourceService.handleResourceUpdate(lessonId, ResourceType.VIDEO, ObjectType.LESSON, videos, chosenPrimaryVideoId);
        }
        if(thumbnails!=null && !thumbnails.isEmpty()) {
            resourceService.handleResourceUpdate(lessonId, ResourceType.THUMBNAIL, ObjectType.LESSON, thumbnails, chosenPrimaryThumbnailId);
        }
        if (documents != null && !documents.isEmpty()) {
            resourceService.saveResourceList(lesson.getId(), ObjectType.LESSON, ResourceType.DOCUMENT, documents, false);
        }

        return lessonMapper.toBasicResponseDTO(lesson);
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void softDeteletionLesson(Long lessonId) {
        // Kiểm tra lesson tồn tại
        Lesson lesson = lessonRepository.findByIdAndStatus(lessonId, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("lesson.notfound"));
        lesson.setStatus(Status.DELETE);
        lessonRepository.save(lesson);
    }

}
