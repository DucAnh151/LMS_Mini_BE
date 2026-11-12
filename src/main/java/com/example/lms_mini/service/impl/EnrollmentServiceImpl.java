package com.example.lms_mini.service.impl;

import com.example.lms_mini.Utils.EscapeHelper;
import com.example.lms_mini.dto.request.enrollment.EnrollmentRequestDTO;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.enrollment.EnrollmentBasicResponseDTO;
import com.example.lms_mini.dto.response.enrollment.StudentEnrollmentDTO;
import com.example.lms_mini.entity.Course;
import com.example.lms_mini.entity.Enrollment;
import com.example.lms_mini.entity.Student;
import com.example.lms_mini.enums.Status;
import com.example.lms_mini.exception.ResourceAlreadyExistsException;
import com.example.lms_mini.exception.ResourceNotFoundException;
import com.example.lms_mini.mapper.CourseMapper;
import com.example.lms_mini.mapper.EnrollmentMapper;
import com.example.lms_mini.repository.CourseRepository;
import com.example.lms_mini.repository.EnrollmentRepository;
import com.example.lms_mini.repository.StudentRepository;
import com.example.lms_mini.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {


    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository, CourseRepository courseRepository, CourseMapper courseMapper, EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public EnrollmentBasicResponseDTO updateEnrollment(Long enrollmentId, Long newCourseId) {
        // Kiểm tra enrollment tồn tại
        Enrollment enrollment = enrollmentRepository.findByIdAndStatus(enrollmentId, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("enrollment.notfound"));

        // Chỉ cập nhật nếu courseId mới khác với courseId hiện tại
        if (!enrollment.getCourse().getId().equals(newCourseId)) {
            // Kiểm tra course mới tồn tại
            Course newCourse = courseRepository.findByIdAndStatus(newCourseId, Status.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("course.notfound"));

            // Kiểm tra sinh viên đã đăng ký khóa học mới chưa
            Long studentId = enrollment.getStudent().getId();
            boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(studentId, newCourseId);
            if (alreadyEnrolled) {
                throw new ResourceAlreadyExistsException("enrollment.already_in_course");
            }

            // Cập nhật enrollment với course mới
            enrollment.setCourse(newCourse);

            return enrollmentMapper.toBasicDTO(enrollmentRepository.save(enrollment));
        } else {
            throw new ResourceAlreadyExistsException("enrollment.already_in_course");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<CourseBasicResponseDTO> registerCourses(EnrollmentRequestDTO request) {
        // Kiểm tra student tồn tại
        Student student = studentRepository.findByIdAndStatus(request.getStudentId(), Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("student.notfound"));
        // Lấy danh sách course từ courseIds
        List<Course> courses = courseRepository.findAllById(request.getCourseIds());

        if (courses.size() != new HashSet<>(request.getCourseIds()).size()) {
            throw new ResourceNotFoundException("course.notfound");
        }

        // Lấy danh sách courseId mà sinh viên đã đăng ký
        List<Long> existingCourseIds = enrollmentRepository.findCourseIdsByStudentIdAndCourseIdIn(student.getId(), request.getCourseIds(), Status.ACTIVE);

        Set<Long> toEnrollIds = new HashSet<>(request.getCourseIds());
        existingCourseIds.forEach(toEnrollIds::remove);

        if (toEnrollIds.isEmpty()) {
            throw new ResourceAlreadyExistsException("enrollment.all_exists");
        }

        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        List<Enrollment> newEnrollments = toEnrollIds.stream()
                .map(courseId -> {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setCourse(courseMap.get(courseId));
                    return enrollment;
                })
                .collect(Collectors.toList());

        enrollmentRepository.saveAll(newEnrollments);

        return courses.stream()
                .map(courseMapper::toBasicResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentEnrollmentDTO> getStudentsByCourseId(Long courseId, String keyword, Status status, Pageable pageable) {
        // Kiểm tra course tồn tại
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("course.notfound"));

        String searchKeyword = EscapeHelper.escapeLike(keyword);

        return enrollmentRepository.getStudentsByCourseId(courseId, searchKeyword, status, pageable);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void softDeleteRegistration(Long id) {
        // Kiểm tra enrollment tồn tại
        Enrollment enrollment = enrollmentRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("enrollment.notfound"));

        // Soft delete enrollment
        enrollment.setStatus(Status.DELETE);
        enrollmentRepository.save(enrollment);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void softDeleteByIds(List<Long> ids) {
        // Kiểm tra enrollment tồn tại
        List<Enrollment> enrollments = enrollmentRepository.findAllByIdInAndStatus(ids, Status.ACTIVE);
        if (enrollments.size() != ids.size()) {
            throw new ResourceNotFoundException("enrollment.notfound");
        }

        enrollmentRepository.softDeleteByIds(ids, Status.DELETE);
    }

}
