package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dto.TransferDetailsDto;
import com.techelevator.tenmo.dto.TransferDto;
import com.techelevator.tenmo.dto.TransferPendingDto;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer getTransferStatusIdByDesc(String status) {
        String sql = "SELECT transfer_status_id " +
                "FROM transfer_status " +
                "WHERE LOWER(TRIM(transfer_status_desc)) = LOWER(TRIM(?))";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, status);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataAccessException e) {

            throw new DaoException("Database access error occurred", e);
        }
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer newTransfer = null;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id,account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) " +
                "RETURNING transfer_id";

        try {
            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());
            newTransfer = getTransferById(newTransferId);

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return newTransfer;

    }



    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer " +
                "WHERE transfer_id = ?";

        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
            if (rowSet.next()) {
                transfer = mapRowToTransfer(rowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public Transfer updateTransfer(Transfer updatedTransfer) {

        String sql = "UPDATE transfer SET transfer_type_id = ?, transfer_status_id = ?, account_from = ?, account_to = ?, amount = ? " +
                "WHERE transfer_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, updatedTransfer.getTransferTypeId(), updatedTransfer.getTransferStatusId(),updatedTransfer.getAccountFromId(), updatedTransfer.getAccountToId(), updatedTransfer.getAmount(), updatedTransfer.getTransferId() );
            if (rowsAffected > 0) {

                return getTransferById(updatedTransfer.getTransferId());
            } else {
                throw new DaoException("transfer update failed, no rows affected");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<TransferDto> getTransfersByUserId(int userId) {
        List<TransferDto> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, afu.username as account_from, atu.username as account_to, amount " +
                "FROM transfer " +
                "JOIN account af ON transfer.account_from = af.account_id " +
                "JOIN tenmo_user afu ON af.user_id = afu.user_id " +
                "JOIN account at ON transfer.account_to = at.account_id " +
                "JOIN tenmo_user atu ON at.user_id = atu.user_id " +
                "WHERE af.user_id = ? OR atu.user_id = ? " +
                "ORDER BY transfer_id ASC;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (results.next()) {
                TransferDto transfer = mapRowToTransferDto(results);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }
    @Override
    public TransferDetailsDto getTransferDetailsById(int transferId) {
        TransferDetailsDto transferDetail = null;
        String sql = "SELECT transfer_id, transfer_type_desc, transfer_status_desc, afu.username as account_from, atu.username as account_to, amount " +
                "FROM transfer " +
                "JOIN transfer_status USING(transfer_status_id) " +
                "JOIN transfer_type USING(transfer_type_id) " +
                "JOIN account af ON transfer.account_from = af.account_id " +
                "JOIN tenmo_user afu ON af.user_id = afu.user_id " +
                "JOIN account at ON transfer.account_to = at.account_id " +
                "JOIN tenmo_user atu ON at.user_id = atu.user_id " +
                "WHERE transfer_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
            if (results.next()) {
                transferDetail = mapRowToTransferDetails(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transferDetail;
    }
    @Override
    public List<TransferPendingDto> getPendingTransfers(int accountId) {
        List<TransferPendingDto> pendingRequests = new ArrayList<>();
        String sql = "SELECT transfer_id, afu.username as account_from, amount " +
                "FROM transfer AS t " +
                "JOIN account af ON t.account_from = af.account_id " +
                "JOIN tenmo_user afu ON af.user_id = afu.user_id " +
                "JOIN transfer_status AS ts " +
                "    ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE ts.transfer_status_desc = 'Pending' " +
                "AND account_to = ? " +
                "ORDER BY transfer_id ASC;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
            while (results.next()) {
                TransferPendingDto pendingRequest = mapRowToTransferPendingDto(results);
                pendingRequests.add(pendingRequest);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return pendingRequests;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs){
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFromId(rs.getInt("account_from"));
        transfer.setAccountFromId(rs.getInt("account_from"));
        transfer.setAccountToId(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }

    private TransferDto mapRowToTransferDto(SqlRowSet rs) {
        TransferDto transfer = new TransferDto();

        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setAccountFrom(rs.getString("account_from"));
        transfer.setAccountTo(rs.getString("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));

        return transfer;
    }

    private TransferPendingDto mapRowToTransferPendingDto(SqlRowSet rs) {
        TransferPendingDto transfer = new TransferPendingDto();

        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setAccountFrom(rs.getString("account_from"));
        transfer.setAmount(rs.getBigDecimal("amount"));

        return transfer;
    }
    private TransferDetailsDto mapRowToTransferDetails(SqlRowSet rs) {
        TransferDetailsDto transferDetail = new TransferDetailsDto();
        transferDetail.setTransferId(rs.getInt("transfer_id"));
        transferDetail.setTransferType(rs.getString("transfer_type_desc"));
        transferDetail.setTransferStatus(rs.getString("transfer_status_desc"));
        transferDetail.setAccountFrom(rs.getString("account_from"));
        transferDetail.setAccountTo(rs.getString("account_to"));
        transferDetail.setAmount(rs.getBigDecimal("amount"));
        return transferDetail;
    }
}
