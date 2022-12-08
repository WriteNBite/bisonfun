package com.bisonfun.repository;

import com.bisonfun.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    boolean existsUserByUsername(String username);
    boolean existsUserByUsernameOrEmail(String username, String email);
}
