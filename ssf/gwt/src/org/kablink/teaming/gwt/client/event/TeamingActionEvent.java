package org.kablink.teaming.gwt.client.event;

import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.shared.GwtEvent;

public class TeamingActionEvent extends GwtEvent<TeamingActionEventHandler> {

	public static Type<TeamingActionEventHandler> TYPE = new Type<TeamingActionEventHandler>();

	private TeamingAction m_action;

	/**
	 * The TeamingActionEvent used to fire off a number of actions in Teaming
	 * 
	 * @param obj - This can be null, but some actions are expecting an obj
	 * @param action - The action to perform 
	 */
	public TeamingActionEvent(TeamingAction action, Object source) {
		this.m_action = action;
		setSource(source);
	}
	
	/**
	 * Get the action associated with this event
	 * @return
	 */
	public TeamingAction getAction() {
		return m_action;
	}
	
	@Override
	public Type<TeamingActionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TeamingActionEventHandler handler) {
		handler.onTeamingAction(this);
	}
}
