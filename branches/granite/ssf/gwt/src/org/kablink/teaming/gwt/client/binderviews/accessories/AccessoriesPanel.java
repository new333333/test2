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
package org.kablink.teaming.gwt.client.binderviews.accessories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.JspLayoutChangedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetJspHtmlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * Class used for the content of the accessories in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class AccessoriesPanel extends ToolPanelBase
	implements
	// Event handlers implemented by this class.
		JspLayoutChangedEvent.Handler
{
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeFlowPanel				m_fp;						// The panel holding the AccessoryPanel's contents.
	private String						m_binderId;					//
	private String						m_html;						//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.JSP_LAYOUT_CHANGED,
	};

	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AccessoriesPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-accessoriesPanel");

		initWidget(m_fp);
		loadAccessoriesMapAsync();
	}

	/*
	 * Asynchronously construct's the contents of the accessories panel
	 */
	private void loadAccessoriesMapAsync() {
		ScheduledCommand constructAccessories = new ScheduledCommand() {
			@Override
			public void execute() {
				loadAccessoriesMapNow();
			}
		};
		Scheduler.get().scheduleDeferred(constructAccessories);
	}
	
	/*
	 * Synchronously construct's the contents of the accessories panel
	 */
	private void loadAccessoriesMapNow() {
		final Long binderId = m_binderInfo.getBinderIdAsLong();
		m_binderId = String.valueOf(binderId);
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("binderId", m_binderId);
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.ACCESSORY_PANEL, model),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetBinderAccessory(),
					VibeJspHtmlType.ACCESSORY_PANEL.toString());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the accessory panel HTML and continue loading.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				m_html = responseData.getHtml();
				loadAccessoryAsync();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadAccessoryAsync() {
		if (m_html != null && !m_html.equals("")) {
			HTMLPanel hp = new HTMLPanel(m_html);
			m_fp.add(hp);
			
			//Make sure any javascript inside the accessory panel gets executes as needed
			GwtClientHelper.jsExecuteJavaScript( hp.getElement() );
		}
		//signal that we are done
		toolPanelReady();
	}
	
	/**
	 * Loads the AccessoriesPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(AccessoriesPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				AccessoriesPanel ap = new AccessoriesPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(ap);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_AccessoriesPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/**
	 * Called when the accessories panel is attached to the document.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the accessories panel is detached from the document.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles JspLayoutChangedEvent's received by this class.
	 * 
	 * Implements the JspLayoutChangedEvent.Handler.onJspLayoutChanged()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onJspLayoutChanged(JspLayoutChangedEvent event) {
		Long binderId = event.getBinderId();
		if (binderId.equals(m_binderInfo.getBinderIdAsLong())) {
			ScheduledCommand doResize = new ScheduledCommand() {
				@Override
				public void execute() {
					panelResized();
				}
			};
			Scheduler.get().scheduleDeferred(doResize);
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
//!		...this needs to be implemented... 
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
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
