package org.kablink.teaming.gwt.client;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class TeamingEventBus extends SimpleEventBus {
	public boolean isEventHandled(Event.Type<?> eventKey){
		return super.isEventHandled(eventKey);
	}
}
