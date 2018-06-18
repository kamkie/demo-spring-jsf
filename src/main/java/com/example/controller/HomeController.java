package com.example.controller;

import com.example.annotation.TimedMethod;
import com.example.component.ResourceBundleBean;
import com.example.entity.User;
import com.example.repository.UsersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Timed
@TimedMethod
@Controller
public class HomeController {

    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;
    private final ResourceBundleBean msg;
    private final BuildProperties buildProperties;
    private final GitProperties gitProperties;

    @Autowired
    public HomeController(UsersRepository usersRepository, ObjectMapper objectMapper, ResourceBundleBean bundleBean, BuildProperties buildProperties, GitProperties gitProperties) {
        this.usersRepository = usersRepository;
        this.objectMapper = objectMapper;
        this.msg = bundleBean;
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
    }

    @RequestMapping({"/hello"})
    public ModelAndView hello() {
        return new ModelAndView("hello").addObject("buildProperties", buildProperties);
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @RequestMapping({"/", "/home"})
    public ResponseEntity home(Principal principal, HttpSession session) throws JsonProcessingException {
        return getResponseEntity(principal, session);
    }

    @Secured({"ROLE_ADMIN"})
    @RequestMapping({"/admin"})
    public ResponseEntity admin(Principal principal, HttpSession session) throws JsonProcessingException {
        return getResponseEntity(principal, session);
    }

    private ResponseEntity getResponseEntity(Principal principal, HttpSession session) throws JsonProcessingException {
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
