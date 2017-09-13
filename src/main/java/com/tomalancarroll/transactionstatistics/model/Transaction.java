package com.tomalancarroll.transactionstatistics.model;

import com.googlecode.cqengine.attribute.Attribute;

import java.time.Instant;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class Transaction {
    /**
     * The amount for this transaction
     */
    private Double amount;

    /**
     * The transaction time instant (UTC)
     */
    private Instant time;

    public Transaction() {}

    public Transaction(Double amount, long timestamp) {
        this.amount = amount;
        this.time = Instant.ofEpochMilli(timestamp);
    }

    public static final Attribute<Transaction, Instant> TIME = attribute("time", Transaction::getTime);
    public static final Attribute<Transaction, Double> AMOUNT = attribute("amount", Transaction::getAmount);

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Instant getTime() {
        return time;
    }

    public void setTimestamp(Long timestamp) {
        this.time = Instant.ofEpochMilli(timestamp);
    }
}
