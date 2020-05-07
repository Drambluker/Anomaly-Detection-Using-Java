package ru.cma.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        assertEquals(report.getTransactions().isEmpty(), false);
    }
}
