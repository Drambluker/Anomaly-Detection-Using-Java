package ru.cma;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.example.utils.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cma.handlers.AnomalyServlet;
import ru.cma.utils.CommonWithXML;

public class Main {
    private static Logger log = LoggerFactory.getLogger(org.example.Main.class.getSimpleName());

    private static Server server;

    public static void main(String[] args) throws Exception {
        PropertyManager.load();
        CommonWithXML.configure();
        runServer();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {

                stopServer();

            }
        }, "Stop Jetty Hook"));
    }

    private static void runServer() {
        int port = PropertyManager.getPropertyAsInteger("server.port", 8026);
        String contextStr = PropertyManager.getPropertyAsString("server.context", "server");

        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(contextStr);
        server.setHandler(context);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(AnomalyServlet.class, "/anomaly");

        try {
            server.start();
            log.error("Server has started at port: " + port);
            //server.join();
        } catch (Throwable t) {
            log.error("Error while starting server", t);
        }
    }

    private static void stopServer() {
        try {
            if (server.isRunning()) {
                server.stop();
            }
        } catch (Exception e) {
            log.error("Error while stopping server", e);
        }
    }
}
