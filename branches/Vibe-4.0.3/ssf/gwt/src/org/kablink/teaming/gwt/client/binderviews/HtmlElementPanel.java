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
import java.util.Map;

import com.google.gwt.user.client.Element;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetHtmlElementInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.HtmlElementInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.HtmlElementInfoRpcResponseData.HtmlElementInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderViewHtmlBlock;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kablink.teaming.gwt.client.widgets.VibeHtmlPanel;

/**
 * Class used for the content of an HTML element the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class HtmlElementPanel extends ToolPanelBase {
	private boolean							m_executeJavaScripOnAttach;	//
	private boolean							m_panelAttached;			//
	private BinderInfo						m_binderInfo;				// The binder this panel is running on. 
	private GwtTeamingMessages				m_messages;					// Access to Vibe's localized message resources.
	private HtmlElementInfoRpcResponseData	m_htmlElementInfo;			// The HtmlElementInfoRpcResponseData for this binder's HTML elements, once they've been queried.
	private VibeFlowPanel					m_fp;						// The panel holding the HtmlElementPanel's contents.
	private BinderViewHtmlBlock m_htmlEntry;

	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private HtmlElementPanel(RequiresResize containerResizer, BinderInfo binderInfo, BinderViewHtmlBlock htmlEntry, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...store the parameters...
		m_binderInfo = binderInfo;
		m_htmlEntry = htmlEntry;
		
		// ...initialize the data members...
		m_messages = GwtTeaming.getMessages();
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-htmlElementPanel");
		initWidget(m_fp);
		if (htmlEntry==null) {
			loadPart1Async();
		} else {
			populatePanelFromBinderViewHtml(htmlEntry);
		}
	}

	/**
	 * Loads the HtmlElementPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final BinderViewHtmlBlock htmlEntry, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(HtmlElementPanel.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				HtmlElementPanel fp = new HtmlElementPanel(containerResizer, binderInfo, htmlEntry, toolPanelReady);
				tpClient.onSuccess(fp);
			}

			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_HtmlElementPanel());
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
		GwtClientHelper.jsExecuteJavaScript(m_fp.getElement(), true);
		GwtClientHelper.jsOnLoadInit();
	}
	
	/*
	 * Asynchronously construct's the contents of the HTML element
	 * panel.
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
	 * Synchronously construct's the contents of the HTML element
	 * panel.
	 */
	private void loadPart1Now() {
		// Request the HTML element information for the binder from the
		// server...
		GwtClientHelper.executeCommand(
				new GetHtmlElementInfoCmd(m_binderInfo),
				new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
								t,
								m_messages.rpcFailure_GetHtmlElementInfo(),
								m_binderInfo.getBinderIdAsLong());
					}

					@Override
					public void onSuccess(VibeRpcResponse response) {
						// ...and use it to populate the panel.
						m_htmlElementInfo = ((HtmlElementInfoRpcResponseData) response.getResponseData());
						populatePanelFromDataAsync();
					}
				});
	}

	/**
	 * Called when the HTML element panel is attached to the document.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach() {
		// Let the widget attach.
		super.onAttach();
		m_panelAttached = true;
		
		// Do we need to execute the HTML element's JavaScript on the
		// attach?
		if (m_executeJavaScripOnAttach) {
			// Yes!  Execute it.
			m_executeJavaScripOnAttach = false;
			executeJavaScriptAsync();
		}
	}
	
	/**
	 * Called when the HTML element panel is detached from the document.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach.
		super.onDetach();
		m_panelAttached = false;
	}
	
	/*
	 * Asynchronously populates the panel from the
	 * HtmlElementInfoRpcResponseData.
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
	 * HtmlElementInfoRpcResponseData.
	 */
	private void populatePanelFromDataNow() {
		// Do we have any HtmlElementInfo's to render?
		List<HtmlElementInfo> htmlElements = m_htmlElementInfo.getHtmlElementInfoList();
		if (GwtClientHelper.hasItems(htmlElements)) {
			// Yes!  Scan them.
			boolean hasHtml = false;
			int     count   = 0;
			for (HtmlElementInfo htmlElement:  htmlElements) {
				// Create a panel to hold this HTML element's HTML.
				VibeFlowPanel htmlElementPanel = buildHtmlPanel(htmlElement);
				if (htmlElementPanel!=null) {
					count += 1;
					if (1 < count) {
						htmlElementPanel.addStyleName("padding10T");
					}
					m_fp.add(htmlElementPanel);
					hasHtml = true;
				}
			}

			// Did we add any HTMLPanel's to the view?
			if (hasHtml) {
				// Yes!  Make sure any JavaScript inside them gets
				// executed.
				if (m_panelAttached)
				     executeJavaScriptAsync();
				else m_executeJavaScripOnAttach = true;
			}
		}
		
		// Everything's constructed and this panel is now ready.
		toolPanelReady();
	}

	private void populatePanelFromBinderViewHtml(BinderViewHtmlBlock htmlEntry) {
		HtmlElementInfo elementInfo = new HtmlElementInfo(htmlEntry.getTag(), htmlEntry.getAttributes(), htmlEntry.getInnerHtml());
		VibeFlowPanel htmlElementPanel = buildHtmlPanel(elementInfo);
		if (htmlElementPanel!=null) {
			m_fp.add(htmlElementPanel);
			if (m_panelAttached)
				executeJavaScriptAsync();
			else m_executeJavaScripOnAttach = true;
		}
	}

	private VibeFlowPanel buildHtmlPanel(HtmlElementInfo htmlElement) {
		// Does the HTML element contain HTML from a custom
		// JSP?
		VibeHtmlPanel htmlPanel = null;
		String html = htmlElement.getCustomJspHtml();
		if (GwtClientHelper.hasString(html)) {
			// Yes!  Create an HTML panel to render it.
			htmlPanel = new VibeHtmlPanel(html);
		}

		else {
			// No, the HTML doesn't contain HTML from a custom
			// JSP!  Does it contain any top or bottom HTML
			String top    = htmlElement.getHtmlTop();    boolean hasTop    = GwtClientHelper.hasString(top);
			String bottom = htmlElement.getHtmlBottom(); boolean hasBottom = GwtClientHelper.hasString(bottom);
			if (hasTop || hasBottom) {
				if (hasTop)
					html = top;
				else html = "";
				if (hasBottom) {
					html += bottom;	// Do we need to do anything between top and bottom?
				}
				String tagName = htmlElement.getRootTagName();
				if (tagName!=null) {
					htmlPanel = new VibeHtmlPanel(tagName, html);
					htmlPanel.setRootAttributes(htmlElement.getRootAttributes());
				} else {
					htmlPanel = new VibeHtmlPanel(html);
				}
			}
		}

		VibeFlowPanel htmlElementPanel = null;
		// Do we have an HTMLPanel to add to the view?
		if (null != htmlPanel) {
			// Yes!  Add it.
			htmlElementPanel = new VibeFlowPanel();
			htmlElementPanel.addStyleName("vibe-htmlElementPerElementPanel");
			htmlElementPanel.add(htmlPanel);
			return htmlElementPanel;
		}
		return htmlElementPanel;
	}
	
	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the HTML element.
		m_fp.clear();
		if (m_htmlEntry==null) {
			loadPart1Async();
		} else {
			populatePanelFromBinderViewHtml(m_htmlEntry);
		}
	}
}
