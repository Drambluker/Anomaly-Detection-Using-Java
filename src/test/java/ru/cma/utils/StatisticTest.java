package ru.cma.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatisticTest {
  double[] testArray;

  @Test
  public void testGetQ1() {
    testArray = new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0}; // 8 / 2 = 4
    assertEquals(Double.compare(Statistic.getQ1(testArray), 1.5), 0);

    testArray = new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0}; // 10 / 2 = 5
    assertEquals(Double.compare(Statistic.getQ1(testArray), 2.0), 0);

    testArray =
        new double[] {
          0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0
        }; // 13 / 2 = 6
    assertEquals(Double.compare(Statistic.getQ1(testArray), 2.5), 0);

    testArray = new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0}; // 7 / 2 = 3
    assertEquals(Double.compare(Statistic.getQ1(testArray), 1), 0);
  }

  @Test
  public void testGetQ3() {
    testArray = new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0}; // 8 / 2 = 4
    assertEquals(Double.compare(Statistic.getQ3(testArray), 6.5), 0);

    testArray = new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0}; // 10 / 2 = 5
    assertEquals(Double.compare(Statistic.getQ3(testArray), 7.0), 0);

    testArray =
        new double[] {
          0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0
        }; // 13 / 2 = 6
    assertEquals(Double.compare(Statistic.getQ3(testArray), 9.5), 0);

    testArray = new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0}; // 7 / 2 = 3
    assertEquals(Double.compare(Statistic.getQ3(testArray), 5.0), 0);
  }
}
