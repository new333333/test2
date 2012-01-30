/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.AvatarInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderOwnerAvatarInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Class used for displaying the avatar of a binder's owner in a tool
 * panel.  
 * 
 * @author drfoster@novell.com
 */
public class BinderOwnerAvatarPanel extends ToolPanelBase {
	private AvatarInfoRpcResponseData	m_binderOwnerAvatar;	// Once read from the server, holds information about the binder owner's avatar.
	private VibeFlowPanel				m_fp;					// The panel holding the content.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private BinderOwnerAvatarPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-binderOwnerAvatarPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the BinderOwnerAvatarPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(BinderOwnerAvatarPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				BinderOwnerAvatarPanel bcp = new BinderOwnerAvatarPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(bcp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_BinderOwnerAvatarPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Asynchronously loads the URL for the binder owner's avatar.
	 */
	private void loadPart1Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the URL for the binder owner's avatar.
	 */
	private void loadPart1Now() {
		GetBinderOwnerAvatarInfoCmd cmd = new GetBinderOwnerAvatarInfoCmd(m_binderInfo.getBinderIdAsLong());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetBinderOwnerAvatarUrl();
				GwtClientHelper.handleGwtRPCFailure(caught, error);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_binderOwnerAvatar = ((AvatarInfoRpcResponseData) result.getResponseData());
				renderBinderOwnerAvatarAsync();
			}			
		});
	}

	/*
	 * Asynchronously renders the binder owner avatar panel.
	 */
	private void renderBinderOwnerAvatarAsync() {
		ScheduledCommand doRender = new ScheduledCommand() {
			@Override
			public void execute() {
				renderBinderOwnerAvatarNow();
			}
		};
		Scheduler.get().scheduleDeferred(doRender);
	}
	
	/*
	 * Synchronously renders the binder owner avatar panel.
	 */
	private void renderBinderOwnerAvatarNow() {
		// If we don't have the avatar information...
		if (null == m_binderOwnerAvatar) {
			// ...we don't display anything.  Bail.
			return;
		}

		// Pull the URL and title from the avatar information...
		String url   = m_binderOwnerAvatar.getUrl();   boolean hasUrl   = GwtClientHelper.hasString(url  );
		String title = m_binderOwnerAvatar.getTitle(); boolean hasTitle = GwtClientHelper.hasString(title);

		// ...and add an Image to the panel for it.
		Image img;
		if (hasUrl) {
		    img = new Image(url);
		}
		else {
			// Note:  We use the setUrl(getSafeUri) approach here so
			// that the width setting applied by the style actually
			// works.
			img = new Image();
			img.setUrl(GwtTeaming.getDataTableImageBundle().userPhoto().getSafeUri());
		}
		img.addStyleName("vibe-binderOwnerAvatarImage");
		if (hasTitle) {
			img.setTitle(title);
		}
		m_fp.add(img);
		
		// Finally, tell our container that we're ready.
		toolPanelReady();
	}

	/**
	 * Called from the binder view to allow the panel to do any work
	 * required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Simply render the binder owner avatar (again, if its already
		// been rendered.)
		m_fp.clear();
		renderBinderOwnerAvatarAsync();
	}
}
