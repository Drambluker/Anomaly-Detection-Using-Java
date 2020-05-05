package ru.cma.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.annotations.SerializedName;
import org.junit.Before;
import org.junit.Test;
import ru.cma.model.Transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class AnomalyDetectionTaskTest {
    private static class Data {
        @SerializedName("Amount")
        private double amount;

        @SerializedName("Class")
        private String classVal;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getClassVal() {
            return classVal;
        }

        public void setClassVal(String classVal) {
            this.classVal = classVal;
        }
    }

    AnomalyDetectionTask anomalyDetection;

    @Before
    public void init() throws FileNotFoundException {
        HashMap<String, List<Transaction>> transactionsByAccount = new HashMap<>();
        List<Transaction> transactions = new ArrayList<>();
        Type listOfDataObj = new TypeToken<ArrayList<Data>>() {
        }.getType();
        List<Data> dataset = CommonWithXML.getPrettyGson().fromJson(new FileReader("datasets/creditcard.json"), listOfDataObj);

        for (Data data : dataset) {
            Transaction transaction = new Transaction();
            transaction.setAccount("TEST");
            transaction.setAmount(data.getAmount());
            transactions.add(transaction);
        }

        transactionsByAccount.put("TEST", transactions);
        anomalyDetection = new AnomalyDetectionTask(transactionsByAccount);
    }

    @Test
    public void testDetectByIsolationForest() {
        anomalyDetection.detectByIsolationForest(anomalyDetection.getTRAINER_CAPACITY());
        anomalyDetection.getLastCheckedIndexesByIF().get("TEST");
        assertEquals(anomalyDetection.getLastCheckedIndexesByIF().get("TEST").intValue(), anomalyDetection.getTransactionByAccount().get("TEST").size() - 1);
    }
}
