package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    Account getAccountByUsername(String username);

    Account getAccountByUserId(int userId);

    Account updateAccount(Account updatedAccount);

    Account getAccountById(int accountId);
}

