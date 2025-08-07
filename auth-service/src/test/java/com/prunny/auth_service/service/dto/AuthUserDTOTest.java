package com.prunny.auth_service.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.auth_service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuthUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuthUserDTO.class);
        AuthUserDTO authUserDTO1 = new AuthUserDTO();
        authUserDTO1.setId(1L);
        AuthUserDTO authUserDTO2 = new AuthUserDTO();
        assertThat(authUserDTO1).isNotEqualTo(authUserDTO2);
        authUserDTO2.setId(authUserDTO1.getId());
        assertThat(authUserDTO1).isEqualTo(authUserDTO2);
        authUserDTO2.setId(2L);
        assertThat(authUserDTO1).isNotEqualTo(authUserDTO2);
        authUserDTO1.setId(null);
        assertThat(authUserDTO1).isNotEqualTo(authUserDTO2);
    }
}
