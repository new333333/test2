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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.lpe.CustomJspConfig;
import org.kablink.teaming.gwt.client.lpe.CustomJspProperties;
import org.kablink.teaming.gwt.client.rpc.shared.ExecuteLandingPageCustomJspCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Widget that renders a custom JSP in a landing page.
 * 
 * @author jwootton@novell.com
 */
public class CustomJspWidget extends VibeWidget {
	private CustomJspProperties	m_properties;
	private String				m_lpBinderId;	// Landing page binder ID.
	private VibeFlowPanel		m_mainPanel;

	/**
	 * Constructor method.
	 * 
	 * This widget simply displays the name of the jsp file that is
	 * associated with the view type.
	 * 
	 * @param config
	 * @param lpBinderId
	 */
	public CustomJspWidget(CustomJspConfig config, String lpBinderId) {
		// Remember the landing page binderId...
		m_lpBinderId = lpBinderId;

		// ...create the main panel for the widget...
		VibeFlowPanel mainPanel = init(config.getProperties(), config.getLandingPageStyle());
		
		// ...and call initWidget(), as all composites must do.
		initWidget(mainPanel);
	}
	
	/*
	 * Asynchronously executes the RPC associated with this enhanced
	 * view using a GWT RPC command.
	 */
	private void executeJspAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				executeJspNow();
			}
		});
	}
	
	/*
	 * Synchronously executes the RPC associated with this enhanced
	 * view using a GWT RPC command.
	 */
	private void executeJspNow() {
		GwtClientHelper.executeCommand(
			new ExecuteLandingPageCustomJspCmd(m_lpBinderId, m_properties.getJspName(), m_properties.createConfigString(), m_properties.getPathType()),
			new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_executeCustomJsp(),
						m_properties.getJspName());
				}
		
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Extract the JSP's HTML from the response...
					String html;
					if (response.getResponseData() != null) {
						StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
						html = responseData.getStringValue();
					}
					else {
						html = "";
					}

					// ...and replace the content of the panel with it.
					m_mainPanel.clear();
					m_mainPanel.add(new HTMLWithJavaScript(html));
				}
			}
		);
	}
	
	/*
	 * Initializes/constructs the JSP widget using the properties and
	 * styles provided.
	 */
	private VibeFlowPanel init(CustomJspProperties properties, String landingPageStyle) {
		// Create a copy of the JSP properties...
		m_properties = new CustomJspProperties();
		m_properties.copy(properties);

		// ...create the widget's main content panel...
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("landingPageWidgetMainPanel" + landingPageStyle);
		m_mainPanel.addStyleName("customJspWidgetMainPanel"   + landingPageStyle);
		
		// ...and set its width and height.
		Style style = m_mainPanel.getElement().getStyle();
		
		// Don't set the width if it is set to 100%.  This causes a
		// scroll bar to appear.
		int  width = m_properties.getWidth();
		Unit unit  = m_properties.getWidthUnits();
		if ((width != 100) || (unit != Unit.PCT)) {
			style.setWidth(width, unit);
		}
		
		// Don't set the height if it is set to 100%.  This causes a
		// scroll bar to appear.
		int height = m_properties.getHeight();
		unit       = m_properties.getHeightUnits();
		if ((height != 100) || (unit != Unit.PCT)) {
			style.setHeight(height, unit);
		}

		// Set when scroll bars are supposed to show.
		style.setOverflow(m_properties.getOverflow());

		// Finally, return the main content panel.
		return m_mainPanel;
	}

	/**
	 * Called when the JSP widget is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the super class process the attach...
		super.onAttach();
		
		// ...and execute the JSP.
		executeJspAsync();
	}
}
