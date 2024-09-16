package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user/")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserDao userDao;

    @Autowired
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    public List<User> getUsersNiCurrentUser(Principal principal){
        log.info(principal.getName() + " Accessing a list of other tenmo users");
        return userDao.getUsersWithoutCurrentUser(principal.getName());
    }
}
