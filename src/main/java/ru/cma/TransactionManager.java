package ru.cma;

import ru.cma.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionManager {
    List<Transaction> transactionHistory = new ArrayList<>();
    HashMap<String, List<Transaction>> transactionByDate = new HashMap<>();
    HashMap<String, List<Transaction>> transactionByAccount = new HashMap<>();

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
        if (transactionByDate.get(transaction.getDate()) == null) {
            transactionByDate.put(transaction.getDate(), new ArrayList<>());
            transactionByDate.get(transaction.getDate()).add(transaction);
        } else transactionByDate.get(transaction.getDate()).add(transaction);

        if (transactionByAccount.get(transaction.getAccount()) == null) {
            transactionByAccount.put(transaction.getAccount(), new ArrayList<>());
            transactionByAccount.get(transaction.getAccount()).add(transaction);
        } else transactionByAccount.get(transaction.getAccount()).add(transaction);


    }

}
