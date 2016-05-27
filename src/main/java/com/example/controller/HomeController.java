package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.annotation.Timed;
import com.example.entity.User;
import com.example.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Timed
@Controller
public class HomeController {

	private final UsersRepository usersRepository;

	@Autowired
	public HomeController(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@RequestMapping({ "/welcome" })
	public ResponseEntity welcome(Principal principal) {
		return getResponseEntity(principal);
	}

	@RequestMapping({ "/", "/home" })
	public ResponseEntity home(Principal principal) {
		return getResponseEntity(principal);
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping({ "/admin" })
	public ResponseEntity admin(Principal principal) {
		return getResponseEntity(principal);
	}

	private ResponseEntity getResponseEntity(Principal principal) {
		log.info("home controller called principal: {}", principal);

		List<User> userList = usersRepository.findAll();
		User user = usersRepository.findOne(1L);
		return ResponseEntity.ok(new Object[] { principal, userList, user });
	}

}
