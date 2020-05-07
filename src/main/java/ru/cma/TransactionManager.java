package ru.cma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import ru.cma.model.Transaction;

public class TransactionManager {
  List<Transaction> transactionHistory = new ArrayList<>();
  Map<String, List<Transaction>> transactionByDate = new ConcurrentHashMap<>();
  Map<String, List<Transaction>> transactionByAccount = new ConcurrentHashMap<>();

  public void addTransaction(Transaction transaction) {
    transactionHistory.add(transaction);

    if (transactionByDate.get(transaction.getDate()) == null) {
      transactionByDate.put(transaction.getDate(), new CopyOnWriteArrayList<>());
    }

    transactionByDate.get(transaction.getDate()).add(transaction);

    if (transactionByAccount.get(transaction.getAccount()) == null) {
      transactionByAccount.put(transaction.getAccount(), new CopyOnWriteArrayList<>());
    }

    transactionByAccount.get(transaction.getAccount()).add(transaction);
  }

  public Map<String, List<Transaction>> getTransactionByAccount() {
    return transactionByAccount;
  }

  public Map<String, List<Transaction>> getTransactionByDate() {
    return transactionByDate;
  }

  public List<Transaction> getTransactionHistory() {
    return transactionHistory;
  }
}
