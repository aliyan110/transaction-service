package com.bank.transaction.service;

import com.bank.transaction.dto.TransactionEvent;
import com.bank.transaction.kafkaClient.TransactionEventProducer;
import com.bank.transaction.kafkaClient.TransactionInitiatedConsumer;
import com.bank.transaction.model.Account;
import com.bank.transaction.model.TransactionEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;

@ApplicationScoped
public class TransactionService {

    @Inject
    TransactionInitiatedConsumer consumer;

    @Inject
    TransactionEventProducer producer;

    @Transactional
    public void process(TransactionEvent event) {

        Account receiver = Account.find("accountNumber", event.toAccount).firstResult();

        if (receiver == null) {
            event.eventType = "TransactionFailed";
            event.status = "FAILED";
            event.timestamp = Instant.now().toString();
            send(event);
            return;
        }

        // Deposit
        receiver.balance += event.amount;
        receiver.persist();

        // Send success event
        event.eventType = "TransactionReceived";
        event.status = "COMPLETED";
        event.timestamp = Instant.now().toString();
        // transaction Save
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.transactionId = event.transactionId;
        transactionEntity.amount = event.amount;
        transactionEntity.timestamp = Instant.now().toString();
        transactionEntity.status = event.status;
        transactionEntity.eventType = event.eventType;
        transactionEntity.fromAccount = event.fromAccount;
        transactionEntity.toAccount = event.toAccount;
        transactionEntity.currency = event.currency;
        transactionEntity.persist();

        System.out.println("TransactionInitiatedConsumer got record and updated");

        send(event);
    }

    private void send(TransactionEvent event) {
        try {
            producer.publish(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
