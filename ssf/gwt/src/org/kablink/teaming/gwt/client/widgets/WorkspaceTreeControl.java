/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayBase;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayHorizontal;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayVertical;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * This widget will display the WorkspaceTree control.
 * 
 * @author drfoster@novell.com
 */
public class WorkspaceTreeControl extends Composite implements ActionRequestor, ActionTrigger {
	private GwtMainPage m_mainPage;
	private TreeDisplayBase m_treeDisplay;
	private TreeMode m_tm;
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	
	/**
	 * The mode this WorkspaceTreeControl is running in.
	 * 
	 * HORIZONTAL:  Typically used in the Teaming bread crumbs.
	 * VERTICAL:    Typically used in the Teaming sidebar. 
	 */
	public enum TreeMode {
		HORIZONTAL,
		VERTICAL,
	}
	
	/**
	 * Constructs a WorkspaceTreeControl based on the information
	 * in the RequestInfo object.
	 *
	 * @param mainPage
	 * @param tm
	 */
	public WorkspaceTreeControl(GwtMainPage mainPage, final String selectedBinderId, TreeMode tm) {
		m_mainPage = mainPage;
		m_tm       = tm;

		final WorkspaceTreeControl wsTree = this;
		final FlowPanel mainPanel = new FlowPanel();
		
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		switch (m_tm) {
		case HORIZONTAL:
			mainPanel.addStyleName( "breadCrumb_Browser" );
			rpcService.getHorizontalTree(new HttpRequestInfo(), selectedBinderId, new AsyncCallback<List<TreeInfo>>() {
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						GwtTeaming.getMessages().rpcFailure_GetTree(),
						selectedBinderId);
				}
				
				public void onSuccess(List<TreeInfo> tiList)  {
					m_treeDisplay = new TreeDisplayHorizontal(wsTree, tiList);
					m_treeDisplay.render(selectedBinderId, mainPanel);
				}
			});
			
			break;
			
		case VERTICAL:
			mainPanel.addStyleName( "workspaceTreeControl" );
			rpcService.getVerticalTree(new HttpRequestInfo(), selectedBinderId, new AsyncCallback<TreeInfo>() {
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						GwtTeaming.getMessages().rpcFailure_GetTree(),
						selectedBinderId);
				}
				public void onSuccess(TreeInfo ti)  {
					// Construct the vertical tree display.
					m_treeDisplay = new TreeDisplayVertical(wsTree, ti);
					
					// Are we starting up showing what's new?
					if (m_mainPage.getRequestInfo().isShowWhatsNewOnLogin()) {
						// Yes!  Then we enter activity stream mode by
						// default.  Tell the menu about the context...
						m_mainPage.getMainMenu().setContext(selectedBinderId, false, "");
						
						// ...and enter activity stream mode.
						m_treeDisplay.setRenderContext(selectedBinderId, mainPanel);
						GwtTeaming.getRpcService().getDefaultActivityStream(new HttpRequestInfo(), selectedBinderId, new AsyncCallback<ActivityStreamInfo>() {
							public void onFailure(Throwable t) {
								// If we couldn't get it, handle the
								// failure...
								GwtClientHelper.handleGwtRPCFailure(
									GwtTeaming.getMessages().rpcFailure_GetDefaultActivityStream());
								
								// ...and just go site wide.
								ActivityStreamInfo asi = new ActivityStreamInfo();
								asi.setActivityStream(ActivityStream.SITE_WIDE);
								asi.setTitle(GwtTeaming.getMessages().treeSiteWide());
								m_treeDisplay.enterActivityStreamMode(asi);
							}
							
							public void onSuccess(ActivityStreamInfo asi) {
								// Does this user have a default saved?
								if (null == asi) {
									// No!  Default to site wide.
									asi = new ActivityStreamInfo();
									asi.setActivityStream(ActivityStream.SITE_WIDE);
									asi.setTitle(GwtTeaming.getMessages().treeSiteWide());
								}
								m_treeDisplay.enterActivityStreamMode(asi);
							}
						});
					}
					
					else {
						// No, we aren't starting in activity stream
						// mode!  Render the tree.
						m_treeDisplay.render(selectedBinderId, mainPanel);
					}
				}
			});
			
			// Set the size of the control.
	        DeferredCommand.addCommand(
	        	new Command() {
	        		public void execute() {
	        			relayoutPage();
	        		}
	        });		
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * Called to add an ActionHandler to this WorkspaceTreeControl.
	 * 
	 * Implements the ActionRequestor.addActionHandler() method.
	 * 
	 * @param actionHandler
	 */
	public void addActionHandler(ActionHandler actionHandler) {
		m_actionHandlers.add(actionHandler);
	}

	/**
	 * Called after a new context has been loaded.
	 * 
	 * @param binderId
	 */
	public void contextLoaded(String binderId) {
		// Simply tell the display that the context has been loaded.
		m_treeDisplay.contextLoaded(binderId);
	}
	
	/**
	 * Called when activity stream mode is to be entered on the sidebar
	 * tree.
	 *
	 * @param defaultASI
	 */
	public void enterActivityStreamMode(ActivityStreamInfo defaultASI) {
		// If we're displaying a sidebar tree...
		if (TreeMode.VERTICAL == m_tm) {
			// ...tell it to load the activity stream navigation
			// ...points.
			m_treeDisplay.enterActivityStreamMode(defaultASI);
		}
	}
	
	/**
	 * Called when activity stream mode is to be exited on the sidebar
	 * tree
	 */
	public void exitActivityStreamMode() {
		// If we're displaying a sidebar tree...
		if (TreeMode.VERTICAL == m_tm) {
			// ...tell it to exit activity stream mode.
			m_treeDisplay.exitActivityStreamMode();
		}
	}
	
	/**
	 * Returns the RequestInfo object associated with this
	 * WorkspaceTreeControl.
	 * 
	 * @return
	 */
	public RequestInfo getRequestInfo() {
		return m_mainPage.getRequestInfo();
	}

	/**
	 * Returns true if the workspace tree control is in activity stream
	 * mode and false otherwise.
	 * 
	 * @return
	 */
	public boolean isInActivityStreamMode() {
		return m_treeDisplay.isInActivityStreamMode();
	}
	
	/**
	 * Called to select an activity stream in the sidebar.
	 *
	 * @param asi
	 */
	public void setActivityStream(ActivityStreamInfo asi) {
		// If we're displaying a sidebar tree...
		if (TreeMode.VERTICAL == m_tm) {
			// ...tell it to select this activity stream.
			m_treeDisplay.setActivityStream(asi);
		}
	}
	
	/**
	 * Called to change the binder being displayed by this
	 * WorkspaceTreeControl.
	 * 
	 * @param binderInfo
	 */
	public void setSelectedBinder(OnSelectBinderInfo binderInfo) {
		m_treeDisplay.setSelectedBinder(binderInfo);
	}
	
	/**
	 * Called to force the workspace tree control to lay itself
	 * out correctly.
	 */
	public void relayoutPage()
	{
		// We only worry about layout if the tree if it's in vertical
		// mode.  Is it?
		if (TreeMode.VERTICAL == m_tm) {
			// Yes!  Force it to lay itself out again.
			DeferredCommand.addCommand(
				new Command() {
					public void execute() {
						relayoutPageImpl();
					}
			});
		}
	}
		
	/*
	 * Implementation method of relayoutPage() that actually performs
	 * the changes.
	 */
	private void relayoutPageImpl() {
		int height;
		Style style;

		// Calculate how high the workspace tree should be...
		height = (Window.getClientHeight() - getAbsoluteTop() - 20);
		
		// ...and set it's height.
		style = getElement().getStyle();
		style.setHeight( height, Style.Unit.PX );
	}

	/**
	 * Called to reset the main menu context to that previously loaded.
	 */
	public void resetMenuContext() {
		m_mainPage.getMainMenu().resetContext();
	}
	
	/**
	 * Called when a selection change is in progress.
	 *
	 * @param osbInfo
	 */
	public void showBinderBusy(OnSelectBinderInfo osbInfo) {
		m_treeDisplay.showBinderBusy(osbInfo);
	}
	
	/**
	 * Fires a TeamingAction at the registered ActionHandler's.
	 * 
	 * Implements the ActionTrigger.triggerAction() method. 
	 * 
	 * @param action
	 * @param obj
	 */
	public void triggerAction(TeamingAction action, Object obj) {
		// Scan the ActionHandler's that have been registered...
		for (Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); ) {
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}
	
	public void triggerAction(TeamingAction action) {
		// Always use the initial form of the method.
		triggerAction(action, null);
	}
}
