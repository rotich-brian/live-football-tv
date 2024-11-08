package com.bottom.footballtv.services;

import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.models.Room.Eventcat;

public interface SelectListener {
    public void onCatItemClick(Eventcat eventcat);
    public void onEventClick(Event event, int linkID);
}
