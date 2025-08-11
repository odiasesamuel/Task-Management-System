package com.prunny.auth_service.service;

import com.prunny.auth_service.client.UserServiceClient;
import com.prunny.auth_service.domain.AuthUser;
import com.prunny.auth_service.repository.AuthUserRepository;
import com.prunny.auth_service.security.TokenService;
import com.prunny.auth_service.service.dto.*;
import com.prunny.auth_service.service.mapper.AuthUserMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.prunny.auth_service.web.rest.errors.AlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.prunny.auth_service.domain.AuthUser}.
 */
@Service
@Transactional
public class AuthUserService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthUserService.class);

    private final AuthUserRepository authUserRepository;

    private final AuthUserMapper authUserMapper;

    private final UserServiceClient userServiceClient;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    public AuthUserService(AuthUserRepository authUserRepository, AuthUserMapper authUserMapper, UserServiceClient userServiceClient, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.authUserRepository = authUserRepository;
        this.authUserMapper = authUserMapper;
        this.userServiceClient = userServiceClient;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public JwtResponse register(RegisterRequestDTO request) {
        LOG.debug("Request to register AuthUser : {}", request);
        AuthUser authUser = authUserMapper.toEntity(request);

        if (authUserRepository.existsByEmail(request.getEmail())) throw new AlreadyExistsException("Oops " + request.getEmail() + " already exist!");

        /* Encode Password before saving to db */
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser = authUserRepository.save(authUser);

        // Call user service client to create user profile
        CreateUserRequest userProfile = new CreateUserRequest(
            request.getEmail(),
            request.getName(),
            request.getPhoneNumber()
        );
        LOG.debug("Sending request to create user profile for: {}", userProfile.getEmail());

        CreateUserResponse profile = userServiceClient.createUserProfile(userProfile);

        List<String> roles = profile.getRoles().stream().map((roleDTO) -> roleDTO.getRoleName()).toList();

        /* Generate Jwt */
        String authToken = tokenService.generateToken(profile.getEmail(), profile.getId(), roles);

        JwtResponse jwtResponse = new JwtResponse(profile.getId(), profile.getEmail(), authToken);

        return jwtResponse;
    }

    public JwtResponse login(LoginRequestDTO request) {
        LOG.debug("Request to login AuthUser : {}", request);
        AuthUser user = authUserRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        boolean isPasswordValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordValid)  throw new BadCredentialsException("Invalid email or password");


        // Call user service client to get user profile
        CreateUserResponse profile = userServiceClient.getUserProfile(user.getEmail());

        List<String> roles = profile.getRoles().stream().map((roleDTO) -> roleDTO.getRoleName()).toList();

        String authToken = tokenService.generateToken(profile.getEmail(), profile.getId(), roles);
        JwtResponse jwtResponse = new JwtResponse(user.getId(), user.getEmail(), authToken);

        return jwtResponse;
    }

    public boolean validateToken(String token) {
        return tokenService.validateToken(token);
    }

    /**
     * Save a authUser.
     *
     * @param authUserDTO the entity to save.
     * @return the persisted entity.
     */
    public AuthUserDTO save(AuthUserDTO authUserDTO) {
        LOG.debug("Request to save AuthUser : {}", authUserDTO);
        AuthUser authUser = authUserMapper.toEntity(authUserDTO);
        authUser = authUserRepository.save(authUser);
        return authUserMapper.toDto(authUser);
    }

    /**
     * Update a authUser.
     *
     * @param authUserDTO the entity to save.
     * @return the persisted entity.
     */
    public AuthUserDTO update(AuthUserDTO authUserDTO) {
        LOG.debug("Request to update AuthUser : {}", authUserDTO);
        AuthUser authUser = authUserMapper.toEntity(authUserDTO);
        authUser = authUserRepository.save(authUser);
        return authUserMapper.toDto(authUser);
    }

    /**
     * Partially update a authUser.
     *
     * @param authUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AuthUserDTO> partialUpdate(AuthUserDTO authUserDTO) {
        LOG.debug("Request to partially update AuthUser : {}", authUserDTO);

        return authUserRepository
            .findById(authUserDTO.getId())
            .map(existingAuthUser -> {
                authUserMapper.partialUpdate(existingAuthUser, authUserDTO);

                return existingAuthUser;
            })
            .map(authUserRepository::save)
            .map(authUserMapper::toDto);
    }

    /**
     * Get all the authUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AuthUserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AuthUsers");
        return authUserRepository.findAll(pageable).map(authUserMapper::toDto);
    }

    /**
     * Get one authUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AuthUserDTO> findOne(Long id) {
        LOG.debug("Request to get AuthUser : {}", id);
        return authUserRepository.findById(id).map(authUserMapper::toDto);
    }

    /**
     * Delete the authUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AuthUser : {}", id);
        authUserRepository.deleteById(id);
    }
}
