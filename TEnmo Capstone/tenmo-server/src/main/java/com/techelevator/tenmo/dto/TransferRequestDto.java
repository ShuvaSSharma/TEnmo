package com.techelevator.tenmo.dto;


import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequestDto {
    @NotNull(message = "The field `userTo` should not be null.")
    private Integer userTo;

    @DecimalMin(value = "1.0", message = "The field `amount` should be greater than 0.")
    private BigDecimal amount;

    // Getters and setters
    public int getUserTo() {
        return userTo;
    }

    public void setUserToTo(int userTo) {
        this.userTo = userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

