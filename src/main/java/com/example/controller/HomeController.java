package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.entity.User;
import com.example.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

	@Autowired
	private UsersRepository usersRepository;

	@RequestMapping({ "/", "/home" })
	public ResponseEntity home(Principal principal) {
		log.info("home controller called principal: {}", principal);

		List<User> userList = usersRepository.findAll();
		User user = usersRepository.findOne(1L);
		return ResponseEntity.ok(new Object[] { principal, userList, user });
	}

}
