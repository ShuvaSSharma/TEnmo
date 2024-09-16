package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferDetailsDto;
import com.techelevator.tenmo.dto.TransferDto;
import com.techelevator.tenmo.dto.TransferPendingDto;
import com.techelevator.tenmo.dto.TransferRequestDto;
import com.techelevator.tenmo.exception.BalanceInsufficientException;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfer/")
@PreAuthorize("isAuthenticated()")
public class TransferController {
    private static final Logger log = LoggerFactory.getLogger(TransferController.class);
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;

    @Autowired
    public TransferController(TransferDao transferDao, UserDao userDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @GetMapping
    public List<TransferDto> getTransfers(Principal principal) {
        String username = principal.getName();
        log.info("{} getting list of transfers that are either the receiver or sender to", username);

        User user = userDao.getUserByUsername(username);
        return transferDao.getTransfersByUserId(user.getId());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TransferDetailsDto> getTransferDetailsById(@PathVariable int id) {
        log.info("Getting transfer details for transfer with ID: {}", id);
        TransferDetailsDto transferDetails = transferDao.getTransferDetailsById(id);
        return transferDetails != null ? ResponseEntity.ok(transferDetails) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransferPendingDto>> getPending(Principal principal) {
        String username = principal.getName();
        log.info("Getting pending transfers for user: {}", principal.getName());
        Account account = accountDao.getAccountByUsername(username);
        List<TransferPendingDto> pendingTransfers = transferDao.getPendingTransfers(account.getAccountId());
        return ResponseEntity.ok(pendingTransfers);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendTeBucks(Principal principal, @Valid @RequestBody TransferRequestDto transferRequestDto) {
        String username = principal.getName();
        log.info("{} is attempting to send TeBucks to user ID: {}", username, transferRequestDto.getUserTo());

        Account accountFrom = accountDao.getAccountByUsername(username);
        Account accountTo = accountDao.getAccountByUserId(transferRequestDto.getUserTo());
        BigDecimal amount = transferRequestDto.getAmount();

        ResponseEntity<String> response = processTransfer(accountFrom, accountTo, amount, true); // Immediate transfer
        if (response.getStatusCode() == HttpStatus.OK) {
            createTransferRecord(accountFrom, accountTo, amount, 2, 2); // status id approved type send
            log.info("{} successfully sent ${} to user ID: {}", username, amount, transferRequestDto.getUserTo());
        }

        return response;
    }

    @PostMapping("/request")
    public ResponseEntity<String> postTransferRequest(Principal principal,@Valid @RequestBody TransferRequestDto transferRequestDto) {
        String username = principal.getName();
        log.info("{} is requesting TeBucks from user ID: {}", username, transferRequestDto.getUserTo());

        Account accountFrom = accountDao.getAccountByUsername(username);
        Account accountTo = accountDao.getAccountByUserId(transferRequestDto.getUserTo());

        createTransferRecord(accountFrom, accountTo, transferRequestDto.getAmount(), 1, 1); // status pending type request
        log.info("Transfer request sent to user ID: {}", transferRequestDto.getUserTo());

        return ResponseEntity.status(HttpStatus.CREATED).body("Transfer request sent.");
    }


    @PutMapping("/{transferId}/update_transfer")
    public ResponseEntity<String> updateTransferRequest(@PathVariable int transferId, @Valid @RequestBody TransferStatus status) {
        try {
            Transfer transfer = transferDao.getTransferById(transferId);
            if (transfer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transfer not found.");
            }

            if (transfer.getTransferStatusId() != 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transfer cannot be altered after it has been approved or rejected.");
            }

            int statusId = transferDao.getTransferStatusIdByDesc(status.getTransferStatusDesc());
            if (statusId == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status description.");
            }

            if (statusId == 2) { // Approved
                Account accountFrom = accountDao.getAccountById(transfer.getAccountFromId());
                Account accountTo = accountDao.getAccountById(transfer.getAccountToId());

                ResponseEntity<String> transferResult = processTransfer(accountFrom, accountTo, transfer.getAmount(), false);

                // Check and log the transfer result
                if (transferResult == null) {
                    log.error("Process transfer returned null for transfer ID {}", transferId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred during transfer processing.");
                }

                if (transferResult.getStatusCode() == HttpStatus.OK) {
                    transfer.setTransferStatusId(statusId);
                    transferDao.updateTransfer(transfer);
                    return ResponseEntity.ok("Transfer successfully approved and completed.");
                } else {
                    return transferResult; // Return error response if the transfer failed
                }
            } else if (statusId == 3) { // Rejected
                transfer.setTransferStatusId(statusId);
                transferDao.updateTransfer(transfer);
                return ResponseEntity.ok("Transfer was successfully rejected.");
            } else {
                log.error("Unsuported status update");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported status update.");
            }
        }  catch (Exception e) {
            log.error("Unexpected error for transfer ID {}: {}", transferId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    private void createTransferRecord(Account accountFrom, Account accountTo, BigDecimal amount, int statusId, int typeId) {
        Transfer transfer = new Transfer(typeId, statusId, accountFrom.getAccountId(), accountTo.getAccountId(), amount);
        transferDao.createTransfer(transfer);
    }

    private ResponseEntity<String> processTransfer(Account accountFrom, Account accountTo, BigDecimal amount, boolean isImmediate) {
        try {
            // Validate input parameters
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount must be greater than zero.");
            }

            // Perform the transfer
            if (isImmediate) {
                accountFrom.withdraw(amount);
                accountTo.deposit(amount);
            } else {
                accountTo.withdraw(amount);
                accountFrom.deposit(amount);
            }


            // Update accounts in the database
            accountDao.updateAccount(accountFrom);
            accountDao.updateAccount(accountTo);

            // Return success message
            return ResponseEntity.ok(isImmediate ? "Transfer successful." : "Transfer request approved and processed.");
        } catch (BalanceInsufficientException e) {
            log.error("Insufficient balance during transfer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient funds for the transfer.");
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument during transfer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid argument: " + e.getMessage());
        } catch (DaoException e) {
            log.error("DAO exception during transfer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during transfer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

}
