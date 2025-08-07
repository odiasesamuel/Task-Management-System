package com.prunny.user_service.service;

import com.prunny.user_service.domain.User;
import com.prunny.user_service.repository.UserRepository;
import com.prunny.user_service.service.dto.UserDTO;
import com.prunny.user_service.service.mapper.UserMapper;
import java.util.Optional;
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

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Save a user.
     *
     * @param userDTO the entity to save.
     * @return the persisted entity.
     */
    public UserDTO save(UserDTO userDTO) {
        LOG.debug("Request to save User : {}", userDTO);
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
       return userMapper.toDto(user);
    }

    /**
     * Update a user.
     *
     * @param userDTO the entity to save.
     * @return the persisted entity.
     */
    public UserDTO update(UserDTO userDTO) {
        LOG.debug("Request to update User : {}", userDTO);
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Partially update a user.
     *
     * @param userDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserDTO> partialUpdate(UserDTO userDTO) {
        LOG.debug("Request to partially update User : {}", userDTO);

        return userRepository
            .findById(userDTO.getId())
            .map(existingUser -> {
                userMapper.partialUpdate(existingUser, userDTO);

                return existingUser;
            })
            .map(userRepository::save)
            .map(userMapper::toDto);
    }

    /**
     * Get all the users.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Users");
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    /**
     * Get all the users with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userRepository.findAllWithEagerRelationships(pageable).map(userMapper::toDto);
    }

    /**
     * Get one user by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findOne(Long id) {
        LOG.debug("Request to get User : {}", id);
        return userRepository.findOneWithEagerRelationships(id).map(userMapper::toDto);
    }

    /**
     * Delete the user by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete User : {}", id);
        userRepository.deleteById(id);
    }
}
