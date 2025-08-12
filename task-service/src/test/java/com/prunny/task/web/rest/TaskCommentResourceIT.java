package com.prunny.task.web.rest;

import static com.prunny.task.domain.TaskCommentAsserts.*;
import static com.prunny.task.web.rest.TestUtil.createUpdateProxyForBean;
import static com.prunny.task.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prunny.task.IntegrationTest;
import com.prunny.task.domain.TaskComment;
import com.prunny.task.repository.TaskCommentRepository;
import com.prunny.task.service.dto.TaskCommentDTO;
import com.prunny.task.service.mapper.TaskCommentMapper;
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
 * Integration tests for the {@link TaskCommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskCommentResourceIT {

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/task-comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private TaskCommentMapper taskCommentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskCommentMockMvc;

    private TaskComment taskComment;

    private TaskComment insertedTaskComment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskComment createEntity() {
        return new TaskComment().comment(DEFAULT_COMMENT).user_id(DEFAULT_USER_ID).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskComment createUpdatedEntity() {
        return new TaskComment().comment(UPDATED_COMMENT).user_id(UPDATED_USER_ID).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        taskComment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTaskComment != null) {
            taskCommentRepository.delete(insertedTaskComment);
            insertedTaskComment = null;
        }
    }

    @Test
    @Transactional
    void createTaskComment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskComment
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);
        var returnedTaskCommentDTO = om.readValue(
            restTaskCommentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskCommentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskCommentDTO.class
        );

        // Validate the TaskComment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTaskComment = taskCommentMapper.toEntity(returnedTaskCommentDTO);
        assertTaskCommentUpdatableFieldsEquals(returnedTaskComment, getPersistedTaskComment(returnedTaskComment));

        insertedTaskComment = returnedTaskComment;
    }

    @Test
    @Transactional
    void createTaskCommentWithExistingId() throws Exception {
        // Create the TaskComment with an existing ID
        taskComment.setId(1L);
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskCommentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCommentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskComment.setComment(null);

        // Create the TaskComment, which fails.
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        restTaskCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskCommentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUser_idIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskComment.setUser_id(null);

        // Create the TaskComment, which fails.
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        restTaskCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskCommentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTaskComments() throws Exception {
        // Initialize the database
        insertedTaskComment = taskCommentRepository.saveAndFlush(taskComment);

        // Get all the taskCommentList
        restTaskCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskComment.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].user_id").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @Test
    @Transactional
    void getTaskComment() throws Exception {
        // Initialize the database
        insertedTaskComment = taskCommentRepository.saveAndFlush(taskComment);

        // Get the taskComment
        restTaskCommentMockMvc
            .perform(get(ENTITY_API_URL_ID, taskComment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskComment.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.user_id").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingTaskComment() throws Exception {
        // Get the taskComment
        restTaskCommentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaskComment() throws Exception {
        // Initialize the database
        insertedTaskComment = taskCommentRepository.saveAndFlush(taskComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskComment
        TaskComment updatedTaskComment = taskCommentRepository.findById(taskComment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaskComment are not directly saved in db
        em.detach(updatedTaskComment);
        updatedTaskComment.comment(UPDATED_COMMENT).user_id(UPDATED_USER_ID).createdAt(UPDATED_CREATED_AT);
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(updatedTaskComment);

        restTaskCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskCommentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskCommentDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskCommentToMatchAllProperties(updatedTaskComment);
    }

    @Test
    @Transactional
    void putNonExistingTaskComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskComment.setId(longCount.incrementAndGet());

        // Create the TaskComment
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskCommentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskComment.setId(longCount.incrementAndGet());

        // Create the TaskComment
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskComment.setId(longCount.incrementAndGet());

        // Create the TaskComment
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskCommentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskCommentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskCommentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskComment = taskCommentRepository.saveAndFlush(taskComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskComment using partial update
        TaskComment partialUpdatedTaskComment = new TaskComment();
        partialUpdatedTaskComment.setId(taskComment.getId());

        partialUpdatedTaskComment.comment(UPDATED_COMMENT).user_id(UPDATED_USER_ID);

        restTaskCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskComment))
            )
            .andExpect(status().isOk());

        // Validate the TaskComment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskCommentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTaskComment, taskComment),
            getPersistedTaskComment(taskComment)
        );
    }

    @Test
    @Transactional
    void fullUpdateTaskCommentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskComment = taskCommentRepository.saveAndFlush(taskComment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskComment using partial update
        TaskComment partialUpdatedTaskComment = new TaskComment();
        partialUpdatedTaskComment.setId(taskComment.getId());

        partialUpdatedTaskComment.comment(UPDATED_COMMENT).user_id(UPDATED_USER_ID).createdAt(UPDATED_CREATED_AT);

        restTaskCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskComment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskComment))
            )
            .andExpect(status().isOk());

        // Validate the TaskComment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskCommentUpdatableFieldsEquals(partialUpdatedTaskComment, getPersistedTaskComment(partialUpdatedTaskComment));
    }

    @Test
    @Transactional
    void patchNonExistingTaskComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskComment.setId(longCount.incrementAndGet());

        // Create the TaskComment
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskCommentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskComment.setId(longCount.incrementAndGet());

        // Create the TaskComment
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskCommentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskComment.setId(longCount.incrementAndGet());

        // Create the TaskComment
        TaskCommentDTO taskCommentDTO = taskCommentMapper.toDto(taskComment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskCommentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskCommentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskComment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskComment() throws Exception {
        // Initialize the database
        insertedTaskComment = taskCommentRepository.saveAndFlush(taskComment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskComment
        restTaskCommentMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskComment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskCommentRepository.count();
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

    protected TaskComment getPersistedTaskComment(TaskComment taskComment) {
        return taskCommentRepository.findById(taskComment.getId()).orElseThrow();
    }

    protected void assertPersistedTaskCommentToMatchAllProperties(TaskComment expectedTaskComment) {
        assertTaskCommentAllPropertiesEquals(expectedTaskComment, getPersistedTaskComment(expectedTaskComment));
    }

    protected void assertPersistedTaskCommentToMatchUpdatableProperties(TaskComment expectedTaskComment) {
        assertTaskCommentAllUpdatablePropertiesEquals(expectedTaskComment, getPersistedTaskComment(expectedTaskComment));
    }
}
