package com.techelevator.tenmo.dao;
//SHAREDCOPY
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;

@Component //indicates that this class will have a dependency injection
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;
    private JdbcTransferDao transferDao;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate, JdbcTransferDao transferDao) { //dependency injection- what we are injecting
        this.jdbcTemplate = jdbcTemplate;
        this.transferDao = transferDao;
    }

    // GET CURRENT USER'S ACCOUNT
    @Override
    public Account getBalance(Principal principal) {
        //Retrieve account
        String sql = "SELECT account.account_id, account.user_id, account.balance\n" +
                "FROM account\n" +
                "JOIN tenmo_user\n" +
                "\tON tenmo_user.user_id = account.user_id\n" +
                "WHERE tenmo_user.username = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, principal.getName());

        Account retrievedAccount = null;
        while (result.next()){
            retrievedAccount = accountMapper(result);
        }

        return retrievedAccount;
    }

    @Override
    public void updateBalance(Principal principal, Transfer transfer) {

        //Get transfer sender original balance and transfer recipient original balance
        String sql = "SELECT balance\n" +
                "FROM account\n" +
                "WHERE account_id = ?;";
        BigDecimal senderOriginalBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, transfer.getAccountFrom());
        BigDecimal recipientOriginalBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, transfer.getAccountTo());

        BigDecimal senderNewBalance = senderOriginalBalance.subtract(transfer.getAmount());
        BigDecimal recipientNewBalance = recipientOriginalBalance.add(transfer.getAmount());

        // Updating the account_FROM, subtracting
        sql = "UPDATE account\n" +
                "SET balance = ?\n" +
                "WHERE account_id = ?";
        jdbcTemplate.update(sql, senderNewBalance, transfer.getAccountFrom());

        // Updating the account_TO, adding
        jdbcTemplate.update(sql, recipientNewBalance, transfer.getAccountTo());
    }

    private Account accountMapper(SqlRowSet result){

        Account account = new Account(
                result.getInt("account_id"),
                result.getInt("user_id"),
                result.getBigDecimal("balance")
        );

        return account;
    }


}
