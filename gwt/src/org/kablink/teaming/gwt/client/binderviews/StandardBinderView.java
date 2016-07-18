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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ShowBinderEvent;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

/**
 * Custom binder view.
 * 
 * @author david
 */
public class StandardBinderView extends BinderViewBase implements ViewReady, ToolPanelReady, ProvidesResize, RequiresResize
{
	private VibeFlowPanel m_breadCrumbPanel;
	private VibeFlowPanel m_descPanel;
	private VibeFlowPanel m_layoutPanel;
	private VibeFlowPanel m_accessoriesPanel;

	/**
	 * Constructor method.
	 *
	 * @param viewReady
	 */
	protected StandardBinderView(BinderViewLayoutData layoutData, UIObject parent, ViewReady viewReady)
	{
		// Simply initialize the super class.
		super(layoutData, parent, viewReady);
		initialize();
	}

	/**
	 * Called to layout page content
	 */
	protected void layoutContent(VibeFlowPanel parentPanel, boolean scrollEntireView)
	{
		// Add a place for the bread crumb control to live.
		m_breadCrumbPanel = buildBreadCrumbPanel(parentPanel);

		// Add a place for the description to live.
		m_descPanel = buildDescriptionPanel(parentPanel);

		// Add a place for the layout based on the binder definition
		{
			m_layoutPanel = new VibeFlowPanel();
			//m_layoutPanel.setWidth("100%");
			//m_layoutPanel.setHeight("100%");
			m_layoutPanel.addStyleName( "vibe-binderView_LayoutPanel" );
			if (!scrollEntireView) {
				m_layoutPanel.addStyleName("vibe-binderView_OverflowAuto");
			}
			parentPanel.add( m_layoutPanel );
		}

		// ...add a place for the accessories.
		m_accessoriesPanel = buildAccessoriesPanel(parentPanel);

		BinderInfo bi = getBinderInfo();
		m_delegatingViewReady.incrementComponent();
		ShowBinderEvent viewEvent = m_layoutData.getUnderlyingShowBinderEvent(m_layoutPanel, this);
		if (viewEvent != null) {
			GwtTeaming.fireEvent(viewEvent);
		}
	}

	/**
	 * Loads the StandardBinderView split point and returns an instance of
	 * it via the callback.
	 *
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderViewLayoutData layoutData, final UIObject parent, final ViewReady viewReady, final ViewClient vClient)
	{
		GWT.runAsync(StandardBinderView.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				StandardBinderView customBinderView;

				customBinderView = new StandardBinderView(layoutData, parent, viewReady);
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
//		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  Heights: parent: " + parent.getOffsetHeight() +
//			"; main: " + m_mainPanel.getOffsetHeight() + "; bread crumb: " + m_breadCrumbPanel.getOffsetHeight() + "; desc:" + m_descPanel.getOffsetHeight() +
//			"; accessories: " + m_accessoriesPanel.getOffsetHeight() + "; footer: " + getFooterHeight());

		int height = parent.getOffsetHeight() + GwtConstants.BINDER_VIEW_ADJUST * 4;
		int width = parent.getOffsetWidth() + GwtConstants.BINDER_VIEW_ADJUST * 2;

//		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  New size: (" + width + "," + height + ")");
		this.setPixelSize(width, height);
		if (!allowToScroll) {
			height = height - m_breadCrumbPanel.getOffsetHeight() - m_descPanel.getOffsetHeight() -
					m_accessoriesPanel.getOffsetHeight() - getFooterHeight();
//			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  New layout panel size: (" + width + "," + height + ")");
			m_layoutPanel.setPixelSize(width, height);
//			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  Layout panel resized.");
			m_layoutPanel.onResize();
		}
	}
}

