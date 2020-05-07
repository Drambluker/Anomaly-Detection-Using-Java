package ru.cma;

import org.junit.Test;
import ru.cma.model.Transaction;

import static org.junit.Assert.*;

public class TransactionManagerTest {

    @Test
    public void transactionManagerTest(){
        TransactionManager trM=new TransactionManager();
        Transaction tr=new Transaction();
        tr.setDate("07.05.2020");
        tr.setAccount("cma");
        tr.setAmount(103.5);

        trM.addTransaction(tr);
        assertEquals( trM.getTransactionByAccount().isEmpty(), false);
        assertEquals( trM.getTransactionByDate().isEmpty(), false);
        assertEquals( trM.getTransactionHistory().isEmpty(), false);


    }

}