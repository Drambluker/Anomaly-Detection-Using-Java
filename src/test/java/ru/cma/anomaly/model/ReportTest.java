package ru.cma.anomaly.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ReportTest {
  @Test
  public void reportTest() {
    Report report = new Report();
    List<Transaction> transactions = new ArrayList<>();
    Transaction trnsctn = new Transaction();
    trnsctn.setAmount(103.5);
    trnsctn.setAccount("cma");
    trnsctn.setDate("07.05.2020");
    transactions.add(trnsctn);

    report.setTransactions(transactions);
    org.junit.Assert.assertEquals(report.getTransactions().isEmpty(), false);
  }
}
