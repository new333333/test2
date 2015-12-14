/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.rpc.shared.FolderEntryDetailsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntryDetailsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails.ShareInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.widgets.GroupMembershipPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class that holds the folder entry viewer sharing manager.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntrySharing extends VibeFlowPanel
	implements
		// Event handlers implemented by this class.
		ContentChangedEvent.Handler
{
	private FolderEntryCallback				m_fec;						// Callback to the folder entry composite.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's data table images.
	private GwtTeamingFilrImageBundle		m_filrImages;				// Access to Filr's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private List<ShareInfo>					m_sharedByItems;			// A List<ShareInfo> of shares of the item with the current user.
	private List<ShareInfo>					m_sharedWithItems;			// A List<ShareInfo> of shares of the item the current user has made.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.CONTENT_CHANGED,
	};
	
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
		m_images     = GwtTeaming.getDataTableImageBundle();
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_messages   = GwtTeaming.getMessages();
		
		// ...and construct the sharing manager's content.
		createContentAsync(true);	// true -> Notify the composite that we're ready.
	}
	
	/*
	 * Asynchronously creates the sharing manager's content.
	 */
	private void createContentAsync(final boolean notifyViewReady) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				createContentNow(notifyViewReady);
			}
		});
	}
	
	/*
	 * Synchronously creates the sharing manager's content.
	 */
	private void createContentNow(boolean notifyViewReady) {
		// Add the panel's style...
		addStyleName("vibe-feView-sharingPanel");

		// ...if the item has been shared...
		if (GwtClientHelper.hasItems(m_sharedByItems) || GwtClientHelper.hasItems(m_sharedWithItems)) {
			// ...create the display widgets for the
			// ...List<ShareInfo>'s...
			createSharedItemContent(m_sharedByItems,   m_messages.folderEntry_SharedBy(),   false                                             );
			createSharedItemContent(m_sharedWithItems, m_messages.folderEntry_SharedWith(), m_fec.getEntityRights().getShareRight().canShare());
		}
		
		else {
			// ...otherwise, display a simple message that it's not
			// ...shared.
			createEmptyShareDisplay();
		}

		// ...and if we were requested to do so...
		if (notifyViewReady) {
			// ...tell the composite that we're ready.
			m_fec.viewComponentReady();
		}
	}

	/*
	 * Displays a simple 'File has not been shared' message.
	 */
	private void createEmptyShareDisplay() {
		// Create a panel for the message...
		VibeFlowPanel shareItemPanel = new VibeFlowPanel();
		shareItemPanel.addStyleName("vibe-feView-shareItemPanel");
		add(shareItemPanel);

		// ...and add the message itself to the panel.
		String what = (GwtClientHelper.isLicenseFilr() ? m_messages.folderEntry_File() : m_messages.folderEntry_Entry());
		Label info = new Label(m_messages.folderEntry_NoShares(what));
		info.addStyleName("vibe-feView-shareItemEmpty");
		shareItemPanel.add(info);
	}
	
	/*
	 * Creates the widgets for displaying the content of a
	 * List<ShareInfo>.
	 */
	private void createSharedItemContent(List<ShareInfo> shares, String caption, boolean makeItemsShareLinks) {
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
			createSharedItem(si, makeItemsShareLinks);
		}
		
	}

	/*
	 * Creates the widgets for displaying the content of an individual
	 * ShareInfo.
	 */
	private void createSharedItem(ShareInfo share, boolean makeItemShareLink) {
		// Create the panel for the share.
		VibeFlowPanel shareItemPanel = new VibeFlowPanel();
		shareItemPanel.addStyleName("vibe-feView-shareItemPanel");
		if (makeItemShareLink) {
			shareItemPanel.addStyleName("cursorPointer");
			shareItemPanel.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					EntityId eid = m_fec.getEntityId();
					GwtTeaming.fireEventAsync(
						new ShareSelectedEntitiesEvent(
							eid.getBinderId(),
							eid));
				}
			},
			ClickEvent.getType());
		}
		add(shareItemPanel);
		
		// Extract the hover text for this share's assignment.
		final AssignmentInfo ai       = share.getUser();
		String	             hover    = ai.getHover();
		boolean              hasHover = GwtClientHelper.hasString(hover);

		// What type of assignee is this?
		switch (ai.getAssigneeType()) {
		case INDIVIDUAL: {
			// An individual user!  Generate their presence control...
			GwtPresenceInfo presence = ai.getPresence();
			final PresenceControl presenceControl = new PresenceControl(String.valueOf(ai.getId()), String.valueOf(ai.getPresenceUserWSId()), false, false, false, presence);
			presenceControl.setImageAlignment("top");
			presenceControl.addStyleName("vibe-feView-shareItemAvatar cursorPointer");
			presenceControl.setAnchorStyleName("cursorPointer");
			setPresenceAvatarUrl(ai, presenceControl);
			presenceControl.addImageStyleName("vibe-feView-shareItemAvatar");
			shareItemPanel.add(presenceControl);
			presenceControl.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					invokeSimpleProfile(ai, presenceControl.getElement());
					event.stopPropagation();
					event.preventDefault();
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
					event.stopPropagation();
					event.preventDefault();
				}
			});
			
			break;
		}
			
		case GROUP:
		case TEAM: {
			// A group or team!  Generate the appropriate image...
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
						event.stopPropagation();
						event.preventDefault();
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
						event.stopPropagation();
						event.preventDefault();
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
		
		case PUBLIC_LINK: {
			// A public link share!  Generate the appropriate image...
			Image assigneeImg = GwtClientHelper.buildImage(m_images.publicLinkAssignee().getSafeUri().asString());
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

		// Add information about the rights granted by the share.
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
			// ...add information about how they can be forwarded.
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
			// ...add it to the display.
			info = new Label(comment);
			info.addStyleName("vibe-feView-shareItemNote");
			shareItemPanel.add(info);
		}
	}
	
	/*
	 * Called to invoke the group membership popup on an
	 * AssignmentInfo.
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
	 * Called to invoke the simple profile dialog on the
	 * AssignmentInfo.
	 */
	private void invokeSimpleProfile(AssignmentInfo ai, Element pElement) {
		Long wsId = ai.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtClientHelper.invokeSimpleProfile(pElement, String.valueOf(ai.getId()), wsIdS, ai.getTitle());
	}
	
	/**
	 * Called when the share panel is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Handles ContentChangedEvent's received by this class.
	 * 
	 * Implements the ContentChangedEvent.Handler.onContentChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContentChanged(final ContentChangedEvent event) {
		// If the content that changed is sharing...
		if (Change.SHARING.equals( event.getChange())) {
			// ...force the share list to reload.
			refreshShareListAsync();
		}
	}
	
	/**
	 * Called when the share panel is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}

	
	/*
	 * Asynchronously refreshes the share list.
	 */
	private void refreshShareListAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				refreshShareListNow();
			}
		});
	}
	
	/*
	 * Synchronously refreshes the share list.
	 */
	private void refreshShareListNow() {
		// Read the folder entry details for the updated share lists.
		GetFolderEntryDetailsCmd cmd = new GetFolderEntryDetailsCmd(m_fec.getEntityId(), false);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetFolderEntryDetails());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the share lists from the details...
				FolderEntryDetailsRpcResponseData responseData = ((FolderEntryDetailsRpcResponseData) response.getResponseData());
				FolderEntryDetails fed = responseData.getFolderEntryDetails();
				m_sharedByItems   = fed.getSharedByItems();
				m_sharedWithItems = fed.getSharedWithItems();

				// ...and recreate the panel's content.
				clear();
				createContentAsync(false);	// false -> Don't notify the composite that we're ready.
			}
		});
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Sets the URL for a presence control to the image to display for
	 * presence for an AssignmentInfo.
	 */
	private void setPresenceAvatarUrl(final AssignmentInfo ai, final PresenceControl pc) {
		// If the AssignmentInfo has an avatar URL...
		String avatarUrl = ai.getAvatarUrl();
		if (GwtClientHelper.hasString(avatarUrl)) {
			// ...just use it.
			pc.setImageOverride(avatarUrl);
			return;
		}

		// If the assignee isn't an individual user...
		if (!(ai.getAssigneeType().isIndividual())) {
			// ...just use the default.
			pc.setImageOverride(m_images.userPhoto().getSafeUri().asString());
			return;
		}

		// Otherwise, use an appropriate avatar for the user type.
		ImageResource ir;
		if (ai.isUserExternal())
		     ir = m_filrImages.filrExternalUser48();
		else ir = m_images.userPhoto();
		pc.setImageOverride(ir.getSafeUri().asString());
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
