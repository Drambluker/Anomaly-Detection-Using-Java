package ru.cma.model;

import com.google.gson.annotations.Expose;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Transaction {
    String date;
    String account;
    Integer amount;
    boolean anomaly = false;

    @Expose(serialize = false)
    boolean isolationForestWarn = false;

    @Expose(serialize = false)
    boolean boxplotWarn = false;

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
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @XmlElement
    public boolean isAnomaly() {
        return anomaly;
    }

    public void setAnomaly(boolean anomaly) {
        this.anomaly = anomaly;
    }

    public void setIsolationForestWarn(boolean isolationForestWarn) {
        this.isolationForestWarn = isolationForestWarn;
        updateAnomalyFlag();
    }

    public void setBoxplotWarn(boolean boxplotWarn) {
        this.boxplotWarn = boxplotWarn;
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
