package com.prunny.user_service.web.rest;

import static com.prunny.user_service.domain.UserAsserts.*;
import static com.prunny.user_service.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prunny.user_service.IntegrationTest;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.repository.UserRepository;
import com.prunny.user_service.service.UserService;
import com.prunny.user_service.service.dto.UserResponseDTO;
import com.prunny.user_service.service.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PROFILE_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_PICTURE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserRepository userRepositoryMock;

    @Autowired
    private UserMapper userMapper;

    @Mock
    private UserService userServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserMockMvc;

    private User user;

    private User insertedUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createEntity() {
        return new User()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .profilePictureUrl(DEFAULT_PROFILE_PICTURE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createUpdatedEntity() {
        return new User()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL);
    }

    @BeforeEach
    void initTest() {
        user = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUser != null) {
            userRepository.delete(insertedUser);
            insertedUser = null;
        }
    }

    @Test
    @Transactional
    void createUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the User
        UserResponseDTO userDTO = userMapper.toDto(user);
        var returnedUserDTO = om.readValue(
            restUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserResponseDTO.class
        );

        // Validate the User in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUser = userMapper.toEntity(returnedUserDTO);
        assertUserUpdatableFieldsEquals(returnedUser, getPersistedUser(returnedUser));

        insertedUser = returnedUser;
    }

    @Test
    @Transactional
    void createUserWithExistingId() throws Exception {
        // Create the User with an existing ID
        user.setId(1L);
        UserResponseDTO userDTO = userMapper.toDto(user);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        user.setName(null);

        // Create the User, which fails.
        UserResponseDTO userDTO = userMapper.toDto(user);

        restUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        user.setEmail(null);

        // Create the User, which fails.
        UserResponseDTO userDTO = userMapper.toDto(user);

        restUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUsers() throws Exception {
        // Initialize the database
        insertedUser = userRepository.saveAndFlush(user);

        // Get all the userList
        restUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(user.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].profilePictureUrl").value(hasItem(DEFAULT_PROFILE_PICTURE_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(userServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUser() throws Exception {
        // Initialize the database
        insertedUser = userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc
            .perform(get(ENTITY_API_URL_ID, user.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(user.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.profilePictureUrl").value(DEFAULT_PROFILE_PICTURE_URL));
    }

    @Test
    @Transactional
    void getNonExistingUser() throws Exception {
        // Get the user
        restUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUser() throws Exception {
        // Initialize the database
        insertedUser = userRepository.saveAndFlush(user);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUser are not directly saved in db
        em.detach(updatedUser);
        updatedUser
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL);
        UserResponseDTO userDTO = userMapper.toDto(updatedUser);

        restUserMockMvc
            .perform(put(ENTITY_API_URL_ID, userDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isOk());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserToMatchAllProperties(updatedUser);
    }

    @Test
    @Transactional
    void putNonExistingUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        user.setId(longCount.incrementAndGet());

        // Create the User
        UserResponseDTO userDTO = userMapper.toDto(user);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(put(ENTITY_API_URL_ID, userDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        user.setId(longCount.incrementAndGet());

        // Create the User
        UserResponseDTO userDTO = userMapper.toDto(user);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        user.setId(longCount.incrementAndGet());

        // Create the User
        UserResponseDTO userDTO = userMapper.toDto(user);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserWithPatch() throws Exception {
        // Initialize the database
        insertedUser = userRepository.saveAndFlush(user);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the user using partial update
        User partialUpdatedUser = new User();
        partialUpdatedUser.setId(user.getId());

        partialUpdatedUser.phoneNumber(UPDATED_PHONE_NUMBER);

        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUser))
            )
            .andExpect(status().isOk());

        // Validate the User in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUser, user), getPersistedUser(user));
    }

    @Test
    @Transactional
    void fullUpdateUserWithPatch() throws Exception {
        // Initialize the database
        insertedUser = userRepository.saveAndFlush(user);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the user using partial update
        User partialUpdatedUser = new User();
        partialUpdatedUser.setId(user.getId());

        partialUpdatedUser
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .profilePictureUrl(UPDATED_PROFILE_PICTURE_URL);

        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUser))
            )
            .andExpect(status().isOk());

        // Validate the User in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserUpdatableFieldsEquals(partialUpdatedUser, getPersistedUser(partialUpdatedUser));
    }

    @Test
    @Transactional
    void patchNonExistingUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        user.setId(longCount.incrementAndGet());

        // Create the User
        UserResponseDTO userDTO = userMapper.toDto(user);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        user.setId(longCount.incrementAndGet());

        // Create the User
        UserResponseDTO userDTO = userMapper.toDto(user);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        user.setId(longCount.incrementAndGet());

        // Create the User
        UserResponseDTO userDTO = userMapper.toDto(user);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the User in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUser() throws Exception {
        // Initialize the database
        insertedUser = userRepository.saveAndFlush(user);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the user
        restUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, user.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userRepository.count();
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

    protected User getPersistedUser(User user) {
        return userRepository.findById(user.getId()).orElseThrow();
    }

    protected void assertPersistedUserToMatchAllProperties(User expectedUser) {
        assertUserAllPropertiesEquals(expectedUser, getPersistedUser(expectedUser));
    }

    protected void assertPersistedUserToMatchUpdatableProperties(User expectedUser) {
        assertUserAllUpdatablePropertiesEquals(expectedUser, getPersistedUser(expectedUser));
    }
}
