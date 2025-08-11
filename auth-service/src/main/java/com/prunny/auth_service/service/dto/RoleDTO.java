package com.prunny.auth_service.service.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleDTO implements Serializable {

    private Long id;

    @NotNull
    private String roleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleDTO)) {
            return false;
        }

        RoleDTO roleDTO = (RoleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, roleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleDTO{" +
            "id=" + getId() +
            ", roleName='" + getRoleName() + "'" +
            "}";
    }
}
