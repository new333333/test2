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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.BinderDescriptionRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderDescriptionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;


/**
 * Class used for the content of the description in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class DescriptionPanel extends ToolPanelBase {
	private boolean				m_descriptionIsHTML;	// true -> The content of m_description is HTML.  false -> It's plain text.
	private GwtTeamingMessages	m_messages;				// Access to Vibe's localized message resources.
	private String				m_description;			// The binder's description.
	private VibeFlowPanel		m_fp;					// The panel holding the DescriptionPanel's contents.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DescriptionPanel(BinderInfo binderInfo) {
		// Initialize the super class...
		super(binderInfo);
		
		// ...initialize the data members...
		m_messages = GwtTeaming.getMessages();
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the DescriptionPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param binderInfo
	 * @param tpClient
	 */
	public static void createAsync(final BinderInfo binderInfo, final ToolPanelClient tpClient) {
		GWT.runAsync(DescriptionPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				DescriptionPanel fp = new DescriptionPanel(binderInfo);
				tpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DescriptionPanel());
				tpClient.onUnavailable();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the description
	 * panel.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart1Now() {
		final Long binderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetBinderDescriptionCmd(binderId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetBinderDescription(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the description and continue loading.
				BinderDescriptionRpcResponseData responseData = ((BinderDescriptionRpcResponseData) response.getResponseData());
				m_description       = responseData.getDescription();
				m_descriptionIsHTML = responseData.isDescriptionHTML();
				loadPart2Async();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart2Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the description panel.
	 */
	private void loadPart2Now() {
		// If we have a description...
		if (GwtClientHelper.hasString(m_description)) {
			// ...render it.
			m_fp.addStyleName("vibe-binderViewTools vibe-descriptionPanel");
			
//!			...this needs to be implemented...
		
			Label dl = new Label();
			Element dlE = dl.getElement();
			if (m_descriptionIsHTML) {
				dl.addStyleName("vibe-descriptionHTML");
				dlE.setInnerHTML(m_description);
			}
			else {
				dl.addStyleName("vibe-descriptionText");
				dlE.setInnerText(m_description);
			}
			m_fp.add(dl);
		}
	}

	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the description.
		m_fp.clear();
		m_fp.removeStyleName("vibe-binderViewTools vibe-DescriptionPanel");
		loadPart1Async();
	}
}
