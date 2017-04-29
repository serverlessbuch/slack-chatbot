package de.serverlessbuch.slack;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Collections;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class InstallHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        String body = getHtml(System.getenv("SLACK_CLIENT_ID"));
        return new AwsProxyResponse(200, Collections.singletonMap("Content-Type","text/html"), body);
    }

    private String getHtml(String clientId) {
        return "<!DOCTYPE html><html><head><title>Slackbot by ServerlessBuch.de</title></head>" +
                "<body><h1>ServerlessBuchBot</h1><p>Click the button to add @ServerlessBuchBot to Slack!</p>" +
                "<a href=\"https://slack.com/oauth/authorize?scope=commands,bot,channels:history,chat:write:bot&client_id=" + clientId + "\">" +
                "<img alt=\"Add to Slack\" height=\"40\" width=\"139\" src=\"https://platform.slack-edge.com/img/add_to_slack.png\" srcset=\"https://platform.slack-edge.com/img/add_to_slack.png 1x, https://platform.slack-edge.com/img/add_to_slack@2x.png 2x\"/>" +
                "</a></body></html>";
    }

}
