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

public class AnomalyDetectionTask extends TimerTask implements AnomalyDetector {
    private static Logger log = LoggerFactory.getLogger(AnomalyDetectionTask.class.getSimpleName());

    private final int BOXPLOT_CAPACITY;
    private final int TRAINER_CAPACITY;

    HashMap<String, List<Transaction>> transactionByAccount;
    IsolationForestTrainer trainer;

    public AnomalyDetectionTask(HashMap<String, List<Transaction>> transactionByAccount) {
        this.transactionByAccount = transactionByAccount;

        //TODO Pick value
        BOXPLOT_CAPACITY = 10;

        TRAINER_CAPACITY = 250;
    }

    public AnomalyDetectionTask(HashMap<String, List<Transaction>> transactionByAccount, int boxPlotCapacity) {
        this.transactionByAccount = transactionByAccount;
        BOXPLOT_CAPACITY = boxPlotCapacity;
        TRAINER_CAPACITY = 250;
    }

    public AnomalyDetectionTask(HashMap<String, List<Transaction>> transactionByAccount, int boxPlotCapacity, int trainerCapacity) {
        this.transactionByAccount = transactionByAccount;
        BOXPLOT_CAPACITY = boxPlotCapacity;
        TRAINER_CAPACITY = trainerCapacity;
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
        //TODO Add implementation
    }

    private void detectByIsolationForest(int capacity) {
        for (String key : transactionByAccount.keySet()) {
            List<Transaction> transactions = transactionByAccount.get(key);

            if (transactions.size() >= capacity) {
                trainer = new IsolationForestTrainer(capacity);
                addTrainingInstances(transactions, capacity);

                try {
                    trainer.trainModel();
                    classifyTransactionsByTrainer(transactions, capacity);
                } catch (Exception e) {
                    log.error("Error while training and classification transactions by Isolation Forest", e);
                }
            }
        }
    }

    private void addTrainingInstances(@NotNull List<Transaction> transactions, int capacity) {
        for (int i = transactions.size() - capacity; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);

            if (transaction.isAnomaly()) {
                trainer.addTrainingInstances(transaction.getAmount(), Classification.ANOMALY);
            } else {
                trainer.addTrainingInstances(transaction.getAmount(), Classification.NORMAL);
            }
        }
    }

    private void classifyTransactionsByTrainer(@NotNull List<Transaction> transactions, int capacity) throws Exception {
        for (int i = transactions.size() - capacity; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            Classification classification = trainer.classify(transaction.getAmount());

            if (classification == Classification.ANOMALY) {
                transaction.setIsolationForestWarn(true);
            } else {
                transaction.setIsolationForestWarn(false);
            }
        }
    }
}
