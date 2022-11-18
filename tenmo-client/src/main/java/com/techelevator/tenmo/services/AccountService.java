package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

public class AccountService {

    public static final String API_BASE_URL = "http://localhost:8080/balances/";
    private RestTemplate restTemplate = new RestTemplate();

    // GET ACCOUNT FOR CURRENT USER
    public Account getAccount(String principal){

        Account account = null;

        try{
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(principal);
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity<>(header);
            ResponseEntity<Account> result = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, entity, Account.class);
            account = result.getBody();

        } catch (RestClientResponseException | ResourceAccessException e){
            System.out.println("Unable to update balance.");
            BasicLogger.log(e.getMessage());

        } catch (Exception e){
            System.out.println("Something went wrong.");
            BasicLogger.log(e.getMessage());
        }

        return account;

    }

    // UPDATE ACCOUNT BALANCE FOR BOTH USERS
    public boolean updateAccountBalance(String principal, Transfer transfer){

        boolean updated = false;

        try{
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(principal);
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Transfer> entity = new HttpEntity<>(transfer,header);
            restTemplate.exchange(API_BASE_URL, HttpMethod.PUT, entity, Transfer.class);
            updated = true;

        } catch (RestClientResponseException | ResourceAccessException e){
            System.out.println("Unable to update balance.");
            BasicLogger.log(e.getMessage());

        } catch (Exception e){
            System.out.println("Something went wrong.");
            BasicLogger.log(e.getMessage());
        }

        return updated;

    }


}
