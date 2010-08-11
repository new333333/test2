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
package org.kablink.teaming.gwt.client.workspacetree;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * Base class used to drive the display of the various instantiations
 * of a WorkspaceTreeControl,
 * 
 * @author drfoster@novell.com
 *
 */
public abstract class TreeDisplayBase implements ActionTrigger {
	private List<TreeInfo>			m_rootTIList;	// The root TreeInfo object being displayed.
	private TreeInfo				m_rootTI;		// The root TreeInfo object being displayed.
	private WorkspaceTreeControl	m_wsTree;		// The WorkspaceTreeControl being displayed.
	
	final static int EXPANDER_HEIGHT_INT = 16; public final static String EXPANDER_HEIGHT = (EXPANDER_HEIGHT_INT + "px");
	final static int EXPANDER_WIDTH_INT  = 16; public final static String EXPANDER_WIDTH  = (EXPANDER_WIDTH_INT  + "px");
	
	/*
	 * Inner class that implements clicking on the various Binder
	 * links in the tree.
	 */
	class BinderSelector implements ClickHandler {
		private TreeInfo m_ti;

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 */
		BinderSelector(TreeInfo ti) {
			// Simply store the parameters.
			m_ti = ti;
		}
		
		/**
		 * Called when the row selector is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Is the item is a bucket?
			if (m_ti.isBucket()) {
				// Yes!  Simply ignore the click.  We make it an anchor
				// so that the hover, ... works.
			}
			else {
				// No, the item is not a bucket!  Are we in a state
				// where we can change contexts?
				if (canChangeContext()) {
					// Yes!  Select the Binder and tell the
					// WorkspaceTreeControl to handle it.
					selectBinder(m_ti);
					triggerAction(TeamingAction.SELECTION_CHANGED, buildOnSelectBinderInfo(m_ti));
				}
			}
		}
	}
	
	/**
	 * Constructor method.  (1 of 2)
	 *
	 * @param wsTree
	 * @param rootTI
	 */
	public TreeDisplayBase(WorkspaceTreeControl wsTree, TreeInfo rootTI) {
		// Simply store the parameters.
		m_wsTree = wsTree;
		m_rootTI = rootTI;
	}
	
	/**
	 * Constructor method.  (2 of 2)
	 *
	 * @param wsTree
	 * @param rootTIList
	 */
	public TreeDisplayBase(WorkspaceTreeControl wsTree, List<TreeInfo> rootTIList) {
		// Simply store the parameters.
		m_wsTree = wsTree;
		m_rootTIList = rootTIList;
	}

	/**
	 * Abstract methods.
	 */
	abstract OnSelectBinderInfo buildOnSelectBinderInfo(TreeInfo ti);
	abstract void selectBinder(TreeInfo ti);
	public abstract void render(String selectedBinderId, FlowPanel targetPanel);
	public abstract void setSelectedBinder(OnSelectBinderInfo binderInfo);

	/**
	 * Constructs an InlineLabel for a part name of a bucket.
	 * 
	 * @param part
	 * 
	 * @return
	 */
	InlineLabel buildBucketPartLabel(String part) {
		InlineLabel reply = new InlineLabel(part);
		reply.addStyleName("gwtUI_nowrap");
		return reply;
	}
	
	/**
	 * Constructs a range Image for constructing a bucket name.
	 * 
	 * @return
	 */
	Image buildBucketRangeImage() {
		Image reply = new Image(getImages().range());
		reply.addStyleName("gwtUI_vmiddle");
		
//!		// If we use getImages().rangeArrows(), we must set the height
//!		// and width.
//!		reply.setHeight("10");
//!		reply.setWidth("9");
		
		return reply;
	}

	/**
	 * Returns true if the context can be changed and false otherwise.
	 * 
	 * Subclasses of TreeDisplayBase base should override this if they
	 * require any special considerations that must be enforced to
	 * change contexts.
	 * 
	 * @return
	 */
	boolean canChangeContext() {
		// By default, we can always change contexts.
		return true;
	}
	
	/**
	 * Called after a new context has been loaded.
	 * 
	 * Subclasses of TreeDisplayBase base should override this if they
	 * need to do any processing AFTER a new context has been loaded.
	 * 
	 * @param binderId
	 */
	public void contextLoaded(String binderId) {
		// By default, we do nothing special.
	}
	
	/**
	 * Returns the string to display as the hover over text for the
	 * Anchor on a TreeItem.
	 * 
	 * For buckets, this contains a named range of the items in the
	 * bucket.  For non buckets, it's simply the item's title.
	 * 
	 * @param ti
	 * 
	 * @return
	 */
	String getBinderHover(TreeInfo ti) {
		String reply;
		if (ti.isBucket()) {
			reply = getMessages().treeBucketHover(ti.getBucketFirstTitle(), ti.getBucketLastTitle());
		}
		else {
			reply = ti.getBinderTitle();
		}
		return reply;
	}
	
	/**
	 * Returns access to the workspace tree's image bundle.
	 *  
	 * @return
	 */
	GwtTeamingWorkspaceTreeImageBundle getImages() {
		return GwtTeaming.getWorkspaceTreeImageBundle();
	}
	
	/**
	 * Returns the path to Teaming's images.
	 * 
	 * @return
	 */
	String getImagesPath() {
		return m_wsTree.getRequestInfo().getImagesPath();
	}

	/**
	 * Returns access to Teaming's message store.
	 * 
	 * @return
	 */
	GwtTeamingMessages getMessages() {
		return GwtTeaming.getMessages();
	}
	
	/**
	 * Returns the root TreeInfo, if that's what we're displaying.
	 *  
	 * @return
	 */
	TreeInfo getRootTreeInfo() {
		return m_rootTI;
	}

	/**
	 * Returns the root List<TreeInfo>, if that's what we're
	 * displaying.
	 *  
	 * @return
	 */
	List<TreeInfo> getRootTreeInfoList() {
		return m_rootTIList;
	}

	/**
	 * Returns access to the GWT RPC service.
	 * 
	 * @return
	 */
	GwtRpcServiceAsync getRpcService() {
		return GwtTeaming.getRpcService();
	}
	
	/**
	 * Stores a new root TreeInfo.
	 *  
	 * @param rootTI
	 */
	void setRootTreeInfo(TreeInfo rootTI) {
		m_rootTI = rootTI;
	}

	/**
	 * Stores a new root List<TreeInfo>.
	 *  
	 * @param rootTIList
	 */
	void setRootTreeInfoList(List<TreeInfo> rootTIList) {
		m_rootTIList = rootTIList;
	}

	/**
	 * Fires a TeamingAction at the WorkspaceTreeControl's registered
	 * ActionHandler's.
	 * 
	 * Implements the ActionTrigger.triggerAction() method. 
	 *
	 * @param action
	 * @param obj
	 */
	public void triggerAction(TeamingAction action, Object obj) {
		// Simply pass the action to the WorkspaceTreeControl.
		m_wsTree.triggerAction(action, obj);
	}
	
	public void triggerAction(TeamingAction action) {
		// Always use the initial form of the method.
		triggerAction(action, null);
	}
	
	/**
	 * Called when a selection change is in progress.
	 *
	 * Subclasses of TreeDisplayBase base should override this if they
	 * need to do something special while a selection change is in
	 * progress.
	 * 
	 * @param osbInfo
	 */
	public void showBinderBusy(OnSelectBinderInfo osbInfo) {
		// By default, we do nothing special.
	}
}
