package com.example.lms_mini.entity;

import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "resources")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Resource extends BaseEntity {

    @Column(nullable = false)
    String url;

    @Column(name = "object_id", nullable = false)
    Long objectId;

    @Column(name = "is_primary", nullable = false)
    Boolean isPrimary;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false, length = 50)
    ObjectType objectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 50)
    ResourceType resourceType;
}
