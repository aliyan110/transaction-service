package com.bank.transaction.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account extends PanacheEntity {
    public String accountNumber;
    public String iban;
    public String accountTitle;
    public double balance;
    public String currency;
}
