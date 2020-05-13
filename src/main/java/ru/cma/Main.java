package ru.cma;

import org.eclipse.jetty.server.Server;
import ru.cma.neuralNetwork.NeuralNetwork;

public class Main {


  private static Server server;

  public static void main(String[] args) throws Exception
  {
    NeuralNetwork trainer = new NeuralNetwork();
    trainer.addTrainingInstances();
    trainer.trainModel();
  }



}
