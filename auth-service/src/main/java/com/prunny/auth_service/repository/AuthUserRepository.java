package com.prunny.auth_service.repository;

import com.prunny.auth_service.domain.AuthUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AuthUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    boolean existsByEmail(String email);
}
