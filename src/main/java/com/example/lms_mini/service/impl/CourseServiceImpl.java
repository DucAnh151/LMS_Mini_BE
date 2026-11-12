package com.example.lms_mini.service.impl;

import com.example.lms_mini.Utils.EscapeHelper;
import com.example.lms_mini.Utils.FullUrlHelper;
import com.example.lms_mini.dto.request.course.CourseRequestDTO;
import com.example.lms_mini.dto.request.course.CourseUpdateDTO;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.ResourceResponseDTO;
import com.example.lms_mini.dto.response.course.CourseDetailsDTO;
import com.example.lms_mini.entity.Course;
import com.example.lms_mini.entity.Resource;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import com.example.lms_mini.enums.Status;
import com.example.lms_mini.exception.InvalidDataException;
import com.example.lms_mini.exception.ResourceAlreadyExistsException;
import com.example.lms_mini.exception.ResourceNotFoundException;
import com.example.lms_mini.mapper.CourseMapper;
import com.example.lms_mini.mapper.ResourceMapper;
import com.example.lms_mini.repository.CourseRepository;
import com.example.lms_mini.repository.EnrollmentRepository;
import com.example.lms_mini.repository.ResourceRepository;
import com.example.lms_mini.service.CourseService;
import com.example.lms_mini.service.FileStorageService;
import com.example.lms_mini.service.ResourceService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ResourceRepository resourceRepository;
    private final CourseMapper courseMapper;
    private final ResourceMapper resourceMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final ResourceService resourceService;

    public CourseServiceImpl(CourseRepository courseRepository, ResourceRepository resourceRepository, CourseMapper courseMapper, ResourceMapper resourceMapper, FileStorageService fileStorageService, EnrollmentRepository enrollmentRepository, ResourceService resourceService) {
        this.courseRepository = courseRepository;
        this.resourceRepository = resourceRepository;
        this.courseMapper = courseMapper;
        this.resourceMapper = resourceMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.resourceService = resourceService;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createCourse(CourseRequestDTO request,
                             List<MultipartFile> thumbnails) {

        // 1. Validate: Check trùng Mã khóa học
        if (courseRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("course.code.exists");
        }

        // 2. Validate: Bắt buộc phải có Thumbnail
        if (thumbnails == null || thumbnails.isEmpty()) {
            throw new InvalidDataException("course.thumbnail.required");
        }

        // 3. Map & Save Course
        Course course = courseMapper.toEntity(request);
        course = courseRepository.save(course);

        // 4. Lưu Thumbnails (List)
        resourceService.saveResourceList(course.getId(), ObjectType.COURSE, ResourceType.THUMBNAIL, thumbnails, true);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public CourseBasicResponseDTO updateCourse(Long id,
                                               CourseUpdateDTO request,
                                               List<MultipartFile> thumbnails,
                                               Long chosenPrimaryThumbnailId,
                                               List<Long> deletedThumbnailIds) {
        // 1. Tìm Course & Validate Code
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("course.notfound"));

        if (request.getCode() != null && !request.getCode().equals(existingCourse.getCode())) {
            courseRepository.findByCodeAndIdNot(request.getCode(), id)
                    .ifPresent(c -> { throw new ResourceAlreadyExistsException("course.code.exists"); });
        }

        // 2. Update Course
        courseMapper.updateCourseFromDto(existingCourse, request);
        Course savedCourse = courseRepository.save(existingCourse);

        // Xóa mềm các ảnh đã chọn
        if (deletedThumbnailIds != null && !deletedThumbnailIds.isEmpty()) {
            resourceRepository.softDeleteResources(deletedThumbnailIds, id, ObjectType.COURSE, Status.DELETE);
        }

        // 3. Xử lý Thumbnails
        resourceService.handleResourceUpdate(id, ResourceType.THUMBNAIL, ObjectType.COURSE, thumbnails, chosenPrimaryThumbnailId);

        // 4. Return Response
        CourseBasicResponseDTO response = courseMapper.toBasicResponseDTO(savedCourse);

        // Lấy lại URL của thumbnail hiện tại để trả về cho FE
        resourceRepository.findByObjectIdAndObjectTypeAndResourceTypeAndIsPrimaryTrue(id, ObjectType.COURSE, ResourceType.THUMBNAIL)
                .ifPresent(r -> {
                    response.setThumbnailUrl(r.getUrl()); });

        return response;
    }

    @Override
    @Transactional
    public void softDeleteCourse(Long id) {
        boolean exists = courseRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("course.notfound");
        }

        // Kiểm tra nếu có học viên đang học khóa học này thì không được xóa
        boolean hasStudents = enrollmentRepository.existsByCourseIdAndStatus(id, Status.ACTIVE);
        if (hasStudents) {
            throw new InvalidDataException("course.cannot_delete.has_students");
        }
        courseRepository.updateStatus(id, Status.DELETE);
    }

    @Override
    @Transactional
    public long restoreCourse(Long id) {
        // Kiểm tra tồn tại khóa học
        boolean exists = courseRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("course.notfound");
        }
        courseRepository.updateStatus(id, Status.ACTIVE);
        return id;
    }

    @Override
    public Page<CourseBasicResponseDTO> searchCourses(String keyword, CourseLevel level, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        String searchKeyword = EscapeHelper.escapeLike(keyword);

        return courseRepository.searchCourses(searchKeyword, level, minPrice, maxPrice, Status.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailsDTO getCourseDetail(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("course.notfound"));

        CourseDetailsDTO response = courseMapper.toDetailResponseDTO(course);

        // Lấy danh sách Resources
        List<Resource> resources = resourceRepository.findByObjectIdAndObjectTypeAndStatus(id, ObjectType.COURSE, Status.ACTIVE);
        List<ResourceResponseDTO> resourceDTOs = resourceMapper.toResponseDTOList(resources);

        // Xử lý full URL cho từng resource
        resourceDTOs.forEach(res -> {
            res.setUrl(FullUrlHelper.getFullUrl(res.getUrl()));
        });

        response.setResources(resourceDTOs);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public StreamingResponseBody exportCourses(String keyword, CourseLevel level, BigDecimal minPrice, BigDecimal maxPrice) {

        return outputStream -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
                Sheet sheet = workbook.createSheet("Danh sách khóa học");

                String[] HEADERS = { "ID", "Tên khóa học", "Mã khóa học", "Giá tiền", "Cấp độ", "Giảng viên", "Trạng thái" };
                Row headerRow = sheet.createRow(0);

                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                for (int col = 0; col < HEADERS.length; col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(HEADERS[col]);
                    cell.setCellStyle(headerStyle);
                }

                int page = 0;
                final int BATCH_SIZE = 1000;
                int currentRowIndex = 1;

                while (true) {
                    Pageable pageable = PageRequest.of(page, BATCH_SIZE);

                    Page<CourseBasicResponseDTO> coursePage = courseRepository.searchCourses(keyword, level, minPrice, maxPrice, Status.ACTIVE, pageable);

                    List<CourseBasicResponseDTO> courses = coursePage.getContent();

                    if (courses.isEmpty()) {
                        break;
                    }

                    for (CourseBasicResponseDTO course : courses) {
                        Row row = sheet.createRow(currentRowIndex++);

                        row.createCell(0).setCellValue(course.getId());
                        row.createCell(1).setCellValue(course.getName());
                        row.createCell(2).setCellValue(course.getCode());
                        Cell priceCell = row.createCell(3);
                        if (course.getPrice() != null) {
                            priceCell.setCellValue(course.getPrice().doubleValue());
                        } else {
                            priceCell.setCellValue(0);
                        }
                        String levelStr = (course.getLevel() != null) ? course.getLevel().name() : "";
                        row.createCell(4).setCellValue(levelStr);
                        row.createCell(5).setCellValue(course.getInstructorName());
                        String statusStr = (course.getStatus() != null) ? course.getStatus().name() : "";
                        row.createCell(6).setCellValue(statusStr);
                    }
                    page++;
                }
                workbook.write(outputStream);
                workbook.dispose();

            } catch (IOException e) {
                throw new RuntimeException("course.export.failure");
            }
        };
    }

}
