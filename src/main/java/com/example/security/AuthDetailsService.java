package com.example.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.entity.User;
import com.example.repository.UsersRepository;

@Component
public class AuthDetailsService implements UserDetailsService {

	private final UsersRepository userRepository;

	@Autowired
	public AuthDetailsService(UsersRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByLogin(username);
		return user.orElseThrow(() -> new UsernameNotFoundException(String.format("user %s not found", username)));
	}

}
