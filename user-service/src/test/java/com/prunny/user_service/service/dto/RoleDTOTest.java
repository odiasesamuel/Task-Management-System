package com.prunny.user_service.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.user_service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoleResponseDTO.class);
        RoleResponseDTO roleDTO1 = new RoleResponseDTO();
        roleDTO1.setId(1L);
        RoleResponseDTO roleDTO2 = new RoleResponseDTO();
        assertThat(roleDTO1).isNotEqualTo(roleDTO2);
        roleDTO2.setId(roleDTO1.getId());
        assertThat(roleDTO1).isEqualTo(roleDTO2);
        roleDTO2.setId(2L);
        assertThat(roleDTO1).isNotEqualTo(roleDTO2);
        roleDTO1.setId(null);
        assertThat(roleDTO1).isNotEqualTo(roleDTO2);
    }
}
