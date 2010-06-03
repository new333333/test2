package org.kablink.teaming.gwt.client.profile;

import java.util.List;

import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;


public class ProfileFollowSectionPanel extends ProfileSectionPanel {

	private List<TrackedUser> trackedUsers;
	private String IDBASE = "TrackedId";

	public ProfileFollowSectionPanel(ProfileRequestInfo profileRequestInfo, String title, ActionTrigger trigger) {
		
		super(profileRequestInfo, title, trigger);
		
		setStyleName("tracking-subhead");
	}
	
	public void addtrackedPersons(ProfileStats stats) {

		trackedUsers = stats.getTrackedUsers();
		buildTrackedLinks();
	}
	
	private void buildTrackedLinks(){
		
		if(selectedMore) {
			clearWidgets();
		}
		
		SideBarAnchor sbA;

		int count = 0;
		// ...scan the saved searches...
		for (TrackedUser trackedUser: trackedUsers ) {

			// ...creating an item structure for each.
			String id = (IDBASE  + trackedUser.getId());
			sbA = new SideBarAnchor(id, trackedUser.getTitle(), trackedUser.getTitle(), new TrackedPersonClickHandler(trackedUser));
			
			boolean visible = true;
			if(count > 3){
				visible = false;
				showExpandButton();
			}
			
			addContentWidget(sbA, visible);
			count++;
		}
		
		// If there weren't any teams...
		if (0 == count) {
			// ...put something in the menu that tells the user
			// ...that.
			Label content = new Label(messages.mainMenuSearchOptionsNoSavedSearches());
			//content.addStyle():
			addContentWidget(content, true);
			
			return;
		}
	}
	
	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class TrackedPersonClickHandler implements ClickHandler {
		private TrackedUser trackedUser;	// The team clicked on.
	
		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		TrackedPersonClickHandler(TrackedUser t) {
			// Simply store the parameter.
			trackedUser = t;
		}
	
		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
	
			if(GwtClientHelper.hasString(trackedUser.getId())) {
				//String url = "";
				//actionTrigger.triggerAction(TeamingAction.GOTO_PERMALINK_URL, url);
			} 
		}
	}
	
}
