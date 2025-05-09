package com.schedulify.backend.mapper;

import com.schedulify.backend.model.dto.response.MediaAttachmentDTO;
import com.schedulify.backend.model.entity.MediaAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MediaAttachmentMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    MediaAttachmentDTO toDTO(MediaAttachment mediaAttachment);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    MediaAttachment toEntity(MediaAttachmentDTO mediaAttachmentDTO);
}