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

    List<Resource> findByObjectIdAndObjectTypeAndStatus(Long objectId, ObjectType objectType, Status status);

    Optional<Resource> findByObjectIdAndObjectTypeAndIsPrimaryTrue(Long objectId, ObjectType objectType);

    Optional<Resource> findByObjectIdAndObjectTypeAndResourceTypeAndIsPrimaryTrue(Long objectId, ObjectType objectType, ResourceType resourceType);

    @Modifying
    @Query("UPDATE Resource r SET r.isPrimary = false " +
            "WHERE r.objectId = :objectId " +
            "AND r.objectType = :objectType " +
            "AND r.resourceType = :resourceType")
    void removeAllPrimaryResources(@Param("objectId") Long objectId,
                                   @Param("objectType") ObjectType objectType,
                                   @Param("resourceType") ResourceType type);

    @Modifying
    @Query("UPDATE Resource r SET r.status = :status, r.isPrimary = false " +
            "WHERE r.id IN :ids " +
            "AND r.objectId = :objectId " +
            "AND r.objectType = :objectType")
    void softDeleteResources(@Param("ids") List<Long> ids,
                             @Param("objectId") Long objectId,
                             @Param("objectType") ObjectType objectType,
                             @Param("status") Status status);
}
