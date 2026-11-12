package com.example.lms_mini.dto.response;

import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import com.example.lms_mini.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponseDTO {

    Long id;
    String url;
    Boolean isPrimary;
    Status status;
    ObjectType objectType;
    ResourceType resourceType;
}
