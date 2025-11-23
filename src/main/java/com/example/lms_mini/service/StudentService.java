package com.example.lms_mini.service;

import com.example.lms_mini.dto.request.student.StudentRequestDTO;
import com.example.lms_mini.dto.request.student.StudentSearchReqDTO;
import com.example.lms_mini.dto.request.student.StudentUpdateDTO;
import com.example.lms_mini.dto.response.PageResponse;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.student.StudentBasicResponseDTO;
import com.example.lms_mini.dto.response.student.StudentDetailsDTO;
import com.example.lms_mini.dto.response.student.StudentSearchResDTO;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface StudentService {

    StudentDetailsDTO getStudentDetails(Long id);

    Page<StudentSearchResDTO> searchStudents(String keyword, Status status, Pageable pageable);

    Page<CourseBasicResponseDTO> getUnregisteredCourses(Long studentId, String keyword, CourseLevel level, Pageable pageable);

    StudentBasicResponseDTO createStudent(StudentRequestDTO studentRequestDTO, MultipartFile avatarImage);

    StudentBasicResponseDTO updateStudent(Long id, StudentUpdateDTO dto, MultipartFile avatarImage);

    void softDelete(Long studentId);

    long restoreStudent(Long studentId);

    StreamingResponseBody exportStudents(String keyword, Status status);
}
