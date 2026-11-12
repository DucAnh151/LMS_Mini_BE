package com.example.lms_mini.controller;

import com.example.lms_mini.dto.request.StudentRequestDTO;
import com.example.lms_mini.dto.request.StudentSearchReqDTO;
import com.example.lms_mini.dto.request.StudentUpdateDTO;
import com.example.lms_mini.dto.response.DataResponse;
import com.example.lms_mini.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/students")
@Validated
public class StudentController {

    private final StudentService studentService;
    private final MessageSource messageSource;

    public StudentController(StudentService studentService, MessageSource messageSource) {
        this.studentService = studentService;
        this.messageSource = messageSource;
    }

    @GetMapping("/{studentId}")
    public DataResponse<?> getStudentDetails(@Min(value = 1) @PathVariable Long studentId,
                                            Locale locale) {
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("common.success", null, locale))
                .data(studentService.getStudentDetails(studentId))
                .build();
    }

    @GetMapping("/all")
    public DataResponse<?> getAllStudents(@Valid @ModelAttribute StudentSearchReqDTO dto,
                                          @Min(value = 0, message = "{common.page.min}")
                                          @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                          @Min(value = 1, message = "{common.size.min}")
                                          @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                          Locale locale) {
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("common.success", null, locale))
                .data(studentService.searchStudents(dto, PageRequest.of(page, size)))
                .build();
    }

    @PostMapping("")
    public DataResponse<?> createStudent(@Valid @ModelAttribute StudentRequestDTO studentRequestDTO,
                                         @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage,
                                         Locale locale) {
        return DataResponse.builder()
                .status(201)
                .message(messageSource.getMessage("student.create.success", null, locale))
                .data(studentService.createStudent(studentRequestDTO, avatarImage))
                .build();
    }

    @PutMapping("/{studentId}")
    public DataResponse<?> updateStudent(@Min(value = 1) @PathVariable Long studentId,
                                         @Valid @ModelAttribute StudentUpdateDTO dto,
                                         @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage,
                                         Locale locale) {
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("student.update.success", null, locale))
                .data(studentService.updateStudent(studentId, dto, avatarImage))
                .build();
    }

    @DeleteMapping("/{studentId}")
    public DataResponse<?> deleteStudent(@PathVariable Long studentId, Locale locale) {
        studentService.softDelete(studentId);
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("student.delete.success", null, locale))
                .build();
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportStudents(@Valid @ModelAttribute StudentSearchReqDTO dto,
                                                                Locale locale) {
        StreamingResponseBody stream = studentService.exportStudents(dto);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students_export.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(stream);
    }

}
