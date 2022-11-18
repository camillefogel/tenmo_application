package com.techelevator.tenmo.model;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Account {

    private int accountId;
    @NotNull
    private int userId;
    @NotNull
    private BigDecimal balance;

    public Account(){}

    // constructor for existing accounts
    public Account(int accountId, int userId,BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    // constructor for new accounts
    public Account(int accountId,int userId) {
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
