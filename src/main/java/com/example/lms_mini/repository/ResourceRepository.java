package com.example.lms_mini.repository;

import com.example.lms_mini.entity.Resource;
import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import com.example.lms_mini.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByObjectIdAndObjectType(Long objectId, ObjectType objectType);

    Optional<Resource> findByObjectIdAndObjectTypeAndIsPrimaryTrue(Long objectId, ObjectType objectType);

    Optional<Resource> findByObjectIdAndObjectTypeAndResourceTypeAndIsPrimaryTrue(Long objectId, ObjectType objectType, ResourceType resourceType);

    @Query("SELECT r FROM Resource r " +
            "WHERE r.objectId = :courseId " +
            "AND r.objectType = 'COURSE' " +
            "AND r.resourceType = 'THUMBNAIL' " +
            "AND r.isPrimary = true")
    Optional<Resource> findByCourseIdWithPrimaryThumbnail(@Param("objectId") Long courseId);

    @Modifying
    @Query("UPDATE Resource r SET r.isPrimary = false " +
            "WHERE r.objectId = :courseId " +
            "AND r.objectType = 'COURSE' " +
            "AND r.resourceType = 'THUMBNAIL'")
    void removeAllPrimaryThumbnails(@Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Resource r SET r.status = :status, r.isPrimary = false " +
            "WHERE r.id IN :ids " +
            "AND r.objectId = :courseId " +
            "AND r.objectType = 'COURSE'")
    void softDeleteResources(@Param("ids") List<Long> ids,
                             @Param("courseId") Long courseId,
                             @Param("status") Status status);
}
