package com.prunny.user_service.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.prunny.user_service.domain.Role} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleRequestDTO implements Serializable {
    @NotBlank(message = "Role name is required")
    private String roleName;

    private Set<Long> userIds = new HashSet<>();

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleDTO{" +
            ", roleName='" + getRoleName() + "'" +
            ", users=" + getUserIds() +
            "}";
    }
}
