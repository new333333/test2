/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails.ShareInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.widgets.GroupMembershipPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Class that holds the folder entry viewer sharing manager.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntrySharing extends VibeFlowPanel {
	private FolderEntryCallback				m_fec;				// Callback to the folder entry composite.
	private GwtTeamingDataTableImageBundle	m_images;			//
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private List<ShareInfo>					m_sharedByItems;	// A List<ShareInfo> of shares of the item with the current user.
	private List<ShareInfo>					m_sharedWithItems;	// A List<ShareInfo> of shares of the item the current user has made.

	/**
	 * Constructor method.
	 * 
	 * @param fec
	 * @param sharedByItems
	 * @param sharedWithItems
	 */
	public FolderEntrySharing(FolderEntryCallback fec, List<ShareInfo> sharedByItems, List<ShareInfo> sharedWithItems) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_fec             = fec;
		m_sharedByItems   = sharedByItems;
		m_sharedWithItems = sharedWithItems;
		
		// ...initialize the data members requiring it...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		// ...and construct the sharing manager's content.
		createContent();
	}
	
	/*
	 * Creates the sharing manager's content.
	 */
	private void createContent() {
		// Add the panel's style...
		addStyleName("vibe-feView-sharingPanel");

		// ...if the item has been shared...
		if (GwtClientHelper.hasItems(m_sharedByItems) || GwtClientHelper.hasItems(m_sharedWithItems)) {
			// ...create the display widgets for the
			// ...List<ShareInfo>'s...
			createSharedItemContent(m_sharedByItems,   m_messages.folderEntry_SharedBy());
			createSharedItemContent(m_sharedWithItems, m_messages.folderEntry_SharedWith());
		}
		
		else {
			// ...otherwise, display a simply message that it's not
			// ...shared.
			createEmptyShareDisplay();
		}
		
		// ...and tell the composite that we're ready.
		m_fec.viewComponentReady();
	}

	/*
	 * Displays a simple 'File has not been shared' message.
	 */
	private void createEmptyShareDisplay() {
		// Create a panel for the message...
		VibeFlowPanel shareItemPanel = new VibeFlowPanel();
		shareItemPanel.addStyleName("vibe-feView-shareItemPanel");
		add(shareItemPanel);

		// ...and add the message itself.
		String what = (GwtClientHelper.isLicenseFilr() ? m_messages.folderEntry_File() : m_messages.folderEntry_Item());
		Label info = new Label(m_messages.folderEntry_NoShares(what));
		info.addStyleName("vibe-feView-shareItemEmpty");
		shareItemPanel.add(info);
	}
	
	/*
	 * Creates the content for displaying the content of a
	 * List<ShareInfo>.
	 */
	private void createSharedItemContent(List<ShareInfo> shares, String caption) {
		// If we don't have any shares to display...
		if (!(GwtClientHelper.hasItems(shares))) {
			// ...bail.
			return;
		}

		// Create the section header.
		Label headerLabel = new Label(caption);
		headerLabel.addStyleName("vibe-feView-shareItemHeader");
		add(headerLabel);

		// Scan the ShareInfo's...
		for (ShareInfo si:  shares) {
			// ...creating the display for each.
			createSharedItem(si);
		}
		
	}

	/*
	 * Creates the content for displaying the content of an individual
	 * ShareInfo.
	 */
	private void createSharedItem(ShareInfo share) {
		// Create the panel for the share.
		VibeFlowPanel shareItemPanel = new VibeFlowPanel();
		shareItemPanel.addStyleName("vibe-feView-shareItemPanel");
		add(shareItemPanel);
		
		// Extract the hover text for this assignment.
		final AssignmentInfo ai       = share.getUser();
		String	             hover    = ai.getHover();
		boolean              hasHover = GwtClientHelper.hasString(hover);

		// What type of assignee is this?
		switch (ai.getAssigneeType()) {
		case INDIVIDUAL: {
			// An individual user!  Generate their presence control...
			GwtPresenceInfo presence = ai.getPresence();
			final PresenceControl presenceControl = new PresenceControl(String.valueOf(ai.getPresenceUserWSId()), false, false, false, presence);
			presenceControl.setImageAlignment("top");
			presenceControl.addStyleName("vibe-feView-shareItemAvatar cursorPointer");
			presenceControl.setAnchorStyleName("cursorPointer");
			presenceControl.setImageOverride(getPresenceImage(ai));
			presenceControl.addImageStyleName("vibe-feView-shareItemAvatar");
			shareItemPanel.add(presenceControl);
			presenceControl.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					invokeSimpleProfile(ai, presenceControl.getElement());
				}
			});

			// ...and label.
			final Label presenceLabel = new Label(ai.getTitle());
			presenceLabel.addStyleName("vibe-feView-shareItemName cursorPointer");
			if (hasHover) {
				presenceLabel.setTitle(hover);
			}
			shareItemPanel.add(presenceLabel);
			presenceLabel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					invokeSimpleProfile(ai, presenceLabel.getElement());
				}
			});
			
			break;
		}
			
		case GROUP:
		case TEAM: {
			// A group or team!  Generate its image...
			final Image assigneeImg = GwtClientHelper.buildImage(GwtClientHelper.getRequestInfo().getImagesPath() + ai.getPresenceDude());
			assigneeImg.addStyleName("vibe-feView-shareItemAvatar");
			shareItemPanel.add(assigneeImg);
			boolean isGroup = ai.getAssigneeType().isGroup();
			if (isGroup) {
				assigneeImg.addStyleName("cursorPointer");
				assigneeImg.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						invokeGroupMembership(ai, assigneeImg.getElement());
					}
				});
			}

			// ...and label.
			int    members       = ai.getMembers();
			String membersString = GwtTeaming.getMessages().vibeDataTable_MemberCount(String.valueOf(members));
			String assigneeLabel = (ai.getTitle() + " " + membersString);
			final Label assignee = new Label(assigneeLabel);
			assignee.addStyleName("vibe-feView-shareItemName");
			if (hasHover) {
				assignee.setTitle(hover);
			}
			shareItemPanel.add(assignee);
			if (isGroup) {
				assignee.addStyleName("cursorPointer");
				assignee.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						invokeGroupMembership(ai, assignee.getElement());
					}
				});
			}
			
			break;
		}
			
		case PUBLIC: {
			// A public share!  Generate the appropriate image...
			Image assigneeImg = GwtClientHelper.buildImage(m_images.groupAssignee().getSafeUri().asString());
			assigneeImg.addStyleName("vibe-feView-shareItemAvatar");
			shareItemPanel.add(assigneeImg);

			// ...and label.
			Label assignee = new Label(ai.getTitle());
			assignee.addStyleName("vibe-feView-shareItemName");
			if (hasHover) {
				assignee.setTitle(hover);
			}
			shareItemPanel.add(assignee);
			
			break;
		}
		}

		// Add information about when the share was created.
		Label info = new Label(m_messages.folderEntry_SharedOn(share.getShareDate()));
		info.addStyleName("vibe-feView-shareItemInfo");
		shareItemPanel.add(info);

		// If the share expires...
		String expDate = share.getExpiresDate();
		if (GwtClientHelper.hasString(expDate)) {
			// ...and information about when.
			info = new Label(m_messages.folderEntry_ShareExpires(expDate));
			info.addStyleName("vibe-feView-shareItemInfo");
			shareItemPanel.add(info);
		}

		// Add information about the rights granted with the share.
		String right;
		ShareRights rights = share.getRights();
		switch (rights.getAccessRights()) {
		default:
		case VIEWER:       right = m_messages.folderEntry_ShareRight_Viewer();      break;
		case CONTRIBUTOR:  right = m_messages.folderEntry_ShareRight_Contributor(); break;
		case EDITOR:       right = m_messages.folderEntry_ShareRight_Editor();      break;
		}
		info = new Label(m_messages.folderEntry_ShareRights(right));
		info.addStyleName("vibe-feView-shareItemInfo");
		shareItemPanel.add(info);

		// If the share rights can be forwarded...
		if (rights.getCanShareForward()) {
			// ...add information about how.
			StringBuffer sb = new StringBuffer();
			if (rights.getCanShareWithInternalUsers()) {
				sb.append(m_messages.folderEntry_ShareReshare_Internal());
			}
			if (rights.getCanShareWithExternalUsers()) {
				if (0 < sb.length()) {
					sb.append(", ");
				}
				sb.append(m_messages.folderEntry_ShareReshare_External());
			}
			if (rights.getCanShareWithPublic()) {
				if (0 < sb.length()) {
					sb.append(", ");
				}
				sb.append(m_messages.folderEntry_ShareReshare_Public());
			}
			info = new Label(m_messages.folderEntry_ShareReshares(sb.toString()));
			info.addStyleName("vibe-feView-shareItemInfo");
			shareItemPanel.add(info);
		}

		// If the share has a comment...
		String comment = share.getComment();
		if (GwtClientHelper.hasString(comment)) {
			// ...add it to the share's display.
			info = new Label(comment);
			info.addStyleName("vibe-feView-shareItemNote");
			shareItemPanel.add(info);
		}
	}
	
	/*
	 * Returns the URL to the image to display for presence for the
	 * cell.
	 */
	private String getPresenceImage(AssignmentInfo ai) {
		String reply = ai.getAvatarUrl();
		if (!(GwtClientHelper.hasString(reply))) {
			reply = m_images.userPhoto().getSafeUri().asString();
		}
		return reply;
	}
	
	/*
	 * Called to invoke the group membership popup on the principal.
	 */
	private void invokeGroupMembership(AssignmentInfo ai, Element pElement) {
		GroupMembershipPopup gmp = ((GroupMembershipPopup) ai.getMembershipPopup());
		if (null == gmp) {
			gmp = new GroupMembershipPopup(
				true,	// true  -> Auto hide.
				false,	// false -> Not modal.
				ai.getTitle(),
				String.valueOf(ai.getId()));
			ai.setMembershipPopup(gmp);
		}
		gmp.showRelativeTo(GwtClientHelper.getUIObjectFromElement(pElement));
	}
	
	/*
	 * Called to invoke the simple profile dialog on the principal's
	 * presence.
	 */
	private void invokeSimpleProfile(AssignmentInfo ai, Element pElement) {
		Long wsId = ai.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtClientHelper.invokeSimpleProfile(pElement, wsIdS, ai.getTitle());
	}
	
	/**
	 * Shows/hides the sharing information.
	 * 
	 * @param show
	 */
	public void setSharingVisible(boolean show) {
//!		...this needs to be implemented...
	}
}
