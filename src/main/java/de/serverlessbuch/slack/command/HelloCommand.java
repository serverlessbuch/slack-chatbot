package de.serverlessbuch.slack.command;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Slf4j
class HelloCommand {

    static String processCommand(Map<String, String> request) {
        String name = request.get("user_name");
        log.info("Say hello to {}", name);
        return "Hello " + name + " @ " + request.get("team_domain") + " in channel " + request.get("channel_name") + " from Lambda!";
    }

}
