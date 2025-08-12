package com.prunny.user_service.repository;

import com.prunny.user_service.domain.Role;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Role entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleName(String roleName);

    Optional<Role> findByRoleName(String roleName);

    @Modifying
    @Query(value = "DELETE FROM rel_jhi_user__roles WHERE roles_id = :roleId", nativeQuery = true)
    void deleteRoleUserRelationships(@Param("roleId") Long roleId);
}
