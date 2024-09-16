package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dto.TransferDto;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {

    private static final Transfer TRANSFER_1 = new Transfer(3001, 2, 2, 2001, 2002, new BigDecimal("100.00"));
    private static final Transfer TRANSFER_2 = new Transfer(3002, 2, 1, 2002, 2001, new BigDecimal("200.00"));
    private static final Transfer TRANSFER_3 = new Transfer(3003, 1, 2, 2001, 2002, new BigDecimal("50.00"));

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }

    @Test
    public void getTransferById_given_invalid_transfer_id_returns_null() {
        Transfer actualTransfer = sut.getTransferById(-1);

        Assert.assertNull(actualTransfer);
    }

    @Test(expected = DaoException.class)
    public void updateTransfer_with_invalid_data_throws_exception() {
        Transfer invalidTransfer = new Transfer(TRANSFER_1.getTransferId(), 1, 1, 2001, 2002, null);

        sut.updateTransfer(invalidTransfer);
    }

    @Test
    public void getTransferStatusIdByDesc_returns_status_id() {
        Integer statusId = sut.getTransferStatusIdByDesc("Approved");

        Assert.assertNotNull(statusId);
        Assert.assertEquals((Integer) 2, statusId);
    }

    @Test
    public void getTransferStatusIdByDesc_given_invalid_status_returns_null() {
        Integer statusId = sut.getTransferStatusIdByDesc("InvalidStatus");

        Assert.assertNull(statusId);
    }
}