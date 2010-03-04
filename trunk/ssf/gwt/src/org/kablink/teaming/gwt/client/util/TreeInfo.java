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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingWorkspaceTreeImageBundle;
import org.kablink.teaming.gwt.client.OnSelectHandler;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;


/**
 * Class used to communicate workspace tree information between the
 * client (i.e., the WorkspaceTreeControl) and the server (i.e.,
 * GwtRpcServiceImpl.getTreeInfo().)
 * 
 * @author drfoster@novell.com
 *
 */
public class TreeInfo implements IsSerializable {
	private List<TreeInfo> m_childBindersAL = new ArrayList<TreeInfo>();
	private BinderType m_binderType = BinderType.OTHER;
	private boolean m_binderExpanded;
	private boolean m_binderSelected;
	private FolderType m_folderType = FolderType.NOT_A_FOLDER;
	private int m_binderChildren = 0;
	private String m_binderIconName;
	private String m_binderId;
	private String m_binderTitle = "";
	private String m_binderPermalink = "";
	private String m_binderTrashPermalink = "";
	private WorkspaceType m_wsType = WorkspaceType.NOT_A_WORKSPACE;
	
	private final static int BINDER_HEIGHT_INT   = 16; private final static String BINDER_HEIGHT   = (BINDER_HEIGHT_INT   + "px");
	private final static int BINDER_WIDTH_INT    = 16; private final static String BINDER_WIDTH    = (BINDER_WIDTH_INT    + "px");
	private final static int EXPANDER_HEIGHT_INT = 16; private final static String EXPANDER_HEIGHT = (EXPANDER_HEIGHT_INT + "px");
	private final static int EXPANDER_WIDTH_INT  = 16; private final static String EXPANDER_WIDTH  = (EXPANDER_WIDTH_INT  + "px");
	
	private final static String EXTENSION_ID_BASE            = "workspaceTreeBinder_";
	private final static String EXTENSION_ID_SELECTOR_BASE   = (EXTENSION_ID_BASE + "Selector_");
	private final static String EXTENSION_ID_SELECTOR_ID     = (EXTENSION_ID_BASE + "SelectorId");
	private final static String EXTENSION_ID_TRASH_BASE      = (EXTENSION_ID_BASE + "Trash_");
	private final static String EXTENSION_ID_TRASH_PERMALINK = (EXTENSION_ID_BASE + "TrashPermalink");

	/**
	 * The type of Binder referenced by this TreeInfo object.  
	 */
	public enum BinderType implements IsSerializable {
		FOLDER,
		WORKSPACE,
		
		OTHER,
	}
	
	/**
	 * If the referenced Binder is a Folder, the type of Folder
	 * referenced by this TreeInfo object.  
	 */
	public enum FolderType implements IsSerializable {
		BLOG,
		CALENDAR,
		DISCUSSION,
		FILE,
		MINIBLOG,
		PHOTOALBUM,
		TASK,
		TRASH,
		SURVEY,
		WIKI,
		
		OTHER,
		NOT_A_FOLDER,
	}
	
	/**
	 * If the referenced Binder is a Workspace, the type of Workspace
	 * referenced by this TreeInfo object.  
	 */
	public enum WorkspaceType implements IsSerializable {
		GLOBAL_ROOT,
		PROFILE_ROOT,
		TEAM,
		TEAM_ROOT,
		TOP,
		TRASH,
		USER,
		
		OTHER,
		NOT_A_WORKSPACE,
	}

	/*
	 * Inner class that implements clicking on the various tree
	 * expansion widgets.
	 */
	private static class Expander implements ClickHandler {
		private Grid m_grid;
		private Image m_expanderImg;
		private int m_gridRow;
		private RequestInfo m_ri;
		private TreeInfo m_ti;
		private WorkspaceTreeControl m_wsTree;

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 * @param wsTree
		 * @param grid
		 * @param gridRow
		 * @param expanderImg
		 */
		Expander(RequestInfo ri, WorkspaceTreeControl wsTree, TreeInfo ti, Grid grid, int gridRow, Image expanderImg) {
			// Simply store the parameters.
			m_ri = ri;
			m_wsTree = wsTree;
			m_ti = ti;
			m_grid = grid;
			m_gridRow = gridRow;
			m_expanderImg = expanderImg;
		}

		/**
		 * Called when the expander is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
				
			// Are we collapsing the row?
			if (m_ti.isBinderExpanded()) {
				// Yes!  Can we mark the row as being closed?
				rpcService.collapseTreeNode(m_ti.m_binderId, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable t)       {}
					public void onSuccess(Boolean   success) {
						// Yes!  Update the TreeInfo, re-render the
						// row and change the row's Anchor Image to a
						// tree_opener.
						m_ti.setBinderExpanded(false);
						reRenderRow(m_ri, m_wsTree, m_grid, m_gridRow, m_ti);
						m_expanderImg.setResource(GwtTeaming.getWorkspaceTreeImageBundle().tree_opener());
					}
				});
			}
				
			else {
				// No, we aren't collapsing it!  We must be expanding
				// it.  Can we get a TreeInfo the expansion?
				rpcService.expandTreeNode(m_ti.m_binderId, new AsyncCallback<TreeInfo>() {
					public void onFailure(Throwable t) {}
					public void onSuccess(TreeInfo expandedTI) {
						// Yes!  Update the TreeInfo, and if there are
						// any expanded rows, render them and change the
						// row's Anchor Image to a tree_closer.
						m_ti.setBinderExpanded(true);
						m_ti.setChildBindersList(expandedTI.getChildBindersList());
						if (0 < m_ti.getBinderChildren()) {
							reRenderRow(m_ri, m_wsTree, m_grid, m_gridRow, m_ti);
						}
						m_expanderImg.setResource(GwtTeaming.getWorkspaceTreeImageBundle().tree_closer());
					}
				});
					
			}
		}
	}
	
	/*
	 * Inner class that implements clicking on the various Binder
	 * links in the tree.
	 */
	private static class Selector implements ClickHandler {
		private TreeInfo m_ti;
		private WorkspaceTreeControl m_wsTree;

		/**
		 * Class constructor.
		 * 
		 * @param wsTree
		 * @param ti
		 */
		Selector(WorkspaceTreeControl wsTree, TreeInfo ti) {
			// Simply store the parameters.
			m_wsTree = wsTree;
			m_ti = ti;
		}
		
		/**
		 * Called when the row selector is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// If we're connected to a WorkspaceTreeControl that's got
			// some OnSelectHandler's registered...
			m_ti.selectBinder();
			List<OnSelectHandler> oshList = ((null == m_wsTree) ? null : m_wsTree.getOnSelectHandlersList());
			if ((null != oshList) && (0 < oshList.size())) {
				// Scan them...
				OnSelectBinderInfo osbi = m_ti.buildOnSelectBinderInfo();
				for (Iterator<OnSelectHandler> oshIT = oshList.iterator(); oshIT.hasNext(); ) {
					// Calling each OnSelectHandler with an
					// OnSelectBinderInfo object.
					oshIT.next().onSelect(osbi);
				}
			}
		}
	}

	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TreeInfo() {
		// Nothing to do.
	}

	/**
	 * Returns an OnSelectBinderInfo object that corresponds to this
	 * TreeInfo object.
	 * 
	 * @return
	 */
	public OnSelectBinderInfo buildOnSelectBinderInfo() {
		// Construct an OnSelectBinderInfo for this TreeInfo object.
		OnSelectBinderInfo reply = new OnSelectBinderInfo(this);
		
		// Is this TreeInfo object the trash Binder?
		if (isBinderTrash()) {
			// Yes!  Is there another Binder selected?
			String selectedId = Document.get().getElementById(EXTENSION_ID_SELECTOR_ID).getAttribute("value");
			if (GwtClientHelper.hasString(selectedId)) {
				// Yes!  Then we'll return it's trash permalink URL and
				// not the URL from the trash.  This will cause the
				// trash to display on the selected Binder and not on
				// the workspace, unless no Binder is selected.
				Element selectedElement = Document.get().getElementById(selectedId);
				String trashPermalink = selectedElement.getAttribute(EXTENSION_ID_TRASH_PERMALINK);
				reply.setBinderUrl(trashPermalink);
			}
		}
		
		// If we get here, reply refers to the OnSelectBinderInfo
		// object for this TreeInfo.  Return it.
		return reply;
	}
	
	/*
	 * Removes the widgets from a Grid row.
	 */
	private static void clearRow(Grid grid, int row) {
		grid.remove(grid.getWidget(row, 0));
		grid.remove(grid.getWidget(row, 1));
	}
	
	/**
	 * Creates a copy TreeInfo with the base information from this
	 * TreeInfo.
	 * 
	 * @return
	 */
	public TreeInfo copyBaseTI() {
		// Create the target TreeInfo...
		TreeInfo reply = new TreeInfo();

		// ...copy the information from this TreeInfo... 
		reply.setBinderType(          getBinderType()          );
		reply.setFolderType(          getFolderType()          );
		reply.setBinderExpanded(      isBinderExpanded()       );
		reply.setBinderSelected(      isBinderSelected()       );
		reply.setBinderIconName(      getBinderIconName()      );
		reply.setBinderId(            getBinderId()            );
		reply.setBinderTitle(         getBinderTitle()         );
		reply.setBinderPermalink(     getBinderPermalink()     );
		reply.setBinderTrashPermalink(getBinderTrashPermalink());
		reply.setWorkspaceType(       getWorkspaceType()       );
		
		// ...store an empty child Binder's List<TreeInfo>...
		reply.setChildBindersList(new ArrayList<TreeInfo>());

		// ...and return it.
		return reply;
	}
	
	/**
	 * Returns the number of children in the Binder corresponding to
	 * this TreeInfo object.
	 * 
	 * @return
	 */
	public int getBinderChildren() {
		return m_binderChildren;
	}

	/**
	 * Returns the name of the Binder icon for the Binder corresponding
	 * to this TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderIconName() {
		return m_binderIconName;
	}

	/**
	 * Returns the Binder ID for the Binder corresponding to this
	 * TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderId() {
		return m_binderId;
	}

	/**
	 * Returns the permalink to the Binder corresponding to this
	 * TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderPermalink() {
		return m_binderPermalink;
	}

	/**
	 * Returns the title of the Binder corresponding to this TreeInfo
	 * object.
	 * 
	 * @return
	 */
	public String getBinderTitle() {
		return m_binderTitle;
	}

	/**
	 * Returns the permalink to the trash for the Binder corresponding
	 * to this TreeInfo object.
	 * 
	 * @return
	 */
	public String getBinderTrashPermalink() {
		return m_binderTrashPermalink;
	}

	/**
	 * Returns the type of Binder this TreeInfo object refers
	 * to.
	 * 
	 * @return
	 */
	public BinderType getBinderType() {
		return m_binderType;
	}
	
	/**
	 * Returns the GWT ImageResource of the image to display next to
	 * the Binder.
	 * 
	 * @return
	 */
	public ImageResource getBinderImage() {
//!		...this needs to be implemented...
		
		ImageResource reply = null;
		GwtTeamingWorkspaceTreeImageBundle images = GwtTeaming.getWorkspaceTreeImageBundle();
		switch (getBinderType()) {
		case FOLDER:
			switch (getFolderType()) {
			case BLOG:        reply = images.folder_comment();  break;
			case CALENDAR:    reply = images.folder_calendar(); break;
			case DISCUSSION:  reply = images.folder_comment();  break;
			case FILE:        reply = images.folder_file();     break;
			case MINIBLOG:    reply = images.folder_comment();  break;
			case PHOTOALBUM:  reply = images.folder_photo();    break;
			case TASK:        reply = images.folder_task();     break;
			case TRASH:       reply = images.folder_trash();    break;
			case SURVEY:                                        break;
			case WIKI:                                          break;
			case OTHER:                                         break;
			}
			
			if (null == reply) {
				reply = images.folder_generic();
			}
			
			break;
			
		case WORKSPACE:
			switch (getWorkspaceType()) {
			case GLOBAL_ROOT:                                        break;
			case PROFILE_ROOT:                                       break;
			case TEAM:          reply = images.workspace_team();     break;
			case TEAM_ROOT:                                          break;
			case TOP:                                                break;
			case TRASH:         reply = images.workspace_trash();    break;
			case USER:          reply = images.workspace_personal(); break;
			case OTHER:                                              break;
			}
			
			if (null == reply) {
				reply = images.workspace_generic();
			}
			
			break;
		}
		
		return reply;
	}
	
	/**
	 * Returns the List<TreeInfo> of the Binder's contained in the
	 * Binder corresponding to this TreeInfo.
	 * 
	 * @return
	 */
	public List<TreeInfo> getChildBindersList() {
		return m_childBindersAL;
	}
	
	/**
	 * Returns the GWT ImageResource of the image to display for the
	 * expander next to the Binder.  If no expander should be shown,
	 * null is returned.
	 * 
	 * @return
	 */
	public ImageResource getExpanderImage() {
		ImageResource reply = null;
		
		if (0 < getBinderChildren()) {
			GwtTeamingWorkspaceTreeImageBundle images = GwtTeaming.getWorkspaceTreeImageBundle();
			if (isBinderExpanded())
			     reply = images.tree_closer();
			else reply = images.tree_opener();
		}

		return reply;
	}
	
	/**
	 * If this TreeInfo object refers to a Folder, returns its type.
	 * 
	 * @return
	 */
	public FolderType getFolderType() {
		return ((BinderType.FOLDER == m_binderType) ? m_folderType : FolderType.NOT_A_FOLDER);
	}

	/*
	 * Returns the ID to use for the selector for this TreeInfo.
	 */
	private String getSelectorId() {
		String reply;
		if (isBinderTrash())
			 reply = (EXTENSION_ID_TRASH_BASE    + getBinderId());
		else reply = (EXTENSION_ID_SELECTOR_BASE + getBinderId());
		return reply;
	}
	
	/**
	 * If this TreeInfo object refers to a Workspace, returns its type.
	 * 
	 * @return
	 */
	public WorkspaceType getWorkspaceType() {
		return ((BinderType.WORKSPACE == m_binderType) ? m_wsType : WorkspaceType.NOT_A_WORKSPACE);
	}

	/**
	 * Returns true if the Binder corresponding to this TreeInfo object
	 * should be expanded and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderExpanded() {
		return m_binderExpanded;
	}

	/**
	 * Returns true of this TreeInfo object refers to a Binder that's a
	 * Folder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderFolder() {
		return (BinderType.FOLDER == m_binderType);
	}
	
	/**
	 * Returns true if the Binder corresponding to this TreeInfo object
	 * should be selected and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderSelected() {
		return m_binderSelected;
	}

	/**
	 * Returns true if this TreeInfo object refers a trash Binder and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderTrash() {
		return ((FolderType.TRASH == getFolderType()) || (WorkspaceType.TRASH == getWorkspaceType()));
	}
	
	/**
	 * Returns true of this TreeInfo object refers to a Binder that's a
	 * Workspace and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinderWorkspace() {
		return (BinderType.WORKSPACE == m_binderType);
	}
	
	/**
	 * Called to render the information in this TreeInfo object into a
	 * FlowPanel.
	 *
	 * @param ri
	 * @param wsTree
	 * @param targetPanel
	 */
	public void render(RequestInfo ri, WorkspaceTreeControl wsTree, FlowPanel targetPanel) {
		// Create a hidden <INPUT> that we'll use to store the ID of
		// the currently selected Binder.
		Hidden selectedId = new Hidden();
		selectedId.getElement().setId(EXTENSION_ID_SELECTOR_ID);
		targetPanel.add(selectedId);
		
		// Create the WorkspaceTree control's header...
		Label selectorLabel = new Label(getBinderTitle());
		selectorLabel.addStyleName("workspaceTreeControlHeader");
		selectorLabel.getElement().setId(getSelectorId());
		selectorLabel.getElement().setAttribute(EXTENSION_ID_TRASH_PERMALINK, getBinderTrashPermalink());
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(selectorLabel.getElement());
		selectorA.addClickHandler(new Selector(wsTree, this));
		targetPanel.add(selectorA);

		// ...its content panel...
		Grid grid = new Grid();
		grid.resizeColumns(2);
		grid.addStyleName("workspaceTreeControlBody");
		targetPanel.add(grid);
		
		// ...and if there are any rows to display...
		for (Iterator<TreeInfo> tii = getChildBindersList().iterator(); tii.hasNext(); ) {
			// ...render them.
			int row = grid.getRowCount();
			grid.insertRow(row);
			renderRow(ri, wsTree, grid, row, tii.next());
		}
	}
	
	/*
	 * Called to render an individual row in the WorkspaceTree control.
	 */
	private static void renderRow(RequestInfo ri, WorkspaceTreeControl wsTree, Grid grid, int row, TreeInfo ti) {
		// Is this row expandable?
		Widget expanderWidget;
		Image expanderImg;
		ImageResource expanderImgRes = ti.getExpanderImage();
		boolean showExpander = (null != expanderImgRes);
		if (showExpander) {
			// Yes!  Put an expander Anchor to allow expanding and
			// collapsing of its contents.
			expanderImg = new Image(expanderImgRes);
			expanderImg.addStyleName("workspaceTreeBinderExpanderImg");
			Anchor expanderA = new Anchor();
			expanderA.getElement().appendChild(expanderImg.getElement());
			expanderA.addClickHandler(new Expander(ri, wsTree, ti, grid, row, expanderImg));
			expanderWidget = expanderA;
		}
		else {
			// No, it isn't expandable!  Put a 16x16 spacer in place of
			// the expander.
			expanderImgRes = GwtTeaming.getWorkspaceTreeImageBundle().spacer_1px();
			expanderImg = new Image(expanderImgRes);
			expanderImg.setWidth(EXPANDER_WIDTH);
			expanderImg.setHeight(EXPANDER_HEIGHT);
			expanderWidget = expanderImg;
		}

		// Generate the widgets to select the Binder.
		HorizontalPanel hp = new HorizontalPanel();
		Image binderImg;
		String binderIconName = ti.getBinderIconName();
		if (GwtClientHelper.hasString(binderIconName)) {
			binderImg = new Image();
			binderImg.setUrl(ri.getImagesPath() + binderIconName);
		}
		else {
			ImageResource binderImgRes = ti.getBinderImage();
			if (null == binderImgRes) {
				binderImgRes = GwtTeaming.getWorkspaceTreeImageBundle().spacer_1px();
				binderImg = new Image(binderImgRes);
			}
			else {
				binderImg = new Image(binderImgRes);
			}
			binderImg.setVisibleRect(0, 0, BINDER_WIDTH_INT, BINDER_HEIGHT_INT);
		}
		binderImg.addStyleName("workspaceTreeBinderImg");
		binderImg.setWidth(BINDER_WIDTH);
		binderImg.setHeight(BINDER_HEIGHT);
		hp.add(binderImg);
		Label selectorLabel = new Label(ti.getBinderTitle());
		selectorLabel.addStyleName("workspaceTreeBinderAnchor");
		selectorLabel.getElement().setId(ti.getSelectorId());
		if (!(ti.isBinderTrash())) {
			selectorLabel.getElement().setAttribute(
				EXTENSION_ID_TRASH_PERMALINK,
				ti.getBinderTrashPermalink());
		}
		hp.add(selectorLabel);
		hp.addStyleName("workspaceTreeControlRow");
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(hp.getElement());
		selectorA.addClickHandler(new Selector(wsTree, ti));

		// If this Binder is supposed to be selected...
		if (ti.isBinderSelected()) {
			// ...select it.
			ti.selectBinder();
		}
		
		// Add the row to the Grid.
		grid.setWidget(row, 0, expanderWidget);
		grid.setWidget(row, 1, selectorA);
		
		CellFormatter cf = grid.getCellFormatter();
		cf.setWidth(row, 1, "100%");
		
		// Is the row showing an expander?
		if (showExpander) {
			// Yes!  Align it to the top of its cell.
			cf.setAlignment(
				row,
				0,
				HasHorizontalAlignment.ALIGN_LEFT,
				HasVerticalAlignment.ALIGN_TOP);

			// Is the row expanded?
			if (ti.isBinderExpanded()) {
				// Yes!  Then we need to render its contents.
				VerticalPanel vp = new VerticalPanel();
				Widget w = grid.getWidget(row, 1);
				grid.remove(w);
				vp.add(w);
				Grid expansionGrid = new Grid();
				expansionGrid.resizeColumns(2);
				vp.add(expansionGrid);
				grid.setWidget(row, 1, vp);
				for (Iterator<TreeInfo> tii = ti.getChildBindersList().iterator(); tii.hasNext(); ) {
					int expansionRow = expansionGrid.getRowCount();
					expansionGrid.insertRow(expansionRow);
					renderRow(ri, wsTree, expansionGrid, expansionRow, tii.next());
				}
			}
		}
	}

	/*
	 * Clears and re-renders a TreeInfo object into a Grid row.
	 */
	private static void reRenderRow(RequestInfo ri, WorkspaceTreeControl wsTree, Grid grid, int row, TreeInfo ti) {
		clearRow(grid, row);
		renderRow(ri, wsTree, grid, row, ti);
	}
	
	/**
	 * Store a count of the children of a Binder.
	 * 
	 * @param binderChildren
	 */
	public void setBinderChildren(int binderChildren) {
		m_binderChildren = binderChildren;
	}
	
	/**
	 * Stores the name of the icon for the Binder.
	 * 
	 * @param binderIconName
	 */
	public void setBinderIconName(String binderIconName) {
		m_binderIconName = binderIconName;
		
	}
	
	/**
	 * Stores the ID of a Binder.
	 * 
	 * @param binderId
	 */
	public void setBinderId(String binderId) {
		m_binderId = binderId;
		
	}
	public void setBinderId(Long binderId) {
		setBinderId(String.valueOf(binderId));
	}

	/**
	 * Stores whether the Binder should be expanded.
	 * 
	 * @param binderExpanded
	 */
	public void setBinderExpanded(boolean binderExpanded) {
		m_binderExpanded = binderExpanded;
	}

	/**
	 * Stores a Binder's permalink in this TreeInfo object.
	 * 
	 * @param binderPermalink
	 */
	public void setBinderPermalink(String binderPermalink) {
		m_binderPermalink = binderPermalink;
	}

	/**
	 * Stores whether the Binder should be selected.
	 * 
	 * @param binderSelected
	 */
	public void setBinderSelected(boolean binderSelected) {
		m_binderSelected = binderSelected;
	}

	/**
	 * Stores a Binder's title in this TreeInfo object.
	 * 
	 * @param binderTitle
	 */
	public void setBinderTitle(String binderTitle) {
		m_binderTitle = binderTitle;
	}

	/**
	 * Stores a Binder's trash permalink in this TreeInfo object.
	 * 
	 * @param binderTrashPermalink
	 */
	public void setBinderTrashPermalink(String binderTrashPermalink) {
		m_binderTrashPermalink = binderTrashPermalink;
	}

	/**
	 * Stores the type of Binder referenced by this TreeInfo object.
	 * 
	 * @param binderType
	 */
	public void setBinderType(BinderType binderType) {
		// Store the BinderType...
		m_binderType = binderType;
		
		// ...and reset the FolderType and WorkspaceType.
		if      (m_binderType == BinderType.FOLDER)    {m_folderType = FolderType.OTHER;        m_wsType = WorkspaceType.NOT_A_WORKSPACE;}
		else if (m_binderType == BinderType.WORKSPACE) {m_folderType = FolderType.NOT_A_FOLDER; m_wsType = WorkspaceType.OTHER;}
		else                                           {m_folderType = FolderType.NOT_A_FOLDER; m_wsType = WorkspaceType.NOT_A_WORKSPACE;}
	}
	
	/**
	 * Stores an ArrayList<TreeInfo> of the Binder's contained in the
	 * Binder corresponding to this TreeInfo.
	 * 
	 * @return
	 */
	public void setChildBindersList(List<TreeInfo> childBindersList) {
		m_childBindersAL = childBindersList;
		m_binderChildren = ((null == m_childBindersAL) ? 0 : m_childBindersAL.size());
	}
	
	/**
	 * Stores the type of Folder referenced by this TreeInfo object, if
	 * it references a Folder.
	 * 
	 * @param folderType
	 */
	public void setFolderType(FolderType folderType) {
		// Validate the FolderType for the BinderType...
		if (isBinderFolder()) {
			if (FolderType.NOT_A_FOLDER == folderType) {
				folderType = FolderType.OTHER;
			}
		}
		else {
			folderType = FolderType.NOT_A_FOLDER;
		}
		
		// ...and store it.
		m_folderType = folderType;
	}

	/**
	 * Stores the type of Workspace referenced by this TreeInfo object,
	 * if it references a Workspace.
	 * 
	 * @param wsType
	 */
	public void setWorkspaceType(WorkspaceType wsType) {
		// Validate the WorkspaceType for the BinderType...
		if (isBinderWorkspace()) {
			if (WorkspaceType.NOT_A_WORKSPACE == wsType) {
				wsType = WorkspaceType.OTHER;
			}
		}
		else {
			wsType = WorkspaceType.NOT_A_WORKSPACE;
		}
		
		// ...and store it.
		m_wsType = wsType;
	}

	/*
	 * Does whatever is necessary UI wise to select the Binder
	 * represented by this TreeInfo.
	 */
	private void selectBinder() {
		// If this isn't a trash Binder...
		if (!(isBinderTrash())) {
			// ...mark it as having been selected.
			String selectedId_New = getSelectorId();
			Element selectorLabel_New = Document.get().getElementById(selectedId_New);
			selectorLabel_New.addClassName("workspaceTreeBinderSelected");

			// ...mark any previous selection as not being selected...
			Element selectorId = Document.get().getElementById(EXTENSION_ID_SELECTOR_ID);
			String selectedId_Old = selectorId.getAttribute("value");
			if (GwtClientHelper.hasString(selectedId_Old)) {
				Element selectorLabel_Old = Document.get().getElementById(selectedId_Old);
				selectorLabel_Old.removeClassName("workspaceTreeBinderSelected");
			}
			
			// ...and store the new ID as having been selected.
			selectorId.setAttribute("value", selectedId_New);
		}
	}
}
