package com.prunny.user_service.service;

import com.prunny.user_service.domain.Role;
import com.prunny.user_service.domain.Team;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.repository.RoleRepository;
import com.prunny.user_service.repository.UserRepository;
import com.prunny.user_service.service.dto.RoleRequestDTO;
import com.prunny.user_service.service.dto.RoleResponseDTO;
import com.prunny.user_service.service.mapper.RoleMapper;

import java.util.HashSet;
import java.util.List;
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
 * Service Implementation for managing {@link com.prunny.user_service.domain.Role}.
 */
@Service
@Transactional
public class RoleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Save a role.
     *
     * @param roleRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public RoleResponseDTO save(RoleRequestDTO roleRequestDTO) {
        LOG.debug("Request to save Role : {}", roleRequestDTO);

        if (roleRepository.existsByRoleName(roleRequestDTO.getRoleName())) throw new AlreadyExistException("Role with this name already exist");

        List<User> users = userRepository.findAllById(roleRequestDTO.getUserIds());
        Set<User> userSet = new HashSet<>(users);

        Role role = roleMapper.toEntity(roleRequestDTO);
        role.setUsers(userSet);

        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    /**
     * Update a role.
     *
     * @param roleRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public RoleResponseDTO update(Long id, RoleRequestDTO roleRequestDTO) {
        LOG.debug("Request to update Role : {}", roleRequestDTO);

        Role existingRole = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Check for duplicate name in OTHER role (not the current one)
        roleRepository.findByRoleName(roleRequestDTO.getRoleName())
            .filter(role -> !role.getId().equals(id))
            .ifPresent(t -> {
                throw new AlreadyExistException("Role with this name already exists");
            });

        List<User> users = userRepository.findAllById(roleRequestDTO.getUserIds());
        Set<User> userSet = new HashSet<>(users);

        existingRole.setRoleName(roleRequestDTO.getRoleName());
        existingRole.setUsers(userSet);

        Role savedRole = roleRepository.save(existingRole);
        return roleMapper.toDto(savedRole);
    }

    /**
     * Partially update a role.
     *
     * @param roleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RoleResponseDTO> partialUpdate(RoleResponseDTO roleDTO) {
        LOG.debug("Request to partially update Role : {}", roleDTO);

        return roleRepository
            .findById(roleDTO.getId())
            .map(existingRole -> {
                roleMapper.partialUpdate(existingRole, roleDTO);

                return existingRole;
            })
            .map(roleRepository::save)
            .map(roleMapper::toDto);
    }

    /**
     * Get all the roles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Roles");
        return roleRepository.findAll(pageable).map(roleMapper::toDto);
    }

    /**
     * Get one role by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RoleResponseDTO> findOne(Long id) {
        LOG.debug("Request to get Role : {}", id);
        return roleRepository.findById(id).map(roleMapper::toDto);
    }

    /**
     * Delete the role by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Role : {}", id);

        if (!roleRepository.existsById(id)) throw new ResourceNotFoundException("Role not found with id: " + id);

        roleRepository.deleteRoleUserRelationships(id);
        roleRepository.deleteById(id);
    }
}
