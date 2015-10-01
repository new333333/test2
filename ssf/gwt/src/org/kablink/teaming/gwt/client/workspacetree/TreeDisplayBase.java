/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageTeamsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageUsersDlgEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent.CollectionCallback;
import org.kablink.teaming.gwt.client.event.MenuLoadedEvent.MenuItem;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.TreeMode;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class used to drive the display of the various instantiations
 * of a WorkspaceTreeControl,
 * 
 * @author drfoster@novell.com
 */
public abstract class TreeDisplayBase {
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
		@Override
		public void onClick(ClickEvent event) {
			// Is the item is a bucket?
			if (m_ti.isBucket()) {
				// Yes!  Simply ignore the click.  We make it an anchor
				// so that the hover, ... works.
			}

			// No, the item is not a bucket!  Is it an activity stream?
			else if (m_ti.isActivityStream()) {
				// Yes!  Does it have an event defined on it?
				TeamingEvents te = m_ti.getActivityStreamEvent();
				if ((null != te) && (TeamingEvents.UNDEFINED != te)) {
					// Yes!  Fire it.
					GwtTeaming.fireEvent(
						new ActivityStreamEvent(
							m_ti.getActivityStreamInfo()));
				}
			}
			
			else {
				// No, the item is not an activity stream either!  Are
				// we in a state where we can change contexts?
				if (canChangeContext()) {
					// Yes!  Are we viewing the trash in the manage
					// user's dialog? 
					if (isTrash() && m_ti.getBinderInfo().isBinderProfilesRootWS()) {
						// Yes!  Simply tell the dialog to exit the trash viewer.
						GwtTeaming.fireEventAsync(
							new InvokeManageUsersDlgEvent(
								false));	// false -> Not a trash view.
					}
					
					// No, we aren't viewing the trash in the manage
					// user's dialog!  Are we viewing the trash in the
					// manage teams dialog?
					else if (isTrash() && m_ti.getBinderInfo().isBinderTeamsRootWS()) {
						// Yes!  Simply tell the dialog to exit the trash viewer.
						GwtTeaming.fireEventAsync(
							new InvokeManageTeamsDlgEvent(
								false));	// false -> Not a trash view.
					}
					
					else {
						// No, we aren't viewing the trash in the
						// manage teams dialog either!  Select the
						// Binder and tell the WorkspaceTreeControl to
						// handle it.
						selectBinder(m_ti);
						GwtTeaming.fireEvent(
							new ChangeContextEvent(
								buildOnSelectBinderInfo(
									m_ti)));
					}
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
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		m_wsTree = wsTree;
		m_rootTI = rootTI;
	}
	
	/**
	 * Constructor method.  (2 of 2)
	 *
	 * @param tm
	 * @param wsTree
	 * @param rootTIList
	 */
	public TreeDisplayBase(WorkspaceTreeControl wsTree, List<TreeInfo> rootTIList) {
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		m_wsTree     = wsTree;
		m_rootTIList = rootTIList;
	}

	/**
	 * Abstract methods.
	 */
	abstract OnSelectBinderInfo buildOnSelectBinderInfo(TreeInfo ti);
	abstract void selectBinder(TreeInfo ti);
	public abstract boolean isInActivityStreamMode();
	public abstract void    getSidebarCollection(CollectionCallback collectionCallback);
	public abstract void    menuHide();
	public abstract void    menuShow();
	public abstract void    menuLoaded(MenuItem menuItem);
	public abstract void    refreshSidebarTree();
	public abstract void    rerootSidebarTree();
	public abstract void    render(          BinderInfo selectedBinderInfo, FlowPanel targetPanel);
	public abstract void    setRenderContext(BinderInfo selectedBinderInfo, FlowPanel targetPanel);
	public abstract void    setSelectedBinder(OnSelectBinderInfo binderInfo);

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
	 */
	public void clearBusySpinner() {
		// By default, we do nothing special.
	}
	
	/**
	 * Called when activity stream mode is to be entered on the sidebar
	 * tree.
	 *
	 * The vertical subclass of TreeDisplayBase overrides this to
	 * implement entering activity stream mode.
	 * 
	 * @param defaultASI
	 * @param fromEnterEvent
	 */
	public void enterActivityStreamMode(ActivityStreamInfo defaultASI, boolean fromEnterEvent) {
		// By default, we ignore this.
	}
	
	/**
	 * Called when activity stream mode is to be exited on the sidebar
	 * tree.
	 *
	 * The vertical subclass of TreeDisplayBase overrides this to
	 * implement exiting activity stream mode.
	 * 
	 * @param exitMode
	 */
	public void exitActivityStreamMode(ExitMode exitMode) {
		// By default, we ignore this.
	}
	
	/**
	 * Returns the string to display as the hover over text for the
	 * Anchor on a TreeItem.
	 * 
	 * For buckets, this contains a named range of the items in the
	 * bucket.  For non buckets, it's simply the item's hover text,
	 * if any.
	 * 
	 * @param ti
	 * 
	 * @return
	 */
	String getBinderHover(TreeInfo ti) {
		String reply;
		if (ti.isBucket())
			 reply = getMessages().treeBucketHover(ti.getBucketInfo().getBucketTuple1(), ti.getBucketInfo().getBucketTuple2());
		else reply = ti.getBinderHover();
		return reply;
	}

	/**
	 * Returns access to the Filr image bundle.
	 *  
	 * @return
	 */
	GwtTeamingFilrImageBundle getFilrImages() {
		return GwtTeaming.getFilrImageBundle();
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
	 * Returns access to the Vibe base image bundle.
	 *  
	 * @return
	 */
	GwtTeamingImageBundle getBaseImages() {
		return GwtTeaming.getImageBundle();
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
	 * Returns the binder this tree control was built from.
	 * 
	 * @return
	 */
	BinderInfo getSelectedBinderInfo() {
		return m_wsTree.getSelectedBinderInfo();
	}
	
	/**
	 * Returns the TreeMode being displayed.
	 * 
	 * @return
	 */
	TreeMode getTreeMode() {
		return m_wsTree.getTreeMode();
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
	 * Returns true if the workspace tree is currently hidden because
	 * of an empty sidebar and false otherwise.
	 * 
	 * @return
	 */
	final public boolean isTreeHiddenByEmptySidebar() {
		return m_wsTree.isTreeHiddenByEmptySidebar();
	}
	
	/**
	 * Returns true if the main menu is visible and false otherwise.
	 * 
	 * @return
	 */
	boolean isMainMenuVisible() {
		return ((null != m_wsTree) && m_wsTree.isMainMenuVisible());
	}
	
	/**
	 * Returns true if the tree is displaying for a trash view and
	 * false otherwise.
	 * 
	 * @return
	 */
	final public boolean isTrash() {
		return m_wsTree.isTrash();
	}

	/**
	 * Returns true if the workspace tree is visible and false
	 * otherwise.
	 * 
	 * @return
	 */
	final public boolean isTreeVisible() {
		return m_wsTree.isVisible();
	}

	/**
	 * Called to reset the main menu context to that previously loaded.
	 */
	void resetMenuContext() {
		m_wsTree.resetMenuContext();
	}
	
	/**
	 * Called to select an activity stream in the sidebar.
	 *
	 * The vertical subclass of TreeDisplayBase will override this to
	 * force the setting of the activity stream in the sidebar.
	 * 
	 * @param asi
	 */
	public void setActivityStream(ActivityStreamInfo asi) {
		// By default, we ignore this.
	}
	
	/**
	 * Sets the image resource on a binder image based on its TreeInfo.
	 * 
	 * @param ti
	 * @param iconSize
	 */
	public void setBinderImageResource(TreeInfo ti, BinderIconSize iconSize, ImageResource defaultImg) {
		// Do we have an Image widget to store the image resource in?
		Image binderImg = ((Image) ti.getBinderUIImage());
		if (null != binderImg) {
			// Yes!  Does the TreeInfo have the name of an icon to use?
			String binderIcon = ti.getBinderIcon(iconSize);
			if ((!(ti.getBinderInfo().isFolderHome())) && (!(ti.getBinderInfo().isFolderMyFilesStorage())) && GwtClientHelper.hasString(binderIcon)) {
				// Yes!  Set its URL into the Image.
				if (binderIcon.startsWith("/"))
				     binderImg.setUrl(getImagesPath() + binderIcon.substring(1));
				else binderImg.setUrl(getImagesPath() + binderIcon);
			}
			
			else {
				// No, the TreeInfo doesn't have the name of an icon to
				// use!  Does it have an ImageResource to use?
				ImageResource binderImgRes = ti.getBinderImage(iconSize);
				if (null == binderImgRes) {
					// No!  Use the default ImageResource.
					binderImgRes = defaultImg;
				}
				
				// We always display images via their URL so that they
				// can be scaled when necessary. 
				binderImg.setUrl(binderImgRes.getSafeUri());
			}

			// Apply any scaling specified to the image.
			int width  = ti.getBinderIconWidth( iconSize); if ((-1) != width)  binderImg.setWidth( width  + "px");
			int height = ti.getBinderIconHeight(iconSize); if ((-1) != height) binderImg.setHeight(height + "px");
		}
	}
	
	public void setBinderImageResource(TreeInfo ti, BinderIconSize iconSize) {
		// Always use the initial form of the method.
		setBinderImageResource(ti, iconSize, getImages().spacer_1px());
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
	 * Sets the hover text on a widget, guarding against exceptions.
	 * 
	 * @param w
	 * @param hover
	 */
	void setWidgetHover(Widget w, String hover) {
		if ((null != w) && GwtClientHelper.hasString(hover)) {
			w.setTitle(hover);
		}
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
