package com.tomalancarroll.transactionstatistics.service;

import com.tomalancarroll.transactionstatistics.model.Transaction;
import com.tomalancarroll.transactionstatistics.model.TransactionStatistics;
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
    Transaction transactionOutOfWindow = new Transaction(amount4, epochTimeUTC4);

    @Test
    public void testSaveAndStatistics() throws Exception {
        transactionService.save(transaction1);
        transactionService.save(transaction2);
        transactionService.save(transaction3);
        transactionService.save(transactionOutOfWindow);

        TransactionStatistics transactionStatistics = transactionService.getStatisticsForWindowOfInterest();
        assertEquals(41.1, transactionStatistics.avg, 0);
        assertEquals(123.3, transactionStatistics.sum, 0);
        assertEquals(3, transactionStatistics.count);
        assertEquals(33.3, transactionStatistics.min, 0);
        assertEquals(48, transactionStatistics.max, 0);
    }


}