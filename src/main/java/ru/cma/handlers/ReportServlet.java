package ru.cma.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.cma.TransactionManager;
import ru.cma.model.Report;
import ru.cma.utils.CommonWithXML;

public class ReportServlet extends HttpServlet {
  TransactionManager manager = AnomalyServlet.manager;

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
