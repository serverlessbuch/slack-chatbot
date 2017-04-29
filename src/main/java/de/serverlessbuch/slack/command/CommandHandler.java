package de.serverlessbuch.slack.command;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
public class CommandHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse>  {

    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        log.info("Received body: {}", input.getBody());
        Map<String, String> request = parseBody(input.getBody());
        String token = request.get("token");
        String command = request.get("command");
        String text = request.get("text");
        log.info("Received slash command: {} {}", command, text);

        AwsProxyResponse response;
        if (!System.getenv("SLACK_VERIFICATION_TOKEN").equals(token)) {
            response = new AwsProxyResponse(403);
        } else {
            String body;
            switch (command) {
                case "/hello":
                    body = HelloCommand.processCommand(request);
                    break;
                case "/calc":
                    body = CalculatorCommand.processCommand(text);
                    break;
                default:
                    body = "Sorry, couldn't understand your request " + command + " " + text;
            }
            Map<String, String> headers = Collections.singletonMap("Content-Type", "text/plain");
            response = new AwsProxyResponse(200, headers, body);
        }

        return response;
    }

    private Map<String, String> parseBody(String body) {
        return Arrays.stream(body.split("&")).map(p -> p.split("=")).collect(Collectors.toMap(s -> s[0], s -> {
            try {
                return s.length > 1 ? URLDecoder.decode(s[1], "UTF-8") : "";
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }));
    }

}
