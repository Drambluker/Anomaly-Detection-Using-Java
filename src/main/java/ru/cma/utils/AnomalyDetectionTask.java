package ru.cma.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cma.ml.isolationForest.Classification;
import ru.cma.ml.isolationForest.IsolationForestTrainer;
import ru.cma.model.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.Arrays;

public class AnomalyDetectionTask extends TimerTask implements AnomalyDetector {
    private static Logger log = LoggerFactory.getLogger(AnomalyDetectionTask.class.getSimpleName());
    private final int BOXPLOT_CAPACITY;
    private final int TRAINER_CAPACITY;

    HashMap<String, List<Transaction>> transactionByAccount;
    HashMap<String, Integer> lastCheckedIndexesByBoxplot;
    HashMap<String, Integer> lastCheckedIndexesByIF;
    IsolationForestTrainer trainer;

    public AnomalyDetectionTask(@NotNull HashMap<String, List<Transaction>> transactionByAccount) {
        this.transactionByAccount = transactionByAccount;

        //TODO Pick value
        BOXPLOT_CAPACITY = 10;

        TRAINER_CAPACITY = 250;
        lastCheckedIndexesByBoxplot = new HashMap<>();
        lastCheckedIndexesByIF = new HashMap<>();
    }

    public AnomalyDetectionTask(@NotNull HashMap<String, List<Transaction>> transactionByAccount, int boxPlotCapacity) {
        this.transactionByAccount = transactionByAccount;
        BOXPLOT_CAPACITY = boxPlotCapacity;
        TRAINER_CAPACITY = 250;
        lastCheckedIndexesByBoxplot = new HashMap<>();
        lastCheckedIndexesByIF = new HashMap<>();
    }

    public AnomalyDetectionTask(@NotNull HashMap<String, List<Transaction>> transactionByAccount, int boxPlotCapacity, int trainerCapacity) {
        this.transactionByAccount = transactionByAccount;
        BOXPLOT_CAPACITY = boxPlotCapacity;
        TRAINER_CAPACITY = trainerCapacity;
        lastCheckedIndexesByBoxplot = new HashMap<>();
        lastCheckedIndexesByIF = new HashMap<>();
    }

    public int getBOXPLOT_CAPACITY() {
        return BOXPLOT_CAPACITY;
    }

    public int getTRAINER_CAPACITY() {
        return TRAINER_CAPACITY;
    }

    public HashMap<String, List<Transaction>> getTransactionByAccount() {
        return transactionByAccount;
    }

    public HashMap<String, Integer> getLastCheckedIndexesByBoxplot() {
        return lastCheckedIndexesByBoxplot;
    }

    public HashMap<String, Integer> getLastCheckedIndexesByIF() {
        return lastCheckedIndexesByIF;
    }

    public void setTransactionByAccount(HashMap<String, List<Transaction>> transactionByAccount) {
        this.transactionByAccount = transactionByAccount;
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
        int numTransactions, lastCheckedIndex;
        double Q1, Q3, interQ, bottomLine, topLine;
        double[] amountArray;

        for (String key : transactionByAccount.keySet()) {
            transactions = transactionByAccount.get(key);
            numTransactions = transactions.size();

            if (numTransactions >= capacity) {
                if (lastCheckedIndexesByBoxplot.get(key) == null) {
                    lastCheckedIndex = capacity - 1;
                    lastCheckedIndexesByBoxplot.put(key, lastCheckedIndex);
                } else {
                    lastCheckedIndex = lastCheckedIndexesByBoxplot.get(key);
                }

                for (int i = lastCheckedIndex + 1; i < numTransactions; i++) {
                    amountArray = copyTransactionsToArray(transactions, i, capacity);
                    Arrays.sort(amountArray);

                    if ((amountArray.length / 2) % 2 == 1) {
                        Q1 = amountArray[amountArray.length / 4];
                        Q3 = amountArray[amountArray.length / 4 * 3 + 1];

                    } else {
                        Q1 = (amountArray[amountArray.length / 4 - 1] + amountArray[amountArray.length / 4]) / 2;
                        Q3 = (amountArray[amountArray.length / 4 * 3] + amountArray[amountArray.length / 4 * 3 + 1]) / 2;
                    }

                    interQ = Q3 - Q1;
                    topLine = 1.5 * interQ + Q3;
                    bottomLine = Q1 - 1.5 * interQ;

                    classifyTransactionByBoxplot(transactions.get(i), bottomLine, topLine);
                }

                lastCheckedIndexesByBoxplot.put(key, numTransactions - 1);
            }
        }
    }

    private double[] copyTransactionsToArray(@NotNull List<Transaction> transactions, int index, int capacity) {
        double[] amountArray = new double[capacity];

        for (int i = 0, j = index - capacity; i < capacity; i++, j++) {
            amountArray[i] = transactions.get(j).getAmount();
        }

        return amountArray;
    }

    private void classifyTransactionByBoxplot(@NotNull Transaction transaction, double bottomLine, double topLine) {
        double amount = transaction.getAmount();

        if (amount < bottomLine || amount > topLine) {
            transaction.setBoxplotWarn(true);
        } else {
            transaction.setBoxplotWarn(false);
        }
    }

    public void detectByIsolationForest(int capacity) {
        List<Transaction> transactions;
        int numTransactions, lastCheckedIndex;

        for (String key : transactionByAccount.keySet()) {
            transactions = transactionByAccount.get(key);
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
                        log.error("Error while training and classification transactions by Isolation Forest", e);
                    }
                }

                lastCheckedIndexesByIF.put(key, numTransactions - 1);
            }
        }
    }

    private void addTrainingInstances(@NotNull List<Transaction> transactions, int index, int capacity) {
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
