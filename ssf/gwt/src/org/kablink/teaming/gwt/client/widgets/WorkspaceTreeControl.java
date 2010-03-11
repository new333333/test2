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

import org.kablink.teaming.gwt.client.ActionHandler;
import org.kablink.teaming.gwt.client.ActionRequestor;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.TeamingAction;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.TreeDisplayHorizontal;
import org.kablink.teaming.gwt.client.util.TreeDisplayVertical;
import org.kablink.teaming.gwt.client.util.TreeInfo;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * This widget will display the WorkspaceTree control.
 * 
 * @author drfoster@novell.com
 */
public class WorkspaceTreeControl extends Composite implements ActionRequestor {
	private RequestInfo m_requestInfo;
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
	 * @param requestInfo
	 * @param tm
	 */
	public WorkspaceTreeControl(RequestInfo requestInfo, TreeMode tm) {
		m_requestInfo = requestInfo;

		final String selectedBinderId = m_requestInfo.getBinderId();
		final WorkspaceTreeControl wsTree = this;
		final FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName( "workspaceTreeControl" );
		
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		switch (tm) {
		case HORIZONTAL:
			rpcService.getHorizontalTree(selectedBinderId, new AsyncCallback<TreeInfo>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}
				public void onSuccess(TreeInfo ti)  {
					TreeDisplayHorizontal tdh = new TreeDisplayHorizontal(ti);
					tdh.render(selectedBinderId, m_requestInfo, wsTree, mainPanel);
				}
			});
			
			break;
			
		case VERTICAL:
			rpcService.getVerticalTree(selectedBinderId, new AsyncCallback<TreeInfo>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}
				public void onSuccess(TreeInfo ti)  {
					TreeDisplayVertical tdv = new TreeDisplayVertical(ti);
					tdv.render(selectedBinderId, m_requestInfo, wsTree, mainPanel);
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
	 * Fires a TeamingAction at the registered ActionHandler's.
	 * 
	 * @param action
	 * @param obj
	 */
	public void triggerAction(TeamingAction action) {
		// Always use the final form of the method.
		triggerAction(action, null);
	}
	
	public void triggerAction(TeamingAction action, Object obj) {
		// Scan the ActionHandler's that have been registered...
		for (Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); ) {
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}
}
