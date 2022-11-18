package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080/transfers";
    private RestTemplate restTemplate = new RestTemplate();

    // ADD A NEW TRANSFER
    public Transfer create(String principal, Transfer transfer) {

        Transfer newTransfer = null;

        try {
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(principal); //use principal instead of current user
            header.setContentType(MediaType.APPLICATION_JSON); //probably need this in other methods too?
            HttpEntity<Transfer> entity = new HttpEntity<>(transfer, header);
            newTransfer = restTemplate.postForObject(API_BASE_URL, entity, Transfer.class); //exchange?
        } catch (RestClientResponseException | ResourceAccessException e){
            System.out.println("Unable to create transfer.");
            BasicLogger.log(e.getMessage());
        } catch (Exception e){
            System.out.println("Something went wrong.");
            BasicLogger.log(e.getMessage());
        }

        return newTransfer;
    }

    public Transfer[] list(String principal) {

        Transfer[] transfers = new Transfer[]{};

        try {
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(principal);
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity<>(header);
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Unable to retrieve all transfers.");
            BasicLogger.log(e.getMessage());

        } catch (Exception e) {
            System.out.println("Something went wrong.");
            BasicLogger.log(e.getMessage());
        }
        return transfers;

    }

    public List<Transfer> listPending(String principal) {

        List<Transfer> pendingTransfers = new ArrayList<>();

        Transfer[] transfers = list(principal);

        for (Transfer item : transfers) {
            if (item.getTransferStatus() == 1) {
                pendingTransfers.add(item);
            }
        }

        return pendingTransfers;
    }

    public Transfer getTransfer(String principal, int transferId){

        Transfer transfer = null;

        try{
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(principal);
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity<>(header);
            ResponseEntity<Transfer> result = restTemplate.exchange(API_BASE_URL + "/" + transferId, HttpMethod.GET, entity, Transfer.class);
            transfer = result.getBody();

        } catch (RestClientResponseException | ResourceAccessException e){
            System.out.println("Unable to return transfer.");
            BasicLogger.log(e.getMessage());

        } catch (Exception e){
            System.out.println("Something went wrong.");
            BasicLogger.log(e.getMessage());
        }

        return transfer;
    }

    //TODO Update Transfer Status after Approved
    public boolean update(String principal, int transferId, Transfer updatedTransfer){

        boolean updated = false;

        try{
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(principal);
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Transfer> entity = new HttpEntity<>(updatedTransfer, header);
            restTemplate.exchange(API_BASE_URL + "/" + transferId, HttpMethod.PUT, entity, Transfer.class);
            updated = true;
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        } catch (Exception e){
            BasicLogger.log(e.getMessage());
        }

        return updated;
    }

}
