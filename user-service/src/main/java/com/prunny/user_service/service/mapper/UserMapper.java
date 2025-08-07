package com.prunny.user_service.service.mapper;

import com.prunny.user_service.domain.Role;
import com.prunny.user_service.domain.Team;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.service.dto.RoleDTO;
import com.prunny.user_service.service.dto.TeamDTO;
import com.prunny.user_service.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDTO, User> {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleRoleNameSet")
    @Mapping(target = "teams", source = "teams", qualifiedByName = "teamTeamNameSet")
    UserDTO toDto(User s);

    @Mapping(target = "removeRoles", ignore = true)
    @Mapping(target = "removeTeams", ignore = true)
    User toEntity(UserDTO userDTO);

    @Named("roleRoleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "roleName", source = "roleName")
    RoleDTO toDtoRoleRoleName(Role role);

    @Named("roleRoleNameSet")
    default Set<RoleDTO> toDtoRoleRoleNameSet(Set<Role> role) {
        return role.stream().map(this::toDtoRoleRoleName).collect(Collectors.toSet());
    }

    @Named("teamTeamName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "teamName", source = "teamName")
    TeamDTO toDtoTeamTeamName(Team team);

    @Named("teamTeamNameSet")
    default Set<TeamDTO> toDtoTeamTeamNameSet(Set<Team> team) {
        return team.stream().map(this::toDtoTeamTeamName).collect(Collectors.toSet());
    }
}
