package com.quickcart.quickCart.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from users where email = :email", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "select * from users where name= :name", nativeQuery = true)
    Optional<User> findByName(String name);
}
