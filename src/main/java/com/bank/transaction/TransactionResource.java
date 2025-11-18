package com.bank.transaction;

import com.bank.transaction.dto.AccountDTO;
import com.bank.transaction.model.Account;
import com.bank.transaction.model.TransactionEntity;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/internal/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create( @RequestBody AccountDTO dto) {
        Account account = new Account();
        account.accountNumber = dto.accountNumber;
        account.accountTitle = dto.accountTitle;
        account.balance = dto.balance;
        account.currency = dto.currency;
        account.iban = dto.iban;
        account.persist();

        return Response.ok(account).build();
    }

    @GET
    public Response getAll() {
        return Response.ok(Account.listAll()).build();
    }

    @GET
    @Path("/{accNo}")
    public Response getByAccountNumber(@PathParam("accNo") String accNo) {
        Account acc = Account.find("accountNumber", accNo).firstResult();
        if (acc == null)
            return Response.status(Response.Status.NOT_FOUND).entity("Account not found").build();

        return Response.ok(acc).build();
    }

    @GET
    @Path("/{accNo}/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionsByAccountNumber(@PathParam("accNo") String accNo) {

        System.out.println("request for---------------: "+accNo);

        List<TransactionEntity> transactions = TransactionEntity.list(
                "status = ?1 AND (fromAccount = ?2 OR toAccount = ?2)",
                "COMPLETED",
                accNo
        );

//        if (transactions.isEmpty()) {
//            return Response.status(Response.Status.NOT_FOUND)
//                    .entity("No transactions found for account: " + accNo)
//                    .build();
//        }

        return Response.ok(transactions).build();
    }
    @GET
    @Path("/transactions")
    public Response getAllTransactions() {
        return Response.ok(TransactionEntity.listAll()).build();
    }
}
