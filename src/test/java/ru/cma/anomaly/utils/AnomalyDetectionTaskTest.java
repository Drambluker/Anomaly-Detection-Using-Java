package ru.cma.anomaly.utils;

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
import ru.cma.anomaly.model.Transaction;

public class AnomalyDetectionTaskTest {
  private static class Data {
    @SerializedName("Amount")
    double amount;

    @SerializedName("Class")
    String classVal;
  }

  List<Data> normalDataset;
  List<Data> uniformDataset;
  HashMap<String, List<Transaction>> normalTransactionsByAccount;
  HashMap<String, List<Transaction>> uniformTransactionsByAccount;

  @Before
  public void init() throws FileNotFoundException {
    normalTransactionsByAccount = new HashMap<>();
    uniformTransactionsByAccount = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();
    Type listOfDataObj = new TypeToken<ArrayList<Data>>() {}.getType();

    normalDataset =
        ru.cma.anomaly.utils.CommonWithXML.getPrettyGson()
            .fromJson(new FileReader("datasets/normalDistribution.json"), listOfDataObj);

    for (Data data : normalDataset) {
      Transaction transaction = new Transaction();
      transaction.setAccount("NORMAL");
      transaction.setAmount(data.amount);
      transactions.add(transaction);
    }

    normalTransactionsByAccount.put("NORMAL", transactions);

    transactions = new ArrayList<>();
    uniformDataset =
        ru.cma.anomaly.utils.CommonWithXML.getPrettyGson()
            .fromJson(new FileReader("datasets/uniformDistribution.json"), listOfDataObj);

    for (Data data : uniformDataset) {
      Transaction transaction = new Transaction();
      transaction.setAccount("UNIFORM");
      transaction.setAmount(data.amount);
      transactions.add(transaction);
    }

    uniformTransactionsByAccount.put("UNIFORM", transactions);
  }

  @Test
  public void testDetectNormal() {
    ru.cma.anomaly.utils.AnomalyDetectionTask anomalyDetection = new ru.cma.anomaly.utils.AnomalyDetectionTask(normalTransactionsByAccount);
    anomalyDetection.detect();
    List<Transaction> transactions = anomalyDetection.getTransactionsByAccount().get("NORMAL");

    printGeneralSummary(transactions);
    printAnomalySummary(transactions);
    printNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByBoxplot().get("NORMAL").intValue(),
        anomalyDetection.getTransactionsByAccount().get("NORMAL").size() - 1);
    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByIF().get("NORMAL").intValue(),
        anomalyDetection.getTransactionsByAccount().get("NORMAL").size() - 1);
  }

  @Test
  public void testDetectUniform() {
    ru.cma.anomaly.utils.AnomalyDetectionTask anomalyDetection = new ru.cma.anomaly.utils.AnomalyDetectionTask(uniformTransactionsByAccount);
    anomalyDetection.detect();
    List<Transaction> transactions = anomalyDetection.getTransactionsByAccount().get("UNIFORM");

    printGeneralSummary(transactions);
    printAnomalySummary(transactions);
    printNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByBoxplot().get("UNIFORM").intValue(),
        anomalyDetection.getTransactionsByAccount().get("UNIFORM").size() - 1);
    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByIF().get("UNIFORM").intValue(),
        anomalyDetection.getTransactionsByAccount().get("UNIFORM").size() - 1);
  }

  private void printGeneralSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numTransactions = transactions.size();

    for (int i = 0; i < numTransactions; i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

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
    int numAnomaly = getNumClassValInDataset("ANOMALY");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

      if (transaction.isAnomaly() && data.classVal.equals("ANOMALY")) {
        correct++;
      }
    }

    incorrect = numAnomaly - correct;
    printSummary("Anomaly Summary", correct, incorrect, numAnomaly);
  }

  private void printNormalSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numNormal = getNumClassValInDataset("NORMAL");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

      if (!transaction.isAnomaly() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numNormal - correct;
    printSummary("Normal Summary", correct, incorrect, numNormal);
  }

  @Test
  public void testDetectNormalByBoxplot() {
    ru.cma.anomaly.utils.AnomalyDetectionTask anomalyDetection = new ru.cma.anomaly.utils.AnomalyDetectionTask(normalTransactionsByAccount);
    anomalyDetection.detectByBoxplot(anomalyDetection.BOXPLOT_CAPACITY);
    List<Transaction> transactions = anomalyDetection.getTransactionsByAccount().get("NORMAL");

    printBoxplotGeneralSummary(transactions);
    printBoxplotAnomalySummary(transactions);
    printBoxplotNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByBoxplot().get("NORMAL").intValue(),
        anomalyDetection.getTransactionsByAccount().get("NORMAL").size() - 1);
  }

  @Test
  public void testDetectUniformByBoxplot() {
    ru.cma.anomaly.utils.AnomalyDetectionTask anomalyDetection = new ru.cma.anomaly.utils.AnomalyDetectionTask(uniformTransactionsByAccount);
    anomalyDetection.detectByBoxplot(anomalyDetection.BOXPLOT_CAPACITY);
    List<Transaction> transactions = anomalyDetection.getTransactionsByAccount().get("UNIFORM");

    printBoxplotGeneralSummary(transactions);
    printBoxplotAnomalySummary(transactions);
    printBoxplotNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByBoxplot().get("UNIFORM").intValue(),
        anomalyDetection.getTransactionsByAccount().get("UNIFORM").size() - 1);
  }

  private void printBoxplotGeneralSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numTransactions = transactions.size();

    for (int i = 0; i < numTransactions; i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

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
    int numAnomaly = getNumClassValInDataset("ANOMALY");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

      if (transaction.isBoxPlotWarn() && data.classVal.equals("ANOMALY")) {
        correct++;
      }
    }

    incorrect = numAnomaly - correct;
    printSummary("Anomaly Summary", correct, incorrect, numAnomaly);
  }

  private void printBoxplotNormalSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numNormal = getNumClassValInDataset("NORMAL");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

      if (!transaction.isBoxPlotWarn() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numNormal - correct;
    printSummary("Normal Summary", correct, incorrect, numNormal);
  }

  @Test
  public void testDetectNormalByIsolationForest() {
    ru.cma.anomaly.utils.AnomalyDetectionTask anomalyDetection = new ru.cma.anomaly.utils.AnomalyDetectionTask(normalTransactionsByAccount);
    anomalyDetection.detectByIsolationForest(anomalyDetection.TRAINER_CAPACITY);
    List<Transaction> transactions = anomalyDetection.getTransactionsByAccount().get("NORMAL");

    printIsolationForestGeneralSummary(transactions);
    printIsolationForestAnomalySummary(transactions);
    printIsolationForestNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByIF().get("NORMAL").intValue(),
        anomalyDetection.getTransactionsByAccount().get("NORMAL").size() - 1);
  }

  @Test
  public void testDetectUniformByIsolationForest() {
    ru.cma.anomaly.utils.AnomalyDetectionTask anomalyDetection = new ru.cma.anomaly.utils.AnomalyDetectionTask(uniformTransactionsByAccount);
    anomalyDetection.detectByIsolationForest(anomalyDetection.TRAINER_CAPACITY);
    List<Transaction> transactions = anomalyDetection.getTransactionsByAccount().get("UNIFORM");

    printIsolationForestGeneralSummary(transactions);
    printIsolationForestAnomalySummary(transactions);
    printIsolationForestNormalSummary(transactions);

    org.junit.Assert.assertEquals(
        anomalyDetection.getLastCheckedIndexesByIF().get("UNIFORM").intValue(),
        anomalyDetection.getTransactionsByAccount().get("UNIFORM").size() - 1);
  }

  private void printIsolationForestGeneralSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numTransactions = transactions.size();

    for (int i = 0; i < numTransactions; i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

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
    int numAnomaly = getNumClassValInDataset("ANOMALY");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

      if (transaction.isIsolationForestWarn() && data.classVal.equals("ANOMALY")) {
        correct++;
      }
    }

    incorrect = numAnomaly - correct;
    printSummary("Anomaly Summary", correct, incorrect, numAnomaly);
  }

  private void printIsolationForestNormalSummary(@NotNull List<Transaction> transactions) {
    int correct = 0, incorrect;
    int numNormal = getNumClassValInDataset("NORMAL");

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      Data data = normalDataset.get(i);

      if (!transaction.isIsolationForestWarn() && data.classVal.equals("NORMAL")) {
        correct++;
      }
    }

    incorrect = numNormal - correct;
    printSummary("Normal Summary", correct, incorrect, numNormal);
  }

  private int getNumClassValInDataset(String classVal) {
    int num = 0;

    for (int i = 0; i < normalDataset.size(); i++) {
      if (normalDataset.get(i).classVal.equals(classVal)) {
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
