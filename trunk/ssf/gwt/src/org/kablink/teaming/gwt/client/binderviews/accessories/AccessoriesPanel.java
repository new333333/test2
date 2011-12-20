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
import org.kablink.teaming.gwt.client.rpc.shared.BinderAccessoriesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderAccessoriesCmd;
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


/**
 * Class used for the content of the accessories in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class AccessoriesPanel extends ToolPanelBase {
	private VibeFlowPanel	m_fp;	// The panel holding the AccessoryPanel's contents.
	private AccessoryLayout m_dashboardLayout;
	private Map<String,VibeFlowPanel> m_flowPanelMap;
	private String m_binderId;
	private int m_accessoryCount = 0;
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AccessoriesPanel(BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(binderInfo, toolPanelReady);
		
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
		GwtClientHelper.executeCommand(
				new GetBinderAccessoriesCmd(binderId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetBinderAccessories(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the description and continue loading.
				BinderAccessoriesRpcResponseData responseData = ((BinderAccessoriesRpcResponseData) response.getResponseData());
				m_dashboardLayout = responseData.getDashboardLayout();
				loadAccessoryPanelAsync();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadAccessoryPanelAsync() {
		ScheduledCommand constructAccessory = new ScheduledCommand() {
			@Override
			public void execute() {
				loadAccessoryPanelNow();
			}
		};
		Scheduler.get().scheduleDeferred(constructAccessory);
	}

	/*
	 * Synchronously construct's the contents of the description panel.
	 */
	private void loadAccessoryPanelNow() {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("binderId", m_binderId);
		m_flowPanelMap = new HashMap<String,VibeFlowPanel>();
		String componentId = "";
    	List<String> dashboardLayout = m_dashboardLayout.getLayout();
    	if (null != dashboardLayout) {
    		m_accessoryCount = dashboardLayout.size();
	    	for (String cId : dashboardLayout) {
	    		componentId = cId;
	    		model.put("ssComponentId", componentId);
	    		VibeFlowPanel vfp =  new VibeFlowPanel();
	    		m_flowPanelMap.put(componentId, vfp);
	    		m_fp.add(vfp);
				GwtClientHelper.executeCommand(
						new GetJspHtmlCmd(VibeJspHtmlType.ACCESSORY, model),
						new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							m_messages.rpcFailure_GetBinderAccessory(),
							VibeJspHtmlType.ACCESSORY.toString());
					}
					
					@Override
					public void onSuccess(VibeRpcResponse response) {
						// Store the description and continue loading.
						JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
						Map<String,Object> context = responseData.getContext();
						String componentId = (String)context.get("ssComponentId");
						String html = responseData.getHtml();
						VibeFlowPanel vfp = m_flowPanelMap.get(componentId);
						vfp.add(new HTMLPanel(html));
						loadAccessoryAsync();
					}
				});
			}
    	}

    	if (componentId.equals("")) {
    		//There are no accessories to show
			// ...tell who's using it that it's ready to go.
			toolPanelReady();
    	}
	}
		
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadAccessoryAsync() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadAccessoryNow();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	private void loadAccessoryNow() {
		//If this is the last one to be fetched, signal that we are done
		m_accessoryCount--;
		if (m_accessoryCount <= 0) {
			toolPanelReady();
		}
	}


	/**
	 * Loads the AccessoriesPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param binderInfo
	 * @param tpClient
	 */
	public static void createAsync(final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(AccessoriesPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				AccessoriesPanel ap = new AccessoriesPanel(binderInfo, toolPanelReady);
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
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
//!		...this needs to be implemented... 
	}
}
