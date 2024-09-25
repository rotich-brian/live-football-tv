package com.bottom.footballtv.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AppData implements Serializable {
    private String version_name;
    private String version;
    private String message;
    private String update_url;
    private String telegram_user;
    private boolean show_later;
    private boolean from_telegram;
    private boolean from_playStore;

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public String getTelegram_user() {
        return telegram_user;
    }

    public void setTelegram_user(String telegram_user) {
        this.telegram_user = telegram_user;
    }

    public boolean isShow_later() {
        return show_later;
    }

    public void setShow_later(boolean show_later) {
        this.show_later = show_later;
    }

    public boolean isFrom_telegram() {
        return from_telegram;
    }

    public void setFrom_telegram(boolean from_telegram) {
        this.from_telegram = from_telegram;
    }

    public boolean isFrom_playStore() {
        return from_playStore;
    }

    public void setFrom_playStore(boolean from_playStore) {
        this.from_playStore = from_playStore;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppData{" +
                "version_name='" + version_name;
    }
}
