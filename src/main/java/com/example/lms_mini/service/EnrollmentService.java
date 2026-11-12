package com.example.lms_mini.service;

import com.example.lms_mini.dto.request.enrollment.EnrollmentRequestDTO;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.enrollment.EnrollmentBasicResponseDTO;
import com.example.lms_mini.dto.response.enrollment.StudentEnrollmentDTO;
import com.example.lms_mini.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnrollmentService {

    EnrollmentBasicResponseDTO updateEnrollment(Long enrollmentId, Long newCourseId);

    List<CourseBasicResponseDTO> registerCourses(EnrollmentRequestDTO dto);

    void softDeleteRegistration(Long id);

    Page<StudentEnrollmentDTO> getStudentsByCourseId(Long courseId, String keyword, Status status, Pageable pageable);

    // Bulk soft delete
    void softDeleteByIds(List<Long> ids);

}
