package com.techelevator.tenmo.model;


import java.math.BigDecimal;

public class TransferPendingDto {
    private int transferId;
    private String accountFrom;
    private BigDecimal amount;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    // Getters and setters
    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTranferId(int transferId) {
    }
    @Override
    public String toString() {
        return String.format("TransferPendingDto [transferId=%d, accountFrom=%s, amount=%s]",
                transferId, accountFrom, amount);
    }
}
