package ru.cma.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DatasetGenerator {
  static Random random = new Random(2020);

  public static void main(String[] args) throws IOException {
    sampleGeneration(10000);
  }

  static void sampleGeneration(int size) throws IOException {
    uniformDistribution(size);
    normalDistribution(size);
  }

  static void uniformDistribution(int size) throws IOException {
    FileWriter writer = new FileWriter("src\\main\\resources\\uniformDistribution.csv", false);
    writer.write("Date,Account,Amount,Class" + System.getProperty("line.separator"));

    for (int i = 0; i < size; i++) {
      if (i > 100) {
        if (i % 5 == 1) {
          if (random.nextBoolean()) {
            writer.write(
                "09/05/2020"
                    + ","
                    + "JMeter"
                    + ","
                    + String.valueOf(random.nextDouble() * 10000)
                    + ","
                    + "ANOMALY"
                    + System.getProperty("line.separator"));
          } else {
            writer.write(
                "09/05/2020"
                    + ","
                    + "JMeter"
                    + ","
                    + String.valueOf(random.nextDouble() * 100)
                    + ","
                    + "NORMAL"
                    + System.getProperty("line.separator"));
          }
        } else {
          writer.write(
              "09/05/2020"
                  + ","
                  + "JMeter"
                  + ","
                  + String.valueOf(random.nextDouble() * 100)
                  + ","
                  + "NORMAL"
                  + System.getProperty("line.separator"));
        }
      } else {
        writer.write(
            "09/05/2020"
                + ","
                + "JMeter"
                + ","
                + String.valueOf(random.nextDouble() * 100)
                + ","
                + "NORMAL"
                + System.getProperty("line.separator"));
      }
    }

    writer.flush();
    writer.close();
  }

  static void normalDistribution(int size) throws IOException {
    FileWriter writer = new FileWriter("src\\main\\resources\\normalDistribution.csv", false);
    writer.write("Date,Account,Amount,Class" + System.getProperty("line.separator"));

    for (int i = 0; i < size; i++) {
      if (i > 100) {
        if (i % 5 == 1) {
          if (random.nextBoolean()) {
            writer.write(
                "09/05/2020"
                    + ","
                    + "JMeter"
                    + ","
                    + String.valueOf(random.nextGaussian() * 10000)
                    + ","
                    + "ANOMALY"
                    + System.getProperty("line.separator"));
          } else {
            writer.write(
                "09/05/2020"
                    + ","
                    + "JMeter"
                    + ","
                    + String.valueOf(random.nextGaussian() * 100)
                    + ","
                    + "NORMAL"
                    + System.getProperty("line.separator"));
          }
        } else {
          writer.write(
              "09/05/2020"
                  + ","
                  + "JMeter"
                  + ","
                  + String.valueOf(random.nextGaussian() * 100)
                  + ","
                  + "NORMAL"
                  + System.getProperty("line.separator"));
        }
      } else {
        writer.write(
            "09/05/2020"
                + ","
                + "JMeter"
                + ","
                + String.valueOf(random.nextGaussian() * 100)
                + ","
                + "NORMAL"
                + System.getProperty("line.separator"));
      }
    }

    writer.flush();
    writer.close();
  }
}
