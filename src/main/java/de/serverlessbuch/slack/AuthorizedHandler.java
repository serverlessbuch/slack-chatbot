package de.serverlessbuch.slack;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Collections;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
public class AuthorizedHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {

        Map<String, String> queryStringParameters = input.getQueryStringParameters();
        String code = queryStringParameters.get("code");

        String clientId = System.getenv("SLACK_CLIENT_ID");
        String clientSecret = System.getenv("SLACK_CLIENT_SECRET");

        SlackWebClient slack = new SlackWebClient();
        Map<String, Object> authResponse = slack.authorizeAccess(clientId, clientSecret, code);

        OAuthRepo oauthRepo = OAuthRepo.instance(System.getenv("S3_BUCKET"));
        Map<String, String> bot = (Map<String, String>) authResponse.get("bot");
        oauthRepo.storeAccessToken(authResponse.get("team_id").toString(), bot.get("bot_access_token"));

        String body = getHtml();
        return new AwsProxyResponse(200, Collections.singletonMap("Content-Type", "text/html"), body);
    }

    private String getHtml() {
        return "<!DOCTYPE html><html><head><title>Slackbot by ServerlessBuch.de</title></head>" +
                "<body><h1>ServerlessBuchBot</h1><p>Thanks and enjoy!</p></body></html>";
    }

}
