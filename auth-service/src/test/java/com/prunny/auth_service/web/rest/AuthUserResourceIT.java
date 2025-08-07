package com.prunny.auth_service.web.rest;

import static com.prunny.auth_service.domain.AuthUserAsserts.*;
import static com.prunny.auth_service.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prunny.auth_service.IntegrationTest;
import com.prunny.auth_service.domain.AuthUser;
import com.prunny.auth_service.repository.AuthUserRepository;
import com.prunny.auth_service.service.dto.AuthUserDTO;
import com.prunny.auth_service.service.mapper.AuthUserMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link AuthUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuthUserResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/auth-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private AuthUserMapper authUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuthUserMockMvc;

    private AuthUser authUser;

    private AuthUser insertedAuthUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuthUser createEntity() {
        return new AuthUser().email(DEFAULT_EMAIL).password(DEFAULT_PASSWORD);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuthUser createUpdatedEntity() {
        return new AuthUser().email(UPDATED_EMAIL).password(UPDATED_PASSWORD);
    }

    @BeforeEach
    void initTest() {
        authUser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAuthUser != null) {
            authUserRepository.delete(insertedAuthUser);
            insertedAuthUser = null;
        }
    }

    @Test
    @Transactional
    void createAuthUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuthUser
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);
        var returnedAuthUserDTO = om.readValue(
            restAuthUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuthUserDTO.class
        );

        // Validate the AuthUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuthUser = authUserMapper.toEntity(returnedAuthUserDTO);
        assertAuthUserUpdatableFieldsEquals(returnedAuthUser, getPersistedAuthUser(returnedAuthUser));

        insertedAuthUser = returnedAuthUser;
    }

    @Test
    @Transactional
    void createAuthUserWithExistingId() throws Exception {
        // Create the AuthUser with an existing ID
        authUser.setId(1L);
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        authUser.setEmail(null);

        // Create the AuthUser, which fails.
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        restAuthUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        authUser.setPassword(null);

        // Create the AuthUser, which fails.
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        restAuthUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAuthUsers() throws Exception {
        // Initialize the database
        insertedAuthUser = authUserRepository.saveAndFlush(authUser);

        // Get all the authUserList
        restAuthUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(authUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }

    @Test
    @Transactional
    void getAuthUser() throws Exception {
        // Initialize the database
        insertedAuthUser = authUserRepository.saveAndFlush(authUser);

        // Get the authUser
        restAuthUserMockMvc
            .perform(get(ENTITY_API_URL_ID, authUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(authUser.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD));
    }

    @Test
    @Transactional
    void getNonExistingAuthUser() throws Exception {
        // Get the authUser
        restAuthUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuthUser() throws Exception {
        // Initialize the database
        insertedAuthUser = authUserRepository.saveAndFlush(authUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the authUser
        AuthUser updatedAuthUser = authUserRepository.findById(authUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuthUser are not directly saved in db
        em.detach(updatedAuthUser);
        updatedAuthUser.email(UPDATED_EMAIL).password(UPDATED_PASSWORD);
        AuthUserDTO authUserDTO = authUserMapper.toDto(updatedAuthUser);

        restAuthUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, authUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuthUserToMatchAllProperties(updatedAuthUser);
    }

    @Test
    @Transactional
    void putNonExistingAuthUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authUser.setId(longCount.incrementAndGet());

        // Create the AuthUser
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, authUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuthUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authUser.setId(longCount.incrementAndGet());

        // Create the AuthUser
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuthUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authUser.setId(longCount.incrementAndGet());

        // Create the AuthUser
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuthUserWithPatch() throws Exception {
        // Initialize the database
        insertedAuthUser = authUserRepository.saveAndFlush(authUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the authUser using partial update
        AuthUser partialUpdatedAuthUser = new AuthUser();
        partialUpdatedAuthUser.setId(authUser.getId());

        partialUpdatedAuthUser.email(UPDATED_EMAIL).password(UPDATED_PASSWORD);

        restAuthUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthUser))
            )
            .andExpect(status().isOk());

        // Validate the AuthUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthUserUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAuthUser, authUser), getPersistedAuthUser(authUser));
    }

    @Test
    @Transactional
    void fullUpdateAuthUserWithPatch() throws Exception {
        // Initialize the database
        insertedAuthUser = authUserRepository.saveAndFlush(authUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the authUser using partial update
        AuthUser partialUpdatedAuthUser = new AuthUser();
        partialUpdatedAuthUser.setId(authUser.getId());

        partialUpdatedAuthUser.email(UPDATED_EMAIL).password(UPDATED_PASSWORD);

        restAuthUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthUser))
            )
            .andExpect(status().isOk());

        // Validate the AuthUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthUserUpdatableFieldsEquals(partialUpdatedAuthUser, getPersistedAuthUser(partialUpdatedAuthUser));
    }

    @Test
    @Transactional
    void patchNonExistingAuthUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authUser.setId(longCount.incrementAndGet());

        // Create the AuthUser
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, authUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuthUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authUser.setId(longCount.incrementAndGet());

        // Create the AuthUser
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuthUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authUser.setId(longCount.incrementAndGet());

        // Create the AuthUser
        AuthUserDTO authUserDTO = authUserMapper.toDto(authUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(authUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuthUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuthUser() throws Exception {
        // Initialize the database
        insertedAuthUser = authUserRepository.saveAndFlush(authUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the authUser
        restAuthUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, authUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return authUserRepository.count();
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

    protected AuthUser getPersistedAuthUser(AuthUser authUser) {
        return authUserRepository.findById(authUser.getId()).orElseThrow();
    }

    protected void assertPersistedAuthUserToMatchAllProperties(AuthUser expectedAuthUser) {
        assertAuthUserAllPropertiesEquals(expectedAuthUser, getPersistedAuthUser(expectedAuthUser));
    }

    protected void assertPersistedAuthUserToMatchUpdatableProperties(AuthUser expectedAuthUser) {
        assertAuthUserAllUpdatablePropertiesEquals(expectedAuthUser, getPersistedAuthUser(expectedAuthUser));
    }
}
