package ru.cma.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionTest {
    @Test
    public void transactionTest() {
        Transaction tr = new Transaction();

        tr.setAccount("cma");
        assertEquals("cma", tr.getAccount());

        tr.setDate("07.05.2020");
        assertEquals("07.05.2020", tr.getDate());

        tr.setAmount(103.5);
        assertEquals(Double.compare(103.5, tr.getAmount()), 0);

        tr.setAnomaly(true);
        assertEquals(true, tr.isAnomaly());

        tr.setBoxPlotWarn(true);
        assertEquals(true, tr.isBoxPlotWarn());

        tr.setIsolationForestWarn(true);
        assertEquals(true, tr.isIsolationForestWarn());
    }
}
