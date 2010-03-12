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

import java.util.HashMap;
import java.util.Iterator;

import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's sidebar.
 * 
 * @author drfoster@novell.com
 *
 */
public class TreeDisplayVertical extends TreeDisplayBase {
	private HashMap<String,Integer> m_renderDepths;		// A map of the depths the Binder's are are displayed at.
	private long 					m_selectedBinderId;	// The ID of the currently selected binder.

	// The follow controls the height and width of the images displayed
	// by this object.
	private final static int BINDER_HEIGHT_INT = 16; private final static String BINDER_HEIGHT = (BINDER_HEIGHT_INT + "px");
	private final static int BINDER_WIDTH_INT  = 16; private final static String BINDER_WIDTH  = (BINDER_WIDTH_INT  + "px");

	// The following are used for widget IDs assigned to various
	// objects in a running WorkspaceTreeControl.
	private final static String EXTENSION_ID_BASE            = "workspaceTreeBinder_";
	private final static String EXTENSION_ID_SELECTOR_ANCHOR = "selectorAnchor_";
	private final static String EXTENSION_ID_SELECTOR_BASE   = (EXTENSION_ID_BASE + "Selector_");
	private final static String EXTENSION_ID_SELECTOR_ID     = (EXTENSION_ID_BASE + "SelectorId");
	private final static String EXTENSION_ID_TRASH_BASE      = (EXTENSION_ID_BASE + "Trash_");
	private final static String EXTENSION_ID_TRASH_PERMALINK = (EXTENSION_ID_BASE + "TrashPermalink");

	/*
	 * Inner class that implements clicking on the various tree
	 * expansion widgets.
	 */
	private class BinderExpander implements ClickHandler {
		private Grid m_grid;
		private Image m_expanderImg;
		private int m_gridRow;
		private TreeInfo m_ti;

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 * @param grid
		 * @param gridRow
		 * @param expanderImg
		 */
		BinderExpander(TreeInfo ti, Grid grid, int gridRow, Image expanderImg) {
			// Simply store the parameters.
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
			final GwtRpcServiceAsync rpcService = getRpcService();
				
			// Are we collapsing the row?
			if (m_ti.isBinderExpanded()) {
				// Yes!  Can we mark the row as being closed?
				rpcService.persistNodeCollapse(m_ti.getBinderId(), new AsyncCallback<Boolean>() {
					public void onFailure(Throwable t)       {}
					public void onSuccess(Boolean success) {
						// Yes!  Update the TreeInfo, re-render the
						// row and change the row's Anchor Image to a
						// tree_opener.
						m_ti.setBinderExpanded(false);
						reRenderRow(m_grid, m_gridRow, m_ti);
						m_expanderImg.setResource(getImages().tree_opener());
					}
				});
			}
				
			else {
				// No, we aren't collapsing it!  We must be expanding
				// it.  Can we mark the row as being opened?
				rpcService.persistNodeExpand(m_ti.getBinderId(), new AsyncCallback<Boolean>() {
					public void onFailure(Throwable t) {}
					public void onSuccess(Boolean success) {
						// Yes!  Can we get a TreeInfo for the
						// expansion?
						rpcService.getVerticalNode(m_ti.getBinderId(), new AsyncCallback<TreeInfo>() {
							public void onFailure(Throwable t) {}
							public void onSuccess(TreeInfo expandedTI) {
								// Yes!  Update the TreeInfo, and if
								// there are any expanded rows, render
								// them and change the row's Anchor
								// Image to a tree_closer.
								m_ti.setBinderExpanded(true);
								m_ti.setChildBindersList(expandedTI.getChildBindersList());
								if (0 < m_ti.getBinderChildren()) {
									reRenderRow(m_grid, m_gridRow, m_ti);
								}
								m_expanderImg.setResource(getImages().tree_closer());
							}
						});
					}
				});
			}
		}
	}
	
	/*
	 * Inner class used to handle mouse events for a Binder selector.  
	 */
	private static class BinderSelectorMouseHandler implements MouseOverHandler, MouseOutHandler {
		private String m_selectorGridId;
		
		/**
		 * Class constructor.
		 * 
		 * @param selectorGridId
		 */
		BinderSelectorMouseHandler(String selectorGridId) {
			// Simply store the parameters.
			m_selectorGridId = selectorGridId;
		}
		
		/**
		 * Called when the mouse leaves a Binder selector.
		 * 
		 * @param me
		 */
		public void onMouseOut(MouseOutEvent me) {
			// Simply remove the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
			selectorPanel_New.removeClassName("workspaceTreeControlRowHover");
		}
		
		/**
		 * Called when the mouse enters a Binder selector.
		 * 
		 * @param me
		 */
		public void onMouseOver(MouseOverEvent me) {
			// Simply add the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
			selectorPanel_New.addClassName("workspaceTreeControlRowHover");
		}
	}
	
	/**
	 * Constructor method.
	 */
	public TreeDisplayVertical(RequestInfo requestInfo, WorkspaceTreeControl wsTree, TreeInfo rootTI) {
		// Construct the super class...
		super(requestInfo, wsTree, rootTI);
		
		// ...and initialize everything else.
		m_selectedBinderId = (-1);
		m_renderDepths = new HashMap<String,Integer>();
	}

	/**
	 * Returns an OnSelectBinderInfo object that corresponds to a
	 * TreeInfo object.
	 * 
	 * Implements TreeDisplayBase.buildOnSelectBinderInfo().
	 * 
	 * @param ti
	 * 
	 * @return
	 */
	OnSelectBinderInfo buildOnSelectBinderInfo(TreeInfo ti) {
		// Construct an OnSelectBinderInfo for this TreeInfo object.
		OnSelectBinderInfo reply = new OnSelectBinderInfo(ti);
		
		// Is this TreeInfo object the trash Binder?
		if (ti.isBinderTrash()) {
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

	/*
	 * Returns the depth that TreeInfo was last rendered at.
	 */
	private int getRenderDepth(TreeInfo ti) {
		int reply;
		Integer depth = m_renderDepths.get(ti.getBinderId());
		if (null == depth)
			 reply = 0;
		else reply = depth.intValue();
		return reply;
	}
	
	/*
	 * Returns the ID to use for the selector for this TreeInfo.
	 */
	private static String getSelectorId(TreeInfo ti) {
		String reply;
		if (ti.isBinderTrash())
			 reply = (EXTENSION_ID_TRASH_BASE    + ti.getBinderId());
		else reply = (EXTENSION_ID_SELECTOR_BASE + ti.getBinderId());
		return reply;
	}
	
	/**
	 * Returns true if the Binder corresponding to this TreeInfo object
	 * should be selected and false otherwise.
	 * 
	 * @return
	 */
	private boolean isBinderSelected(TreeInfo ti) {
		return (Long.parseLong(ti.getBinderId()) == m_selectedBinderId);
	}

	/**
	 * Called to render the information in this TreeInfo object into a
	 * FlowPanel.
	 *
	 * @param selectedBinderId
	 * @param ri
	 * @param targetPanel
	 */
	public void render(String selectedBinderId, FlowPanel targetPanel) {
		// Track the Binder that's to be initially selected.
		setSelectedBinderId(selectedBinderId);
		
		// Create a hidden <INPUT> that we'll use to store the ID of
		// the currently selected Binder.
		Hidden selectedId = new Hidden();
		selectedId.getElement().setId(EXTENSION_ID_SELECTOR_ID);
		targetPanel.add(selectedId);
		
		// Create the WorkspaceTree control's header...
		Label selectorLabel = new Label(getRootTreeInfo().getBinderTitle());
		selectorLabel.addStyleName("workspaceTreeControlHeader");
		selectorLabel.getElement().setId(getSelectorId(getRootTreeInfo()));
		selectorLabel.getElement().setAttribute(EXTENSION_ID_TRASH_PERMALINK, getRootTreeInfo().getBinderTrashPermalink());
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(selectorLabel.getElement());
		selectorA.addClickHandler(new BinderSelector(getRootTreeInfo()));
		targetPanel.add(selectorA);

		// ...its content panel...
		Grid grid = new Grid();
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(2);
		grid.addStyleName("workspaceTreeControlBody");
		targetPanel.add(grid);
		
		// ...and if there are any rows to display...
		for (Iterator<TreeInfo> tii = getRootTreeInfo().getChildBindersList().iterator(); tii.hasNext(); ) {
			// ...render them.
			int row = grid.getRowCount();
			grid.insertRow(row);
			renderRow(grid, row, tii.next(), 0);
		}
	}
	
	/*
	 * Called to render an individual row in the WorkspaceTree control.
	 */
	private void renderRow(Grid grid, int row, TreeInfo ti, int renderDepth) {
		// Store the depth at which we're rendering this Binder.
		setRenderDepth(ti, renderDepth);
		
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
			expanderA.addClickHandler(new BinderExpander(ti, grid, row, expanderImg));
			expanderWidget = expanderA;
		}
		else {
			// No, it isn't expandable!  Put a 16x16 spacer in place of
			// the expander.
			expanderImgRes = getImages().spacer_1px();
			expanderImg = new Image(expanderImgRes);
			expanderImg.setWidth(EXPANDER_WIDTH);
			expanderImg.setHeight(EXPANDER_HEIGHT);
			expanderWidget = expanderImg;
		}

		// Generate the widgets to select the Binder.
		Grid selectorGrid = new Grid(1, 3);
		selectorGrid.setCellSpacing(0);
		selectorGrid.setCellPadding(0);
		Image binderImg;
		String binderIconName = ti.getBinderIconName();
		if (GwtClientHelper.hasString(binderIconName)) {
			binderImg = new Image();
			binderImg.setUrl(getImagesPath() + binderIconName);
		}
		else {
			ImageResource binderImgRes = ti.getBinderImage();
			if (null == binderImgRes) {
				binderImgRes = getImages().spacer_1px();
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
		selectorGrid.setWidget(0, 0, binderImg);
		Label selectorLabel = new Label(ti.getBinderTitle());
		selectorLabel.setWordWrap(false);
		selectorLabel.addStyleName("workspaceTreeBinderAnchor");
		selectorLabel.getElement().setId(getSelectorId(ti));
		if (!(ti.isBinderTrash())) {
			selectorLabel.getElement().setAttribute(
				EXTENSION_ID_TRASH_PERMALINK,
				ti.getBinderTrashPermalink());
		}
		selectorGrid.setWidget(0, 1, selectorLabel);
		selectorGrid.setWidget(0, 2, new Label("\u00A0"));
		selectorGrid.getCellFormatter().setWidth(0, 2, "100%");
		selectorGrid.setWidth(String.valueOf(SELECTOR_GRID_WIDTH - (SELECTOR_GRID_DEPTH_OFFSET * renderDepth)) + "px");
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(selectorGrid.getElement());
		selectorA.addClickHandler(new BinderSelector(ti));
		selectorA.setWidth("100%");
		String selectorGridId = (EXTENSION_ID_SELECTOR_ANCHOR + getSelectorId(ti));
		selectorGrid.getElement().setId(selectorGridId);
		selectorGrid.addStyleName("workspaceTreeControlRow");

		// If this Binder is supposed to be selected...
		if (isBinderSelected(ti)) {
			// ...select it.
			selectBinder(ti);
		}
		
		// Add the row to the Grid.
		grid.setWidget(row, 0, expanderWidget);
		grid.setWidget(row, 1, selectorA);

		// Install a mouse handler on the selector Anchor so that we
		// can manage hover overs on them.
		BinderSelectorMouseHandler bsmh = new BinderSelectorMouseHandler(selectorGridId);
		selectorA.addMouseOverHandler(bsmh);
		selectorA.addMouseOutHandler( bsmh);
		
		// Is the row showing an expander?
		if (showExpander) {
			// Yes!  Align it to the top of its cell.
			grid.getCellFormatter().setAlignment(
				row,
				0,
				HasHorizontalAlignment.ALIGN_LEFT,
				HasVerticalAlignment.ALIGN_TOP);

			// Is the row expanded?
			if (ti.isBinderExpanded()) {
				// Yes!  Then we need to render its contents.
				VerticalPanel vp = new VerticalPanel();
				vp.setSpacing(0);
				Widget w = grid.getWidget(row, 1);
				grid.remove(w);
				vp.add(w);
				Grid expansionGrid = new Grid();
				expansionGrid.setCellSpacing(0);
				expansionGrid.setCellPadding(0);
				expansionGrid.resizeColumns(2);
				vp.add(expansionGrid);
				grid.setWidget(row, 1, vp);
				for (Iterator<TreeInfo> tii = ti.getChildBindersList().iterator(); tii.hasNext(); ) {
					int expansionRow = expansionGrid.getRowCount();
					expansionGrid.insertRow(expansionRow);
					renderRow(expansionGrid, expansionRow, tii.next(), (renderDepth + 1));
				}
			}
		}
	}

	/*
	 * Clears and re-renders a TreeInfo object into a Grid row.
	 */
	private void reRenderRow(Grid grid, int row, TreeInfo ti) {
		clearRow(grid, row);
		renderRow(grid, row, ti, getRenderDepth(ti));
	}
	
	/**
	 * Does whatever is necessary UI wise to select the Binder
	 * represented by a TreeInfo.
	 * 
	 * Implementation of TreeDisplayBase.selectBinder().
	 * 
	 * @param ti
	 */
	void selectBinder(TreeInfo ti) {
		// If this isn't a trash Binder...
		if (!(ti.isBinderTrash())) {
			// ...mark it as having been selected.
			String selectedId_New = getSelectorId(ti);
			Element selectorLabel_New = Document.get().getElementById(selectedId_New);
			selectorLabel_New.addClassName("workspaceTreeBinderSelected");
			Element selectorPanel_New = Document.get().getElementById(EXTENSION_ID_SELECTOR_ANCHOR + selectedId_New);
			if (null != selectorPanel_New) {
				selectorPanel_New.addClassName("workspaceTreeControlRowSelected");
			}

			// ...mark any previous selection as not being selected...
			Element selectorId = Document.get().getElementById(EXTENSION_ID_SELECTOR_ID);
			String selectedId_Old = selectorId.getAttribute("value");
			if (GwtClientHelper.hasString(selectedId_Old)) {
				Element selectorLabel_Old = Document.get().getElementById(selectedId_Old);
				selectorLabel_Old.removeClassName("workspaceTreeBinderSelected");
				
				Element selectorPanel_Old = Document.get().getElementById(EXTENSION_ID_SELECTOR_ANCHOR + selectedId_Old);
				if (null != selectorPanel_Old) {
					selectorPanel_Old.removeClassName("workspaceTreeControlRowSelected");
				}
			}
			
			// ...and store the new ID as having been selected.
			selectorId.setAttribute("value", selectedId_New);
		}
	}

	/*
	 * Store the depth at which a TreeInfo object was last rendered.
	 */
	private void setRenderDepth(TreeInfo ti, int depth) {
		m_renderDepths.put(ti.getBinderId(), new Integer(depth));
	}
	
	/*
	 * Stores which Binder is selected.
	 */
	private void setSelectedBinderId(String selectedBinderId) {
		m_selectedBinderId = Long.parseLong(selectedBinderId);
	}
}
