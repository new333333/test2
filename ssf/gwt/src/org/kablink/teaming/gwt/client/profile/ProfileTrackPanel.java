package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ProfileTrackPanel extends Composite {

	ProfileRequestInfo profileRequestInfo;
	
	public ProfileTrackPanel(final ProfileRequestInfo profileRequestInfo) {
		
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
			
			timer.schedule( 250 );
		}
		
		
		// All composites must call initWidget() in their constructors.
		initWidget( rightContent );
	}
	
	private void createSections(ProfileRequestInfo profileRequestInfo, final FlowPanel mainPanel) {
			
		ProfileTrackSectionPanel aboutMeSection = new ProfileTrackSectionPanel(profileRequestInfo, "");
		mainPanel.add(aboutMeSection);

		ProfileTrackSectionPanel teamsSection = new ProfileTrackSectionPanel(profileRequestInfo, "Teams:");
		mainPanel.add(teamsSection);

		ProfileTrackSectionPanel trackingSection = new ProfileTrackSectionPanel(profileRequestInfo, "Tracking:");
		mainPanel.add(trackingSection);

		ProfileTrackSectionPanel trackedBy = new ProfileTrackSectionPanel(profileRequestInfo, "Tracked By:");
		mainPanel.add(trackedBy);

		ProfileTrackSectionPanel searchesSection = new ProfileTrackSectionPanel(profileRequestInfo, "Saved Searches:");
		mainPanel.add(searchesSection);
	}
}
