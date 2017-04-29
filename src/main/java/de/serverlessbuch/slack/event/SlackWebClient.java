package de.serverlessbuch.slack.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
@RequiredArgsConstructor
class SlackWebClient {

    private static final String SLACK_URL = "https://slack.com/api/";

    private static ObjectMapper mapper = new ObjectMapper();

    private final String oauthAccessToken;

    void postMessage(String text, String channel) {
        String method = "chat.postMessage";

        Map<String, String> reply = new HashMap<>();
        reply.put("text", text);
        reply.put("channel", channel);

        sendRequest(method, reply);
    }

    @SneakyThrows
    private void sendRequest(String method, Map<String, String> reply) {
        log.info("Sending {} request to Slack: {}", method, reply);

        reply.put("token", oauthAccessToken);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(SLACK_URL + method);

        String jsonString = mapper.writeValueAsString(reply);
        StringEntity stringEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);

        post.setEntity(stringEntity);

        HttpResponse response = httpClient.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream inputStream = response.getEntity().getContent();

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer);
        log.info("Slack responded with: {} - {}", statusCode, writer.toString());
    }

}
