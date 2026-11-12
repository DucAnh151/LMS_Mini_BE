package com.example.lms_mini.service;

import com.example.lms_mini.dto.request.StudentRequestDTO;
import com.example.lms_mini.dto.request.StudentSearchReqDTO;
import com.example.lms_mini.dto.request.StudentUpdateDTO;
import com.example.lms_mini.dto.response.PageResponse;
import com.example.lms_mini.dto.response.StudentBasicResponseDTO;
import com.example.lms_mini.dto.response.StudentDetailsDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface StudentService {

    StudentDetailsDTO getStudentDetails(Long id);

    PageResponse<?> searchStudents(StudentSearchReqDTO dto, Pageable pageable);

    StudentBasicResponseDTO createStudent(StudentRequestDTO studentRequestDTO, MultipartFile avatarImage);

    StudentBasicResponseDTO updateStudent(Long id, StudentUpdateDTO dto, MultipartFile avatarImage);

    void softDelete(Long studentId);

    StreamingResponseBody exportStudents(StudentSearchReqDTO dto);
}
