package ru.cma.anomaly.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Report {
  List<Transaction> transactions;

  @XmlElement(name = "transaction")
  public List<ru.cma.anomaly.model.Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<ru.cma.anomaly.model.Transaction> transactions) {
    this.transactions = transactions;
  }
}
