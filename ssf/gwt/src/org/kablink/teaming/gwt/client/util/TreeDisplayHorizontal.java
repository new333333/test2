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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's bread crumbs.
 * 
 * @author drfoster@novell.com
 *
 */
public class TreeDisplayHorizontal {
	@SuppressWarnings("unused")
	private TreeInfo	m_rootTI;	// The root TreeInfo object being displayed.
	
	/*
	 * Inner class that implements clicking the close button.
	 */
	private static class CloseButtonSelector implements ClickHandler {
		private WorkspaceTreeControl m_wsTree;
		
		/**
		 * Class constructor.
		 * 
		 * @param action
		 * @param actionObject
		 */
		CloseButtonSelector(WorkspaceTreeControl wsTree) {
			// Simply store the parameters.
			m_wsTree = wsTree;
		}
		
		/**
		 * Called when the button is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Fire the action.
			m_wsTree.triggerAction(TeamingAction.HIERARCHY_BROWSER_CLOSED);
		}
	}


	/**
	 * Constructor method.
	 * 
	 * @param rootTI
	 */
	public TreeDisplayHorizontal(TreeInfo rootTI) {
		// Simply store the parameters.
		m_rootTI = rootTI;
	}

	/*
	 * Creates a FlowPanel for the close push button.
	 */
	private static FlowPanel createClosePanel(WorkspaceTreeControl wsTree) {
		// Create the panel...
		FlowPanel panel = new FlowPanel();
		
		// ...create the Image...
		Image img = new Image(GwtTeaming.getWorkspaceTreeImageBundle().breadcrumb_close());
		img.addStyleName("mainBreadCrumb_CloseImg");
		img.setTitle(GwtTeaming.getMessages().treeCloseBreadCrumbs());
		
		// ...create the Anchor...
		Anchor a = new Anchor();
		a.addStyleName("mainBreadCrumb_CloseA");
		
		// ...tie things together...
		a.getElement().appendChild(img.getElement());
		a.addClickHandler(new CloseButtonSelector(wsTree));
		
		// ...and add the Anchor to the panel and return it.
		panel.add(a);
		return panel;
	}

	/**
	 * Called to render the information in a TreeInfo object into a
	 * FlowPanel.
	 *
	 * @param ri
	 * @param wsTree
	 * @param targetPanel
	 */
	public void render(String selectedBinderId, RequestInfo ri, WorkspaceTreeControl wsTree, FlowPanel targetPanel) {
		FlowPanel closePanel = createClosePanel(wsTree);
		targetPanel.add(closePanel);
		
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.addStyleName("mainBreadCrumb_Content");
		
		Label toDoLabel = new Label("TreeDisplayHorizontal.render( 'The horizontal WorkspaceTreeControl has not been implemented yet.' )");
		toDoLabel.addStyleName("mainBreadCrumb_ContentEach");
		toDoLabel.setWordWrap(false);
		contentPanel.add(toDoLabel);
		
		targetPanel.add(contentPanel);
	}
}
