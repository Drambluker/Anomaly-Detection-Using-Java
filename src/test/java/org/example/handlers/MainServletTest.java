package org.example.handlers;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.example.Main;
import org.example.model.Answer;
import org.example.model.Request;
import org.example.utils.Common;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainServletTest {
    @Before
    public void init() {
        Main.runServer(8090, "/");
    }

    @Test
    public void doGet() throws Exception {
        String url = "http://localhost:8090/path";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        org.junit.Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void doPost() throws Exception {
        String url = "http://localhost:8090/path";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);

        Request r = new Request();
        r.setName("Eric");

        StringEntity entity = new StringEntity(Common.getPrettyGson().toJson(r));
        request.setEntity(entity);

        HttpResponse response = client.execute(request);

        HttpEntity resp = response.getEntity();
        String respStr = IOUtils.toString(resp.getContent());

        Answer a = Common.getPrettyGson().fromJson(respStr, Answer.class);

        org.junit.Assert.assertEquals("Eric", a.getItems().get(0).getName());
    }


    @After
    public void entTest() {
        Main.stopServer();
    }


}