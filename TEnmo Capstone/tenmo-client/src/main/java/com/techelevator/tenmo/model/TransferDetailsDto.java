package com.techelevator.tenmo.model;



import java.math.BigDecimal;

public class TransferDetailsDto {

    private int transferId;
    private String transferType;
    private String transferStatus;
    private String accountFrom;
    private String accountTo;
    private BigDecimal amount;

    public TransferDetailsDto() {
    }

    public TransferDetailsDto(int transferId, String accountFrom, BigDecimal amount) {
        this.transferId = transferId;
        this.accountFrom = accountFrom;
        this.amount = amount;
    }

    public TransferDetailsDto(int transferId, String transferType, String transferStatus, String accountFrom, String accountTo, BigDecimal amount) {
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

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

