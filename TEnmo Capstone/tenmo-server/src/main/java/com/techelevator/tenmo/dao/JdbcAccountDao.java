package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class JdbcAccountDao implements AccountDao{


    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Account getAccountByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account " +
                "JOIN tenmo_user tu USING(user_id) " +
                "WHERE tu.username = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            if(results.next()){
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public Account getAccountByUserId(int userId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account " +
                "WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if(results.next()){
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public Account updateAccount(Account updatedAccount) {
        Account account = updatedAccount;
        String sql = "UPDATE account SET user_id = ?, balance = ? " +
                "WHERE account_id = ?;";
        try {
            int rowsAffected = jdbcTemplate.update(sql, account.getUserId(), account.getBalance(), account.getAccountId());
            if (rowsAffected > 0) {
                // Fetch the updated account to return it (if needed)
                return getAccountById(updatedAccount.getAccountId());
            } else {
                // Handle the case where the account was not found or updated
                throw new DaoException("Account update failed, no rows affected");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    public Account getAccountById(int accountId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
            if(results.next()){
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }
    private Account mapRowToAccount(SqlRowSet results){
        Account account = new Account();
        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getBigDecimal("balance"));
        return account;
    }

}

