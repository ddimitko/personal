package com.ddimitko.personal.repositories;

import com.ddimitko.personal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUserTag(String userTag);
    boolean existsByUserTag(String userTag);
    boolean existsByEmail(String email);

}
