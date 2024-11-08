package com.bottom.footballtv.models.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Event implements Serializable {
    @PrimaryKey
    @NonNull
    private String EventId;
    private String Match;
    private String Category;
    private String Thumbnail;
    private Date eventTime;
    private String Link1;
    private String Link2;
    private String Link3;
    private String Origin1;
    private String Referrer1;
    private String User_Agent1;
    private String Origin2;
    private String Referrer2;
    private String User_Agent2;
    private String Origin3;
    private String Referrer3;
    private String User_Agent3;
    private boolean isTop;

    @NonNull
    public String getEventId() {
        return EventId;
    }

    public void setEventId(@NonNull String eventId) {
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

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
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

    public String getOrigin1() {
        return Origin1;
    }

    public void setOrigin1(String origin1) {
        Origin1 = origin1;
    }

    public String getReferrer1() {
        return Referrer1;
    }

    public void setReferrer1(String referrer1) {
        Referrer1 = referrer1;
    }

    public String getUser_Agent1() {
        return User_Agent1;
    }

    public void setUser_Agent1(String user_Agent1) {
        User_Agent1 = user_Agent1;
    }

    public String getOrigin2() {
        return Origin2;
    }

    public void setOrigin2(String origin2) {
        Origin2 = origin2;
    }

    public String getReferrer2() {
        return Referrer2;
    }

    public void setReferrer2(String referrer2) {
        Referrer2 = referrer2;
    }

    public String getUser_Agent2() {
        return User_Agent2;
    }

    public void setUser_Agent2(String user_Agent2) {
        User_Agent2 = user_Agent2;
    }

    public String getOrigin3() {
        return Origin3;
    }

    public void setOrigin3(String origin3) {
        Origin3 = origin3;
    }

    public String getReferrer3() {
        return Referrer3;
    }

    public void setReferrer3(String referrer3) {
        Referrer3 = referrer3;
    }

    public String getUser_Agent3() {
        return User_Agent3;
    }

    public void setUser_Agent3(String user_Agent3) {
        User_Agent3 = user_Agent3;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }
}
