package com.example.lms_mini.service;

import com.example.lms_mini.dto.request.lesson.LessonRequestDTO;
import com.example.lms_mini.dto.request.lesson.LessonUpdateDTO;
import com.example.lms_mini.dto.response.lesson.LessonBasicResponseDTO;
import com.example.lms_mini.dto.response.lesson.LessonDetailsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LessonService {

    void createLesson(Long courseId, LessonRequestDTO dto, List<MultipartFile> thumbnails, List<MultipartFile> videos, List<MultipartFile> documents);

    List<LessonBasicResponseDTO> getLessonsByCourseId(Long courseId);

    LessonDetailsDTO getLessonDetails(Long lessonId);

    LessonBasicResponseDTO updateLesson(Long lessonId,
                                        LessonUpdateDTO dto,
                                        List<MultipartFile> thumbnails,
                                        List<MultipartFile> videos,
                                        List<MultipartFile> documents,
                                        Long chosenPrimaryVideoId,
                                        Long chosenPrimaryThumbnailId,
                                        List<Long> deletedResourceIds);

    void softDeteletionLesson(Long lessonId);
}
