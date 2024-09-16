package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferRequestDto {
    private int userTo;
    private BigDecimal amount;

    // Getters and setters
    public int getUserTo() {
        return userTo;
    }

    public void setUserTo(int userTo) {
        this.userTo = userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

