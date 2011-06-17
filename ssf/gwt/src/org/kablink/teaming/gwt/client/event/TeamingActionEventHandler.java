package org.kablink.teaming.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface TeamingActionEventHandler extends EventHandler {
	void onTeamingAction(TeamingActionEvent event);
}
