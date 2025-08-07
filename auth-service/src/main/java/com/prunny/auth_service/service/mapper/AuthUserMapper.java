package com.prunny.auth_service.service.mapper;

import com.prunny.auth_service.domain.AuthUser;
import com.prunny.auth_service.service.dto.AuthUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuthUser} and its DTO {@link AuthUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuthUserMapper extends EntityMapper<AuthUserDTO, AuthUser> {}
