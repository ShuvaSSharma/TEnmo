package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Transfer {

    private Long transferId;
    private int userFromId;
    private int userToId;
    private BigDecimal amount;
    private String status;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public int getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(int userFromId) {
        this.userFromId = userFromId;
    }

    public int getUserToId() {
        return userToId;
    }

    public void setUserToId(int userToId) {
        this.userToId = userToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return userFromId == transfer.userFromId &&
                userToId == transfer.userToId &&
                Objects.equals(transferId, transfer.transferId) &&
                Objects.equals(amount, transfer.amount) &&
                Objects.equals(status, transfer.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId, userFromId, userToId, amount, status);
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", userFromId=" + userFromId +
                ", userToId=" + userToId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}