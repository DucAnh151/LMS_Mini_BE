package com.example.lms_mini.mapper;

import com.example.lms_mini.dto.response.ResourceResponseDTO;
import com.example.lms_mini.entity.Resource;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {


    List<ResourceResponseDTO> toResponseDTOList(List<Resource> resources);

}
