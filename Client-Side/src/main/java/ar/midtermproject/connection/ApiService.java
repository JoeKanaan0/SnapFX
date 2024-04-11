package ar.midtermproject.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ApiService {

    private final ObjectMapper objectMapper;

    public ApiService() {
        this.objectMapper = new ObjectMapper();
    }

    public String makeGetRequest(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClientManager.getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return EntityUtils.toString(response.getEntity());
        } else {
            // Handle errors or unexpected status codes here
            throw new RuntimeException("Failed to fetch data from URL: " + url + " with status code: " + statusCode);
        }
    }

    public String makePostRequest(String url, Object requestBody) throws IOException {
        CloseableHttpClient httpClient = HttpClientManager.getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");

        String requestBodyString = objectMapper.writeValueAsString(requestBody);
        httpPost.setEntity(new StringEntity(requestBodyString));

        HttpResponse response = httpClient.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return EntityUtils.toString(response.getEntity());
        } else {
            // Handle errors or unexpected status codes here
            throw new RuntimeException("Failed to post data to URL: " + url + " with status code: " + statusCode);
        }
    }

    public String makePutRequest(String url, Object requestBody) throws IOException {
        CloseableHttpClient httpClient = HttpClientManager.getHttpClient();
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Content-Type", "application/json");

        String requestBodyString = objectMapper.writeValueAsString(requestBody);
        httpPut.setEntity(new StringEntity(requestBodyString));

        HttpResponse response = httpClient.execute(httpPut);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return EntityUtils.toString(response.getEntity());
        } else {
            // Handle errors or unexpected status codes here
            throw new RuntimeException("Failed to update data at URL: " + url + " with status code: " + statusCode);
        }
    }

    public String makeDeleteRequest(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClientManager.getHttpClient();
        HttpDelete httpDelete = new HttpDelete(url);
        HttpResponse response = httpClient.execute(httpDelete);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return "Success with status code: " + statusCode;
        } else {
            // Handle errors or unexpected status codes here
            throw new RuntimeException("Failed to delete data from URL: " + url + " with status code: " + statusCode);
        }
    }
}
