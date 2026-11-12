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

    @Mapping(target = "avatarUrl", source = "avatarUrl")
    StudentBasicResponseDTO toBasicResponseDTO(Student student, String avatarUrl);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Student updateEntityFromDto(StudentUpdateDTO dto, @MappingTarget Student entity);

    @Mapping(target = "avatarUrl", source = "avatarUrl")
    StudentDetailsDTO toDetailsDTO(Student student, String avatarUrl);
}
