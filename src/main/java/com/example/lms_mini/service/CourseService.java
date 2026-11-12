package com.example.lms_mini.service;

import com.example.lms_mini.dto.request.course.CourseUpdateDTO;
import com.example.lms_mini.dto.response.course.CourseDetailsDTO;
import com.example.lms_mini.dto.request.course.CourseRequestDTO;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.enums.CourseLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.math.BigDecimal;
import java.util.List;

public interface CourseService {

    void createCourse(CourseRequestDTO request, List<MultipartFile> thumbnails);

    CourseBasicResponseDTO updateCourse(Long id, CourseUpdateDTO request, List<MultipartFile> thumbnails, Long chosenPrimaryThumbnailId, List<Long> deletedThumbnailIds);

    void softDeleteCourse(Long id);

    CourseDetailsDTO getCourseDetail(Long id);

    Page<CourseBasicResponseDTO> searchCourses(String keyword, CourseLevel level, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    StreamingResponseBody exportCourses(String keyword, CourseLevel level, BigDecimal minPrice, BigDecimal maxPrice);
}
