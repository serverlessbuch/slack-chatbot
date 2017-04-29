package de.serverlessbuch.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SlackWebClient {

    private static final String SLACK_URL = "https://slack.com/api/";

    private static ObjectMapper mapper = new ObjectMapper();
    private static HttpClient httpClient = HttpClientBuilder.create().build();

    private String oauthAccessToken;

    @SneakyThrows
    public Map<String, Object> authorizeAccess(String clientId, String clientSecret, String code) {
        String method = "oauth.access";

        HttpGet get = new HttpGet(SLACK_URL + method);
        URI uri = new URIBuilder(get.getURI())
                .addParameter("client_id", clientId)
                .addParameter("client_secret", clientSecret)
                .addParameter("code", code)
                .build();
        get.setURI(uri);

        HttpResponse response = httpClient.execute(get);
        String responseString = getResponseString(response);

        return mapper.readValue(responseString, MapperUtil.mapTypeReference);
    }

    public void postMessage(String text, String channel) {
        String method = "chat.postMessage";

        Map<String, String> reply = new HashMap<>();
        reply.put("text", text);
        reply.put("channel", channel);

        sendRequest(method, reply);
    }

    @SneakyThrows
    private void sendRequest(String method, Map<String, String> reply) {

        reply.put("token", oauthAccessToken);
        log.info("Sending {} request to Slack: {}", method, reply);

        HttpPost post = new HttpPost(SLACK_URL + method);

        String jsonString = mapper.writeValueAsString(reply);
        StringEntity stringEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);

        post.setEntity(stringEntity);

        HttpResponse response = httpClient.execute(post);
        getResponseString(response);
    }

    @SneakyThrows
    private String getResponseString(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream inputStream = response.getEntity().getContent();

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer);
        String responseString = writer.toString();
        log.info("Slack responded with: {} - {}", statusCode, responseString);

        return responseString;
    }

}
