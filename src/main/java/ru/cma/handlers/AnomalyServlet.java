package ru.cma.handlers;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.model.Answer;

import org.example.utils.PropertyManager;
import ru.cma.TransactionManager;
import ru.cma.utils.AnomalyDetectionTask;
import ru.cma.model.Transaction;
import ru.cma.utils.CommonWithXML;
import ru.cma.model.Report;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Timer;

import static java.util.Objects.isNull;

public class AnomalyServlet extends HttpServlet {
    TransactionManager manager = new TransactionManager();

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqStr = IOUtils.toString(req.getInputStream());
        Transaction transaction = CommonWithXML.getPrettyGson().fromJson(reqStr, Transaction.class);
        manager.addTransaction(transaction);
        if (StringUtils.isBlank(reqStr) ||
                isNull(transaction.getDate()) || isNull(transaction.getAccount()) || isNull(transaction.getAmount())) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(CommonWithXML.getPrettyGson().toJson(new Answer("BAD", null)));
            return;
        }
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(CommonWithXML.getPrettyGson().toJson(new Answer("OK", null)));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Report report = new Report();

        if (request.getParameter("date") != null && request.getParameter("account") == null) {
            report.setTransactions(manager.getTransactionByDate().get(request.getParameter("date")));
            response.setStatus(HttpServletResponse.SC_OK);
        } else if (request.getParameter("date") == null && request.getParameter("account") != null) {
            report.setTransactions(manager.getTransactionByAccount().get(request.getParameter("acсount")));
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.setContentType("application/xml");
        response.getWriter().println(CommonWithXML.toFormattedXmlOrNull(report));
    }
}
