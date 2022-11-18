package com.techelevator.tenmo.model;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Transfer {

    private int transferId;

    @NotNull
    private int transferType;
    @NotNull
    private int transferStatus;
    @NotNull
    private int accountFrom;
    @NotNull
    private int accountTo;
    @NotNull
    private BigDecimal amount;

    public Transfer(){}

    public Transfer(int transferId, int transferType, int transferStatus, int accountFrom, int accountTo, @NotNull BigDecimal amount) {
        this.transferId = transferId;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public int getTransferType() {
        return transferType;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // SETTERS
    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
