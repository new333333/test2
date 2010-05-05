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
	protected List<TeamInfo> teamList;

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
			public void onSuccess(List<TeamInfo> tList)  {
				
				teamList = tList;
				
				// Scan the teams...
				int teamCount = 0;
				SideBarAnchor sbA;
				for (Iterator<TeamInfo> tIT = tList.iterator(); tIT.hasNext(); ) {
					
					//if 
					if(teamCount == 4) {
						String teamId = (IDBASE + "More");
						sbA = new SideBarAnchor(teamId, "more...", "more...", new TeamClickHandler(new TeamInfo()));
						addContentWidget(sbA);
						return;
					}
					
					// ...creating an item structure for each.
					TeamInfo team = tIT.next();
					String teamId = (IDBASE + team.getBinderId());
					
					sbA = new SideBarAnchor(teamId, team.getTitle(), team.getEntityPath(), new TeamClickHandler(team));
					addContentWidget(sbA);
					teamCount += 1;
				}
				
				// If there weren't any teams...
				if (0 == teamCount) {
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
		private TeamInfo team;	// The team clicked on.

		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		TeamClickHandler(TeamInfo t) {
			// Simply store the parameter.
			team = t;
		}

		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {

			if(team.getPermalinkUrl() != "") {
				gotoUrl(team.getPermalinkUrl(), true);
			} else {
				//is this more than lets expand the 
			   
			}
		}
	}

	private void expand() {
		
		Window.alert("Expand button pressed");
		
//		// Scan the teams...
//		int teamCount = 0;
//		SideBarAnchor sbA;
//		for (Iterator<TeamInfo> tIT = teamList.iterator(); tIT.hasNext(); ) {
//			
//			//if 
//			if(teamCount == 1) {
//				String teamId = (IDBASE + "More");
//				sbA = new SideBarAnchor(teamId, "More", "", new TeamClickHandler(new TeamInfo()));
//				return;
//			}
//			
//			// ...creating an item structure for each.
//			TeamInfo team = tIT.next();
//			String teamId = (IDBASE + team.getBinderId());
//			
//			sbA = new SideBarAnchor(teamId, team.getTitle(), team.getEntityPath(), new TeamClickHandler(team));
//			addContentWidget(sbA);
//			teamCount += 1;
//		}
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
			GwtClientHelper.jsLoadUrlInTopWindow( (String) obj );
			
//			if (isPermalink)
//			     Window.Location.replace( (String) obj );
//			else GwtClientHelper.jsLoadUrlInTopWindow( (String) obj );
		}
		else
			Window.alert( "in gotoUrl() and obj is not a String object" );
	}//end gotoUrl()
}
