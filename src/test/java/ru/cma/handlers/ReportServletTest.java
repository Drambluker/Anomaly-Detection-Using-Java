package ru.cma.handlers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.cma.Main;

public class ReportServletTest {
  @Before
  public void init() {
    Main.runServer(8026, "/");
  }

  @Test
  public void doGet() throws Exception {
    String url = "http://localhost:8026/report?date=07.05.2000";
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url);

    HttpResponse response = client.execute(request);
    org.junit.Assert.assertEquals(200, response.getStatusLine().getStatusCode());
  }

  @After
  public void endTest() {
    Main.stopServer();
  }
}
