package com.schedulify.backend.mapper;

import com.schedulify.backend.model.dto.response.PostDTO;
import com.schedulify.backend.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.username")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "platformPosts", ignore = true)
    @Mapping(target = "mediaAttachments", ignore = true)
    PostDTO toDTO(Post post);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "platformPosts", ignore = true)
    @Mapping(target = "mediaAttachments", ignore = true)
    Post toEntity(PostDTO postDTO);
}