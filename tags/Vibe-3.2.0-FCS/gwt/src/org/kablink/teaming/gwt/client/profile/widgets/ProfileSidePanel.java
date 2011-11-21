/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.rpc.shared.GetProfileStatsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class ProfileSidePanel extends Composite {

	private ProfileRequestInfo profileRequestInfo;
	private ProfileSectionPanel aboutMeSection;
	private ProfileSectionPanel teamsSection;
	private ProfileSectionPanel groupsSection;
	private ProfileSectionPanel followingSection;
	private ProfileSectionPanel savedSearches;
	private FlowPanel content;
	private ProfileStatsPanel statsPanel;
	private ProfileStats profileStats;
	private FlowPanel topContent;

	public ProfileSidePanel(final ProfileRequestInfo profileRequestInfo) {

		this.profileRequestInfo = profileRequestInfo;

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
		
		ProfileAttribute aboutMeAttr = findAttrByDataName(cat, "aboutMe");
		if (aboutMeAttr != null) {
			aboutMeSection = new ProfileFollowSectionPanel(profileRequestInfo, GwtTeaming.getMessages().profileAboutMe());
			aboutMeSection.setStyleName("aboutHeading");
			aboutMeSection.addStyleName("smalltext");
			aboutMeSection.getHeadingLabel().setStyleName("aboutLabel");
			topContent.add(aboutMeSection);
			
			if (aboutMeAttr != null) {
				if (aboutMeAttr.getValue() != null) {
					HTML aboutMeLabel = new HTML((String)aboutMeAttr.getValue());
					aboutMeLabel.setStyleName("aboutDesc");
					aboutMeSection.add(aboutMeLabel);
				}
			}
		}

		if (attrExist(cat, "profileTeams")) {
			teamsSection = new ProfileTeamsPanel(profileRequestInfo, GwtTeaming.getMessages().profileTeams());
			content.add(teamsSection);
		}

		if (attrExist(cat, "profileGroups")) {
			groupsSection = new ProfileGroupsPanel(profileRequestInfo, GwtTeaming.getMessages().profileGroups());
			content.add(groupsSection);
		}

		if (attrExist(cat, "profileFollowing")) {
			followingSection = new ProfileFollowSectionPanel(profileRequestInfo,
					GwtTeaming.getMessages().profileFollowing());
			content.add(followingSection);
		}

//		if (attrExist(cat, "profileFollowers")) {
//			trackedBy = new ProfileTrackSectionPanel(profileRequestInfo,
//					"Followers");
//			rightColumn.add(trackedBy);
//		}

		if (attrExist(cat, "profileSavedSearches")) {
			if (profileRequestInfo.isOwner()) {
				savedSearches = new ProfileSearchesSectionPanel(
						profileRequestInfo, GwtTeaming.getMessages().profileSavedSearches());
				content.add(savedSearches);
			}
		}
		
		//Populate the sidebar widgets
		fillProfileStats();
	}
	
	private void fillProfileStats() {

		// create an async callback to handle the result of the request to get
		// the state:
		AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>() {

			public void onFailure(Throwable t) {
				// display error
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetProfileStats(),
					profileRequestInfo.getBinderId());
			}

			public void onSuccess( VibeRpcResponse response ) {
				profileStats = (ProfileStats) response.getResponseData();

				if(statsPanel != null) {
					statsPanel.addStats(profileStats);
				}

				if(followingSection != null) {
					((ProfileFollowSectionPanel) followingSection).addtrackedPersons(profileStats);
				}
			}
		};

		if(profileStats == null) {
			GetProfileStatsCmd cmd;
			
			cmd = new GetProfileStatsCmd( profileRequestInfo.getBinderId(), profileRequestInfo.getUserId() );
			GwtClientHelper.executeCommand( cmd, callback );
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
	
	private ProfileAttribute findAttrByDataName(ProfileCategory cat, String dataName) {
		for (ProfileAttribute attr : cat.getAttributes()) {
			if (attr.getDataName().equals(dataName)) {
				return attr;
			}
		}
		return null;
	}

	public void updateQuota(String usedQuota) {
		statsPanel.updateQuota(usedQuota);
	}
}
