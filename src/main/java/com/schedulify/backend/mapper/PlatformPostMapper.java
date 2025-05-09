package com.schedulify.backend.mapper;

import com.schedulify.backend.model.dto.response.PlatformPostDTO;
import com.schedulify.backend.model.entity.PlatformPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatformPostMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "socialAccountId", source = "socialAccount.id")
    @Mapping(target = "socialAccountName", source = "socialAccount.accountName")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PlatformPostDTO toDTO(PlatformPost platformPost);

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "socialAccount", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    PlatformPost toEntity(PlatformPostDTO platformPostDTO);
}