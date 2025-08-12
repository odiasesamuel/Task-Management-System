package com.prunny.user_service.service.mapper;

import com.prunny.user_service.domain.Role;
import com.prunny.user_service.domain.Team;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.service.dto.RoleResponseDTO;
import com.prunny.user_service.service.dto.TeamResponseDTO;
import com.prunny.user_service.service.dto.UserRequestDTO;
import com.prunny.user_service.service.dto.UserResponseDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserResponseDTO, User> {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleRoleNameSet")
    @Mapping(target = "teams", source = "teams", qualifiedByName = "teamTeamNameSet")
    UserResponseDTO toDto(User s);

    @Mapping(target = "removeRoles", ignore = true)
    @Mapping(target = "removeTeams", ignore = true)
    User toEntity(UserRequestDTO userDTO);

    @Named("roleRoleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "roleName", source = "roleName")
    RoleResponseDTO toDtoRoleRoleName(Role role);

    @Named("roleRoleNameSet")
    default Set<RoleResponseDTO> toDtoRoleRoleNameSet(Set<Role> role) {
        return role.stream().map(this::toDtoRoleRoleName).collect(Collectors.toSet());
    }

    @Named("teamTeamName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "teamName", source = "teamName")
    TeamResponseDTO toDtoTeamTeamName(Team team);

    @Named("teamTeamNameSet")
    default Set<TeamResponseDTO> toDtoTeamTeamNameSet(Set<Team> team) {
        return team.stream().map(this::toDtoTeamTeamName).collect(Collectors.toSet());
    }

    @Mapping(target = "removeRoles", ignore = true)
    @Mapping(target = "removeTeams", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget User entity, UserRequestDTO userRequestDTO);
}
