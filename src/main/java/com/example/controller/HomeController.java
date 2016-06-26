package com.example.controller;

import com.example.annotation.Timed;
import com.example.component.ResourceBundleBean;
import com.example.entity.User;
import com.example.repository.UsersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
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
import java.util.Optional;

import static com.example.utils.LambdaExceptionUtil.rethrowConsumer;

@Slf4j
@Timed
@Controller
public class HomeController {

    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;
    private final ResourceBundleBean msg;
    private final BuildProperties buildProperties;

    @Autowired
    public HomeController(UsersRepository usersRepository, ObjectMapper objectMapper, ResourceBundleBean bundleBean, BuildProperties buildProperties) {
        this.usersRepository = usersRepository;
        this.objectMapper = objectMapper;
        this.msg = bundleBean;
        this.buildProperties = buildProperties;
    }

    @RequestMapping({"/hello"})
    public ModelAndView hello(Principal principal, HttpSession session) {
        return new ModelAndView("hello").addObject("buildProperties", buildProperties);
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login(Principal principal, HttpSession session) {
        return new ModelAndView("login");
    }

    @RequestMapping({"/welcome"})
    public ResponseEntity welcome(Principal principal, HttpSession session) throws JsonProcessingException {
        return getResponseEntity(principal, session);
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

        List<User> userList = usersRepository.findAll();
        User user = usersRepository.findOne(1L);

        Optional.ofNullable(session).ifPresent(rethrowConsumer(httpSession -> {
            httpSession.setAttribute("principal", objectMapper.writeValueAsString(principal));
        }));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("message", msg.get("hello.text"));
        map.put("sessionId", session.getId());
        map.put("principal", principal);
        map.put("user", user);
        map.put("userList", userList);
        map.put("buildProperties", buildProperties);

        return ResponseEntity.ok(map);
    }

}
