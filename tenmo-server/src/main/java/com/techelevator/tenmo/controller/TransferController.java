package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.apache.coyote.Request;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@Component // indicates that this class will have a dependency injection
@RestController
public class TransferController {

    private JdbcTransferDao transferDao;
    private Principal principal;

    public TransferController(JdbcTransferDao transferDao) { //what is being injected
        this.transferDao = transferDao;
    }

    // ADD A NEW TRANSFER
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer add(Principal principal, @Valid @RequestBody Transfer transfer) {

        return transferDao.create(principal, transfer);
    }

    // GET TRANSFER BY ID
    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public Transfer getTransfer(Principal principal, @PathVariable int id) {

        Transfer transfer = transferDao.getTransfer(principal, id);

        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found");
        } else {
            return transfer;
        }
    }

    // LIST TRANSFERS
    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> getList(Principal principal) {
        return transferDao.list(principal);
    }

    // UPDATE TRANSFER STATUS
    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
    public boolean updateStatus(@PathVariable int id, Principal principal, Transfer transfer) {
        return transferDao.update(principal, transfer);
    }

}
