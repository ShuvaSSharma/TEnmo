package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/account/")
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private AccountDao accountDao;
    private UserDao userDao;

    @Autowired
    public AccountController(AccountDao accountDao){
        this.accountDao = accountDao;
    }


    @GetMapping("balance")
    public BigDecimal getBalance(Principal principal){
        BigDecimal balance = null;
        log.info("{} Accessing their account balance", principal.getName());
        try{
            balance = accountDao.getAccountByUsername(principal.getName()).getBalance();
            return balance;
        } catch (DaoException e){
            log.error("Unable to retrieve balance for {} ", principal.getName());
        }
        return balance;
    }


}
