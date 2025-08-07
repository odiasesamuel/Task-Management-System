package com.prunny.user_service.repository;

import com.prunny.user_service.domain.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Team entity.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("select team from Team team where team.admin.email = ?#{authentication.name}")
    List<Team> findByAdminIsCurrentUser();

    default Optional<Team> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Team> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Team> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select team from Team team left join fetch team.admin", countQuery = "select count(team) from Team team")
    Page<Team> findAllWithToOneRelationships(Pageable pageable);

    @Query("select team from Team team left join fetch team.admin")
    List<Team> findAllWithToOneRelationships();

    @Query("select team from Team team left join fetch team.admin where team.id =:id")
    Optional<Team> findOneWithToOneRelationships(@Param("id") Long id);
}
