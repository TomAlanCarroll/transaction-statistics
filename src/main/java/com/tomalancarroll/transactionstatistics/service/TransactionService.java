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

    private IndexedCollection<Transaction> transactions;
    /**
     * The window of interest for our transactions in seconds.
     * This will be used to select statistics within the time range from (now - transaction.window.of.interest) to now
     */
    private final long windowOfInterest;

    @Autowired
    public TransactionService(@Value("${transaction.window.of.interest}") final long windowOfInterest) {
        this.windowOfInterest = windowOfInterest;

        this.transactions = new ObjectLockingIndexedCollection<>();
        // Improve retrieval query performance by adding an index on time
        this.transactions.addIndex(NavigableIndex.onAttribute(Transaction.TIME));
    }

    /**
     * Saves the given transaction to the in-memory {@link ObjectLockingIndexedCollection}
     * @param transaction A well-formed transaction with a not-null amount and time
     * @throws IllegalArgumentException If transaction, amount, or time are null,
     *                                  or if the transaction time is after the current time
     */
    public void save(Transaction transaction) throws IllegalArgumentException {
        if (transaction == null || transaction.getAmount() == null || transaction.getTime() == null) {
            throw new IllegalArgumentException("Null or invalid transaction provided to TransactionService");
        }
        if (transaction.getTime().isAfter(Instant.now())) {
            throw new IllegalArgumentException("The transaction time must be before the current UTC time");
        }

        transactions.add(transaction);
    }

    /**
     * Resets the in-memory transaction store
     */
    public void deleteAll() {
        this.transactions.clear();
    }

    /**
     * Gets the statistics for the previous window of interest (default is last 60 seconds)
     * @return The
     */
    public TransactionStatistics getStatisticsForWindowOfInterest() {
        Instant end = Instant.now();
        Instant start = end.minusSeconds(windowOfInterest);
        Query<Transaction> query = between(Transaction.TIME, start, end);
        ResultSet<Transaction> transactionsInWindow = transactions.retrieve(query,
                queryOptions(orderBy(ascending(Transaction.AMOUNT))));

        return calculateStatistics(transactionsInWindow);
    }

    /**
     * Calculates statistics for a given set of transactions
     */
    private TransactionStatistics calculateStatistics(ResultSet<Transaction> transactions) {
        TransactionStatistics statistics = new TransactionStatistics();
        int counter = 0;
        int transactionCount = transactions.size();

        statistics.count = transactionCount;
        for (Transaction transaction : transactions) {
            statistics.sum += transaction.getAmount();

            if (counter == 0) {
                statistics.min = transaction.getAmount();
            }
            if (counter == transactionCount - 1) { // Not else-if for the scenario of a single transaction
                statistics.max = transaction.getAmount();
            }

            counter++;
        }

        if (statistics.count > 0) {
            statistics.avg = statistics.sum / statistics.count;
        }

        return statistics;
    }
}
