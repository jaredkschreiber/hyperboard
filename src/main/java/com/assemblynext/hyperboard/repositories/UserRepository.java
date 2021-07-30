package com.assemblynext.hyperboard.repositories;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import com.assemblynext.hyperboard.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, BigInteger> {
    Optional<User> findUserByUsername(String u);

    @Query("select u from User u where u.admin = true or u.mod = true")
    List<User> findStaff();
}
