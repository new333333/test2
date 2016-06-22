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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.event.*;
import org.kablink.teaming.gwt.client.rpc.shared.*;
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
public class CustomBinderView extends WorkspaceViewBase implements ViewReady, ToolPanelReady, ProvidesResize, RequiresResize
{
	private VibeFlowPanel m_mainPanel;
	private VibeFlowPanel m_breadCrumbPanel;
	private VibeFlowPanel m_descPanel;
	private VibeFlowPanel m_layoutPanel;
	private VibeFlowPanel m_accessoriesPanel;
	private VibeFlowPanel m_footerPanel;

	private BinderViewLayout m_viewLayout;
	private ViewType m_viewType;
	private int componentTotal;
	private int componentReady;

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
		m_mainPanel = new VibeFlowPanel();
		//m_mainPanel.setWidth("100%");
		//m_mainPanel.setHeight("100%");
		m_mainPanel.addStyleName( "vibe-binderView" );

		// Add a place for the bread crumb control to live.
		{
			GwtClientHelper.consoleLog("CustomBinderView: Creating BreadCrumbPanel");
			m_breadCrumbPanel = new VibeFlowPanel();
			m_breadCrumbPanel.addStyleName( "vibe-binderView_BreadCrumbPanel" );
			m_mainPanel.add( m_breadCrumbPanel );

			BreadCrumbPanel.createAsync( this, getBinderInfo(), this, new ToolPanelBase.ToolPanelClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}

				@Override
				public void onSuccess( ToolPanelBase breadCrumb )
				{
					m_breadCrumbPanel.add( breadCrumb );
				}
			});
		}

		// Add a place for the description to live.
		{
			GwtClientHelper.consoleLog("CustomBinderView: Creating Description Panel");
			m_descPanel = new VibeFlowPanel();
			m_descPanel.addStyleName( "vibe-binderView_DescPanel" );
			m_mainPanel.add( m_descPanel );

			DescriptionPanel.createAsync( this, getBinderInfo(), this, new ToolPanelBase.ToolPanelClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}

				@Override
				public void onSuccess( ToolPanelBase tpb )
				{
					m_descPanel.add( tpb );
				}
			} );
		}

		// Add a place for the layout based on the binder definition
		{
			GwtClientHelper.consoleLog("CustomBinderView: Creating Layout Panel");
			m_layoutPanel = new VibeFlowPanel();
			//m_layoutPanel.setWidth("100%");
			//m_layoutPanel.setHeight("100%");
			m_layoutPanel.addStyleName( "vibe-binderView_LayoutPanel" );
			m_mainPanel.add( m_layoutPanel );
		}

		// ...add a place for the accessories.
		GwtClientHelper.consoleLog("CustomBinderView: Creating Accessories Panel");
		m_accessoriesPanel = new VibeFlowPanel();
		m_accessoriesPanel.addStyleName( "vibe-binderView_AccessoriesPanel" );
		m_mainPanel.add( m_accessoriesPanel );

		AccessoriesPanel.createAsync( this, getBinderInfo(), this, new ToolPanelBase.ToolPanelClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}

			@Override
			public void onSuccess( ToolPanelBase accessories )
			{
				m_accessoriesPanel.add( accessories );
			}
		});

		// Add a place for the footer
		{
			GwtClientHelper.consoleLog("CustomBinderView: Creating Footer Panel");
			m_footerPanel = new VibeFlowPanel();
			m_footerPanel.addStyleName( "vibe-binderView_FooterPanel" );
			m_mainPanel.add( m_footerPanel );

			FooterPanel.createAsync( this, getBinderInfo(), this, new ToolPanelBase.ToolPanelClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}

				@Override
				public void onSuccess( ToolPanelBase tpb )
				{
					m_footerPanel.add( tpb );
				}
			} );
		}

		initWidget( m_mainPanel );

		this.addChildControls(m_viewLayout, m_layoutPanel);
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
				GwtClientHelper.consoleLog("CustomBinderView: BinderViewTwoColumnTable ");
				BinderViewTwoColumnTable tableDef = (BinderViewTwoColumnTable) viewDef;
				GwtClientHelper.consoleLog("CustomBinderView: constructing 2 column VibeGrid");
				VibeGrid viewGrid = new VibeGrid(1,2);
				viewGrid.addStyleName("vibe-grid");
				//viewGrid.setHeight("100%");
				if (tableDef.getWidth() != null) {
					viewGrid.setWidth(tableDef.getWidth().toString());
//				} else {
//					viewGrid.setWidth("100%");
				}
				if (tableDef.getColumn1Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(0, tableDef.getColumn1Width().toString());
				}
				if (tableDef.getColumn2Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(1, tableDef.getColumn2Width().toString());
				}
				viewPanel = viewGrid;
			} else if (viewDef instanceof BinderViewThreeColumnTable) {
				BinderViewThreeColumnTable tableDef = (BinderViewThreeColumnTable) viewDef;
				GwtClientHelper.consoleLog("CustomBinderView: constructing 3 column VibeGrid");
				VibeGrid viewGrid = new VibeGrid(1,3);
				viewGrid.addStyleName("vibe-grid");
				//viewGrid.setHeight("100%");
				if (tableDef.getWidth()!=null) {
					viewGrid.setWidth(tableDef.getWidth().toString());
//				} else {
//					viewGrid.setWidth("100%");
				}
				if (tableDef.getColumn1Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(0, tableDef.getColumn1Width().toString());
				}
				if (tableDef.getColumn2Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(1, tableDef.getColumn2Width().toString());
				}
				if (tableDef.getColumn3Width()!=null) {
					viewGrid.getColumnFormatter().setWidth(2, tableDef.getColumn2Width().toString());
				}
				viewPanel = viewGrid;
			} else {
				BinderViewContainer containerDef = (BinderViewContainer) viewDef;
				GwtClientHelper.consoleLog("CustomBinderView: constructing VibeFlowPanel");
				VibeFlowPanel flowPanel = new VibeFlowPanel();
				flowPanel.addStyleName("vibe-flow");
				//flowPanel.setHeight("100%");
				if (containerDef.getWidth()!=null) {
					flowPanel.setWidth(containerDef.getWidth().toString());
//				} else {
//					flowPanel.setWidth("100%");
				}
				viewPanel = flowPanel;
			}
			GwtClientHelper.consoleLog("CustomBinderView: parentWidget=" + parentWidget.getClass().getSimpleName() + "; viewPanel=" + viewPanel.getClass().getSimpleName());
			parentWidget.showWidget((Widget) viewPanel);
			addChildControls((BinderViewContainer) viewDef, viewPanel);
		} else {
			VibeFlowPanel flowPanel = new VibeFlowPanel();
			flowPanel.addStyleName("vibe-flow");
//			flowPanel.setWidth("100%");
			//flowPanel.setHeight("100%");
			parentWidget.showWidget(flowPanel);
			if (viewDef instanceof BinderViewFolderListing) {
				BinderInfo bi = getBinderInfo();
				componentTotal++;
				GwtClientHelper.consoleLog("CustomBinderView: new async component.  componentTotal = " + componentTotal);
				ShowBinderEvent viewEvent = GwtClientFolderViewHelper.buildGwtBinderLayoutEvent(bi, m_viewType, flowPanel, this);
				if (viewEvent != null) {
					GwtTeaming.fireEvent(viewEvent);
				}
			} else if (viewDef instanceof BinderViewJsp) {
				componentTotal++;
				GwtClientHelper.consoleLog("CustomBinderView: new async component.  componentTotal = " + componentTotal);
				executeJspAsync((BinderViewJsp) viewDef, flowPanel, this);
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
		GWT.runAsync(CustomBinderView.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				CustomBinderView customBinderView;

				customBinderView = new CustomBinderView(folderInfo, viewType, viewLayout, viewReady);
				vClient.onSuccess(customBinderView);
			}

			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_CustomBinderView());
				vClient.onUnavailable();
			}
		});
	}

	/*
    * Asynchronously loads the next part of the accessories panel.
    */
	private void executeJspAsync(final BinderViewJsp jspView, final VibeFlowPanel parent, final ViewReady viewReady) {
		GwtClientHelper.deferCommand(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				executeJspNow(jspView, parent, viewReady);
			}
		});
	}

	/*
     * Synchronously loads the next part of the accessories panel.
     */
	private void executeJspNow(BinderViewJsp jspView, final VibeFlowPanel parent, final ViewReady viewReady) {
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("jsp", jspView.getJsp());
		model.put("binderId", getBinderInfo().getBinderId());
		model.put("itemId", jspView.getItemId());
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.BUILT_IN_JSP, model),
				new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
								t,
								m_messages.rpcFailure_GetJspHtml(),
								VibeJspHtmlType.BUILT_IN_JSP.toString());
					}

					@Override
					public void onSuccess(VibeRpcResponse response) {
						// Store the accessory panel HTML and continue loading.
						JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
						String html = responseData.getHtml();
						HTMLPanel htmlPanel = new HTMLPanel(html);
						parent.add(htmlPanel);
						executeJavaScriptAsync(htmlPanel, viewReady);
					}
				});
	}

	/*
	 * Asynchronously executes the JavaScript in the HTML panel.
	 */
	private void executeJavaScriptAsync(final HTMLPanel htmlPanel, final ViewReady viewReady) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				executeJavaScriptNow(htmlPanel, viewReady);
			}
		});
	}

	/*
	 * Asynchronously executes the JavaScript in the HTML panel.
	 */
	private void executeJavaScriptNow(HTMLPanel htmlPanel, final ViewReady viewReady) {
		GwtClientHelper.jsExecuteJavaScript(htmlPanel.getElement(), true);
		GwtClientHelper.jsOnLoadInit();
		viewReady.viewReady();
	}

	@Override
	public void viewReady() {
		if (componentReady<componentTotal) {
			componentReady++;
			GwtClientHelper.consoleLog("CustomBinderView: component ready.  componentReady = " + componentReady);
			if (componentReady==componentTotal) {
				GwtClientHelper.consoleLog("CustomBinderView: All components ready!");
				super.viewReady();
			}
		}

		else {
			GwtClientHelper.debugAlert("FolderViewBase.viewReady( *Internal Error* ):  Unexpected call to viewReady() method.");
		}
	}

	public void setViewSize() {
		UIObject parent = m_parent;
		if (parent==null) {
			parent = GwtTeaming.getMainPage().getMainContentLayoutPanel();
		}
		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize(). Parent=" + parent.getClass().getSimpleName() + "; Parent height: " + parent.getOffsetWidth());
		setPixelSize((parent.getOffsetWidth() + getContentHeightAdjust()), (parent.getOffsetHeight() + getContentWidthAdjust()));
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
		GwtClientHelper.consoleLog("CustomBinderView: onResizeNow()");
		super.onResize();
		m_mainPanel.onResize();
	}//end onResizeNow()


}

