package com.prunny.user_service.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.user_service.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamResponseDTO.class);
        TeamResponseDTO teamDTO1 = new TeamResponseDTO();
        teamDTO1.setId(1L);
        TeamResponseDTO teamDTO2 = new TeamResponseDTO();
        assertThat(teamDTO1).isNotEqualTo(teamDTO2);
        teamDTO2.setId(teamDTO1.getId());
        assertThat(teamDTO1).isEqualTo(teamDTO2);
        teamDTO2.setId(2L);
        assertThat(teamDTO1).isNotEqualTo(teamDTO2);
        teamDTO1.setId(null);
        assertThat(teamDTO1).isNotEqualTo(teamDTO2);
    }
}
