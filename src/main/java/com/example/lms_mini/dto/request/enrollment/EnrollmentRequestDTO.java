package com.example.lms_mini.dto.request.enrollment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentRequestDTO {

    @NotNull(message = "{enrollment.student.notnull}")
    Long studentId;

    @NotEmpty(message = "{enrollment.courses.notempty}")
    List<Long> courseIds;
}
