package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;


public class ProfileFollowSectionPanel extends ProfileSectionPanel {

	private List<GwtUser> trackedUsers;
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
		for (GwtUser trackedUser: trackedUsers ) {

			// ...creating an item structure for each.
			String id = (IDBASE  + trackedUser.getUserId());
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
			Label content = new Label(messages.profileNotFollowing());
			//content.addStyle():
			addContentWidget(content, true);
			
			return;
		}
	}
	
	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class TrackedPersonClickHandler implements ClickHandler {
		private GwtUser trackedUser;	// The team clicked on.
	
		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		TrackedPersonClickHandler(GwtUser t) {
			// Simply store the parameter.
			trackedUser = t;
		}
	
		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			
			if(GwtClientHelper.hasString(trackedUser.getUserId())) {
				String url = GwtClientHelper.appendUrlParam( trackedUser.getViewWorkspaceUrl(), "operation", "showProfile" );
				OnSelectBinderInfo osbInfo = new OnSelectBinderInfo( trackedUser.getWorkspaceId(), url, false, Instigator.OTHER );
				actionTrigger.triggerAction(TeamingAction.SELECTION_CHANGED, osbInfo );
			}

//			if(GwtClientHelper.hasString(trackedUser.getUserId())) {
//				final String userId = trackedUser.getUserId();
//				GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
//			
//				rpcService.getUserPermalink( userId, new AsyncCallback<String>() {
//					public void onFailure( Throwable t ) {
//						Window.alert( t.toString() );
//					}//end onFailure()
//					
//					public void onSuccess( String url ) {
//						OnSelectBinderInfo osbInfo;
//						url = GwtClientHelper.appendUrlParam( url, "operation", "showProfile" );
//						osbInfo = new OnSelectBinderInfo( trackedUser.getWorkspaceId(), url, false, Instigator.OTHER );
//						actionTrigger.triggerAction(TeamingAction.SELECTION_CHANGED, osbInfo );
//					}// end onSuccess()
//				});// end AsyncCallback()
//			} 
		}
	}
	
}
