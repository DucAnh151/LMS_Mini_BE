package com.example.lms_mini.mapper;

import com.example.lms_mini.dto.response.enrollment.EnrollmentBasicResponseDTO;
import com.example.lms_mini.entity.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {StudentMapper.class, CourseMapper.class})
public interface EnrollmentMapper {

    EnrollmentBasicResponseDTO toBasicDTO(Enrollment enrollment);
}
