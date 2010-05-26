package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class ProfileSidePanel extends Composite {

	private ProfileRequestInfo profileRequestInfo;
	private ProfileSectionPanel aboutMeSection;
	private ProfileSectionPanel teamsSection;
	private ProfileSectionPanel trackingSection;
	private ProfileSectionPanel trackedBy;
	private ProfileSectionPanel savedSearches;
	private FlowPanel rightColumn;
	private ProfileStatsPanel statsPanel;
	private FlowPanel photoPanel;

	public ProfileSidePanel(final ProfileRequestInfo profileRequestInfo) {

		this.profileRequestInfo = profileRequestInfo;

		final FlowPanel rightContent = new FlowPanel();
		rightContent.setStyleName("column-r");

		photoPanel = new FlowPanel();
		photoPanel.addStyleName("userPhotoStats");
		rightContent.add(photoPanel);
		
		// Add the User's Photo and link
		ProfilePhoto photo = new ProfilePhoto(profileRequestInfo);
		photoPanel.add(photo);

		// Add the Content
		rightColumn = new FlowPanel();
		rightColumn.addStyleName("content");
		rightContent.add(rightColumn);

		
		//Add error Div
		createMessageDiv();
		
		// All composites must call initWidget() in their constructors.
		initWidget(rightContent);
	}

	private void createMessageDiv() {
		FlowPanel msgDiv = null; 
		InlineLabel msgLabel = null;
		
		if(profileRequestInfo.isQuotasEnabled() && profileRequestInfo.isOwner()) {
			if(profileRequestInfo.isDiskQuotaExceeded()){
				msgDiv = new FlowPanel();
				msgLabel = new InlineLabel("Data quota exceeded!");
			} else if (profileRequestInfo.isDiskQuotaHighWaterMarkExceeded() && !profileRequestInfo.isDiskQuotaExceeded()) {
				msgDiv = new FlowPanel();
				msgLabel = new InlineLabel("Data quota almost exceeded!");
			}
		}
		
		if(msgLabel != null){
			msgDiv.addStyleName("stats_error_msg");
			msgDiv.add(msgLabel);
			photoPanel.add(msgDiv);
		}
	}

	public void setCategory(ProfileCategory cat) {

		if (attrExist(cat, "profileStats")) {
			// Add the stats div to the upper left of the right column
			statsPanel = new ProfileStatsPanel(profileRequestInfo);
			photoPanel.add(statsPanel);
		} else {
			//create empty space 
			statsPanel = new ProfileStatsPanel(profileRequestInfo);
			rightColumn.add(statsPanel);
		}

		if (attrExist(cat, "profileAboutMe")) {
			
			aboutMeSection = new ProfileTrackSectionPanel(profileRequestInfo,
					"About Me:");
			aboutMeSection.addStyleName("aboutHeading");
			aboutMeSection.addStyleName("smalltext");
			aboutMeSection.getHeadingLabel().setStyleName("aboutLabel");
			rightColumn.add(aboutMeSection);
			
			String aboutMeText = (String)findAttrByName(cat, "profileAboutMe").getValue();
			InlineLabel aboutMeLabel = new InlineLabel(aboutMeText);
			aboutMeLabel.setStyleName("aboutDesc");
			
			aboutMeSection.add(aboutMeLabel);
		}

		if (attrExist(cat, "profileTeams")) {
			teamsSection = new ProfileTeamsPanel(profileRequestInfo, "Teams:");
			rightColumn.add(teamsSection);
		}

		if (attrExist(cat, "profileFollowers")) {
			trackingSection = new ProfileTrackSectionPanel(profileRequestInfo,
					"Following:");
			rightColumn.add(trackingSection);
		}

		if (attrExist(cat, "profileFollowing")) {
			trackedBy = new ProfileTrackSectionPanel(profileRequestInfo,
					"Followers:");
			rightColumn.add(trackedBy);
		}

		if (attrExist(cat, "profileSavedSearches")) {
			if (profileRequestInfo.isOwner()) {
				savedSearches = new ProfileSearchesSectionPanel(
						profileRequestInfo, "Saved Searches:");
				rightColumn.add(savedSearches);
			}
		}
	}

	private boolean attrExist(ProfileCategory cat, String name) {
		if(findAttrByName(cat, name) != null )
			return true;
		return false;
	}
	
	private ProfileAttribute findAttrByName(ProfileCategory cat, String name) {
		for (ProfileAttribute attr : cat.getAttributes()) {
			if (attr.getName().equals(name)) {
				return attr;
			}
		}
		return null;
	}
}
