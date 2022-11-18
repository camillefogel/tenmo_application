package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.security.Principal;

public interface AccountDao {

    Account getBalance(Principal principal);

    void updateBalance(Principal principal, Transfer transfer);

}
