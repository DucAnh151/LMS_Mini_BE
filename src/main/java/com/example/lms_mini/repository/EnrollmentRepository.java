package com.example.lms_mini.repository;

import com.example.lms_mini.dto.response.enrollment.StudentEnrollmentDTO;
import com.example.lms_mini.entity.Enrollment;
import com.example.lms_mini.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Enrollment> findByIdAndStatus(Long id, Status status);

    List<Enrollment> findAllByIdInAndStatus(List<Long> ids, Status status);

    @Query("SELECT e.course.id FROM Enrollment e " +
            "WHERE e.student.id = :studentId " +
            "AND e.course.id IN :courseIds " +
            "AND e.status = :status")
    List<Long> findCourseIdsByStudentIdAndCourseIdIn(@Param("studentId") Long studentId,
                                                     @Param("courseIds") List<Long> courseIds,
                                                     @Param("status") Status status);

    @Query("SELECT new com.example.lms_mini.dto.response.enrollment.StudentEnrollmentDTO(" +
            "e.id, e.createdDate, e.status, " +
            "s.id, s.fullName, s.gender, s.email, s.phoneNumber) " +
            "FROM Enrollment e " +
            "JOIN e.student s " +
            "WHERE e.course.id = :courseId " +
            "AND (:keyword IS NULL OR (" +
            "   LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\' OR " +
            "   LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\' OR " +
            "   s.phoneNumber LIKE CONCAT('%', :keyword, '%') ESCAPE '\\'" +
            ")) " +
            "AND (:status IS NULL OR e.status = :status)")
    Page<StudentEnrollmentDTO> getStudentsByCourseId(
            @Param("courseId") Long courseId,
            @Param("keyword") String keyword,
            @Param("status") Status status,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Enrollment e SET e.status = :status WHERE e.id IN :ids")
    void softDeleteByIds(@Param("ids") List<Long> ids,
                         @Param("status") Status status);
}
