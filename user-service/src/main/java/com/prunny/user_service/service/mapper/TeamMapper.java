package com.prunny.user_service.service.mapper;

import com.prunny.user_service.domain.Team;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.service.dto.TeamDTO;
import com.prunny.user_service.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Team} and its DTO {@link TeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityMapper<TeamDTO, Team> {
    @Mapping(target = "admin", source = "admin", qualifiedByName = "userName")
    @Mapping(target = "members", source = "members", qualifiedByName = "userNameSet")
    TeamDTO toDto(Team s);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "removeMembers", ignore = true)
    Team toEntity(TeamDTO teamDTO);

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
