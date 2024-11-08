package com.bottom.footballtv.models;

import java.io.Serializable;

public class LiveEvent implements Serializable {
    private String EventId;
    private String Stream;
    private String Origin;
    private String Referrer;
    private String User_Agent;

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getStream() {
        return Stream;
    }

    public void setStream(String stream) {
        Stream = stream;
    }

    public String getOrigin() {
        return Origin;
    }

    public void setOrigin(String origin) {
        Origin = origin;
    }

    public String getReferrer() {
        return Referrer;
    }

    public void setReferrer(String referrer) {
        Referrer = referrer;
    }

    public String getUser_Agent() {
        return User_Agent;
    }

    public void setUser_Agent(String user_Agent) {
        User_Agent = user_Agent;
    }
}
