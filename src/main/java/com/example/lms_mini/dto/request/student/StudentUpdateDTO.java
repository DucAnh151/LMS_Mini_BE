package com.example.lms_mini.dto.request.student;

import com.example.lms_mini.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentUpdateDTO {

    String fullName;

    String identityNumber;

    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate birthDate;

    Gender gender;

    @Email(message = "student.email.invalid")
    String email;

    @NotBlank(message = "{student.phone_number.notblank}")
    String phoneNumber;

    @Size(max = 255, message = "student.address.size")
    String address;
}
