package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public static final String API_BASE_URL = "http://localhost:8080/users";
    private RestTemplate restTemplate = new RestTemplate();

    public User[] getUsers(String principal){

        User[] userList = new User[]{};

        try {
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(principal);
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity<>(header);
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, entity, User[].class);
            userList = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e){
            System.out.println("Unable to retrieve all users.");
            BasicLogger.log(e.getMessage());

        } catch (Exception e){
            System.out.println("Something went wrong.");
            BasicLogger.log(e.getMessage());
        }
        return userList;
    }

    public User getUserById(String principal, int id) {/////////////////////////////////////////////////////////////////////////////////////////////////////

        User retrievedUser = new User();

        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            header.setBearerAuth(principal);//////////////////////////////////////////////////////////////////////////////////////////////////////////////
            HttpEntity entity = new HttpEntity<>(header);
            retrievedUser = restTemplate.exchange(API_BASE_URL + "/" + id, HttpMethod.GET, entity, User.class).getBody();

        } catch (RestClientResponseException | ResourceAccessException e){
            return null;//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }

        return retrievedUser;//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public User getUserByAccount(String principal, int id) { /////////////////////////////////////////////////////////////////////////////////////pass in principal

        User user = new User();

        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            header.setBearerAuth(principal); //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            HttpEntity entity = new HttpEntity<>(header);
            user = restTemplate.exchange(API_BASE_URL + "/accounts/" + id, HttpMethod.GET, entity, User.class).getBody();/////////////////////////////
            return user;
        } catch (RestClientResponseException | ResourceAccessException e){
            return null;
        }

    }

}
