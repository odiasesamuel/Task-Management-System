package com.prunny.task.web.rest;

import static com.prunny.task.domain.TaskAttachmentAsserts.*;
import static com.prunny.task.web.rest.TestUtil.createUpdateProxyForBean;
import static com.prunny.task.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prunny.task.IntegrationTest;
import com.prunny.task.domain.TaskAttachment;
import com.prunny.task.repository.TaskAttachmentRepository;
import com.prunny.task.service.dto.TaskAttachmentDTO;
import com.prunny.task.service.mapper.TaskAttachmentMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TaskAttachmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskAttachmentResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_URL = "AAAAAAAAAA";
    private static final String UPDATED_FILE_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_UPLOADED_BY_USER_ID = 1L;
    private static final Long UPDATED_UPLOADED_BY_USER_ID = 2L;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/task-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Autowired
    private TaskAttachmentMapper taskAttachmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskAttachmentMockMvc;

    private TaskAttachment taskAttachment;

    private TaskAttachment insertedTaskAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskAttachment createEntity() {
        return new TaskAttachment()
            .fileName(DEFAULT_FILE_NAME)
            .fileUrl(DEFAULT_FILE_URL)
            .uploadedByUserId(DEFAULT_UPLOADED_BY_USER_ID)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskAttachment createUpdatedEntity() {
        return new TaskAttachment()
            .fileName(UPDATED_FILE_NAME)
            .fileUrl(UPDATED_FILE_URL)
            .uploadedByUserId(UPDATED_UPLOADED_BY_USER_ID)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        taskAttachment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTaskAttachment != null) {
            taskAttachmentRepository.delete(insertedTaskAttachment);
            insertedTaskAttachment = null;
        }
    }

    @Test
    @Transactional
    void createTaskAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskAttachment
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);
        var returnedTaskAttachmentDTO = om.readValue(
            restTaskAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskAttachmentDTO.class
        );

        // Validate the TaskAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTaskAttachment = taskAttachmentMapper.toEntity(returnedTaskAttachmentDTO);
        assertTaskAttachmentUpdatableFieldsEquals(returnedTaskAttachment, getPersistedTaskAttachment(returnedTaskAttachment));

        insertedTaskAttachment = returnedTaskAttachment;
    }

    @Test
    @Transactional
    void createTaskAttachmentWithExistingId() throws Exception {
        // Create the TaskAttachment with an existing ID
        taskAttachment.setId(1L);
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskAttachment.setFileName(null);

        // Create the TaskAttachment, which fails.
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        restTaskAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFileUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskAttachment.setFileUrl(null);

        // Create the TaskAttachment, which fails.
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        restTaskAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTaskAttachments() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        // Get all the taskAttachmentList
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskAttachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileUrl").value(hasItem(DEFAULT_FILE_URL)))
            .andExpect(jsonPath("$.[*].uploadedByUserId").value(hasItem(DEFAULT_UPLOADED_BY_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @Test
    @Transactional
    void getTaskAttachment() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        // Get the taskAttachment
        restTaskAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, taskAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskAttachment.getId().intValue()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.fileUrl").value(DEFAULT_FILE_URL))
            .andExpect(jsonPath("$.uploadedByUserId").value(DEFAULT_UPLOADED_BY_USER_ID.intValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingTaskAttachment() throws Exception {
        // Get the taskAttachment
        restTaskAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaskAttachment() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAttachment
        TaskAttachment updatedTaskAttachment = taskAttachmentRepository.findById(taskAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaskAttachment are not directly saved in db
        em.detach(updatedTaskAttachment);
        updatedTaskAttachment
            .fileName(UPDATED_FILE_NAME)
            .fileUrl(UPDATED_FILE_URL)
            .uploadedByUserId(UPDATED_UPLOADED_BY_USER_ID)
            .createdAt(UPDATED_CREATED_AT);
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(updatedTaskAttachment);

        restTaskAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAttachmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskAttachmentToMatchAllProperties(updatedTaskAttachment);
    }

    @Test
    @Transactional
    void putNonExistingTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // Create the TaskAttachment
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // Create the TaskAttachment
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // Create the TaskAttachment
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAttachment using partial update
        TaskAttachment partialUpdatedTaskAttachment = new TaskAttachment();
        partialUpdatedTaskAttachment.setId(taskAttachment.getId());

        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskAttachment))
            )
            .andExpect(status().isOk());

        // Validate the TaskAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTaskAttachment, taskAttachment),
            getPersistedTaskAttachment(taskAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateTaskAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAttachment using partial update
        TaskAttachment partialUpdatedTaskAttachment = new TaskAttachment();
        partialUpdatedTaskAttachment.setId(taskAttachment.getId());

        partialUpdatedTaskAttachment
            .fileName(UPDATED_FILE_NAME)
            .fileUrl(UPDATED_FILE_URL)
            .uploadedByUserId(UPDATED_UPLOADED_BY_USER_ID)
            .createdAt(UPDATED_CREATED_AT);

        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskAttachment))
            )
            .andExpect(status().isOk());

        // Validate the TaskAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskAttachmentUpdatableFieldsEquals(partialUpdatedTaskAttachment, getPersistedTaskAttachment(partialUpdatedTaskAttachment));
    }

    @Test
    @Transactional
    void patchNonExistingTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // Create the TaskAttachment
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskAttachmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // Create the TaskAttachment
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAttachment.setId(longCount.incrementAndGet());

        // Create the TaskAttachment
        TaskAttachmentDTO taskAttachmentDTO = taskAttachmentMapper.toDto(taskAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskAttachment() throws Exception {
        // Initialize the database
        insertedTaskAttachment = taskAttachmentRepository.saveAndFlush(taskAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskAttachment
        restTaskAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskAttachmentRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TaskAttachment getPersistedTaskAttachment(TaskAttachment taskAttachment) {
        return taskAttachmentRepository.findById(taskAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedTaskAttachmentToMatchAllProperties(TaskAttachment expectedTaskAttachment) {
        assertTaskAttachmentAllPropertiesEquals(expectedTaskAttachment, getPersistedTaskAttachment(expectedTaskAttachment));
    }

    protected void assertPersistedTaskAttachmentToMatchUpdatableProperties(TaskAttachment expectedTaskAttachment) {
        assertTaskAttachmentAllUpdatablePropertiesEquals(expectedTaskAttachment, getPersistedTaskAttachment(expectedTaskAttachment));
    }
}
