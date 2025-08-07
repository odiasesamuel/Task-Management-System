package com.prunny.user_service.repository;

import com.prunny.user_service.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserRepositoryWithBagRelationships {
    Optional<User> fetchBagRelationships(Optional<User> user);

    List<User> fetchBagRelationships(List<User> users);

    Page<User> fetchBagRelationships(Page<User> users);
}
