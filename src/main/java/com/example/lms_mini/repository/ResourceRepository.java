package com.example.lms_mini.repository;

import com.example.lms_mini.entity.Resource;
import com.example.lms_mini.enums.ObjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByObjectIdAndObjectType(Long objectId, ObjectType objectType);

    Optional<Resource> findByObjectIdAndObjectTypeAndIsPrimaryTrue(Long objectId, ObjectType objectType);
}
