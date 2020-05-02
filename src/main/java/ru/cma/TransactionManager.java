package ru.cma;

import ru.cma.model.Transaction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TransactionManager {
    List<Transaction> transactionHistory;
    HashMap<String, List<Transaction>> transactionByDate = new HashMap<String, List<Transaction>>();
    HashMap<String, List<Transaction>> transactionByAccount = new HashMap<String, List<Transaction>>();

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
        if (transactionByDate.get(transaction.getDate()) == null) {
            transactionByDate.put(transaction.getDate(), new LinkedList<Transaction>());
            transactionByDate.get(transaction.getDate()).add(transaction);
        } else transactionByDate.get(transaction.getDate()).add(transaction);

        if (transactionByAccount.get(transaction.getAccount()) == null) {
            transactionByAccount.put(transaction.getAccount(), new LinkedList<Transaction>());
            transactionByAccount.get(transaction.getAccount()).add(transaction);
        } else transactionByAccount.get(transaction.getAccount()).add(transaction);


    }

}
