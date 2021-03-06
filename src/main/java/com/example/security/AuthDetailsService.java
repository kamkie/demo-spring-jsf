package com.example.security;

import com.example.annotation.TimedMethod;
import com.example.entity.User;
import com.example.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    @Autowired
    public AuthDetailsService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @TimedMethod
    public User loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByLogin(username);
        return user.orElseThrow(() -> new UsernameNotFoundException(String.format("user %s not found", username)));
    }

}
