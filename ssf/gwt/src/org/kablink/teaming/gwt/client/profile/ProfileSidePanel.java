package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ProfileSidePanel extends Composite {

	ProfileRequestInfo profileRequestInfo;
	
	public ProfileSidePanel(final ProfileRequestInfo profileRequestInfo) {
		
		this.profileRequestInfo = profileRequestInfo;
		
		final FlowPanel rightContent = new FlowPanel();
		rightContent.setStyleName("column-r");
		
		//Add the User's Photo and link
		ProfilePhoto photo = new ProfilePhoto(profileRequestInfo);
		rightContent.add(photo);
		
		//Add the Content 
		final FlowPanel rightColumn = new FlowPanel();
		rightColumn.addStyleName("content");
		rightContent.add(rightColumn);
		
		//Add the stats div to the upper left of the right column
		ProfileStats statsPanel = new ProfileStats(profileRequestInfo);
		rightColumn.add(statsPanel);
		
		{
			Timer timer;
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					createSections(profileRequestInfo, rightColumn);
				}// end run()
			};
			
			timer.schedule( 25 );
		}
		
		
		// All composites must call initWidget() in their constructors.
		initWidget( rightContent );
	}
	
	private void createSections(ProfileRequestInfo profileRequestInfo, final FlowPanel mainPanel) {
			
		ProfileSectionPanel aboutMeSection = new ProfileTrackSectionPanel(profileRequestInfo, "");
		mainPanel.add(aboutMeSection);

		ProfileSectionPanel teamsSection = new ProfileTeamsPanel(profileRequestInfo, "Teams:");
		mainPanel.add(teamsSection);
		
		ProfileSectionPanel trackingSection = new ProfileTrackSectionPanel(profileRequestInfo, "Following:");
		mainPanel.add(trackingSection);

		ProfileSectionPanel trackedBy = new ProfileTrackSectionPanel(profileRequestInfo, "Followers:");
		mainPanel.add(trackedBy);

		if(profileRequestInfo.isOwner()) {
			ProfileSectionPanel searchesSection = new ProfileTrackSectionPanel(profileRequestInfo, "Saved Searches:");
			mainPanel.add(searchesSection);
		}
	}


}
