package com.tomalancarroll.transactionstatistics.model;

public class TransactionStatistics {
    /**
     * Specifies the total sum of transaction value in the last transaction.window.of.interest seconds
     */
    public double sum;

    /**
     * Specifies the average amount of transaction value in the last transaction.window.of.interest seconds
     */
    public double avg;

    /**
     * Specifies the single highest transaction value in the last transaction.window.of.interest seconds
     */
    public double max;

    /**
     * Specifies the single lowest transaction value in the last transaction.window.of.interest seconds
     */
    public double min;

    /**
     * Specifies the total number of transactions happened in the last transaction.window.of.interest seconds
     */
    public long count;

}
