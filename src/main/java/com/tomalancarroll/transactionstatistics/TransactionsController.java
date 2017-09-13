package com.tomalancarroll.transactionstatistics;

import com.tomalancarroll.transactionstatistics.model.Transaction;
import com.tomalancarroll.transactionstatistics.model.TransactionStatistics;
import com.tomalancarroll.transactionstatistics.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;

@Controller
public class TransactionsController {
    /**
     * The window of interest for our transactions in seconds.
     * This will be used to either return HTTP status 200 or 204 depending if the added transaction is within the window
     */
    @Value("${transaction.window.of.interest}")
    private long windowOfInterest;

    @Autowired
    public TransactionService transactionService;

    @RequestMapping(path= "/transactions", method = RequestMethod.POST)
    public ResponseEntity addTransaction(@RequestBody Transaction transaction) {
        try {
            Instant windowStartInstant = Instant.now().minusSeconds(windowOfInterest);

            transactionService.save(transaction);

            if (transaction.getTime().isAfter(windowStartInstant)) {
                return ResponseEntity.status(HttpStatus.OK).build(); // transaction time is within window of interest
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // transaction time is outside window of interest
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @RequestMapping(path= "/statistics", method = RequestMethod.GET)
    public @ResponseBody
    TransactionStatistics getStatistics() {
        return transactionService.getStatisticsForWindowOfInterest();
    }
}