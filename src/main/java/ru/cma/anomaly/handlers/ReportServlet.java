package ru.cma.anomaly.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.cma.anomaly.TransactionManager;
import ru.cma.anomaly.model.Report;
import ru.cma.anomaly.utils.CommonWithXML;

public class ReportServlet extends HttpServlet {
  TransactionManager manager = ru.cma.anomaly.handlers.AnomalyServlet.manager;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Report report = new Report();

    if (request.getParameter("date") != null && request.getParameter("account") == null) {
      report.setTransactions(manager.getTransactionByDate().get(request.getParameter("date")));
      response.setStatus(HttpServletResponse.SC_OK);
    } else if (request.getParameter("date") == null && request.getParameter("account") != null) {
      report.setTransactions(
          manager.getTransactionByAccount().get(request.getParameter("account")));
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    response.setContentType("application/xml");
    response.getWriter().println(CommonWithXML.toFormattedXmlOrNull(report));
  }
}
