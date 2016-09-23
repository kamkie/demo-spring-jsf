package com.example.repository;

import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
}
