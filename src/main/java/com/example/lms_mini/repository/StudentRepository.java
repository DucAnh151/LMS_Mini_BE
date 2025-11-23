package com.example.lms_mini.repository;

import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.student.StudentSearchResDTO;
import com.example.lms_mini.entity.Student;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByIdAndStatus(Long id, Status status);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByIdentityNumber(String identityNumber);

    Optional<Student> findByEmailAndIdNot(String email, Long id);

    Optional<Student> findByPhoneNumberAndIdNot(String phoneNumber, Long id);

    Optional<Student> findByIdentityNumberAndIdNot(String identityNumber, Long id);

    @Query("SELECT new com.example.lms_mini.dto.response.student.StudentSearchResDTO(" +
            "s.id, s.fullName, s.email, s.phoneNumber, s.birthDate, s.gender, s.address, s.status) " +
            "FROM Student s " +
            "WHERE 1=1 " +
            "AND (:keyword IS NULL OR (" +
            "   LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\' OR " +
            "   LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\' OR " +
            "   s.phoneNumber LIKE CONCAT('%', :keyword, '%') ESCAPE '\\')) " +
            "AND (:status IS NULL OR s.status = :status)")
    Page<StudentSearchResDTO> searchStudents(
            @Param("keyword") String keyword,
            @Param("status") Status status,
            Pageable pageable
    );

    @Query("SELECT new com.example.lms_mini.dto.response.course.CourseBasicResponseDTO(" +
            "c.id, c.name, c.code, c.price, c.level, c.instructorName, c.status, " +
            "(SELECT r.url FROM Resource r WHERE r.objectId = c.id AND r.objectType = 'COURSE' AND r.resourceType = 'THUMBNAIL' AND r.isPrimary = true)) " +
            "FROM Course c " +
            "WHERE c.status = 'ACTIVE' " +
            "AND c.id NOT IN (" +
            "    SELECT e.course.id FROM Enrollment e " +
            "    WHERE e.student.id = :studentId AND e.status = 'ACTIVE') " +
            "AND (:keyword IS NULL OR (" +
            "   LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\' OR " +
            "   LOWER(c.instructorName) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\')) " +
            "AND (:level IS NULL OR c.level = :level)")
    Page<CourseBasicResponseDTO> getUnregisteredCourses(
            @Param("studentId") Long studentId,
            @Param("keyword") String keyword,
            @Param("level") CourseLevel level,
            Pageable pageable
    );
}
