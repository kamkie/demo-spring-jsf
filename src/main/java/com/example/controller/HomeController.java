package com.example.controller;

import com.example.annotation.TimedMethod;
import com.example.component.ResourceBundleBean;
import com.example.entity.User;
import com.example.repository.UsersRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import tools.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Timed
@TimedMethod
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;
    private final ResourceBundleBean msg;
    private final BuildProperties buildProperties;
    private final GitProperties gitProperties;

    @GetMapping({"/hello"})
    public ModelAndView hello(HttpServletRequest request) {
        return new ModelAndView("hello")
                .addObject("buildProperties", buildProperties)
                .addObject("request", request)
                .addObject("kamkie", usersRepository.findByLogin("kamkie").orElse(null));
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping({"/", "/home"})
    public ResponseEntity<?> home(Principal principal, HttpSession session) {
        return getResponseEntity(principal, session);
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping({"/admin"})
    public ResponseEntity<?> admin(Principal principal, HttpSession session) {
        return getResponseEntity(principal, session);
    }

    private ResponseEntity<?> getResponseEntity(Principal principal, HttpSession session) {
        log.info("home controller called principal: {}", principal);

        var userList = usersRepository.findAll();
        User user = usersRepository.findById(1L).orElseThrow(IllegalArgumentException::new);

        session.setAttribute("principal", objectMapper.writeValueAsString(principal));
        return ResponseEntity.ok(buildResponse(principal, session, userList, user));
    }

    private Map<String, Object> buildResponse(Principal principal, HttpSession session, List<User> userList, User user) {
        var map = new LinkedHashMap<String, Object>();
        map.put("sessionId", session.getId());
        map.put("message", msg.get("hello.text"));
        map.put("principal", principal);
        map.put("user", user);
        map.put("userList", userList);
        map.put("buildProperties", buildProperties);
        map.put("gitProperties", gitProperties);
        return map;
    }

}
