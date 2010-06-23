package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;

public class ProfileSidePanel extends Composite {

	private ProfileRequestInfo profileRequestInfo;
	private ProfileSectionPanel aboutMeSection;
	private ProfileSectionPanel teamsSection;
	private ProfileSectionPanel followingSection;
	private ProfileSectionPanel savedSearches;
	private FlowPanel content;
	private ProfileStatsPanel statsPanel;
	private ActionTrigger actionTrigger;
	private ProfileStats profileStats;
	private FlowPanel topContent;

	public ProfileSidePanel(final ProfileRequestInfo profileRequestInfo, ActionTrigger trigger) {

		this.profileRequestInfo = profileRequestInfo;
		actionTrigger = trigger;

		final FlowPanel columnr = new FlowPanel();
		columnr.setStyleName("column-r");

		// Add the top Content panel
		topContent = new FlowPanel();
		topContent.addStyleName("s-topContent");
		columnr.add(topContent);
		
		// Add the Content
		content = new FlowPanel();
		content.addStyleName("content");
		columnr.add(content);
		
		// Add the User's Photo and link
		ProfilePhoto photo = new ProfilePhoto(profileRequestInfo);
		topContent.add(photo);
		
		// All composites must call initWidget() in their constructors.
		initWidget(columnr);
	}

	private void createMessageDiv() {
		if(profileRequestInfo.isQuotasEnabled() && profileRequestInfo.isOwner()) {
			FlowPanel msgDiv = null; 
			InlineLabel msgLabel = null;

			if(profileRequestInfo.isDiskQuotaExceeded()){
				msgDiv = new FlowPanel();
				msgLabel = new InlineLabel("Data quota exceeded!");
			} else if (profileRequestInfo.isDiskQuotaHighWaterMarkExceeded() && !profileRequestInfo.isDiskQuotaExceeded()) {
				msgDiv = new FlowPanel();
				msgLabel = new InlineLabel("Data quota almost exceeded!");
			}

			if(msgDiv != null && msgLabel != null) {
				msgDiv.addStyleName("stats_error_msg");
				msgDiv.add(msgLabel);
				topContent.add(msgDiv);
			}
		}
	}

	public void setCategory(ProfileCategory cat) {

		if (attrExist(cat, "profileStats")) {
			// Add the stats div to the upper left of the right column
			statsPanel = new ProfileStatsPanel(profileRequestInfo);
			topContent.add(statsPanel);
		} else {
			//create empty space 
			statsPanel = new ProfileStatsPanel(profileRequestInfo);
			topContent.add(statsPanel);
		}
		
		//Add error Div
		createMessageDiv();

		if (attrExist(cat, "profileAboutMe")) {
			
			aboutMeSection = new ProfileFollowSectionPanel(profileRequestInfo, "About Me", actionTrigger);
			aboutMeSection.setStyleName("aboutHeading");
			aboutMeSection.addStyleName("smalltext");
			aboutMeSection.getHeadingLabel().setStyleName("aboutLabel");
			topContent.add(aboutMeSection);
			
			ProfileAttribute attr = findAttrByName(cat, "profileAboutMe");
			if(attr != null) {
				if(attr.getValue() != null) {
					String aboutMeText = (String) attr.getValue();
					HTML aboutMeLabel = new HTML(aboutMeText);
					aboutMeLabel.setStyleName("aboutDesc");
					aboutMeSection.add(aboutMeLabel);
				}
			}
		}

		if (attrExist(cat, "profileTeams")) {
			teamsSection = new ProfileTeamsPanel(profileRequestInfo, "Teams", actionTrigger);
			content.add(teamsSection);
		}

		if (attrExist(cat, "profileFollowing")) {
			followingSection = new ProfileFollowSectionPanel(profileRequestInfo,
					"Following", actionTrigger);
			content.add(followingSection);
		}

//		if (attrExist(cat, "profileFollowers")) {
//			trackedBy = new ProfileTrackSectionPanel(profileRequestInfo,
//					"Followers", actionTrigger);
//			rightColumn.add(trackedBy);
//		}

		if (attrExist(cat, "profileSavedSearches")) {
			if (profileRequestInfo.isOwner()) {
				savedSearches = new ProfileSearchesSectionPanel(
						profileRequestInfo, "Saved Searches",actionTrigger);
				content.add(savedSearches);
			}
		}
		
		//Populate the sidebar widgets
		fillProfileStats();
	}
	
	private void fillProfileStats() {

		// create an async callback to handle the result of the request to get
		// the state:
		AsyncCallback<ProfileStats> callback = new AsyncCallback<ProfileStats>() {

			public void onFailure(Throwable t) {
				// display error
				Window.alert("Error: " + t.getMessage());
			}

			public void onSuccess(ProfileStats stats) {
				profileStats = stats;

				if(statsPanel != null) {
					statsPanel.addStats(profileStats);
				}

				if(followingSection != null) {
					((ProfileFollowSectionPanel) followingSection).addtrackedPersons(profileStats);
				}
			}
		};

		if(profileStats == null) {
			GwtRpcServiceAsync gwtRpcService = (GwtRpcServiceAsync) GWT.create(GwtRpcService.class);
			gwtRpcService.getProfileStats(new HttpRequestInfo(), profileRequestInfo.getBinderId(), callback);
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
