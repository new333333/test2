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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserListInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UserListInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserListInfoRpcResponseData.UserListInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Class used for the content of the user list in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class UserListPanel extends ToolPanelBase {
	private BinderInfo						m_folderInfo;	// The folder this panel is running on. 
	private GwtTeamingDataTableImageBundle	m_images;		// Access to Vibe's data table image bundle.
	private GwtTeamingMessages				m_messages;		// Access to Vibe's localized message resources.
	private UserListInfoRpcResponseData		m_userListInfo;	// The UserListInfoRpcResponseData for this folder's user lists, once they've been queried.
	private VibeFlowPanel					m_fp;			// The panel holding the UserListPanel's contents.

	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private UserListPanel(RequiresResize containerResizer, BinderInfo folderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, folderInfo, toolPanelReady);
		
		// ...store the parameters...
		m_folderInfo = folderInfo;
		
		// ...initialize the data members...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-userListPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/*
	 * Handle a PresenceControl being clicked.
	 */
	private void clickOnPresenceControl(PrincipalInfo pi, PresenceControl presenceControl) {
		Element piElement = presenceControl.getElement();
		invokeSimpleProfileDlg(pi, piElement);
	}
	
	/**
	 * Loads the UserListPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param folderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo folderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(UserListPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				UserListPanel fp = new UserListPanel(containerResizer, folderInfo, toolPanelReady);
				tpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_UserListPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Returns the URL to the image to display for presence for the
	 * cell.
	 */
	private String getPresenceImage(PrincipalInfo pi) {
		String reply = pi.getAvatarUrl();
		if (!(GwtClientHelper.hasString(reply))) {
			reply = m_images.userPhoto().getSafeUri().asString();
		}
		return reply;
	}
	
	/*
	 * Called to invoke the simple profile dialog on the principal's
	 * presence.
	 */
	private void invokeSimpleProfileDlg(PrincipalInfo pi, Element pElement) {
		Long wsId = pi.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtClientHelper.invokeSimpleProfile(pElement, String.valueOf(pi.getId()), wsIdS, pi.getTitle());
	}
	
	/*
	 * Asynchronously construct's the contents of the user list panel.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously construct's the contents of the user list panel.
	 */
	private void loadPart1Now() {
		// Request the user list information for the folder from the
		// server...
		GwtClientHelper.executeCommand(
				new GetUserListInfoCmd(m_folderInfo),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetUserListInfo(),
					m_folderInfo.getBinderIdAsLong());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and use it to populate the panel.
				m_userListInfo = ((UserListInfoRpcResponseData) response.getResponseData());
				populatePanelFromDataAsync();
			}
		});
	}

	/*
	 * Asynchronously populates the panel from the
	 * UserListInfoRpcResponseData.
	 */
	private void populatePanelFromDataAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populatePanelFromDataNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the panel from the
	 * UserListInfoRpcResponseData.
	 */
	private void populatePanelFromDataNow() {
		// If we have any UserListInfo's to render...
		List<UserListInfo> users = m_userListInfo.getUserListInfoList();
		if (GwtClientHelper.hasItems(users)) {
			// ...scan...
			int count = 0;
			for (UserListInfo user:  users) {
				// ...and render them.
				count += 1;
				VibeFlowPanel userListPanel = new VibeFlowPanel();
				userListPanel.addStyleName("vibe-userListPerUserPanel");
				if (1 < count) {
					userListPanel.addStyleName("padding10T");
				}
				m_fp.add(userListPanel);
				
				InlineLabel userListLabel = new InlineLabel(user.getCaption());
				userListLabel.addStyleName("vibe-userListPerUserLabel");
				userListPanel.add(userListLabel);
				
				List<PrincipalInfo> piList = user.getUsers();
				if (GwtClientHelper.hasItems(piList)) {
					for (PrincipalInfo pi:  piList) {
						VibeFlowPanel piPanel = new VibeFlowPanel();
						piPanel.addStyleName("vibe-userListPerUserPrincipalPanel");
						userListPanel.add(piPanel);
						renderPrincipalInfo(piPanel, pi);
					}
				}
			}
		}
		
		toolPanelReady();
	}
	
	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the user list.
		m_fp.clear();
		loadPart1Async();
	}
	
	/*
	 * Renders a PrincipalInfo object into a VibeFlowPanel.
	 */
	private void renderPrincipalInfo(VibeFlowPanel piPanel, final PrincipalInfo pi) {
		final GwtPresenceInfo presence = pi.getPresence();
		final PresenceControl presenceControl = new PresenceControl(String.valueOf(pi.getId()), String.valueOf(pi.getPresenceUserWSId()), false, false, false, presence);
		presenceControl.setImageAlignment("top");
		presenceControl.addStyleName("vibe-userListPerUserPresence-control displayInline verticalAlignTop");
		presenceControl.setAnchorStyleName("cursorPointer");
		presenceControl.setImageOverride(getPresenceImage(pi));
		presenceControl.addImageStyleName("vibe-userListPerUserPresence-image");
		presenceControl.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickOnPresenceControl(pi, presenceControl);
			}
		});
		piPanel.add(presenceControl);
		
		// ...add a name link for it...
		Label presenceLabel = new Label(pi.getTitle());
		presenceLabel.addStyleName("vibe-userListPerUserPresence-label");
		presenceLabel.addStyleName(pi.isUserDisabled() ? "vibe-userListPerUserPresence-disabled" : "vibe-userListPerUserPresence-enabled");
		if ((!(pi.isUserWSInTrash())) && (!(pi.isUserDisabled()))) {
			// ...unless the user's workspace is in the trash or the
			// ...user is disabled...
			presenceLabel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					clickOnPresenceControl(pi, presenceControl);
				}
			});
		}
		String hover = pi.getEmailAddress();
		if (GwtClientHelper.hasString(hover)) {
			presenceLabel.setTitle(hover);
		}
		piPanel.add(presenceLabel);
	}
}
