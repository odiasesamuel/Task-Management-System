package com.prunny.user_service.repository;

import com.prunny.user_service.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the User entity.
 *
 * When extending this class, extend UserRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface UserRepository extends UserRepositoryWithBagRelationships, JpaRepository<User, Long> {
    default Optional<User> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default Optional<User> findOneByEmailWithEagerRelationships(String email) {
        return this.fetchBagRelationships(this.findByEmail(email));
    }

    default List<User> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<User> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
