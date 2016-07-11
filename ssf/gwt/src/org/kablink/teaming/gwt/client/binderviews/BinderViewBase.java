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
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.event.ShowBinderEvent;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

/**
 * Custom binder view.
 * 
 * @author david
 */
public abstract class BinderViewBase extends WorkspaceViewBase implements ViewReady, ToolPanelReady, ProvidesResize, RequiresResize
{
	protected BinderViewLayoutData m_layoutData;
	protected VibeFlowPanel m_mainPanel;
	protected VibeFlowPanel m_footerPanel;

	protected ViewType m_viewType;
	protected DelegatingViewReady m_delegatingViewReady;

	/**
	 * Constructor method.
	 *
	 * @param layoutData
	 * @param viewReady
	 */
	protected BinderViewBase(BinderViewLayoutData layoutData, UIObject parent, ViewReady viewReady)
	{
		// Simply initialize the super class.
		super(layoutData.getBinderInfo(), parent, viewReady);
		m_layoutData = layoutData;
		m_viewReady = new DelegatingViewReady(viewReady, new SimpleViewReady());
		m_delegatingViewReady = (DelegatingViewReady) m_viewReady;
		m_viewType = layoutData.getViewType();
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
		m_mainPanel.addStyleName("vibe-viewBase");
		boolean scrollEntireView = scrollEntireView();
		if (scrollEntireView) {
			m_mainPanel.addStyleName("vibe-binderView_OverflowAuto");
		}

		this.layoutContent(m_mainPanel, scrollEntireView);

		// Add a place for the footer
		buildFooterPanel();

		initWidget( m_mainPanel );
	}

	private void buildFooterPanel() {
		m_footerPanel = new VibeFlowPanel();
		m_footerPanel.addStyleName("vibe-binderView_FooterPanel");
		m_mainPanel.add(m_footerPanel);

		m_delegatingViewReady.incrementComponent();
		FooterPanel.createAsync(this, getBinderInfo(), this, new ToolPanelClientImpl(m_footerPanel, m_viewReady));
	}

	abstract protected void layoutContent(VibeFlowPanel parentPanel, boolean scrollEntireView);

	abstract public void setViewSize();

	protected VibeFlowPanel buildAccessoriesPanel(HasWidgets parentPanel) {
		VibeFlowPanel accessoriesPanel = new VibeFlowPanel();
		accessoriesPanel.addStyleName( "vibe-binderView_AccessoriesPanel" );
		parentPanel.add(accessoriesPanel);

		m_delegatingViewReady.incrementComponent();
		AccessoriesPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClientImpl(accessoriesPanel, m_viewReady));
		return accessoriesPanel;
	}

	protected VibeFlowPanel buildBreadCrumbPanel(HasWidgets parentPanel) {
		VibeFlowPanel breadCrumbPanel = new VibeFlowPanel();
		breadCrumbPanel.addStyleName( "vibe-binderView_BreadCrumbPanel" );
		parentPanel.add(breadCrumbPanel);

		m_delegatingViewReady.incrementComponent();
		BreadCrumbPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClientImpl(breadCrumbPanel, m_viewReady));
		return breadCrumbPanel;
	}

	protected VibeFlowPanel buildDescriptionPanel(HasWidgets parentPanel) {
		VibeFlowPanel descPanel = new VibeFlowPanel();
		descPanel.addStyleName( "vibe-binderView_DescPanel" );
		parentPanel.add(descPanel);

		m_delegatingViewReady.incrementComponent();
		DescriptionPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClientImpl(descPanel, m_viewReady));
		return descPanel;
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
		GwtClientHelper.consoleLog("BinderViewBase: onResizeNow()");
		super.onResize();
		//m_mainPanel.onResize();
	}//end onResizeNow()

	private class SimpleViewReady implements ViewReady {
		@Override
		public void viewReady() {
			setViewSize();

		}
	}

}

