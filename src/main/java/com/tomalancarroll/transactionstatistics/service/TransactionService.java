package com.tomalancarroll.transactionstatistics.service;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.ObjectLockingIndexedCollection;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import com.tomalancarroll.transactionstatistics.model.Transaction;
import com.tomalancarroll.transactionstatistics.model.TransactionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.googlecode.cqengine.query.QueryFactory.*;

@Service
public class TransactionService {

    private final IndexedCollection<Transaction> transactions;
    private final int windowOfInterest;

    @Autowired
    public TransactionService(@Value("${transaction.window.of.interest}") final int windowOfInterest) {
        this.windowOfInterest = windowOfInterest;

        this.transactions = new ObjectLockingIndexedCollection<>();
        this.transactions.addIndex(NavigableIndex.onAttribute(Transaction.TIME));
    }

    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

    public TransactionStatistics getStatisticsForWindowOfInterest() {
        Instant end = Instant.now();
        Instant start = end.minusSeconds(windowOfInterest);
        Query<Transaction> query = between(Transaction.TIME, start, end);
        ResultSet<Transaction> transactionsInWindow = transactions.retrieve(query,
                queryOptions(orderBy(ascending(Transaction.AMOUNT))));

        TransactionStatistics statistics = new TransactionStatistics();
        int counter = 0;
        int transactionCount = transactionsInWindow.size();
        statistics.count = transactionCount;
        for (Transaction transaction : transactionsInWindow) {
            statistics.sum += transaction.getAmount();

            if (counter == 0) {
                statistics.min = transaction.getAmount();
            } else if (counter == transactionCount - 1) {
                statistics.max = transaction.getAmount();
            }

            counter++;
        }

        // Finally calculate the average
        if (statistics.count > 0) {
            statistics.avg = statistics.sum / statistics.count;
        }

        return statistics;
    }
}
