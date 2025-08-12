package com.prunny.task.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.task.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskAttachmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAttachmentDTO.class);
        TaskAttachmentDTO taskAttachmentDTO1 = new TaskAttachmentDTO();
        taskAttachmentDTO1.setId(1L);
        TaskAttachmentDTO taskAttachmentDTO2 = new TaskAttachmentDTO();
        assertThat(taskAttachmentDTO1).isNotEqualTo(taskAttachmentDTO2);
        taskAttachmentDTO2.setId(taskAttachmentDTO1.getId());
        assertThat(taskAttachmentDTO1).isEqualTo(taskAttachmentDTO2);
        taskAttachmentDTO2.setId(2L);
        assertThat(taskAttachmentDTO1).isNotEqualTo(taskAttachmentDTO2);
        taskAttachmentDTO1.setId(null);
        assertThat(taskAttachmentDTO1).isNotEqualTo(taskAttachmentDTO2);
    }
}
