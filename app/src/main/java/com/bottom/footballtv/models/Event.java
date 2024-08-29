package com.bottom.footballtv.models;

public class Event {
    private String EventId;
    private String Match;
    private String Category;
    private String Thumbnail;
    private String Link1;
    private String Link2;
    private String Link3;
    private String Origin;
    private String Referrer;
    private String User_Agent;

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getMatch() {
        return Match;
    }

    public void setMatch(String match) {
        Match = match;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getLink1() {
        return Link1;
    }

    public void setLink1(String link1) {
        Link1 = link1;
    }

    public String getLink2() {
        return Link2;
    }

    public void setLink2(String link2) {
        Link2 = link2;
    }

    public String getLink3() {
        return Link3;
    }

    public void setLink3(String link3) {
        Link3 = link3;
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


    @Override
    public String toString() {
        return "Event{" +
                "Match='" + Match + '\'' +
                '}';
    }
}
