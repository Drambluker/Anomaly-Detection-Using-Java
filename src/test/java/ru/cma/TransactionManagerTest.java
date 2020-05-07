package ru.cma;

import org.junit.Test;
import ru.cma.model.Transaction;

public class TransactionManagerTest {
  @Test
  public void transactionManagerTest() {
    TransactionManager trM = new TransactionManager();
    Transaction tr = new Transaction();
    tr.setDate("07.05.2020");
    tr.setAccount("cma");
    tr.setAmount(103.5);

    trM.addTransaction(tr);
    org.junit.Assert.assertEquals(trM.getTransactionByAccount().isEmpty(), false);
    org.junit.Assert.assertEquals(trM.getTransactionByDate().isEmpty(), false);
    org.junit.Assert.assertEquals(trM.getTransactionHistory().isEmpty(), false);
  }
}
