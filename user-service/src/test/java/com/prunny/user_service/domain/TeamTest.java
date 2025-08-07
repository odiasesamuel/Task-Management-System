package com.prunny.user_service.domain;

import static com.prunny.user_service.domain.TeamTestSamples.*;
import static com.prunny.user_service.domain.UserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.user_service.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Team.class);
        Team team1 = getTeamSample1();
        Team team2 = new Team();
        assertThat(team1).isNotEqualTo(team2);

        team2.setId(team1.getId());
        assertThat(team1).isEqualTo(team2);

        team2 = getTeamSample2();
        assertThat(team1).isNotEqualTo(team2);
    }

    @Test
    void adminTest() {
        Team team = getTeamRandomSampleGenerator();
        User userBack = getUserRandomSampleGenerator();

        team.setAdmin(userBack);
        assertThat(team.getAdmin()).isEqualTo(userBack);

        team.admin(null);
        assertThat(team.getAdmin()).isNull();
    }

    @Test
    void membersTest() {
        Team team = getTeamRandomSampleGenerator();
        User userBack = getUserRandomSampleGenerator();

        team.addMembers(userBack);
        assertThat(team.getMembers()).containsOnly(userBack);
        assertThat(userBack.getTeams()).containsOnly(team);

        team.removeMembers(userBack);
        assertThat(team.getMembers()).doesNotContain(userBack);
        assertThat(userBack.getTeams()).doesNotContain(team);

        team.members(new HashSet<>(Set.of(userBack)));
        assertThat(team.getMembers()).containsOnly(userBack);
        assertThat(userBack.getTeams()).containsOnly(team);

        team.setMembers(new HashSet<>());
        assertThat(team.getMembers()).doesNotContain(userBack);
        assertThat(userBack.getTeams()).doesNotContain(team);
    }
}
