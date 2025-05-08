package com.quickcart.quickCart.user;

import com.quickcart.quickCart.auth.dto.UserDtoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE role='MODER'", nativeQuery = true)
    List<User> getModer();

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE name= :name", nativeQuery = true)
    Optional<User> findByName(String name);

    @Query("SELECT new com.quickcart.quickCart.auth.dto.UserDtoInfo(" +
            "u.id, u.username, u.email, u.location, u.role) " +
            "FROM User u WHERE u.email = :email")
    Optional<UserDtoInfo> findInfoByEmail(String email);

    @Query("SELECT new com.quickcart.quickCart.auth.dto.UserDtoInfo(" +
            "u.id, u.username, u.email, u.location, u.role) " +
            "FROM User u WHERE u.id = :id")
    Optional<UserDtoInfo> findInfoById(Long id);

}
