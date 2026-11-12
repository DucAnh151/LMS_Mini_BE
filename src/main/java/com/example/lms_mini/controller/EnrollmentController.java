package com.example.lms_mini.controller;

import com.example.lms_mini.dto.request.enrollment.EnrollmentRequestDTO;
import com.example.lms_mini.dto.response.DataResponse;
import com.example.lms_mini.service.EnrollmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/enrollments")
@Validated
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final MessageSource messageSource;

    public EnrollmentController(EnrollmentService enrollmentService, MessageSource messageSource) {
        this.enrollmentService = enrollmentService;
        this.messageSource = messageSource;
    }

    @PutMapping("/{id}")
    public DataResponse<?> updateEnrollment(@Min(value = 1) @PathVariable Long id,
                                            @Min(value = 1) @RequestParam Long newCourseId,
                                            Locale locale) {
        return DataResponse.builder()
                .status(200)
                .message(messageSource.getMessage("enrollment.update.success", null, locale))
                .data(enrollmentService.updateEnrollment(id, newCourseId))
                .build();
    }

    @PostMapping("")
    public DataResponse<?> registerCourses(@Valid @ModelAttribute EnrollmentRequestDTO dto, Locale locale) {

        return DataResponse.builder()
                .status(201)
                .message(messageSource.getMessage("enrollment.create.success", null, locale))
                .data(enrollmentService.registerCourses(dto))
                .build();
    }

    @DeleteMapping("/{id}")
    public DataResponse<?> softDeleteEnrollment(@Min(value = 1) @PathVariable Long id, Locale locale) {
        enrollmentService.softDeleteRegistration(id);
        return DataResponse.builder()
                .status(204)
                .message(messageSource.getMessage("enrollment.delete.success", null, locale))
                .build();
    }

    @DeleteMapping("/bulk-delete")
    public DataResponse<?> bulkSoftDeleteEnrollments(@RequestParam List<Long> ids, Locale locale) {
        enrollmentService.softDeleteByIds(ids);
        return DataResponse.builder()
                .status(204)
                .message(messageSource.getMessage("enrollment.delete.success", null, locale))
                .build();
    }
}
