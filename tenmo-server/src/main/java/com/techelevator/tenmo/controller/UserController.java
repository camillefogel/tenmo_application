package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@Component //indicates that this will have a dependency injection
@RestController
public class UserController {

    private JdbcUserDao userDao;

    public UserController(JdbcUserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getUsers(){

        List<User> userList = userDao.findAll();
        return userList;
    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET) //do we want this path to be accessible? security?
    public User getUserById(@PathVariable int id) {
        return userDao.getUserById(id);
    }

    @RequestMapping(path = "/users/accounts/{id}", method = RequestMethod.GET) //do we want this path to be accessible? security?
    public User getUserByAccount(@PathVariable int id){
        return userDao.getUserByAccount(id);
    }
}
