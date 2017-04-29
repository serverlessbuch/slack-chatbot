package de.serverlessbuch.slack.event;

import lombok.Data;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
class Event {
    private String type;
    private String subtype;
    private String user;
    private String username;
    private String botId;
    private String text;
    private String ts;
    private String channel;
    private String eventTs;

    static Event of(Map<String, String> map) {
        Event e = new Event();
        e.setType(map.get("type"));
        e.setSubtype(map.get("subtype"));
        e.setUser(map.get("user"));
        e.setUsername(map.get("username"));
        e.setBotId(map.get("bot_id"));
        e.setText(map.get("text"));
        e.setTs(map.get("ts"));
        e.setChannel(map.get("channel"));
        e.setEventTs(map.get("event_ts"));
        return e;
    }
}
