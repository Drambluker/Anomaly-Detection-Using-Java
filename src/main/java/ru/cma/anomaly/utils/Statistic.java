package ru.cma.anomaly.utils;

public class Statistic {
  public static double getQ1(double[] array) {
    double q1;

    if (array.length % 2 == 0) {
      if ((array.length / 2) % 2 == 0) {
        q1 = (array[array.length / 4 - 1] + array[array.length / 4]) / 2;
      } else {
        q1 = array[array.length / 4];
      }
    } else {
      if ((array.length / 2) % 2 == 1) {
        q1 = array[array.length / 4];
      } else {
        q1 = (array[array.length / 4 - 1] + array[array.length / 4]) / 2;
      }
    }

    return q1;
  }

  public static double getQ3(double[] array) {
    double q3;

    if (array.length % 2 == 0) {
      if ((array.length / 2) % 2 == 0) {
        q3 = (array[array.length / 4 * 3] + array[array.length / 4 * 3 + 1]) / 2;
      } else {
        q3 = array[array.length / 4 * 3 + 1];
      }
    } else {
      if ((array.length / 2) % 2 == 1) {
        q3 = array[array.length / 4 * 3 + 2];
      } else {
        q3 = (array[array.length / 4 * 3] + array[array.length / 4 * 3 + 1]) / 2;
      }
    }

    return q3;
  }
}
