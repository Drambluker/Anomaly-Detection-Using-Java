package ru.cma.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Transaction {
    String date;
    String account;
    Double amount;

    private transient boolean isolationForestWarn = false;
    private transient boolean boxplotWarn = false;
    private transient boolean anomaly = false;

    @XmlElement
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @XmlElement
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @XmlElement
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @XmlElement
    public boolean isAnomaly() {
        return anomaly;
    }

    public void setAnomaly(boolean anomaly) {
        this.anomaly = anomaly;
    }

    @XmlTransient
    public void setBoxplotWarn(boolean boxplotWarn) {
        this.boxplotWarn = boxplotWarn;
        updateAnomalyFlag();
    }

    @XmlTransient
    public void setIsolationForestWarn(boolean isolationForestWarn) {
        this.isolationForestWarn = isolationForestWarn;
        updateAnomalyFlag();
    }

    private void updateAnomalyFlag() {
        if (boxplotWarn && isolationForestWarn) {
            anomaly = true;
        } else {
            anomaly = false;
        }
    }
}
