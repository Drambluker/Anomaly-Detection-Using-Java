package org.example.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnswerTest {

    @Test
    public void testAnwer() {
        Answer answer = new Answer("STATUS", null);

        assertEquals("STATUS", answer.getStatus());

        answer.setStatus("ST2");

        assertEquals("ST2", answer.getStatus());

    }
}