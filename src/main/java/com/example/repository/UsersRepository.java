package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.User;

public interface UsersRepository extends JpaRepository<User, Long> {

	Optional<User> findByLogin(String login);
}
