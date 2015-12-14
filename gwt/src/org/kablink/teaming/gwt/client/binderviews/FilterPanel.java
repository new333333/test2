/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.rpc.shared.BinderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderFiltersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderFilter;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Class used for the content of the filters in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class FilterPanel extends ToolPanelBase {
	private List<BinderFilter>	m_binderFilters;	//
	private List<String>		m_currentFilters;	//
	private String				m_filterEditUrl;	//
	private String				m_filtersOffUrl;	//
	private VibeFlowPanel		m_fp;				// The panel holding the FilterPanel's contents.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FilterPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-filterPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the FilterPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(FilterPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				FilterPanel fp = new FilterPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FilterPanel());
				tpClient.onUnavailable();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the filter panel.
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
	 * Synchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart1Now() {
		final Long binderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetBinderFiltersCmd(binderId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetBinderFilters(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the filter information and continue loading.
				BinderFiltersRpcResponseData responseData = ((BinderFiltersRpcResponseData) response.getResponseData());
				m_binderFilters  = responseData.getBinderFilters();
				m_filterEditUrl  = responseData.getFilterEditUrl();
				m_filtersOffUrl  = responseData.getFiltersOffUrl();
				m_currentFilters = responseData.getCurrentFilters();
				if (null == m_currentFilters) {
					m_currentFilters = new ArrayList<String>();
				}
				loadPart2Async();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
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
	 * Synchronously construct's the contents of the filter panel.
	 */
	private void loadPart2Now() {
		// Create a grid to hold the filtering information...
		FlexTable filtersGrid = new FlexTable();
		filtersGrid.addStyleName("vibe-filtersGrid");
		m_fp.add(filtersGrid);
		filtersGrid.setWidth("98%");
		FlexCellFormatter cf = filtersGrid.getFlexCellFormatter();

		// ...add the 'Filter:' label to the grid...
		InlineLabel il = new InlineLabel(m_messages.vibeBinderFilter_Filter());
		il.addStyleName("vibe-filtersLabel");
		il.setWordWrap(false);
		filtersGrid.setWidget(    0, 0, il                               );
		cf.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		cf.setWidth(              0, 0, "1%"                             );
		cf.setWordWrap(           0, 0, false                            );

		// ...add a panel to hold the filters themselves....
		VibeFlowPanel filtersPanel = new VibeFlowPanel();
		filtersPanel.addStyleName("vibe-filtersPanel");
		filtersGrid.setWidget(    0, 1, filtersPanel                     );
		cf.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		cf.setWidth(              0, 1, "93%"                            );

		// ...if we have a URL to turn off filtering...
		if (GwtClientHelper.hasString(m_filtersOffUrl)) {
			// ...add that to the filters panel...
			renderFilterLink(
				filtersPanel,
				m_messages.vibeBinderFilter_None(),
				m_filtersOffUrl,
				"vibe-filterAnchorLabel",
				m_currentFilters.isEmpty());
		}
		
		// ...scan the filters defined on the binder...
		for (BinderFilter bf:  m_binderFilters) {
			// ...adding a link for each to the filters panel...
			String filterName = bf.getFilterName();
			renderFilterLink(
				filtersPanel,
				filterName,
				bf.getFilterAddUrl(),
				"vibe-filterAnchorLabel",
				m_currentFilters.contains(bf.getFilterSpec()));
		}

		// ...add a panel to hold the link to edit the filters...
		VibeFlowPanel filterEditPanel = new VibeFlowPanel();
		filterEditPanel.addStyleName("vibe-filterEditPanel");
		filtersGrid.setWidget(    0, 2, filterEditPanel                  );
		cf.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
		cf.setWidth(              0, 2, "5%"                             );
		cf.setWordWrap(           0, 2, false                            );
		
		// ...and if we have a link to edit the filters...
		if (GwtClientHelper.hasString(m_filterEditUrl)) {
			// ...add that to the panel.
			renderFilterLink(
				filterEditPanel,
				m_messages.vibeBinderFilter_Filters(),
				m_filterEditUrl,
				"vibe-filterEditLabel",
				false);
		}
		
		// Finally, tell who's using this tool panel that it's ready to
		// go.
		toolPanelReady();
	}

	/*
	 * Renders a filter link into a panel.
	 */
	private static void renderFilterLink(VibeFlowPanel fp, String filterName, final String filterUrl, String labelStyle, boolean currentFilter) {
		// Create the Anchor for the filter...
		final Anchor a = new Anchor();
		a.addStyleName("vibe-filterAnchor");
		InlineLabel il = new InlineLabel(filterName);
		il.addStyleName(labelStyle);
		String selStyle = (currentFilter ? "vibe-filterAnchorLabel-current" : "vibe-filterAnchorLabel-notcurrent");
		il.addStyleName(selStyle);
		a.getElement().appendChild(il.getElement());
		fp.add(a);
		
		// ...and add the necessary handlers to it.
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtTeaming.fireEvent(new GotoContentUrlEvent(filterUrl));
			}
		});
		a.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				a.removeStyleName("vibe-filterAnchor-hover");
			}
		});
		a.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				a.addStyleName("vibe-filterAnchor-hover");
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
		// Reset the widgets and reload the filters.
		m_fp.clear();
		loadPart1Async();
	}
}
