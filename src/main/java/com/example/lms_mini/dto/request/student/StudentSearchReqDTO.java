package com.example.lms_mini.dto.request.student;

import com.example.lms_mini.enums.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentSearchReqDTO {

    String fullName;

    String email;

    String phoneNumber;

    Status status;
}
