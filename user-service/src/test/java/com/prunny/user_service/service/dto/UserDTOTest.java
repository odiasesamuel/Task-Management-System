package com.prunny.user_service.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.user_service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserResponseDTO.class);
        UserResponseDTO userDTO1 = new UserResponseDTO();
        userDTO1.setId(1L);
        UserResponseDTO userDTO2 = new UserResponseDTO();
        assertThat(userDTO1).isNotEqualTo(userDTO2);
        userDTO2.setId(userDTO1.getId());
        assertThat(userDTO1).isEqualTo(userDTO2);
        userDTO2.setId(2L);
        assertThat(userDTO1).isNotEqualTo(userDTO2);
        userDTO1.setId(null);
        assertThat(userDTO1).isNotEqualTo(userDTO2);
    }
}
