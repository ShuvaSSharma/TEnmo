package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Transfer {
    private Integer transferId;
    @NotNull(message = "The field `transferTypeId` should not be null.")
    private Integer transferTypeId;

    @NotNull(message = "The field `transferStatusId` should not be null.")
    private Integer transferStatusId;

    @NotNull(message = "The field `transferStatusId` should not be nullk.")
    private Integer accountFromId;
    @NotNull(message = "The field `accountToId` should not be null.")
    private Integer accountToId;
    @DecimalMin(value = "1.0", message = "The field `amount` should be greater than 0.")
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer(int transferId, int transferTypeId, int transferStatusId, int accountFromId, int accountToId, BigDecimal amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }
    public Transfer(int transferTypeId, int transferStatusId, int accountFromId, int accountToId, BigDecimal amount) {
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
