package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.security.Principal;
import java.util.List;

public interface TransferDao {

    List<Transfer> list(Principal principal);

    Transfer create(Principal principal, Transfer transfer);

    boolean update(Principal principal, Transfer transfer);

    Transfer getTransfer(Principal principal, int transferId);

}
