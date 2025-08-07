package com.prunny.auth_service.domain;

import static com.prunny.auth_service.domain.AuthUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.auth_service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuthUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuthUser.class);
        AuthUser authUser1 = getAuthUserSample1();
        AuthUser authUser2 = new AuthUser();
        assertThat(authUser1).isNotEqualTo(authUser2);

        authUser2.setId(authUser1.getId());
        assertThat(authUser1).isEqualTo(authUser2);

        authUser2 = getAuthUserSample2();
        assertThat(authUser1).isNotEqualTo(authUser2);
    }
}
