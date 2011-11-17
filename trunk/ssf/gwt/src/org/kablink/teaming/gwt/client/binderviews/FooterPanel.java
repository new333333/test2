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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetFooterToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * Class used for the content of the footer panel in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class FooterPanel extends ToolPanelBase {
	private List<ToolbarItem>	m_toolbarIems;	//
	private ToolbarItem			m_footerTBI;	//
	private VibeFlowPanel		m_fp;			// The panel holding the FooterPanel's contents.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FooterPanel(BinderInfo binderInfo) {
		// Initialize the super class...
		super(binderInfo);
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-footerPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the FooterPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param binderInfo
	 * @param tpClient
	 */
	public static void createAsync(final BinderInfo binderInfo, final ToolPanelClient tpClient) {
		GWT.runAsync(FooterPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				FooterPanel fp = new FooterPanel(binderInfo);
				tpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FooterPanel());
				tpClient.onUnavailable();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the footer panel.
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
		final Long folderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetFooterToolbarItemsCmd(folderId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFooterToolbarItems(),
					folderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the toolbar items and continue loading.
				GetToolbarItemsRpcResponseData responseData = ((GetToolbarItemsRpcResponseData) response.getResponseData());
				m_toolbarIems = responseData.getToolbarItems();
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
	 * Synchronously construct's the contents of the footer panel.
	 */
	private void loadPart2Now() {
		// If we don't have a footer toolbar item...
		m_footerTBI = ToolbarItem.getNestedToolbarItem(m_toolbarIems, "ssFooterToolbar");
		if (null == m_footerTBI) {
			// ...we don't display anything in the footer.
			return;
		}
		
		// If we the footer toolbar item doesn't contain a permalink
		// item...
		ToolbarItem permalinkTBI = m_footerTBI.getNestedToolbarItem("permalink");
		if (null == permalinkTBI) {
			// ...we don't display anything in the footer.
			return;
		}

		// Create an Anchor for the foot link...
		final Anchor a = new Anchor();
		a.addStyleName("vibe-footerAnchor");
		ToolbarItem iCalTBI = m_footerTBI.getNestedToolbarItem("iCalendar");
		String ilString = ((null == iCalTBI) ? "" : (iCalTBI.getTitle() + ", "));
		ilString += permalinkTBI.getTitle();
		InlineLabel il = new InlineLabel(ilString);
		il.addStyleName("vibe-footerAnchorLabel");
		a.getElement().appendChild(il.getElement());
		m_fp.add(a);

		// ...and add the handlers to it.
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				renderPermalinksAsync();
			}
		});
		a.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				a.removeStyleName("vibe-footerAnchor-hover");
			}
		});
		a.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				a.addStyleName("vibe-footerAnchor-hover");
			}
		});
	}

	/*
	 * Asynchronously creates and renders the permalink information.
	 */
	private void renderPermalinksAsync() {
		ScheduledCommand renderPermalinks = new ScheduledCommand() {
			@Override
			public void execute() {
				renderPermalinksNow();
			}
		};
		Scheduler.get().scheduleDeferred(renderPermalinks);
	}
	
	/*
	 * Synchronously creates and renders the permalink information.
	 */
	private void renderPermalinksNow() {
//!		...this needs to be implemented...
		Window.alert("FooterPanel.renderPermalinksNow():  ...this needs to be implemented...");
	}
	
	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the footer.
		m_fp.clear();
		loadPart1Async();
	}
}
