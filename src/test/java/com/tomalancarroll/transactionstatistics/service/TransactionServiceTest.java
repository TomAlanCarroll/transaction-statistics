package com.tomalancarroll.transactionstatistics.service;

import com.tomalancarroll.transactionstatistics.model.Transaction;
import com.tomalancarroll.transactionstatistics.model.TransactionStatistics;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    TransactionService transactionService;

    final Instant now = Instant.now();

    final double amount1 = 42;
    final long epochTimeUTC1 = now.toEpochMilli() - 40000; // UTC: Now minus 40 seconds
    Transaction transaction1 = new Transaction(amount1, epochTimeUTC1);

    final double amount2 = 48;
    final long epochTimeUTC2 = now.toEpochMilli() - 20000; // UTC: Now minus 20 seconds
    Transaction transaction2 = new Transaction(amount2, epochTimeUTC2);

    final double amount3 = 33.3;
    final long epochTimeUTC3 = now.toEpochMilli() - 1000; // UTC: Now minus 1 second
    Transaction transaction3 = new Transaction(amount3, epochTimeUTC3);

    final double amount4 = 33.3;
    final long epochTimeUTC4 = now.toEpochMilli() - 70000; // UTC: Now minus 70 seconds, won't be in stats
    Transaction transactionOutOfWindow1 = new Transaction(amount4, epochTimeUTC4);

    final double amount5 = 11;
    final long epochTimeUTC5 = 1483228800000L; // UTC: 1 January 2017 00:00:00
    Transaction transactionOutOfWindow2 = new Transaction(amount5, epochTimeUTC5);

    @After
    public void tearDown() throws Exception {
        transactionService.deleteAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull() throws Exception {
        transactionService.save(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmpty() throws Exception {
        transactionService.save(new Transaction());
    }

    @Test
    public void testSingleTransactionSaveAndStatistics() throws Exception {
        transactionService.save(transaction1);
        transactionService.save(transactionOutOfWindow1);

        TransactionStatistics transactionStatistics = transactionService.getStatisticsForWindowOfInterest();
        assertEquals(42, transactionStatistics.avg, 0);
        assertEquals(42, transactionStatistics.sum, 0);
        assertEquals(1, transactionStatistics.count);
        assertEquals(42, transactionStatistics.min, 0);
        assertEquals(42, transactionStatistics.max, 0);
    }

    @Test
    public void testMultiTransactionSaveAndStatistics() throws Exception {
        transactionService.save(transaction1);
        transactionService.save(transaction2);
        transactionService.save(transaction3);
        transactionService.save(transactionOutOfWindow2);

        TransactionStatistics transactionStatistics = transactionService.getStatisticsForWindowOfInterest();
        assertEquals(41.1, transactionStatistics.avg, 0);
        assertEquals(123.3, transactionStatistics.sum, 0);
        assertEquals(3, transactionStatistics.count);
        assertEquals(33.3, transactionStatistics.min, 0);
        assertEquals(48, transactionStatistics.max, 0);
    }

    @Test
    public void testOutOfWindowTransactions() throws Exception {
        transactionService.save(transactionOutOfWindow1);
        transactionService.save(transactionOutOfWindow2);

        TransactionStatistics transactionStatistics = transactionService.getStatisticsForWindowOfInterest();
        assertEquals(0, transactionStatistics.avg, 0);
        assertEquals(0, transactionStatistics.sum, 0);
        assertEquals(0, transactionStatistics.count);
        assertEquals(0, transactionStatistics.min, 0);
        assertEquals(0, transactionStatistics.max, 0);
    }

}