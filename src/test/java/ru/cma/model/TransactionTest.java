package ru.cma.model;

import org.junit.Test;

public class TransactionTest {
  @Test
  public void transactionTest() {
    Transaction tr = new Transaction();

    tr.setAccount("cma");
    org.junit.Assert.assertEquals("cma", tr.getAccount());

    tr.setDate("07.05.2020");
    org.junit.Assert.assertEquals("07.05.2020", tr.getDate());

    tr.setAmount(103.5);
    org.junit.Assert.assertEquals(Double.compare(103.5, tr.getAmount()), 0);

    tr.setAnomaly(true);
    org.junit.Assert.assertEquals(true, tr.isAnomaly());

    tr.setBoxPlotWarn(true);
    org.junit.Assert.assertEquals(true, tr.isBoxPlotWarn());

    tr.setIsolationForestWarn(true);
    org.junit.Assert.assertEquals(true, tr.isIsolationForestWarn());
  }
}
