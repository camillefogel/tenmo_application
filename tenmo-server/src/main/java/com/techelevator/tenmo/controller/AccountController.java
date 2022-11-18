package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@Component //indicates that this will have a dependency injection
@RestController
public class AccountController {

    private JdbcAccountDao accountDao;

    public AccountController(JdbcAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/balances", method = RequestMethod.GET)
    public Account getBalance(Principal principal){
        return accountDao.getBalance(principal);
    }

    @RequestMapping(path = "/balances", method = RequestMethod.PUT)
    public void updateBalance(Principal principal, @RequestBody @Valid Transfer transfer) {
        accountDao.updateBalance(principal, transfer);
    }

}