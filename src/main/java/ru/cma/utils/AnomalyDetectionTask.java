package ru.cma.utils;

import ru.cma.model.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class AnomalyDetectionTask extends TimerTask implements AnomalyDetector {
    HashMap<String, List<Transaction>> transactionByAccount;

    public AnomalyDetectionTask(HashMap<String, List<Transaction>> transactionByAccount) {
        this.transactionByAccount = transactionByAccount;
    }

    @Override
    public void run() {
        detect();
    }

    @Override
    public void detect() {
        //TODO Add some implementation
    }
}
