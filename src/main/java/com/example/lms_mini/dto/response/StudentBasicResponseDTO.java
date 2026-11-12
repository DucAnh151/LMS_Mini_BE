package com.example.lms_mini.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class StudentBasicResponseDTO {

    Long id;

    String fullName;

    String email;

    String phoneNumber;

    String status;

    String avatarUrl;
}
