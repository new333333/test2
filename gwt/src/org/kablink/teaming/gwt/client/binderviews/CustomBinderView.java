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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.kablink.teaming.gwt.client.BlogArchiveFolder;
import org.kablink.teaming.gwt.client.BlogArchiveMonth;
import org.kablink.teaming.gwt.client.BlogPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.event.*;
import org.kablink.teaming.gwt.client.rpc.shared.*;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificFolderData;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.VibeEntityViewPanel;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeGrid;

import java.util.ArrayList;

/**
 * Custom binder view.
 * 
 * @author david
 */
public class CustomBinderView extends WorkspaceViewBase implements VibeEntityViewPanel
{
	private BinderViewLayout m_viewLayout;
	private ViewType m_viewType;
	/**
	 * Constructor method.
	 *
	 * @param binderInfo
	 * @param viewReady
	 */
	private CustomBinderView(BinderInfo binderInfo, ViewType viewType, BinderViewLayout viewLayout, ViewReady viewReady)
	{
		// Simply initialize the super class.
		super( binderInfo, viewReady);
		m_viewLayout = viewLayout;
		m_viewType = viewType;
		constructView();
	}
	
	/**
	 * Called to construct the view.
	 */
	public void constructView()
	{
		GwtClientHelper.consoleLog("CustomBinderView: constructView()");
		this.addChildControls(m_viewLayout , this);
	}

	public void addChildControls(BinderViewContainer parentDef, VibeEntityViewPanel parentWidget) {
		GwtClientHelper.consoleLog("CustomBinderView: addChildControls() for " + parentDef.getClass().getName());
		for (BinderViewDefBase child : parentDef.getChildren()) {
			addControl(child, parentWidget);
		}
	}

	public void addControl(BinderViewDefBase viewDef, VibeEntityViewPanel parentWidget) {
		GwtClientHelper.consoleLog("CustomBinderView: addControl() for " + viewDef.getClass().getName());
		if (viewDef instanceof BinderViewContainer) {
			VibeEntityViewPanel viewPanel;
			if (viewDef instanceof BinderViewTwoColumnTable) {
				BinderViewTwoColumnTable tableDef = (BinderViewTwoColumnTable) viewDef;
				VibeGrid viewGrid = new VibeGrid(1,2);
				viewGrid.addStyleName("vibe-grid");
				if (tableDef.getWidth() != null) {
					viewGrid.setWidth(tableDef.getWidth().toString());
				}
				if (tableDef.getColumn1Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(1, tableDef.getColumn1Width().toString());
				}
				if (tableDef.getColumn2Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(2, tableDef.getColumn2Width().toString());
				}
				viewPanel = viewGrid;
			} else if (viewDef instanceof BinderViewThreeColumnTable) {
				BinderViewThreeColumnTable tableDef = (BinderViewThreeColumnTable) viewDef;
				VibeGrid viewGrid = new VibeGrid(1,3);
				viewGrid.addStyleName("vibe-grid");
				if (tableDef.getWidth()!=null) {
					viewGrid.setWidth(tableDef.getWidth().toString());
				}
				if (tableDef.getColumn1Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(1, tableDef.getColumn1Width().toString());
				}
				if (tableDef.getColumn2Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(2, tableDef.getColumn2Width().toString());
				}
				if (tableDef.getColumn3Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(3, tableDef.getColumn2Width().toString());
				}
				viewPanel = viewGrid;
			} else {
				BinderViewContainer containerDef = (BinderViewContainer) viewDef;
				VibeFlowPanel flowPanel = new VibeFlowPanel();
				flowPanel.addStyleName("vibe-flow");
				if (containerDef.getWidth()!=null) {
					flowPanel.setWidth(containerDef.getWidth().toString());
				}
				viewPanel = flowPanel;
			}
			parentWidget.showWidget((Widget) viewPanel);
			addChildControls((BinderViewContainer) viewDef, viewPanel);
		} else {
			if (viewDef instanceof BinderViewFolderListing) {
				BinderInfo bi = getBinderInfo();
				ShowBinderEvent viewEvent = GwtClientFolderViewHelper.buildGwtBinderLayoutEvent(bi, m_viewType, parentWidget, null);
				if (viewEvent!=null) {
					GwtTeaming.fireEvent(viewEvent);
				}
			} else {
				VibeEntityViewPanel viewPanel = new VibeFlowPanel();
				parentWidget.showWidget((Widget)viewPanel);
			}
		}
	}

	/**
	 * Loads the BlogFolderView split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync( final BinderInfo folderInfo, final ViewType viewType, final BinderViewLayout viewLayout, final ViewReady viewReady, final ViewClient vClient )
	{
		GWT.runAsync( CustomBinderView.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				CustomBinderView customBinderView;
				
				customBinderView = new CustomBinderView( folderInfo, viewType, viewLayout, viewReady );
				vClient.onSuccess( customBinderView );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( m_messages.codeSplitFailure_CustomBinderView() );
				vClient.onUnavailable();
			}
		});
	}

	@Override
	public void showWidget(Widget widget) {
		GwtClientHelper.consoleLog("CustomBinderView: showWidget() for " + widget.getClass().getName());
		this.initWidget(widget);
	}
}
