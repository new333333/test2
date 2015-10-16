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
package org.kablink.teaming.gwt.client.binderviews.accessories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.HideAccessoriesEvent;
import org.kablink.teaming.gwt.client.event.JspLayoutChangedEvent;
import org.kablink.teaming.gwt.client.event.ResetEntryMenuEvent;
import org.kablink.teaming.gwt.client.event.ShowAccessoriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetAccessoryStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetJspHtmlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveAccessoryStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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
		HideAccessoriesEvent.Handler,
		JspLayoutChangedEvent.Handler,
		ShowAccessoriesEvent.Handler
{
	private boolean						m_executeJavaScripOnAttach;	//
	private boolean						m_panelAttached;			//
	private boolean						m_notifyOnReady;			// true -> Notify the container when this panel is ready.  false -> Don't.
	private HTMLPanel					m_htmlPanel;				//
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeFlowPanel				m_fp;						// The panel holding the AccessoryPanel's contents.
	private String						m_binderId;					//
	private String						m_html;						//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.HIDE_ACCESSORIES,
		TeamingEvents.JSP_LAYOUT_CHANGED,
		TeamingEvents.SHOW_ACCESSORIES,
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
		
		// ...initialize any other data members...
		m_notifyOnReady = true;
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-accessoriesPanel");

		initWidget(m_fp);
		loadPart1Async();
	}

	/*
	 * Asynchronously loads the next part of the accessories panel.
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
	 * Synchronously loads the next part of the accessories panel.
	 */
	private void loadPart1Now() {
		// What kind of binder are the accessories being shown on?
		boolean isFolder      = m_binderInfo.isBinderFolder();
		boolean isWorkspace   = ((!isFolder) && m_binderInfo.isBinderWorkspace());
		boolean isProfileRoot = (isWorkspace && m_binderInfo.getWorkspaceType().isProfileRoot());
		boolean isTeamRoot    = (isWorkspace && m_binderInfo.getWorkspaceType().isTeamRoot()   );
		boolean isGlobalRoot  = (isWorkspace && m_binderInfo.getWorkspaceType().isGlobalRoot() );
		
		// For non-folder, non-profile root, non-team root, non-global
		// root binders...
		if ((!isFolder) && (!isProfileRoot) && (!isTeamRoot) && (!isGlobalRoot)) {
			// ...we always show the accessories panel when included in
			// ...the view.
			showAccessoryPanel();
			loadPart2Async();
			return;
		}
		
		GwtClientHelper.executeCommand(
				new GetAccessoryStatusCmd(m_binderInfo.getBinderIdAsLong()),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetAccessoryStatus());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Is the accessory panel supposed to be visible?
				BooleanRpcResponseData responseData = ((BooleanRpcResponseData) response.getResponseData());
				if (responseData.getBooleanValue()) {
					// Yes!  Ensure it's visible and continue loading
					// it.
					showAccessoryPanel();
					loadPart2Async();
				}
				
				else {
					// No, the accessory panel is not supposed to be
					// visible!  Hide it and tell the panel container
					// that we're ready...
					m_fp.clear();
					hideAccessoryPanel();
					
					// ...and if we need to...
					if (m_notifyOnReady) {
						// ...tell our container that we're ready.
						toolPanelReady();
						m_notifyOnReady = false;
					}
				}
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the accessories panel.
	 */
	private void loadPart2Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the accessories panel.
	 */
	private void loadPart2Now() {
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
					m_messages.rpcFailure_GetJspHtml(),
					VibeJspHtmlType.ACCESSORY_PANEL.toString());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the accessory panel HTML and continue loading.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				m_html = responseData.getHtml();
				loadPart3Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the accessories panel.
	 */
	private void loadPart3Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the accessories panel.
	 */
	private void loadPart3Now() {
		// Do we have any HTML for the accessories?
		if (GwtClientHelper.hasString(m_html)) {
			// Yes!  Embed it in an HTML panel in this accessories
			// panel...
			m_htmlPanel = new HTMLPanel(m_html);
			m_fp.add(m_htmlPanel);
			
			// ...and make sure any JavaScript inside it gets executed.
			if (m_panelAttached)
			     executeJavaScriptAsync();
			else m_executeJavaScripOnAttach = true;
		}
		
		// ...and if we need to...
		if (m_notifyOnReady) {
			// ...tell our container that we're ready.
			toolPanelReady();
			m_notifyOnReady = false;
		}
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

	/*
	 * Asynchronously executes the JavaScript in the HTML panel.
	 */
	private void executeJavaScriptAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				executeJavaScriptNow();
			}
		});
	}
	
	/*
	 * Asynchronously executes the JavaScript in the HTML panel.
	 */
	private void executeJavaScriptNow() {
		GwtClientHelper.jsExecuteJavaScript(m_htmlPanel.getElement(), true);
		GwtClientHelper.jsOnLoadInit();
	}
	
	/*
	 * Asynchronously causes the the accessories panel to be hidden.
	 */
	private void hideAccessoriesAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				hideAccessoriesNow();
			}
		});
	}
	
	/*
	 * Synchronously causes the the accessories panel to be hidden.
	 */
	private void hideAccessoriesNow() {
		final Long binderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new SaveAccessoryStatusCmd(binderId, false),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveAccessoryStatus());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Hide the accessories...
				m_fp.clear();
				hideAccessoryPanel();
				
				// ...and reset the entry menu to reflect the change.
				GwtTeaming.fireEvent(new ResetEntryMenuEvent(binderId));
			}
		});
	}
	
	/*
	 * Ensures that the accessory panel is note visible.
	 */
	private void hideAccessoryPanel() {
		m_fp.addStyleName("displayNone2");
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
		m_panelAttached = true;
		
		// Do we need to execute the accessories JavaScript on the
		// attach?
		if (m_executeJavaScripOnAttach) {
			// Yes!  Execute it.
			m_executeJavaScripOnAttach = false;
			executeJavaScriptAsync();
		}
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
		m_panelAttached = false;
	}
	
	/**
	 * Handles HideAccessoriesEvent's received by this class.
	 * 
	 * Implements the HideAccessoriesEvent.Handler.onHideAccessories()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onHideAccessories(HideAccessoriesEvent event) {
		Long binderId = event.getBinderId();
		if (binderId.equals(m_binderInfo.getBinderIdAsLong())) {
			hideAccessoriesAsync();
		}
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
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					panelResized();
				}
			});
		}
	}
	
	/**
	 * Handles ShowAccessoriesEvent's received by this class.
	 * 
	 * Implements the ShowAccessoriesEvent.Handler.onShowAccessories()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowAccessories(ShowAccessoriesEvent event) {
		Long binderId = event.getBinderId();
		if (binderId.equals(m_binderInfo.getBinderIdAsLong())) {
			showAccessoriesAsync();
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
	 * Asynchronously causes the the accessories panel to be shown.
	 */
	private void showAccessoriesAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showAccessoriesNow();
			}
		});
	}
	
	/*
	 * Synchronously causes the the accessories panel to be shown.
	 */
	private void showAccessoriesNow() {
		final Long binderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new SaveAccessoryStatusCmd(binderId, true),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveAccessoryStatus());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes!  Clear and reload the panel...
				m_fp.clear();
				showAccessoryPanel();
				loadPart2Async();
				
				// ...and reset the entry menu to reflect the change.
				GwtTeaming.fireEvent(new ResetEntryMenuEvent(binderId));
			}
		});
	}

	/*
	 * Ensures that the accessory panel is visible.
	 */
	private void showAccessoryPanel() {
		m_fp.removeStyleName("displayNone2");
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
