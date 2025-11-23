package com.example.lms_mini.controller;

import com.example.lms_mini.dto.request.student.StudentRequestDTO;
import com.example.lms_mini.dto.request.student.StudentUpdateDTO;
import com.example.lms_mini.dto.response.DataResponse;
import com.example.lms_mini.dto.response.PageResponse;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.student.StudentSearchResDTO;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Status;
import com.example.lms_mini.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    public DataResponse<?> getAllStudents(@RequestParam(value = "keyword", required = false) String keyword,
                                          @RequestParam(value = "status", required = false) Status status,
                                          @Min(value = 0, message = "{common.page.min}")
                                          @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                          @Min(value = 1, message = "{common.size.min}")
                                          @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                          @PageableDefault(sort = "fullName", direction = Sort.Direction.DESC) Sort sort,
                                          Locale locale) {
        Page<StudentSearchResDTO> pages = studentService.searchStudents(keyword, status, PageRequest.of(page, size, sort));
        PageResponse<?> pageResponse = PageResponse.builder()
                .data(pages.getContent())
                .currentPage(pages.getNumber())
                .pageSize(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .hasNext(pages.hasNext())
                .hasPrevious(pages.hasPrevious())
                .build();

        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("common.success", null, locale))
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{studentId}/unregistered-courses")
    public DataResponse<?> getUnregisteredCourses(@Min(value = 1) @PathVariable Long studentId,
                                                  @RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "level", required = false) CourseLevel level,
                                                  @Min(value = 0, message = "{common.page.min}")
                                                  @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                  @Min(value = 1, message = "{common.size.min}")
                                                  @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                  @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Sort sort,
                                                  Locale locale) {
        Page<CourseBasicResponseDTO> pages = studentService.getUnregisteredCourses(studentId, keyword, level, PageRequest.of(page, size, sort));
        PageResponse<?> pageResponse = PageResponse.builder()
                .data(pages.getContent())
                .currentPage(pages.getNumber())
                .pageSize(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .hasNext(pages.hasNext())
                .hasPrevious(pages.hasPrevious())
                .build();

        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("common.success", null, locale))
                .data(pageResponse)
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

    @PatchMapping("/{studentId}/restore")
    public DataResponse<?> restoreStudent(@PathVariable Long studentId, Locale locale) {
        long restoredId = studentService.restoreStudent(studentId);
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("student.restore.success", null, locale))
                .data(restoredId)
                .build();
    }

    @DeleteMapping("/{studentId}")
    public DataResponse<?> deleteStudent(@PathVariable Long studentId, Locale locale) {
        studentService.softDelete(studentId);
        return DataResponse.builder()
                .status(204)
                .message(messageSource.getMessage("student.delete.success", null, locale))
                .build();
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportStudents(@RequestParam(value = "keyword", required = false) String keyword,
                                                                @RequestParam(value = "status", required = false) Status status) {
        StreamingResponseBody stream = studentService.exportStudents(keyword, status);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students_export.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(stream);
    }

}
