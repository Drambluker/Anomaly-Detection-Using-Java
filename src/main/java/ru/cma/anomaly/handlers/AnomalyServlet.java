package ru.cma.anomaly.handlers;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import ru.cma.anomaly.TransactionManager;
import ru.cma.anomaly.model.Answer;
import ru.cma.anomaly.model.Transaction;
import ru.cma.anomaly.utils.AnomalyDetectionTask;
import ru.cma.anomaly.utils.CommonWithXML;
import ru.cma.core.utils.PropertyManager;

public class AnomalyServlet extends HttpServlet {
  public static TransactionManager manager = new TransactionManager();

  @Override
  public void init() throws ServletException {
    super.init();
    runDetectionSchedule(PropertyManager.getPropertyAsInteger("anomaly.detector.period", 60000));
  }

  private void runDetectionSchedule(int period) {
    Timer timer = new Timer();
    timer.schedule(new AnomalyDetectionTask(manager.getTransactionByAccount()), 0, period);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String reqStr = IOUtils.toString(req.getInputStream(), "UTF-8");
    Transaction transaction = CommonWithXML.getPrettyGson().fromJson(reqStr, Transaction.class);
    manager.addTransaction(transaction);

    if (StringUtils.isBlank(reqStr)
        || Objects.isNull(transaction.getDate())
        || Objects.isNull(transaction.getAccount())
        || Objects.isNull(transaction.getAmount())) {
      resp.setContentType("application/json");
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().println(CommonWithXML.getPrettyGson().toJson(new Answer("BAD")));
      return;
    }

    resp.setContentType("application/json");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().println(CommonWithXML.getPrettyGson().toJson(new Answer("OK")));
  }
}
