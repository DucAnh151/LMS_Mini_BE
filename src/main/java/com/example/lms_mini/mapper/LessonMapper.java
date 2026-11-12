package com.example.lms_mini.mapper;

import com.example.lms_mini.dto.request.lesson.LessonRequestDTO;
import com.example.lms_mini.dto.request.lesson.LessonUpdateDTO;
import com.example.lms_mini.dto.response.lesson.LessonBasicResponseDTO;
import com.example.lms_mini.dto.response.lesson.LessonDetailsDTO;
import com.example.lms_mini.entity.Lesson;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ResourceMapper.class})
public interface LessonMapper {

    Lesson toEntity(LessonRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLessonFromDto(@MappingTarget Lesson lesson, LessonUpdateDTO request);

    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "thumbnails", ignore = true)
    @Mapping(target = "documents", ignore = true)
    LessonDetailsDTO toDetailsDTO(Lesson lesson);


    LessonBasicResponseDTO toBasicResponseDTO(Lesson lesson);
}
