package ru.cma.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.annotations.SerializedName;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import ru.cma.model.Transaction;

public class AnomalyDetectionTaskTest {
  private static class Data {
    @SerializedName("Amount")
    double amount;

    @SerializedName("Class")
    String classVal;
  }

  List<Data> dataset;
  HashMap<String, List<Transaction>> transactionsByAccount;

  @Before
  public void init() throws FileNotFoundException {
    transactionsByAccount = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();
    Type listOfDataObj = new TypeToken<ArrayList<Data>>() {}.getType();
    dataset =
        CommonWithXML.getPrettyGson()
            .fromJson(new FileReader("datasets/creditcard.json"), listOfDataObj);

    for (Data data : dataset) {
      Transaction transaction = new Transaction();
      transaction.setAccount("TEST");
      transaction.setAmount(data.amount);
      transactions.add(transaction);
    }

    transactionsByAccount.put("TEST", transactions);
  }

  @Test
  public void testDetect() {
    AnomalyDetectionTask anomalyDetection = new AnomalyDetectionTask(transactionsByAccount);
    anomalyDetection.detect();
    List<Transaction> transactions = anomalyDetection.getTransactionByAccount().get("TEST");

    printGeneralSummary(transactions);
    printAnomalySummary(transactions);
    printNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByBoxplot().get("TEST").intValue(),
        anomalyDetection.getTransactionByAccount().get("TEST").size() - 1);
    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByIF().get("TEST").intValue(),
        anomalyDetection.getTransactionByAccount().get("TEST").size() - 1);
  }

  private void printGeneralSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numTransactions = transactions.size();

    for (int i = 0; i < numTransactions; i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (transaction.isAnomaly() && data.classVal.equals("ANOMALY")
          || !transaction.isAnomaly() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numTransactions - correct;
    printSummary("General Summary", correct, incorrect, numTransactions);
  }

  private void printAnomalySummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numAnomaly = getNumClassVal("ANOMALY");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (transaction.isAnomaly() && data.classVal.equals("ANOMALY")) {
        correct++;
      }
    }

    incorrect = numAnomaly - correct;
    printSummary("Anomaly Summary", correct, incorrect, numAnomaly);
  }

  private void printNormalSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numNormal = getNumClassVal("NORMAL");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (!transaction.isAnomaly() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numNormal - correct;
    printSummary("Normal Summary", correct, incorrect, numNormal);
  }

  @Test
  public void testDetectByBoxplot() {
    AnomalyDetectionTask anomalyDetection = new AnomalyDetectionTask(transactionsByAccount);
    anomalyDetection.detectByBoxplot(anomalyDetection.BOXPLOT_CAPACITY);
    List<Transaction> transactions = anomalyDetection.getTransactionByAccount().get("TEST");

    printBoxplotGeneralSummary(transactions);
    printBoxplotAnomalySummary(transactions);
    printBoxplotNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByBoxplot().get("TEST").intValue(),
        anomalyDetection.getTransactionByAccount().get("TEST").size() - 1);
  }

  private void printBoxplotGeneralSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numTransactions = transactions.size();

    for (int i = 0; i < numTransactions; i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (transaction.isBoxPlotWarn() && data.classVal.equals("ANOMALY")
          || !transaction.isBoxPlotWarn() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numTransactions - correct;
    printSummary("General Summary", correct, incorrect, numTransactions);
  }

  private void printBoxplotAnomalySummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numAnomaly = getNumClassVal("ANOMALY");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (transaction.isBoxPlotWarn() && data.classVal.equals("ANOMALY")) {
        correct++;
      }
    }

    incorrect = numAnomaly - correct;
    printSummary("Anomaly Summary", correct, incorrect, numAnomaly);
  }

  private void printBoxplotNormalSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numNormal = getNumClassVal("NORMAL");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (!transaction.isBoxPlotWarn() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numNormal - correct;
    printSummary("Normal Summary", correct, incorrect, numNormal);
  }

  @Test
  public void testDetectByIsolationForest() {
    AnomalyDetectionTask anomalyDetection = new AnomalyDetectionTask(transactionsByAccount);
    anomalyDetection.detectByIsolationForest(anomalyDetection.TRAINER_CAPACITY);
    List<Transaction> transactions = anomalyDetection.getTransactionByAccount().get("TEST");

    printIsolationForestGeneralSummary(transactions);
    printIsolationForestAnomalySummary(transactions);
    printIsolationForestNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByIF().get("TEST").intValue(),
        anomalyDetection.getTransactionByAccount().get("TEST").size() - 1);
  }

  private void printIsolationForestGeneralSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numTransactions = transactions.size();

    for (int i = 0; i < numTransactions; i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (transaction.isIsolationForestWarn() && data.classVal.equals("ANOMALY")
          || !transaction.isIsolationForestWarn() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numTransactions - correct;
    printSummary("General Summary", correct, incorrect, numTransactions);
  }

  private void printIsolationForestAnomalySummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numAnomaly = getNumClassVal("ANOMALY");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (transaction.isIsolationForestWarn() && data.classVal.equals("ANOMALY")) {
        correct++;
      }
    }

    incorrect = numAnomaly - correct;
    printSummary("Anomaly Summary", correct, incorrect, numAnomaly);
  }

  private void printIsolationForestNormalSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numNormal = getNumClassVal("NORMAL");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = dataset.get(i);

      if (!transaction.isIsolationForestWarn() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numNormal - correct;
    printSummary("Normal Summary", correct, incorrect, numNormal);
  }

  private int getNumClassVal(String classVal) {
    int num = 0;

    for (int i = 0; i < dataset.size(); i++) {
      if (dataset.get(i).classVal.equals(classVal)) {
        num++;
      }
    }

    return num;
  }

  private void printSummary(String title, int correct, int incorrect, int num) {
    StringBuilder sb = new StringBuilder();
    sb.append(title)
        .append("\nCorrect: ")
        .append(correct)
        .append(" (")
        .append((double) correct / num * 100)
        .append("%)\n");
    sb.append("Incorrect: ")
        .append(incorrect)
        .append(" (")
        .append((double) incorrect / num * 100)
        .append("%)\n");
    System.out.println(sb.toString());
  }
}
