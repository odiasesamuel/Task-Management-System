package com.prunny.user_service.service.mapper;

import com.prunny.user_service.domain.Role;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.service.dto.RoleDTO;
import com.prunny.user_service.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {
    @Mapping(target = "users", source = "users", qualifiedByName = "userNameSet")
    RoleDTO toDto(Role s);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "removeUsers", ignore = true)
    Role toEntity(RoleDTO roleDTO);

    @Named("userName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    UserDTO toDtoUserName(User user);

    @Named("userNameSet")
    default Set<UserDTO> toDtoUserNameSet(Set<User> user) {
        return user.stream().map(this::toDtoUserName).collect(Collectors.toSet());
    }
}
