package ru.cma.anomaly.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cma.anomaly.ml.isolation_forest.Classification;
import ru.cma.anomaly.ml.isolation_forest.IsolationForestTrainer;
import ru.cma.anomaly.model.Transaction;

public class AnomalyDetectionTask extends TimerTask implements AnomalyDetector {
  private static Logger log = LoggerFactory.getLogger(AnomalyDetectionTask.class.getSimpleName());

  public final int BOXPLOT_CAPACITY;
  public final int TRAINER_CAPACITY;

  Map<String, List<Transaction>> transactionsByAccount;
  Map<String, Integer> lastCheckedIndexesByBoxplot;
  Map<String, Integer> lastCheckedIndexesByIF;
  IsolationForestTrainer trainer;

  public AnomalyDetectionTask(@NotNull Map<String, List<Transaction>> transactionsByAccount) {
    this.transactionsByAccount = transactionsByAccount;
    BOXPLOT_CAPACITY = 20;
    TRAINER_CAPACITY = 20;
    lastCheckedIndexesByBoxplot = new HashMap<>();
    lastCheckedIndexesByIF = new HashMap<>();
  }

  public AnomalyDetectionTask(
      @NotNull Map<String, List<Transaction>> transactionsByAccount, int boxPlotCapacity) {
    this.transactionsByAccount = transactionsByAccount;
    BOXPLOT_CAPACITY = boxPlotCapacity;
    TRAINER_CAPACITY = 20;
    lastCheckedIndexesByBoxplot = new HashMap<>();
    lastCheckedIndexesByIF = new HashMap<>();
  }

  public AnomalyDetectionTask(
      @NotNull Map<String, List<Transaction>> transactionsByAccount,
      int boxPlotCapacity,
      int trainerCapacity) {
    this.transactionsByAccount = transactionsByAccount;
    BOXPLOT_CAPACITY = boxPlotCapacity;
    TRAINER_CAPACITY = trainerCapacity;
    lastCheckedIndexesByBoxplot = new HashMap<>();
    lastCheckedIndexesByIF = new HashMap<>();
  }

  public Map<String, List<Transaction>> getTransactionsByAccount() {
    return transactionsByAccount;
  }

  public Map<String, Integer> getLastCheckedIndexesByBoxplot() {
    return lastCheckedIndexesByBoxplot;
  }

  public Map<String, Integer> getLastCheckedIndexesByIF() {
    return lastCheckedIndexesByIF;
  }

  @Override
  public void run() {
    detect();
  }

  @Override
  public void detect() {
    detectByBoxplot(BOXPLOT_CAPACITY);
    detectByIsolationForest(TRAINER_CAPACITY);
  }

  public void detectByBoxplot(int capacity) {
    List<Transaction> transactions;
    int numNormalTransactions, numTransactions, lastCheckedIndex;
    double q1, q3, interQ, bottomLine, topLine;
    double[] amountArray;

    for (String key : transactionsByAccount.keySet()) {
      transactions = transactionsByAccount.get(key);
      numNormalTransactions = getNumNormalTransactions(transactions);
      numTransactions = transactions.size();

      if (numNormalTransactions >= capacity) {
        if (lastCheckedIndexesByBoxplot.get(key) == null) {
          lastCheckedIndex = capacity - 1;
          lastCheckedIndexesByBoxplot.put(key, lastCheckedIndex);
        } else {
          lastCheckedIndex = lastCheckedIndexesByBoxplot.get(key);
        }

        for (int i = lastCheckedIndex + 1; i < numTransactions; i++) {
          amountArray = getNormalTransactionsArray(transactions, i, capacity);
          Arrays.sort(amountArray);
          q1 = Statistic.getQ1(amountArray);
          q3 = ru.cma.anomaly.utils.Statistic.getQ3(amountArray);
          interQ = q3 - q1;
          topLine = 1.5 * interQ + q3;
          bottomLine = q1 - 1.5 * interQ;
          classifyTransactionByBoxplot(transactions.get(i), bottomLine, topLine);
        }

        lastCheckedIndexesByBoxplot.put(key, numTransactions - 1);
      }
    }
  }

  private int getNumNormalTransactions(@NotNull List<Transaction> transactions) {
    int numNormalTransactions = 0;

    for (Transaction transaction : transactions) {
      if (!transaction.isAnomaly()
          && !transaction.isIsolationForestWarn()
          && !transaction.isBoxPlotWarn()) {
        numNormalTransactions++;
      }
    }

    return numNormalTransactions;
  }

  private double[] getNormalTransactionsArray(
      @NotNull List<Transaction> transactions, int index, int capacity) {
    double[] amountArray = new double[capacity];
    Transaction transaction;

    for (int i = 0, j = index - 1; i < capacity && j >= 0; j--) {
      transaction = transactions.get(j);

      if (!transaction.isAnomaly()
          && !transaction.isIsolationForestWarn()
          && !transaction.isBoxPlotWarn()) {
        amountArray[i] = transaction.getAmount();
        i++;
      }
    }

    return amountArray;
  }

  private void classifyTransactionByBoxplot(
      @NotNull Transaction transaction, double bottomLine, double topLine) {
    double amount = transaction.getAmount();

    if (amount < bottomLine || amount > topLine) {
      transaction.setBoxPlotWarn(true);
    } else {
      transaction.setBoxPlotWarn(false);
    }
  }

  public void detectByIsolationForest(int capacity) {
    List<Transaction> transactions;
    int numTransactions, lastCheckedIndex;

    for (String key : transactionsByAccount.keySet()) {
      transactions = transactionsByAccount.get(key);
      numTransactions = transactions.size();

      if (numTransactions >= capacity) {
        if (lastCheckedIndexesByIF.get(key) == null) {
          lastCheckedIndex = capacity - 1;
          lastCheckedIndexesByIF.put(key, lastCheckedIndex);
        } else {
          lastCheckedIndex = lastCheckedIndexesByIF.get(key);
        }

        for (int i = lastCheckedIndex + 1; i < numTransactions; i++) {
          trainer = new IsolationForestTrainer(capacity);
          addTrainingInstances(transactions, i, capacity);

          try {
            trainer.trainModel();
            classifyTransactionByTrainer(transactions.get(i));
          } catch (Exception e) {
            log.error(
                "Error while training and classification transactions by Isolation Forest", e);
          }
        }

        lastCheckedIndexesByIF.put(key, numTransactions - 1);
      }
    }
  }

  private void addTrainingInstances(
      @NotNull List<Transaction> transactions, int index, int capacity) {
    for (int i = index - capacity; i < index; i++) {
      Transaction transaction = transactions.get(i);

      if (transaction.isAnomaly()) {
        trainer.addTrainingInstances(transaction.getAmount(), Classification.ANOMALY);
      } else {
        trainer.addTrainingInstances(transaction.getAmount(), Classification.NORMAL);
      }
    }
  }

  private void classifyTransactionByTrainer(@NotNull Transaction transaction) throws Exception {
    Classification classification = trainer.classify(transaction.getAmount());

    if (classification == Classification.ANOMALY) {
      transaction.setIsolationForestWarn(true);
    } else {
      transaction.setIsolationForestWarn(false);
    }
  }
}
