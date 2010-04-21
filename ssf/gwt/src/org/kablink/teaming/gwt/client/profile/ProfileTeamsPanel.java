package org.kablink.teaming.gwt.client.profile;

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class ProfileTeamsPanel extends ProfileSectionPanel  {

	private final String IDBASE = "myTeams_";

	public ProfileTeamsPanel(ProfileRequestInfo profileRequestInfo, String title) {
		
		super(profileRequestInfo, title);
		
		setStyleName("tracking-subhead");
	
		getTeams();
	}

	private void addContentWidget(Widget w) {
		add(w);
	}
	
	private void getTeams() {
		
		rpcService.getTeams(profileRequestInfo.getBinderId(), new AsyncCallback<List<TeamInfo>>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(List<TeamInfo> mtList)  {
				// Scan the teams...
				int mtCount = 0;
				SideBarAnchor sbA;
				for (Iterator<TeamInfo> mtIT = mtList.iterator(); mtIT.hasNext(); ) {
					// ...creating an item structure for each.
					TeamInfo mt = mtIT.next();
					String mtId = (IDBASE + mt.getBinderId());
					
					sbA = new SideBarAnchor(mtId, mt.getTitle(), mt.getEntityPath(), new TeamClickHandler(mt));
					addContentWidget(sbA);
					mtCount += 1;
				}
				
				// If there weren't any teams...
				if (0 == mtCount) {
					// ...put something in the menu that tells the user
					// ...that.
					Label content = new Label(messages.mainMenuMyTeamsNoTeams());
					//content.addStyle():
					addContentWidget(content);
				}
			}
			
		});
		
	}
	
	
	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class TeamClickHandler implements ClickHandler {
		private TeamInfo m_myTeam;	// The team clicked on.

		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		TeamClickHandler(TeamInfo myTeam) {
			// Simply store the parameter.
			m_myTeam = myTeam;
		}

		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {

			gotoUrl(m_myTeam.getPermalinkUrl(), false);
		}
	}

	/*
	 * This method will be called to goto a permalink URL received as a
	 * parameter.
	 * 
	 */
	private void gotoUrl( Object obj, boolean isPermalink )
	{
		if ( obj instanceof String )
		{
			if (isPermalink)
			     Window.Location.replace( (String) obj );
			else GwtClientHelper.jsLoadUrlInContentFrame( (String) obj );
		}
		else
			Window.alert( "in gotoUrl() and obj is not a String object" );
	}//end gotoUrl()
}
