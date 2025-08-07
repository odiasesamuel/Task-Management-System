package com.prunny.user_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A User.
 */
@Entity
@Table(name = "jhi_user")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_jhi_user__roles",
        joinColumns = @JoinColumn(name = "jhi_user_id"),
        inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    @JsonIgnoreProperties(value = { "users" }, allowSetters = true)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_jhi_user__teams",
        joinColumns = @JoinColumn(name = "jhi_user_id"),
        inverseJoinColumns = @JoinColumn(name = "teams_id")
    )
    @JsonIgnoreProperties(value = { "admin", "members" }, allowSetters = true)
    private Set<Team> teams = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public User id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public User name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public User email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public User phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    public User profilePictureUrl(String profilePictureUrl) {
        this.setProfilePictureUrl(profilePictureUrl);
        return this;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public User roles(Set<Role> roles) {
        this.setRoles(roles);
        return this;
    }

    public User addRoles(Role role) {
        this.roles.add(role);
        return this;
    }

    public User removeRoles(Role role) {
        this.roles.remove(role);
        return this;
    }

    public Set<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public User teams(Set<Team> teams) {
        this.setTeams(teams);
        return this;
    }

    public User addTeams(Team team) {
        this.teams.add(team);
        return this;
    }

    public User removeTeams(Team team) {
        this.teams.remove(team);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return getId() != null && getId().equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "User{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", profilePictureUrl='" + getProfilePictureUrl() + "'" +
            "}";
    }
}
