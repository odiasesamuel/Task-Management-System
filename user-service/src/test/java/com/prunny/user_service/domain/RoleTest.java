package com.prunny.user_service.domain;

import static com.prunny.user_service.domain.RoleTestSamples.*;
import static com.prunny.user_service.domain.UserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.user_service.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Role.class);
        Role role1 = getRoleSample1();
        Role role2 = new Role();
        assertThat(role1).isNotEqualTo(role2);

        role2.setId(role1.getId());
        assertThat(role1).isEqualTo(role2);

        role2 = getRoleSample2();
        assertThat(role1).isNotEqualTo(role2);
    }

    @Test
    void usersTest() {
        Role role = getRoleRandomSampleGenerator();
        User userBack = getUserRandomSampleGenerator();

        role.addUsers(userBack);
        assertThat(role.getUsers()).containsOnly(userBack);
        assertThat(userBack.getRoles()).containsOnly(role);

        role.removeUsers(userBack);
        assertThat(role.getUsers()).doesNotContain(userBack);
        assertThat(userBack.getRoles()).doesNotContain(role);

        role.users(new HashSet<>(Set.of(userBack)));
        assertThat(role.getUsers()).containsOnly(userBack);
        assertThat(userBack.getRoles()).containsOnly(role);

        role.setUsers(new HashSet<>());
        assertThat(role.getUsers()).doesNotContain(userBack);
        assertThat(userBack.getRoles()).doesNotContain(role);
    }
}
