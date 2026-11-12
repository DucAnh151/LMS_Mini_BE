package com.example.lms_mini.controller;

import com.example.lms_mini.dto.request.course.CourseRequestDTO;
import com.example.lms_mini.dto.request.course.CourseUpdateDTO;
import com.example.lms_mini.dto.response.DataResponse;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.PageResponse;
import com.example.lms_mini.dto.response.course.CourseDetailsDTO;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.service.CourseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/courses")
@Validated
public class CourseController {

    private final CourseService courseService;
    private final MessageSource messageSource;

    public CourseController(CourseService courseService, MessageSource messageSource) {
        this.courseService = courseService;
        this.messageSource = messageSource;
    }

    /*
    * Valid DTO và bắt buộc phải có thumbnail khi tạo khóa học
    * */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<?> createCourse(@Valid @ModelAttribute CourseRequestDTO request,
                                        @RequestParam(value = "thumbnails") List<MultipartFile> thumbnails,
                                        Locale locale) {
        courseService.createCourse(request, thumbnails);
        return DataResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message(messageSource.getMessage("course.create.success", null, locale))
                .build();
    }

    /*
    * Logic update:
    * - Cập nhật thông tin khóa học từ request DTO
    * - Xử lý thêm mới thumbnails từ danh sách thumbnails được upload
    * - Xử lý chọn thumbnail chính dựa trên chosenPrimaryThumbnailId
    * - Xử lý xóa các ảnh muốn ẩn đi dựa trên danh sách deletedThumbnailIds
    * */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DataResponse<?> updateCourse(@Min(value = 1) @PathVariable Long id,
                                        @Valid @ModelAttribute CourseUpdateDTO request,
                                        @RequestParam(value = "thumbnails", required = false) List<MultipartFile> thumbnails,
                                        @RequestParam(value = "chosenPrimaryThumbnailId", required = false) Long chosenPrimaryThumbnailId,
                                        @RequestParam(value = "deletedThumbnailIds", required = false) List<Long> deletedThumbnailIds,
                                        Locale locale) {
        return DataResponse.builder()
                .status(HttpStatus.OK.value())
                .message(messageSource.getMessage("course.update.success", null, locale))
                .data(courseService.updateCourse(id, request, thumbnails, chosenPrimaryThumbnailId, deletedThumbnailIds))
                .build();
    }

    @DeleteMapping("/{id}")
    public DataResponse<?> softDeleteCourse(@Min(value = 1) @PathVariable Long id, Locale locale) {
        courseService.softDeleteCourse(id);
        return DataResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message(messageSource.getMessage("course.delete.success", null, locale))
                .build();
    }

    @GetMapping("/{id}")
    public DataResponse<?> getCourseDetail(@Min(value = 1) @PathVariable Long id, Locale locale) {
        CourseDetailsDTO courseDetails = courseService.getCourseDetail(id);
        return DataResponse.builder()
                .status(HttpStatus.OK.value())
                .message(messageSource.getMessage("common.success", null, locale))
                .data(courseDetails)
                .build();
    }

    @GetMapping
    public DataResponse<?> searchCourses(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) CourseLevel level,
                                         @Positive(message = "course.price.positive") @RequestParam(required = false) BigDecimal minPrice,
                                         @Positive(message = "course.price.positive") @RequestParam(required = false) BigDecimal maxPrice,
                                         @Min(value = 0, message = "{common.page.min}") @RequestParam(defaultValue = "0") int page,
                                         @Max(value = 100, message = "{common.size.max}")
                                         @Min(value = 1, message = "{common.size.min}") @RequestParam(defaultValue = "10") int size,
                                         @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Sort sort,
                                         Locale locale) {
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CourseBasicResponseDTO> pageData = courseService.searchCourses(keyword, level, minPrice, maxPrice, pageable);

        PageResponse<List<CourseBasicResponseDTO>> response = PageResponse.<List<CourseBasicResponseDTO>>builder()
                .data(pageData.getContent())
                .currentPage(pageData.getNumber())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .hasPrevious(pageData.hasPrevious())
                .build();

        return DataResponse.builder()
                .status(HttpStatus.OK.value())
                .message(messageSource.getMessage("common.success", null, locale))
                .data(response)
                .build();
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportCourses(@RequestParam(required = false) String keyword,
                                                               @RequestParam(required = false) CourseLevel level,
                                                               @Positive(message = "course.price.positive") @RequestParam(required = false) BigDecimal minPrice,
                                                               @Positive(message = "course.price.positive") @RequestParam(required = false) BigDecimal maxPrice) {
        StreamingResponseBody stream = courseService.exportCourses(keyword, level, minPrice, maxPrice);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=courses_export.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(stream);
    }
}