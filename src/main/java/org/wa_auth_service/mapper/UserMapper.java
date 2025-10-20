package org.wa_auth_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.wa_auth_service.dto.UserCreateDto;
import org.wa_auth_service.dto.UserDto;
import org.wa_auth_service.dto.UserUpdateDto;
import org.wa_auth_service.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUser(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);
}
