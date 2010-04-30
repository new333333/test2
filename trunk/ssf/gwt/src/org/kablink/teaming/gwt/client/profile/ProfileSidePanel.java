package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ProfileSidePanel extends Composite {

	private ProfileRequestInfo profileRequestInfo;
	private ProfileSectionPanel aboutMeSection;
	private ProfileSectionPanel teamsSection;
	private ProfileSectionPanel trackingSection;
	private ProfileSectionPanel trackedBy;
	private ProfileSectionPanel savedSearches;
	private FlowPanel rightColumn;
	private ProfileStats statsPanel;

	public ProfileSidePanel(final ProfileRequestInfo profileRequestInfo) {

		this.profileRequestInfo = profileRequestInfo;

		final FlowPanel rightContent = new FlowPanel();
		rightContent.setStyleName("column-r");

		// Add the User's Photo and link
		ProfilePhoto photo = new ProfilePhoto(profileRequestInfo);
		rightContent.add(photo);

		// Add the Content
		rightColumn = new FlowPanel();
		rightColumn.addStyleName("content");
		rightContent.add(rightColumn);

		// All composites must call initWidget() in their constructors.
		initWidget(rightContent);
	}

	public void setCategory(ProfileCategory cat) {

		if (findAttrByName(cat, "profileStats")) {
			// Add the stats div to the upper left of the right column
			statsPanel = new ProfileStats(profileRequestInfo);
			rightColumn.add(statsPanel);
		} else {
			//create empty space 
			statsPanel = new ProfileStats(profileRequestInfo);
			rightColumn.add(statsPanel);
		}

		if (findAttrByName(cat, "profileAboutMe")) {
			aboutMeSection = new ProfileTrackSectionPanel(profileRequestInfo,
					"About Me:");
			rightColumn.add(aboutMeSection);
		}

		if (findAttrByName(cat, "profileTeams")) {
			teamsSection = new ProfileTeamsPanel(profileRequestInfo, "Teams:");
			rightColumn.add(teamsSection);
		}

		if (findAttrByName(cat, "profileFollowers")) {
			trackingSection = new ProfileTrackSectionPanel(profileRequestInfo,
					"Following:");
			rightColumn.add(trackingSection);
		}

		if (findAttrByName(cat, "profileFollowing")) {
			trackedBy = new ProfileTrackSectionPanel(profileRequestInfo,
					"Followers:");
			rightColumn.add(trackedBy);
		}

		if (findAttrByName(cat, "profileSavedSearches")) {
			if (profileRequestInfo.isOwner()) {
				savedSearches = new ProfileSearchesSectionPanel(
						profileRequestInfo, "Saved Searches:");
				rightColumn.add(savedSearches);
			}
		}
	}

	private boolean findAttrByName(ProfileCategory cat, String name) {
		boolean found = false;

		for (ProfileAttribute attr : cat.getAttributes()) {
			if (attr.getName().equals(name)) {
				found = true;
				break;
			}
		}

		return found;
	}
}
