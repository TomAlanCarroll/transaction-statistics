package com.tomalancarroll.transactionstatistics;

import com.tomalancarroll.transactionstatistics.model.Transaction;
import com.tomalancarroll.transactionstatistics.model.TransactionStatistics;
import com.tomalancarroll.transactionstatistics.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TransactionsController {
    @Autowired
    public TransactionService transactionService;

    @RequestMapping(path= "/transactions", method = RequestMethod.POST)
    public ResponseEntity addTransaction(@RequestBody Transaction transaction) {
        transactionService.save(transaction);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(transaction);
    }

    @RequestMapping(path= "/statistics", method = RequestMethod.GET)
    public @ResponseBody
    TransactionStatistics getStatistics() {
        return transactionService.getStatisticsForWindowOfInterest();
    }
}