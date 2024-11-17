package com.mmieczkowski.serverguard.user.repository;

import com.mmieczkowski.serverguard.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM ResetPasswordLink rpl JOIN User u on u.id = rpl.user.id WHERE rpl.token = :token")
    Optional<User> findFirstByResetPasswordLinkToken(String token);
}
