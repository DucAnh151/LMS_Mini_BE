package com.example.lms_mini.mapper;

import com.example.lms_mini.dto.request.student.StudentRequestDTO;
import com.example.lms_mini.dto.request.student.StudentUpdateDTO;
import com.example.lms_mini.dto.response.student.StudentBasicResponseDTO;
import com.example.lms_mini.dto.response.student.StudentDetailsDTO;
import com.example.lms_mini.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toEntity(StudentRequestDTO studentRequestDTO);

    StudentBasicResponseDTO toBasicResponseDTO(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Student updateEntityFromDto(StudentUpdateDTO dto, @MappingTarget Student entity);

    StudentDetailsDTO toDetailsDTO(Student student);
}
