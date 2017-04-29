package de.serverlessbuch.slack.event;

import lombok.Data;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
class Event {
    private String type;
    private String user;
    private String text;
    private String ts;
    private String channel;
    private String eventTs;

    static Event of(Map<String, String> map) {
        Event e = new Event();
        e.setType(map.get("type"));
        e.setUser(map.get("user"));
        e.setText(map.get("text"));
        e.setTs(map.get("ts"));
        e.setChannel(map.get("channel"));
        e.setEventTs(map.get("event_ts"));
        return e;
    }
}
