package com.bank.transaction.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class TransactionEntity extends PanacheEntity {

    public String transactionId;
    public String eventType;
    public String fromAccount;
    public String toAccount;
    public double amount;
    public String currency;
    public String status;
    public String timestamp;
}

