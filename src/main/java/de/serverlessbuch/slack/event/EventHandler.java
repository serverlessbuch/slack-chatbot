package de.serverlessbuch.slack.event;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.serverlessbuch.slack.MapperUtil;
import de.serverlessbuch.slack.OAuthRepo;
import de.serverlessbuch.slack.SlackWebClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
public class EventHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse>  {

    private static ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        log.info("Received body: {}", input.getBody());

        Map<String, Object> request = mapper.readValue(input.getBody(), MapperUtil.mapTypeReference);

        if (!System.getenv("SLACK_VERIFICATION_TOKEN").equals(request.get("token"))) {
            return new AwsProxyResponse(403);
        }

        AwsProxyResponse response = new AwsProxyResponse(200, Collections.singletonMap("X-Slack-No-Retry", "1"));

        switch (request.get("type").toString()) {
            case "url_verification":
                response.setHeaders(Collections.singletonMap("Content-Type", "text/plain"));
                response.setBody(request.get("challenge").toString());
                break;

            case "event_callback":
                //noinspection unchecked
                Event event = Event.of((Map<String, String>) request.get("event"));
                log.info("Event {}", event);
                if ("message".equals(event.getType())) {
                    OAuthRepo oAuthRepo = OAuthRepo.instance(System.getenv("S3_BUCKET"));
                    String accessToken = oAuthRepo.getAccessToken((String) request.get("team_id"));
                    SlackWebClient slack = new SlackWebClient(accessToken);
                    slack.postMessage(event.getText(), event.getChannel());
                }
                break;
        }

        return response;
    }

}
