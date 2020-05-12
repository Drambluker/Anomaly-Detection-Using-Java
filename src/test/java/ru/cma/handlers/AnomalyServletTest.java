package ru.cma.handlers;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.example.model.Answer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.cma.Main;
import ru.cma.model.Transaction;
import ru.cma.utils.CommonWithXML;

public class AnomalyServletTest {
  @Before
  public void init() {
    Main.runServer(8026, "/");
  }

  @Test
  public void doPost() throws Exception {
    String url = "http://localhost:8026/anomaly";
    HttpClient client = HttpClientBuilder.create().build();
    HttpPost request = new HttpPost(url);

    Transaction trnsctn = new Transaction();
    trnsctn.setDate("07.05.2020");
    trnsctn.setAccount("CMA");
    trnsctn.setAmount(103.5);

    StringEntity entity = new StringEntity(CommonWithXML.getPrettyGson().toJson(trnsctn));
    request.setEntity(entity);

    HttpResponse response = client.execute(request);

    HttpEntity resp = response.getEntity();
    String respStr = IOUtils.toString(resp.getContent(), "UTF-8");

    Answer a = CommonWithXML.getPrettyGson().fromJson(respStr, Answer.class);

    org.junit.Assert.assertEquals("OK", a.getStatus());
  }

  @After
  public void endTest() {
    Main.stopServer();
  }
}
