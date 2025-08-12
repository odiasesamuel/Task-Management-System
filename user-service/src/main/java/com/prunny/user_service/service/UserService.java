package com.prunny.user_service.service;

import com.prunny.user_service.domain.Role;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.repository.RoleRepository;
import com.prunny.user_service.repository.UserRepository;
import com.prunny.user_service.service.dto.UserRequestDTO;
import com.prunny.user_service.service.dto.UserResponseDTO;
import com.prunny.user_service.service.mapper.UserMapper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.prunny.user_service.web.rest.errors.AlreadyExistException;
import com.prunny.user_service.web.rest.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.prunny.user_service.domain.User}.
 */
@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    /**
     * Save a user.
     *
     * @param userRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        LOG.debug("Request to save User : {}", userRequestDTO);

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) throw new AlreadyExistException("Oops"  + userRequestDTO.getEmail() + " already exist!");

        Role memberRole = roleRepository.findByRoleName("MEMBER").orElseThrow(() -> new ResourceNotFoundException("Role designated as member does not exist in database therefore user creation failed"));
        Set<Role> roles = Set.of(memberRole);

        User user = userMapper.toEntity(userRequestDTO);
        user.setRoles(roles);

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Partially update a user.
     *
     * @param userRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public UserResponseDTO partialUpdate(Long id, UserRequestDTO userRequestDTO) {
        LOG.debug("Request to partially update User : {}", userRequestDTO);

        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userMapper.partialUpdate(existingUser, userRequestDTO);

        User savedUser = userRepository.save(existingUser);

        return userMapper.toDto(savedUser);
    }

    public UserResponseDTO partialUpdateByEmail(String email, UserRequestDTO userRequestDTO) {
        LOG.debug("Request to partially update User : {}", userRequestDTO);

        User existingUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        userMapper.partialUpdate(existingUser, userRequestDTO);

        User savedUser = userRepository.save(existingUser);

        return userMapper.toDto(savedUser);
    }

    /**
     * Get all the users.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Users");
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    /**
     * Get all the users with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UserResponseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userRepository.findAllWithEagerRelationships(pageable).map(userMapper::toDto);
    }

    /**
     * Get one user by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findOne(Long id) {
        LOG.debug("Request to get User : {}", id);
        return userRepository.findOneWithEagerRelationships(id).map(userMapper::toDto);
    }

    /**
     * Get one user by email.
     *
     * @param email the email of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findOneByEmail(String email) {
        LOG.debug("Request to get User : {}", email);
        return userRepository.findOneByEmailWithEagerRelationships(email).map(userMapper::toDto);
    }

    /**
     * Delete the user by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete User : {}", id);
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("Role not found with id: " + id);

        userRepository.deleteUserTeamRelationships(id);
        userRepository.deleteUserRoleRelationships(id);
        userRepository.deleteById(id);
    }
}
