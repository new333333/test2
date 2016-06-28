/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.event.ShowBinderEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetJspHtmlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.gwt.client.widgets.VibeEntityViewPanel;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeGrid;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom binder view.
 * 
 * @author david
 */
public class StandardBinderView extends WorkspaceViewBase implements ViewReady, ToolPanelReady, ProvidesResize, RequiresResize
{
	private VibeFlowPanel m_mainPanel;
	private VibeFlowPanel m_breadCrumbPanel;
	private VibeFlowPanel m_descPanel;
	private VibeFlowPanel m_layoutPanel;
	private VibeFlowPanel m_accessoriesPanel;
	private VibeFlowPanel m_footerPanel;

	protected ViewType m_viewType;
	protected DelegatingViewReady m_delegatingViewReady;

	/**
	 * Constructor method.
	 *
	 * @param binderInfo
	 * @param viewReady
	 */
	protected StandardBinderView(BinderInfo binderInfo, ViewType viewType, ViewReady viewReady)
	{
		// Simply initialize the super class.
		super( binderInfo, viewReady);
		m_viewReady = new DelegatingViewReady(viewReady, new SimpleViewReady());
		m_delegatingViewReady = (DelegatingViewReady) m_viewReady;
		m_viewType = viewType;
	}

	/**
	 * Called to construct the view.
	 */
	public void constructView()
	{
		m_mainPanel = new VibeFlowPanel();
		//m_mainPanel.setWidth("100%");
		//m_mainPanel.setHeight("100%");
		m_mainPanel.addStyleName( "vibe-binderView" );
		boolean scrollEntireView = scrollEntireView();
		if (scrollEntireView) {
			m_mainPanel.addStyleName("vibe-binderView_OverflowAuto");
		}

		// Add a place for the bread crumb control to live.
		{
			m_breadCrumbPanel = new VibeFlowPanel();
			m_breadCrumbPanel.addStyleName( "vibe-binderView_BreadCrumbPanel" );
			m_mainPanel.add(m_breadCrumbPanel);

			m_delegatingViewReady.incrementComponent();
			BreadCrumbPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClientImpl(m_breadCrumbPanel, m_viewReady));
		}

		// Add a place for the description to live.
		{
			m_descPanel = new VibeFlowPanel();
			m_descPanel.addStyleName( "vibe-binderView_DescPanel" );
			m_mainPanel.add(m_descPanel);

			m_delegatingViewReady.incrementComponent();
			DescriptionPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClientImpl(m_descPanel, m_viewReady));
		}

		// Add a place for the layout based on the binder definition
		{
			m_layoutPanel = new VibeFlowPanel();
			//m_layoutPanel.setWidth("100%");
			//m_layoutPanel.setHeight("100%");
			m_layoutPanel.addStyleName( "vibe-binderView_LayoutPanel" );
			if (!scrollEntireView) {
				m_layoutPanel.addStyleName("vibe-binderView_OverflowAuto");
			}
			m_mainPanel.add( m_layoutPanel );
		}

		// ...add a place for the accessories.
		m_accessoriesPanel = new VibeFlowPanel();
		m_accessoriesPanel.addStyleName( "vibe-binderView_AccessoriesPanel" );
		m_mainPanel.add(m_accessoriesPanel);

		m_delegatingViewReady.incrementComponent();
		AccessoriesPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClientImpl(m_accessoriesPanel, m_viewReady));

		// Add a place for the footer
		{
			m_footerPanel = new VibeFlowPanel();
			m_footerPanel.addStyleName( "vibe-binderView_FooterPanel" );
			m_mainPanel.add(m_footerPanel);

			m_delegatingViewReady.incrementComponent();
			FooterPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClientImpl(m_footerPanel, m_viewReady));
		}

		initWidget( m_mainPanel );

		this.layoutContent(m_layoutPanel);
	}

	protected void layoutContent(VibeFlowPanel layoutPanel) {
		BinderInfo bi = getBinderInfo();
		m_delegatingViewReady.incrementComponent();
		ShowBinderEvent viewEvent = GwtClientFolderViewHelper.buildGwtBinderLayoutEvent(bi, m_viewType, layoutPanel, this);
		if (viewEvent != null) {
			GwtTeaming.fireEvent(viewEvent);
		}

	}

	/**
	 * Loads the StandardBinderView split point and returns an instance of
	 * it via the callback.
	 *
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync( final BinderInfo folderInfo, final ViewType viewType, final ViewReady viewReady, final ViewClient vClient )
	{
		GWT.runAsync(StandardBinderView.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				StandardBinderView customBinderView;

				customBinderView = new StandardBinderView(folderInfo, viewType, viewReady);
				customBinderView.constructView();
				vClient.onSuccess(customBinderView);
			}

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_CustomBinderView());
				vClient.onUnavailable();
			}
		});
	}

	public void setViewSize() {
		boolean allowToScroll = scrollEntireView();

		UIObject parent = m_parent;
		if (parent==null) {
			parent = GwtTeaming.getMainPage().getMainContentLayoutPanel();
		}
		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  Heights: parent: " + parent.getOffsetHeight() +
			"; main: " + m_mainPanel.getOffsetHeight() + "; bread crumb: " + m_breadCrumbPanel.getOffsetHeight() + "; desc:" + m_descPanel.getOffsetHeight() +
			"; accessories: " + m_accessoriesPanel.getOffsetHeight() + "; footer: " + m_footerPanel.getOffsetHeight());

		int height = parent.getOffsetHeight() + GwtConstants.BINDER_VIEW_ADJUST * 4;
		int width = parent.getOffsetWidth() + GwtConstants.BINDER_VIEW_ADJUST * 2;

		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  New size: (" + width + "," + height + ")");
		this.setPixelSize(width, height);
		if (!allowToScroll) {
			height = height - m_breadCrumbPanel.getOffsetHeight() - m_descPanel.getOffsetHeight() -
					m_accessoriesPanel.getOffsetHeight() - m_footerPanel.getOffsetHeight();
			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  New layout panel size: (" + width + "," + height + ")");
			m_layoutPanel.setPixelSize(width, height);
			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  Layout panel resized.");
			m_layoutPanel.onResize();
		}
	}

	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 */
	@Override
	public void toolPanelReady( ToolPanelBase toolPanel )
	{
		// Nothing to do.  We don't need to know when tool panels are ready.
	}

	/**
	 */
	@Override
	public void onResize()
	{
		onResizeAsync();
	}//end onResize()

	/*
	 * Asynchronously resizes the flow panel.
	 */
	private void onResizeAsync()
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				onResizeNow();
			}
		});
	}//end onResizeAsync()

	/*
	 * Synchronously resizes the flow panel.
	 */
	private void onResizeNow()
	{
		GwtClientHelper.consoleLog("StandardBinderView: onResizeNow()");
		super.onResize();
		//m_mainPanel.onResize();
	}//end onResizeNow()

	private class ToolPanelClientImpl implements ToolPanelBase.ToolPanelClient {
		private VibeFlowPanel parentPanel;
		private ViewReady viewReady;

		public ToolPanelClientImpl(VibeFlowPanel parentPanel, ViewReady viewReady) {
			this.parentPanel = parentPanel;
			this.viewReady = viewReady;
		}

		@Override
		public void onSuccess(ToolPanelBase tpb) {
			parentPanel.add(tpb);
			viewReady.viewReady();
		}

		@Override
		public void onUnavailable() {
			// Nothing to do.  Error handled in asynchronous provider.
		}
	}

	private class SimpleViewReady implements ViewReady {
		@Override
		public void viewReady() {
			setViewSize();

		}
	}
}

