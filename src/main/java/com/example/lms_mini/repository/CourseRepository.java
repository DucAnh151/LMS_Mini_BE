package com.example.lms_mini.repository;

import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.entity.Course;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {

    boolean existsByCode(String code);

    Optional<Course> findByCodeAndStatusAndIdNot(String code, Status status, Long id);

    Optional<Course> findByIdAndStatus(Long id, Status status);

    @Query("SELECT new com.example.lms_mini.dto.response.course.CourseBasicResponseDTO(" +
            "c.id, c.name, c.code, c.price, c.level, c.instructorName, c.status, " +
            "(SELECT r.url FROM Resource r WHERE r.objectId = c.id AND r.objectType = 'COURSE' AND r.resourceType = 'THUMBNAIL' AND r.isPrimary = true AND r.status = :status)) " +
            "FROM Course c " +
            "WHERE 1=1 " +
            "AND (:keyword IS NULL OR (c.name LIKE %:keyword% ESCAPE '\\' OR c.instructorName LIKE %:keyword% ESCAPE '\\')) " +
            "AND (:level IS NULL OR c.level = :level) " +
            "AND (:minPrice IS NULL OR c.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR c.price <= :maxPrice)")
    Page<CourseBasicResponseDTO> searchCourses(
            @Param("keyword") String keyword,
            @Param("level") CourseLevel level,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") Status status,
            Pageable pageable
    );
}
