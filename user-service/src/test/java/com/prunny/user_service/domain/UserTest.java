package com.prunny.user_service.domain;

import static com.prunny.user_service.domain.RoleTestSamples.*;
import static com.prunny.user_service.domain.TeamTestSamples.*;
import static com.prunny.user_service.domain.UserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.user_service.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user1 = getUserSample1();
        User user2 = new User();
        assertThat(user1).isNotEqualTo(user2);

        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);

        user2 = getUserSample2();
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void rolesTest() {
        User user = getUserRandomSampleGenerator();
        Role roleBack = getRoleRandomSampleGenerator();

        user.addRoles(roleBack);
        assertThat(user.getRoles()).containsOnly(roleBack);

        user.removeRoles(roleBack);
        assertThat(user.getRoles()).doesNotContain(roleBack);

        user.roles(new HashSet<>(Set.of(roleBack)));
        assertThat(user.getRoles()).containsOnly(roleBack);

        user.setRoles(new HashSet<>());
        assertThat(user.getRoles()).doesNotContain(roleBack);
    }

    @Test
    void teamsTest() {
        User user = getUserRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        user.addTeams(teamBack);
        assertThat(user.getTeams()).containsOnly(teamBack);

        user.removeTeams(teamBack);
        assertThat(user.getTeams()).doesNotContain(teamBack);

        user.teams(new HashSet<>(Set.of(teamBack)));
        assertThat(user.getTeams()).containsOnly(teamBack);

        user.setTeams(new HashSet<>());
        assertThat(user.getTeams()).doesNotContain(teamBack);
    }
}
