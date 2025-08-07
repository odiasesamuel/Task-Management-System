package com.prunny.auth_service.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.prunny.auth_service.domain.AuthUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthUserDTO implements Serializable {

    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthUserDTO)) {
            return false;
        }

        AuthUserDTO authUserDTO = (AuthUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, authUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuthUserDTO{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
