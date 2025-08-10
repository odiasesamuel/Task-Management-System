package com.prunny.user_service.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.prunny.user_service.domain.Team} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamRequestDTO implements Serializable {
    @NotBlank(message = "Team name is required")
    private String teamName;

//    private UserDTO admin;
//    You should get the adminId from the Spring Security context, not from the request body.

    private Set<Long> memberIds = new HashSet<>();

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Set<Long> getMemberIds() {
        return memberIds;
    }

    public void setMembersIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamDTO{" +
            ", teamName='" + getTeamName() + "'" +
            ", memberIds=" + getMemberIds() +
            "}";
    }
}
