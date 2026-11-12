package com.example.lms_mini.entity;

import com.example.lms_mini.enums.LessonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "lessons")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson extends BaseEntity {

    @Column(nullable = false)
    String title;

    @Column(name = "lesson_code", unique = true, nullable = false, length = 50)
    String lessonCode;

    @Column(columnDefinition = "TEXT")
    String summary;

    @Column(columnDefinition = "TEXT")
    String content;

    Integer durationInSeconds;

    @Column(name = "order_index")
    Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", length = 50)
    LessonType lessonType;

    // Relationships

    // Relationship with Course
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "course_id")
    Course course;
}
