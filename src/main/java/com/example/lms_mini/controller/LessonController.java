package com.example.lms_mini.controller;

import com.example.lms_mini.dto.request.lesson.LessonRequestDTO;
import com.example.lms_mini.dto.request.lesson.LessonUpdateDTO;
import com.example.lms_mini.dto.response.DataResponse;
import com.example.lms_mini.service.LessonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1")
public class LessonController {

    private final LessonService lessonService;
    private final MessageSource messageSource;

    public LessonController(LessonService lessonService, MessageSource messageSource) {
        this.lessonService = lessonService;
        this.messageSource = messageSource;
    }

    @Transactional(rollbackFor = Throwable.class)
    @PostMapping("/courses/{courseId}/lessons")
    public DataResponse<?> createLesson(@Min(value = 1) @PathVariable Long courseId,
                                        @Valid @ModelAttribute LessonRequestDTO request,
                                        @RequestParam(value = "thumbnails", required = false) List<MultipartFile> videos,
                                        @RequestParam(value = "videos", required = false) List<MultipartFile> thumbnails,
                                        @RequestParam(value = "documents", required = false) List<MultipartFile> documents,
                                        Locale locale) {
        lessonService.createLesson(courseId, request, thumbnails, videos, documents);

        return DataResponse.builder()
                .status(201)
                .message(messageSource.getMessage("lesson.create.success", null, locale))
                .build();
    }

    @Transactional(rollbackFor = Throwable.class)
    @PutMapping("lessons/{lessonId}")
    public DataResponse<?> updateLesson(@Min(value = 1) @PathVariable(value = "lessonId") Long lessonId,
                                        @Valid @ModelAttribute LessonUpdateDTO dto,
                                        @RequestParam(value = "videos", required = false) List<MultipartFile> videos,
                                        @RequestParam(value = "thumbnails", required = false) List<MultipartFile> thumbnails,
                                        @RequestParam(value = "documents", required = false) List<MultipartFile> documents,
                                        @RequestParam(value = "chosenPrimaryVideoId", required = false) Long chosenPrimaryVideoId,
                                        @RequestParam(value = "chosenPrimaryThumbnailId", required = false) Long chosenPrimaryThumbnailId,
                                        @RequestParam(value = "deletedResourceIds", required = false) List<Long> deletedResourceIds,
                                        Locale locale) {
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("lesson.update.success", null, locale))
                .data(lessonService.updateLesson(lessonId, dto, thumbnails, videos, documents, chosenPrimaryVideoId, chosenPrimaryThumbnailId, deletedResourceIds))
                .build();
    }

    @GetMapping("/courses/{courseId}/lessons")
    public DataResponse<?> getLessonsByCourseId(@Min(value = 1) @PathVariable Long courseId, Locale locale) {
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("common.success", null, locale))
                .data(lessonService.getLessonsByCourseId(courseId))
                .build();
    }

    @GetMapping("/lessons/{lessonId}")
    public DataResponse<?> getLessonDetails(@Min(value = 1) @PathVariable Long lessonId, Locale locale) {
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("common.success", null, locale))
                .data(lessonService.getLessonDetails(lessonId))
                .build();
    }

    @Transactional(rollbackFor = Throwable.class)
    @DeleteMapping("/lessons/{lessonId}")
    public DataResponse<?> deleteLesson(@Min(value = 1) @PathVariable Long lessonId, Locale locale) {
        lessonService.softDeteletionLesson(lessonId);
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("lesson.delete.success", null, locale))
                .build();
    }
}
