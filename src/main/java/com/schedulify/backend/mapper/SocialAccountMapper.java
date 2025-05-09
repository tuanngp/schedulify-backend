package com.schedulify.backend.mapper;

import com.schedulify.backend.model.dto.response.SocialAccountDTO;
import com.schedulify.backend.model.entity.SocialAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SocialAccountMapper {
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    SocialAccountDTO toDTO(SocialAccount socialAccount);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    SocialAccount toEntity(SocialAccountDTO socialAccountDTO);
}