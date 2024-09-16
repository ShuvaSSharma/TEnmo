package com.techelevator.tenmo.model;

public class TransferStatus {
    private int transferStatusId;
    private String transferStatusDesc;

    public TransferStatus(int transferStatusId, String transferStatusDesc) {
        this.transferStatusId = transferStatusId;
        this.transferStatusDesc = transferStatusDesc;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public String getTransferStatusDesc() {
        return transferStatusDesc;
    }

    public void setTransferStatusDesc(String transferStatusDesc) {
        this.transferStatusDesc = transferStatusDesc;
    }
}

