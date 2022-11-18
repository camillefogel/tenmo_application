package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ADD A NEW TRANSFER
    @Override
    public Transfer create(Principal principal, Transfer transfer) {

        // Retrieve accountFrom account numbers with known userIDs
        String sqlFromAccount = "SELECT account_id\n" +
                "FROM account\n" +
                "WHERE user_id = ?;";
        Integer fromAccount = jdbcTemplate.queryForObject(sqlFromAccount, Integer.class, transfer.getAccountFrom());
        Integer toAccount = jdbcTemplate.queryForObject(sqlFromAccount, Integer.class, transfer.getAccountTo());

        // Adds new transfer into database
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";

        Integer newId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferType(), transfer.getTransferStatus(), fromAccount, toAccount, transfer.getAmount());

        return getTransfer(principal, newId);
    }

    // RETRIEVE LIST OF TRANSFERS FOR USER
    @Override
    public List<Transfer> list(Principal principal) {

        List<Transfer> list = new ArrayList<>();

        //Sql Statement to retrieve all transfers for current user
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount\n" +
                "FROM transfer\n" +
                "JOIN account\n" +
                "\tON account.account_id IN (transfer.account_from, transfer.account_to)\n" +
                "JOIN tenmo_user\n" +
                "\tON tenmo_user.user_id = account.user_id\n" +
                "WHERE tenmo_user.username = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, principal.getName());

        // adds all retrieved transfers to list
        while(result.next()) {
            Transfer addedTransfer = transferMapper(result);
            list.add(addedTransfer);
        }
        return list;
    }

    // GET TRANSFER BY ID
    @Override
    public Transfer getTransfer(Principal principal, int transferId) {
        Transfer transfer = null;

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount\n" +
                "FROM transfer\n" +
                "WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);

        if (results.next()) {
            transfer = transferMapper(results);
        }
        return transfer;
    }


    //TODO update transfer status
    @Override
    public boolean update(Principal principal, Transfer transfer) {

        boolean updated = false;

        String sql = "UPDATE transfer\n" +
                "SET transfer_status_id = ?\n" +
                "WHERE transfer_id = ?\n" +
                "RETURNING transfer_status_id;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transfer.getTransferStatus(), transfer.getTransferId());

        if (results.next()) {
            int newStatus = results.getInt("transfer_status_id");

            if (newStatus == transfer.getTransferStatus()) {
                updated = true;
            }
        }

        return updated;
    }

    private Transfer transferMapper(SqlRowSet result){

            Transfer transfer = new Transfer(
                result.getInt("transfer_id"),
                result.getInt("transfer_type_id"),
                result.getInt("transfer_status_id"),
                result.getInt("account_from"),
                result.getInt("account_to"),
                result.getBigDecimal("amount")
        );

        return transfer;
    }
}
