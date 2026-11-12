package com.example.lms_mini.dto.response.student;

import com.example.lms_mini.Utils.FullUrlHelper;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class StudentBasicResponseDTO {

    Long id;

    String fullName;

    String email;

    String phoneNumber;

    String status;

    String avatarUrl;

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = FullUrlHelper.getFullUrl(avatarUrl);
    }

    public StudentBasicResponseDTO(Long id, String fullName, String email, String phoneNumber, String status, String avatarUrl) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.avatarUrl = FullUrlHelper.getFullUrl(avatarUrl);
    }
}
