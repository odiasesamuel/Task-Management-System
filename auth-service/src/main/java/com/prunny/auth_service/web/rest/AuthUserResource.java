package com.prunny.auth_service.web.rest;

import com.prunny.auth_service.domain.AuthUser;
import com.prunny.auth_service.repository.AuthUserRepository;
import com.prunny.auth_service.service.AuthUserService;
import com.prunny.auth_service.service.dto.*;
import com.prunny.auth_service.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link AuthUser}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthUserResource.class);

    private static final String ENTITY_NAME = "authServiceAuthUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuthUserService authUserService;

    private final AuthUserRepository authUserRepository;

    public AuthUserResource(AuthUserService authUserService, AuthUserRepository authUserRepository) {
        this.authUserService = authUserService;
        this.authUserRepository = authUserRepository;
    }

    /**
     * {@code POST  /auth/register} : Register a new authUser.
     *
     * @param registerRequest the registerRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new JwtResponse, or with status {@code 409 (Conflict)} if the authUser has already exists.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) throws URISyntaxException {
        LOG.debug("REST request to register AuthUser : {}", registerRequest);

        JwtResponse jwtResponse = authUserService.register(registerRequest);

        ApiResponse<JwtResponse> apiResponse = new ApiResponse<>("User Registration Sucessful", jwtResponse);

        return ResponseEntity.created(new URI("/api/auth/" + jwtResponse.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, jwtResponse.getId().toString()))
            .body(apiResponse);
    }


    /**
     * {@code POST  /auth/login} : Login a new authUser.
     *
     * @param loginRequest the loginRequest to login.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new JwtResponse, or with status {@code 500 (Internal Server Error)} if the email or password is wrong.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequestDTO loginRequest) throws URISyntaxException {
        LOG.debug("REST request to login AuthUser : {}", loginRequest);
        JwtResponse jwtResponse = authUserService.login(loginRequest);

        ApiResponse<JwtResponse> apiResponse = new ApiResponse<>("User Login Sucessful", jwtResponse);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "User logged in successfully", jwtResponse.getId().toString()))
            .body(apiResponse);
    }

    /**
     * {@code GET  /validate} : get the validity of a token.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}, or with status {@code 401 (Unauthorized)}.
     */
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
        @RequestHeader("Authorization") String authHeader) {
        LOG.debug("REST request to login validateToken : {}", authHeader);

        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return authUserService.validateToken(authHeader.substring(7))
            ? ResponseEntity.ok().build()
            : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * {@code POST  /auth} : Create a new authUser.
     *
     * @param authUserDTO the authUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new authUserDTO, or with status {@code 400 (Bad Request)} if the authUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AuthUserDTO> createAuthUser(@Valid @RequestBody AuthUserDTO authUserDTO) throws URISyntaxException {
        LOG.debug("REST request to save AuthUser : {}", authUserDTO);
        if (authUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new authUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        authUserDTO = authUserService.save(authUserDTO);
        return ResponseEntity.created(new URI("/api/auth/" + authUserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, authUserDTO.getId().toString()))
            .body(authUserDTO);
    }

    /**
     * {@code PUT  /auth/:id} : Updates an existing authUser.
     *
     * @param id the id of the authUserDTO to save.
     * @param authUserDTO the authUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authUserDTO,
     * or with status {@code 400 (Bad Request)} if the authUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the authUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthUserDTO> updateAuthUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuthUserDTO authUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AuthUser : {}, {}", id, authUserDTO);
        if (authUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        authUserDTO = authUserService.update(authUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, authUserDTO.getId().toString()))
            .body(authUserDTO);
    }

    /**
     * {@code PATCH  /auth/:id} : Partial updates given fields of an existing authUser, field will ignore if it is null
     *
     * @param id the id of the authUserDTO to save.
     * @param authUserDTO the authUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authUserDTO,
     * or with status {@code 400 (Bad Request)} if the authUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the authUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the authUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AuthUserDTO> partialUpdateAuthUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuthUserDTO authUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AuthUser partially : {}, {}", id, authUserDTO);
        if (authUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuthUserDTO> result = authUserService.partialUpdate(authUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, authUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /auth} : get all the authUsers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of authUsers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AuthUserDTO>> getAllAuthUsers(@ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of AuthUsers");
        Page<AuthUserDTO> page = authUserService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /auth/:id} : get the "id" authUser.
     *
     * @param id the id of the authUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthUserDTO> getAuthUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AuthUser : {}", id);
        Optional<AuthUserDTO> authUserDTO = authUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(authUserDTO);
    }

    /**
     * {@code DELETE  /auth/:id} : delete the "id" authUser.
     *
     * @param id the id of the authUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AuthUser : {}", id);
        authUserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
