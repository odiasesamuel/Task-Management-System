package com.prunny.user_service.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.prunny.user_service.domain.Team} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamDTO implements Serializable {

    private Long id;

    @NotNull
    private String teamName;

    private UserDTO admin;

    private Set<UserDTO> members = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public UserDTO getAdmin() {
        return admin;
    }

    public void setAdmin(UserDTO admin) {
        this.admin = admin;
    }

    public Set<UserDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDTO> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamDTO)) {
            return false;
        }

        TeamDTO teamDTO = (TeamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamDTO{" +
            "id=" + getId() +
            ", teamName='" + getTeamName() + "'" +
            ", admin=" + getAdmin() +
            ", members=" + getMembers() +
            "}";
    }
}
