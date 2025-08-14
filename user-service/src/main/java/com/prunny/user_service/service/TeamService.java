package com.prunny.user_service.service;

import com.prunny.user_service.domain.Team;
import com.prunny.user_service.domain.User;
import com.prunny.user_service.repository.TeamRepository;
import com.prunny.user_service.repository.UserRepository;
import com.prunny.user_service.security.SecurityUtils;
import com.prunny.user_service.service.dto.TeamRequestDTO;
import com.prunny.user_service.service.dto.TeamResponseDTO;
import com.prunny.user_service.service.mapper.TeamMapper;

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
 * Service Implementation for managing {@link com.prunny.user_service.domain.Team}.
 */
@Service
@Transactional
public class TeamService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    private final TeamMapper teamMapper;

    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a team.
     *
     * @param teamRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamResponseDTO save(TeamRequestDTO teamRequestDTO) {
        LOG.debug("Request to save Team : {}", teamRequestDTO);

        if (teamRepository.existsByTeamName(teamRequestDTO.getTeamName())) throw new AlreadyExistException("Team with this name already exist");

        User admin = SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findByEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<User> members = userRepository.findAllById(teamRequestDTO.getMemberIds());
        Set<User> membersSet = new HashSet<>(members);

        Team team = teamMapper.toEntity(teamRequestDTO);
        team.setMembers(membersSet);
        team.setAdmin(admin);

        team = teamRepository.save(team);
        return teamMapper.toDto(team);
    }

    /**
     * Update a team.
     *
     * @param teamRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamResponseDTO update(Long id, TeamRequestDTO teamRequestDTO) {
        LOG.debug("Request to update Team with id: {} : {}", id, teamRequestDTO);

        Team existingTeam = teamRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));

        // Check for duplicate name in OTHER teams (not the current one)
        teamRepository.findByTeamName(teamRequestDTO.getTeamName())
            .filter(team -> !team.getId().equals(id)) // Exclude current team from duplicate check
            .ifPresent(t -> {
                throw new AlreadyExistException("Team with this name already exists");
            });

        List<User> members = userRepository.findAllById(teamRequestDTO.getMemberIds());
        Set<User> membersSet = new HashSet<>(members);

        existingTeam.setTeamName(teamRequestDTO.getTeamName());
        existingTeam.setMembers(membersSet);

        Team savedTeam = teamRepository.save(existingTeam);
        return teamMapper.toDto(savedTeam);
    }


    /**
     * Get all the teams.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TeamResponseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Teams");
        return teamRepository.findAll(pageable).map(teamMapper::toDto);
    }

    /**
     * Get all the teams with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TeamResponseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return teamRepository.findAllWithEagerRelationships(pageable).map(teamMapper::toDto);
    }

    /**
     * Get one team by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeamResponseDTO> findOne(Long id) {
        LOG.debug("Request to get Team : {}", id);
        return teamRepository.findOneWithEagerRelationships(id).map(teamMapper::toDto);
    }

    /**
     * Delete the team by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Team : {}", id);

        if (!teamRepository.existsById(id)) throw new ResourceNotFoundException("Team not found with id: " + id);

        teamRepository.deleteTeamUserRelationships(id);
        teamRepository.deleteById(id);
    }

    /**
     * Check if the current user can access the specified team
     * Used by @PreAuthorize annotation
     */
    public boolean canAccessTeam(Long teamId) {
        LOG.debug("Checking access to team: {}", teamId);

        // Get current user from security context
        String currentUserEmail = SecurityUtils.getCurrentUserLogin()
            .orElse(null);

        if (currentUserEmail == null) {
            LOG.warn("No authenticated user found");
            return false;
        }

        // Get current user
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElse(null);

        if (currentUser == null) {
            LOG.warn("User not found: {}", currentUserEmail);
            return false;
        }

        // Get team
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            LOG.debug("Team not found: {}", teamId);
            return true; // Let findOne() handle the not found case
        }

        Team team = teamOpt.get();

        // Check if user is member
        boolean isMember = team.getMembers().stream()
            .anyMatch(member -> member.getId().equals(currentUser.getId()));

        LOG.debug("User {} is member of team {}: {}", currentUserEmail, teamId, isMember);

        return isMember;
    }
}
