package com.prunny.user_service.service.mapper;

import com.prunny.user_service.domain.Role;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.service.dto.RoleRequestDTO;
import com.prunny.user_service.service.dto.RoleResponseDTO;
import com.prunny.user_service.service.dto.UserResponseDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleResponseDTO, Role> {
    @Mapping(target = "users", source = "users", qualifiedByName = "userNameSet")
    RoleResponseDTO toDto(Role s);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "removeUsers", ignore = true)
    Role toEntity(RoleRequestDTO roleDTO);

    @Named("userName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "profilePictureUrl", source = "profilePictureUrl")
    UserResponseDTO toDtoUserName(User user);

    @Named("userNameSet")
    default Set<UserResponseDTO> toDtoUserNameSet(Set<User> user) {
        return user.stream().map(this::toDtoUserName).collect(Collectors.toSet());
    }
}
