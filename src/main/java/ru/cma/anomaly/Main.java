package ru.cma.anomaly;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cma.anomaly.handlers.AnomalyServlet;
import ru.cma.anomaly.handlers.ReportServlet;
import ru.cma.anomaly.utils.CommonWithXML;
import ru.cma.core.utils.PropertyManager;

public class Main {
  private static Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

  private static Server server;

  public static void main(String[] args) throws Exception {
    PropertyManager.load();
    CommonWithXML.configure();
    runServer();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> stopServer(), "Stop Jetty Hook"));
  }

  private static void runServer() {
    int port = PropertyManager.getPropertyAsInteger("server.port", 8026);
    String contextStr = PropertyManager.getPropertyAsString("server.context", "server");
    runServer(port, contextStr);
  }

  public static void runServer(int port, String contextStr) {
    server = new Server(port);

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath(contextStr);
    server.setHandler(context);

    ServletHandler handler = new ServletHandler();
    server.setHandler(handler);

    handler.addServletWithMapping(AnomalyServlet.class, "/anomaly");
    handler.addServletWithMapping(ReportServlet.class, "/report");

    try {
      server.start();
      log.error("Server has started at port: " + port);
      // server.join();
    } catch (Throwable t) {
      log.error("Error while starting server", t);
    }
  }

  public static void stopServer() {
    try {
      if (server.isRunning()) {
        server.stop();
      }
    } catch (Exception e) {
      log.error("Error while stopping server", e);
    }
  }
}
