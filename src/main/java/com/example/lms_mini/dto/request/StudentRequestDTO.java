package com.example.lms_mini.dto.request;

import com.example.lms_mini.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentRequestDTO {

    @NotBlank(message = "{student.fullname.notblank}")
    String fullName;

    @NotBlank(message = "{student.identity_number.notblank}")
    String identityNumber;

    @Past
    @NotNull(message = "{student.birthdate.notnull}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate birthDate;

    Gender gender;

    @Email(message = "student.email.invalid")
    @NotBlank(message = "{student.email.notblank}")
    String email;

    @NotBlank(message = "{student.phone_number.notblank}")
    String phoneNumber;

    @NotBlank(message = "{student.address.notblank}")
    @Size(max = 255, message = "student.address.size")
    String address;
}
