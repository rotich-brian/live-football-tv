package com.bottom.footballtv.tools;

import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.models.Eventcat;

public interface SelectListener {
    public void onCatItemClick(Eventcat eventcat);
    public void onEventClick(Event event);
}
