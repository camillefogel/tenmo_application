package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Account {

    private int accountId;
    @NotNull
    private int userId;
    @NotNull
    private BigDecimal balance;

    public Account(){}

    // constructor for existing accounts
    public Account(int accountId, @NotNull int userId, @NotNull BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    // constructor for new accounts
    public Account(int accountId, @NotNull int userId) {
        this.accountId = accountId;
        this.userId = userId;
        balance = new BigDecimal(1000);
    }

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
