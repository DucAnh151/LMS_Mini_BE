package com.example.lms_mini.repository;

import com.example.lms_mini.dto.response.lesson.LessonBasicResponseDTO;
import com.example.lms_mini.entity.Lesson;
import com.example.lms_mini.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l WHERE l.id = :id AND l.status = :status")
    Optional<Lesson> findByIdAndStatus(Long id, Status status);

    boolean existsByLessonCode(String lessonCode);

    @Query("SELECT new com.example.lms_mini.dto.response.lesson.LessonBasicResponseDTO(" +
            "l.id, l.course.id, l.title, l.lessonCode, l.summary, l.durationInSeconds, l.orderIndex, l.lessonType, l.status, " +
            "(SELECT r.url FROM Resource r WHERE r.objectId = l.id AND r.objectType = 'LESSON' AND r.resourceType = 'VIDEO' AND r.isPrimary = true), " +
            "(SELECT r.url FROM Resource r WHERE r.objectId = l.id AND r.objectType = 'LESSON' AND r.resourceType = 'THUMBNAIL' AND r.isPrimary = true)) " +
            "FROM Lesson l " +
            "WHERE l.course.id = :courseId " +
            "AND l.status = :status " +
            "ORDER BY l.orderIndex ASC")
    List<LessonBasicResponseDTO> getLessonsByCourseId(@Param("courseId") Long courseId,
                                                      @Param("status") Status status);
}
