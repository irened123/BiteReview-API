package com.irened.bitereviewapi.repository;

import com.irened.bitereviewapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by display name (used for various scenarios)
    Optional<User> findByDisplayName(String displayName);
    
    // Check if a user with a specific display name already exists
    boolean existsByDisplayName(String displayName);
}
