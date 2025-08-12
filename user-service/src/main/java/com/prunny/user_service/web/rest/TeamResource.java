package com.prunny.user_service.web.rest;

import com.prunny.user_service.repository.TeamRepository;
import com.prunny.user_service.service.TeamService;
import com.prunny.user_service.service.dto.TeamRequestDTO;
import com.prunny.user_service.service.dto.TeamResponseDTO;
import com.prunny.user_service.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.prunny.user_service.domain.Team}.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeamResource.class);

    private static final String ENTITY_NAME = "userServiceTeam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamService teamService;

    private final TeamRepository teamRepository;

    public TeamResource(TeamService teamService, TeamRepository teamRepository) {
        this.teamService = teamService;
        this.teamRepository = teamRepository;
    }

    /**
     * {@code POST  /teams} : Create a new team.
     *
     * @param teamRequestDTO the teamRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamDTO, or with status {@code 400 (Bad Request)} if the team has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PostMapping("")
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO teamRequestDTO) throws URISyntaxException {
        LOG.debug("REST request to save Team : {}", teamRequestDTO);
        TeamResponseDTO teamResponseDTO = teamService.save(teamRequestDTO);

        return ResponseEntity.created(new URI("/api/teams/" + teamResponseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, teamResponseDTO.getId().toString()))
            .body(teamResponseDTO);
    }

    /**
     * {@code PUT  /teams/:id} : Updates an existing team.
     *
     * @param id the id of the teamDTO to save.
     * @param teamRequestDTO the teamRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamDTO,
     * or with status {@code 400 (Bad Request)} if the teamDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> updateTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeamRequestDTO teamRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Team : {}, {}", id, teamRequestDTO);

        TeamResponseDTO updatedTeam = teamService.update(id, teamRequestDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, updatedTeam.getId().toString()))
            .body(updatedTeam);
    }


    /**
     * {@code GET  /teams} : get all the teams.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teams in body.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @GetMapping("")
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Teams");
        Page<TeamResponseDTO> page;
        if (eagerload) {
            page = teamService.findAllWithEagerRelationships(pageable);
        } else {
            page = teamService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /teams/:id} : get the "id" team.
     *
     * @param id the id of the teamDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD') or @teamService.canAccessTeam(#id)")
    public ResponseEntity<TeamResponseDTO> getTeam(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Team : {}", id);
        Optional<TeamResponseDTO> teamDTO = teamService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamDTO);
    }

    /**
     * {@code DELETE  /teams/:id} : delete the "id" team.
     *
     * @param id the id of the teamDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Team : {}", id);
        teamService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
