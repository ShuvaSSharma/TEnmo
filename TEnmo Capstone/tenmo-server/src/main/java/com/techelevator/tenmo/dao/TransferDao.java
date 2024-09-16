package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dto.TransferDetailsDto;
import com.techelevator.tenmo.dto.TransferDto;
import com.techelevator.tenmo.dto.TransferPendingDto;
import com.techelevator.tenmo.model.*;

import java.util.List;

public interface TransferDao {


    Integer getTransferStatusIdByDesc(String status);
    Transfer createTransfer(Transfer transfer);
    List<TransferDto> getTransfersByUserId(int userId);

    TransferDetailsDto getTransferDetailsById(int transferId);

    List<TransferPendingDto> getPendingTransfers(int userId);
    Transfer getTransferById(int transferId);

    Transfer updateTransfer(Transfer updatedTransfer);
//    TransferDetailsDto sendRequest(int accountFrom, int accountTo, BigDecimal ammount);
}