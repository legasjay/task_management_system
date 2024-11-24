package com.olegandreevich.tms.repositories;

import com.olegandreevich.tms.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?1 WHERE u.username = ?2")
    void updatePassword(String password, String username);
}
