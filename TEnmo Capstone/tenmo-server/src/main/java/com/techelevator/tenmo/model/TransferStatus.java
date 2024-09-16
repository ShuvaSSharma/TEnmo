package com.techelevator.tenmo.model;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;



public class TransferStatus {
    @NotNull(message = "The field `transferStatusId` should not be null.")
    private Integer transferStatusId;
    @NotBlank(message = "The field `transferStatusDesc` should not be null.")
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

