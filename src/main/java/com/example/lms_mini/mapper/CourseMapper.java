package com.example.lms_mini.mapper;

import com.example.lms_mini.dto.request.course.CourseUpdateDTO;
import com.example.lms_mini.dto.response.course.CourseBasicResponseDTO;
import com.example.lms_mini.dto.response.course.CourseDetailsDTO;
import com.example.lms_mini.dto.request.course.CourseRequestDTO;
import com.example.lms_mini.entity.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ResourceMapper.class)
public interface CourseMapper {

    Course toEntity(CourseRequestDTO request);

    CourseBasicResponseDTO toBasicResponseDTO(Course course);

    @Mapping(target = "resources", ignore = true)
    CourseDetailsDTO toDetailResponseDTO(Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCourseFromDto(@MappingTarget Course course, CourseUpdateDTO dto);
}
