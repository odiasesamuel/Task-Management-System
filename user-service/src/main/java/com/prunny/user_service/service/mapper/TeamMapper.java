package com.prunny.user_service.service.mapper;

import com.prunny.user_service.domain.Team;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.service.dto.TeamRequestDTO;
import com.prunny.user_service.service.dto.TeamResponseDTO;
import com.prunny.user_service.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Team} and its DTO {@link TeamResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityMapper<TeamResponseDTO, Team> {
    @Mapping(target = "admin", source = "admin", qualifiedByName = "userName")
    @Mapping(target = "members", source = "members", qualifiedByName = "userNameSet")
    TeamResponseDTO toDto(Team s);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "removeMembers", ignore = true)
    Team toEntity(TeamRequestDTO teamDTO);

//    default Team toEntity(TeamRequestDTO dto, Set<User> members) {
//        Team team = toEntity(dto);
//        team.setMembers(members);
//        return team;
//    }

    @Named("userName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "profilePictureUrl", source = "profilePictureUrl")
    UserDTO toDtoUserName(User user);

    @Named("userNameSet")
    default Set<UserDTO> toDtoUserNameSet(Set<User> user) {
        return user.stream().map(this::toDtoUserName).collect(Collectors.toSet());
    }
}
