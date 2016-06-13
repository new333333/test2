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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.BreadCrumbPanel;
import org.kablink.teaming.gwt.client.binderviews.DescriptionPanel;
import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.FilterPanel;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.rpc.shared.CanAddEntitiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetCanAddEntitiesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderHasOtherComponentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyFilesContainerInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.HasOtherComponentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.Html5UploadHelper;
import org.kablink.teaming.gwt.client.util.Html5UploadCallback;
import org.kablink.teaming.gwt.client.util.Html5UploadHost;
import org.kablink.teaming.gwt.client.util.Html5UploadState;
import org.kablink.teaming.gwt.client.widgets.Html5UploadPopup;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Folder view base class.  All folder views should be based off this
 * class.
 * 
 * Provides common housekeeping for a folder view including:
 * 1. Managing various tool panels (e.g., bread crumbs,
 *    accessories, ...)
 * 2. Provides access to various pieces of information about the folder
 *    (e.g., BinderInfo, folder display data, ...)
 * 3. Manages load synchronization for the various components that make
 *    up a folder view.
 * 
 * @author drfoster@novell.com
 */
public abstract class FolderViewBase extends ViewBase
	implements
		DragEnterHandler,
		DragLeaveHandler,
		DragOverHandler,
		DropHandler,
		Html5UploadCallback,
		Html5UploadHost,
		ToolPanelReady
{
	private BinderInfo							m_folderInfo;					// A BinderInfo object that describes the folder being viewed.
	private boolean								m_allowColumnSizing;			// true -> Add the column sizing entry menu item.  false -> Don't.
	private boolean								m_viewReady;					// Set true once the view and all its components are ready.
	private boolean								m_pinning;						// true -> We're showing pinned items.                      false -> We're not.
	private boolean								m_sharedFiles;					// true -> We're showing shared only shared files.          false -> We're not.
	private boolean								m_showUserList;					// true -> We're showing the UserListPanel in the view.     false -> We're not.
	private CalendarDisplayDataProvider			m_calendarDisplayDataProvider;	// A CalendarDisplayDataProvider to use to obtain a CalendarDisplayDataRpcResponseData object.
	private CanAddEntitiesRpcResponseData		m_canAddEntities;				// Contains information about the user's rights to add entities to the current folder.
	private DropPanel							m_dndPanel;						// A DropPanel used for HTML5 based file uploads.
	private FolderDisplayDataRpcResponseData	m_folderDisplayData;			// Various pieces of display information about the folder (sorting, page size, column widths, ...)
	private Html5UploadHelper					m_uploadHelper;					// The HTML5 upload APIs.
	private Html5UploadPopup					m_uploadPopup;					// A popup that's shown while a HTML5 file upload is active.
	private int									m_readyComponents;				// Tracks items as they become ready.
	private Label								m_dndHint;						// A hint displayed while the user is dragging files over the view.
	private List<Widget>						m_verticalPanels;				// Tracks the widgets added as vertical panels.
	private SpinnerPopup						m_busySpinner;					// A spinner shown while something is going on that may take a while.
	private String								m_styleBase;					// Base name for the view specific styles to use for this view.
	private VibeFlowPanel						m_flowPanel;					// The flow panel used to hold the view specific content of the view.
	private VibeFlowPanel						m_rootPanel;					// The root panel containing the view.
	private VibeFlowPanel						m_verticalFlowPanel;			// The flow panel that holds all the components of the view, both common and view specific, that flow vertically down the view.

	// Control whether a FilterPanel can ever be instantiated.  true
	// and they can and false and they can't.
	//
	// Note:  The FilterPanel implements what amounts to same filter
	//    handling as was done in the old JSP code.  Filters are now
	//    integrated in the entry menu so that filter panel is no
	//    loner needed.
	private final static boolean SHOW_LEGACY_FILTERS	= false;
	
	// Control whether the FooterPanel is shown when in Filr mode.
	private final static boolean SHOW_FILR_FOOTER	= true;
	
	// Amount of time the initial resizing of a view should delay to
	// wait for things to stabilize.
	public final static int INITIAL_RESIZE_DELAY	= 500;
	
	// Amount of time the a view should delay to wait for things to
	// stabilize before querying the entry menu about its contents.
	public final static int ENTRY_MENU_READY_DELAY	= 500;
	
	// The following define the indexes into a VibeVerticalPanel of the
	// various panels that make up a folder view.
	public final static int DOWNLOAD_PANEL_INDEX			=  0;
	public final static int MAILTO_PANEL_INDEX				=  1;
	public final static int BREADCRUMB_PANEL_INDEX			=  2;
	public final static int ACCESSORY_PANEL_INDEX			=  3;
	public final static int DESCRIPTION_PANEL_INDEX			=  4;
	public final static int TASK_GRAPHS_PANEL_INDEX			=  5;
	public final static int FILTER_PANEL_INDEX				=  6;
	public final static int BINDER_OWNER_AVATAR_PANEL_INDEX	=  7;
	public final static int USER_LIST_PANEL_INDEX			=  8;
	public final static int ENTRY_MENU_PANEL_INDEX			=  9;
	public final static int CALENDAR_NAVIGATION_PANEL_INDEX	= 10;
	public final static int VIEW_CONTENT_PANEL_INDEX		= 11;
	public final static int FOOTER_PANEL_INDEX				= 12;

	private final static int MINIMUM_CONTENT_HEIGHT		= 150;	// The minimum height (in pixels) of a the data table widget.
	private final static int NO_VSCROLL_ADJUST			=  20;	// Height adjustment required so there's no vertical scroll bar by default.
	
	/*
	 * Enumeration that identifies the various optional panels that
	 * make up a folder view.
	 */
	protected enum FolderPanels {
		DOWNLOAD,
		MAILTO,
		BREADCRUMB,
		ACCESSORIES,
		DESCRIPTION,
		TASK_GRAPHS,
		FILTER,
		BINDER_OWNER_AVATAR,
		USER_LIST,
		ENTRY_MENU,
		CALENDAR_NAVIGATION,
		FOOTER,
	}

	/*
	 * Inner class used to provide a DropPanel that reacts to sizing
	 * events.
	 */
	private class ResizingDropPanel extends DropPanel implements RequiresResize {
		/**
		 * Constructor method.
		 */
		public ResizingDropPanel() {
			// Initialize the super class.
			super();
		}
		
		/**
		 */
		@Override
		public void onResize() {
			onResizeAsync();
		}

		/*
		 * Asynchronously resizes the panel.
		 */
		private void onResizeAsync() {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					onResizeNow();
				}
			});
		}
		
		/*
		 * Synchronously resizes the panel.
		 */
		private void onResizeNow() {
			//setHeight(getNonNegativeInt(m_rootPanel.getOffsetHeight() + getContentHeightAdjust()) + "px");
			//setWidth( getNonNegativeInt(m_rootPanel.getOffsetWidth()  + getContentWidthAdjust())  + "px");
			
			m_verticalFlowPanel.onResize();
		}
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param styleBase
	 * @param allowColumnSizing
	 */
	public FolderViewBase(BinderInfo folderInfo, ViewReady viewReady, String styleBase, boolean allowColumnSizing) {
		// Initialize the super class...
		super(viewReady);

		// ...store the parameters...
		m_folderInfo        = folderInfo;
		m_styleBase         = ((GwtClientHelper.hasString(styleBase) ? styleBase : "vibe-folderView"));
		m_allowColumnSizing = allowColumnSizing;
		
		// ...create the main content panels and initialize the
		// ...composite...
		initWidget(constructInitialContent());

		// ...and finally, asynchronously load the various parts of the
		// ...view.
		loadPart1Async();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	final public BinderInfo                       getFolderInfo()        {return m_folderInfo;                         }	// The binder being viewed.
	final public boolean                          isPinning()            {return m_pinning;                            }	//
	final public boolean                          isProfilesRootWS()     {return m_folderInfo.isBinderProfilesRootWS();}	//
	final public boolean                          isSharedFiles()        {return m_sharedFiles;                        }	//
	final public boolean                          isTrash()              {return m_folderInfo.isBinderTrash();         }	//
	final public FolderDisplayDataRpcResponseData getFolderDisplayData() {return m_folderDisplayData;                  }	//
	final public Long                             getFolderId()          {return m_folderInfo.getBinderIdAsLong();     }	//
	final public VibeFlowPanel                    getFlowPanel()         {return m_flowPanel;                          }	// Flow panel holding the view's content (no toolbars, ...)

	/**
	 * Returns true if the entry viewer should include next/previous
	 * buttons and false otherwise. 
	 *
	 * Classes that extend this class can override this method to
	 * inhibit the next/previous buttons on an entry view.
	 * 
	 * @return
	 */
	protected boolean allowNextPrevOnEntryView() {
		// By default, all folder views support next/previous on entry
		// views.
		return true;
	}
	
	/*
	 * Returns a Widget to use for tool panels that aren't used.
	 */
	private Widget buildToolPanelPlaceholder() {
		// We don't use a place holder for empty tools.
		return null;
	}
	
	/*
	 * Checks how many items are ready and once everything is, calls
	 * the super class' viewReady() method.
	 */
	private void checkReadyness() {
		// Count the tool panels we've got.
		int toolPanels = 0;
		for (Widget w: m_verticalPanels) {
			if ((null != w) && (w instanceof ToolPanelBase)) {
				toolPanels += 1;
			}
		}
		
		// If everything's ready...
		if ((toolPanels + 1) == m_readyComponents) {	// Count of tool panels plus 1 for the view itself.
			// ...tell the super class.
			m_viewReady = true;
			viewComplete();
			super.viewReady();
		}
	}
	
	/*
	 * Creates the initial content panels, ... required by folder
	 * views.
	 */
	private VibeFlowPanel constructInitialContent() {
		// Initialize various data members of the class...
		initDataMembers();
		
		// ...create the main panel for the content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-folderViewBase " + m_styleBase + " vibe-verticalScroll");

		// ...set the sizing adjustments the account for the padding in
		// ...the vibe-folderViewBase style...
		final int padAdjust = (2 * GwtConstants.PANEL_PADDING);
		setContentHeightAdjust(getContentHeightAdjust() - padAdjust);
		setContentWidthAdjust( getContentWidthAdjust()  - padAdjust);

		// ...create a vertical panel to holds the layout that flows
		// ...down the view...
		m_verticalFlowPanel = new VibeFlowPanel();
		m_verticalFlowPanel.addStyleName(m_styleBase + "VerticalPanel");
	
		// ...create a flow panel to put the main content...
		m_flowPanel = new VibeFlowPanel();
		m_flowPanel.addStyleName(m_styleBase + "FlowPanel");

		// ...if the browser supports HTML5 uploads...
		if (GwtClientHelper.jsBrowserSupportsHtml5FileAPIs()) {
			// ...create the panel we'll show while HTML5 uploads are
			// ...active...
			m_uploadPopup = new Html5UploadPopup();
		}
		
		// ...tie everything together...
		trackVerticalPanel(VIEW_CONTENT_PANEL_INDEX, m_flowPanel);
		m_rootPanel.add(m_verticalFlowPanel);
		
		// ...and finally, add an edit-in-place frame.
		m_rootPanel.add(BinderViewsHelper.createEditInPlaceFrame());
		
		return m_rootPanel;
	}

	/**
	 * Called to construct the view.
	 * 
	 * Implemented by classes that extend this base class so that they
	 * can construct and reset the main content of their view.
	 */
	public abstract void constructView();
	public abstract void resetView();
	
	/*
	 * Asynchronously tells the view to construct itself.
	 */
	private void constructViewAsync() {
		// Scan the widgets that were defined for the vertical flow...
		for (Widget w:  m_verticalPanels) {
			if (null != w) {
				// ...adding each to the vertical flow panel...
				m_verticalFlowPanel.add(w);
			}
		}

		// Is this folder a drop target?
		if (supportsDragAndDrop()) {
			// Yes!  Then we need to make it a drop target.  Create a
			// drag and drop panel for dropping into...
			m_dndPanel = new ResizingDropPanel();
			m_dndPanel.addStyleName("vibe-folderViewBase-dndPanel");
			
			// ...connect the various drag and drop handlers to it...
			m_dndPanel.addDragEnterHandler(this);
			m_dndPanel.addDragLeaveHandler(this);
			m_dndPanel.addDragOverHandler( this);
			m_dndPanel.addDropHandler(     this);

			// ...and add it to the flow panel.
			m_rootPanel.remove(m_verticalFlowPanel);
			m_rootPanel.add(m_dndPanel);
			m_dndPanel.add(m_verticalFlowPanel);
		}
		
		// Create a hint we'll show at the top of the window while
		// dragging over it.  We always add this because although the
		// view itself may not be a drop target, one of its nested
		// folders might be, in which case, it will show it.
		m_dndHint = new Label(m_messages.addFilesHtml5PopupDnDHintProd(GwtClientHelper.getProductName()));
		m_dndHint.addStyleName("vibe-folderViewBase-dndHint");
		m_dndHint.setVisible(false);
		m_rootPanel.add(m_dndHint);

		// Finally, asynchronously complete the view construction.
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				constructView();
			}
		});
	}

	/**
	 * Some folders were in the files requested to be uploaded and
	 * were skipped.
	 * 
	 * @param count
	 * @param folderNames
	 * 
	 * Implements the Html5UploadCallback.foldersSkipped() method.
	 */
	@Override
	public void foldersSkipped(int count, String folderNames) {
		GwtClientHelper.deferredAlert(
			m_messages.addFilesHtml5PopupFoldersSkipped(
				folderNames));
	}

	/**
	 * Returns the drag and drop hint Label for the view.
	 * 
	 * Implements the Html5UploadHost.getDndHintLabel() method.
	 */
	@Override
	public Label getDnDHintLabel() {
		return m_dndHint;
	}
	
	/**
	 * Scan the defined tool panels for a download panel and returns
	 * it.
	 * 
	 * @return
	 */
	public DownloadPanel getDownloadPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find a download panel...
			if ((null != w) && (w instanceof DownloadPanel)) {
				// ...return it.
				return ((DownloadPanel) w);
			}
		}
		
		// If we get here, a download panel isn't defined.  Return
		// null.
		return null;
	}
	
	/**
	 * Scan the defined tool panels for an entry menu panel and returns
	 * it.
	 * 
	 * @return
	 */
	public EntryMenuPanel getEntryMenuPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find an entry menu panel...
			if ((null != w) && (w instanceof EntryMenuPanel)) {
				// ...return it.
				return ((EntryMenuPanel) w);
			}
		}
		
		// If we get here, an entry menu panel isn't defined.  Return
		// null.
		return null;
	}
	
	/**
	 * Scan the defined tool panels for a footer panel and returns it.
	 * 
	 * @return
	 */
	public FooterPanel getFooterPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find a footer panel...
			if ((null != w) && (w instanceof FooterPanel)) {
				// ...return it.
				return ((FooterPanel) w);
			}
		}
		
		// If we get here, a footer panel isn't defined.  Return null.
		return null;
	}
	
	/**
	 * Returns the HTML5 upload helper for the view.
	 * 
	 * Implements the Html5UploadHost.getHtml5UploadHelper() method.
	 */
	@Override
	public Html5UploadHelper getHtml5UploadHelper() {
		return m_uploadHelper;
	}
	
	/**
	 * Returns the HTML5 upload popup for the view.
	 * 
	 * Implements the Html5UploadHost.getHtml5UploadPopup() method.
	 */
	@Override
	public Html5UploadPopup getHtml5UploadPopup() {
		return m_uploadPopup;
	}
	
	/**
	 * Scan the defined tool panels for a mail to panel and returns
	 * it.
	 * 
	 * @return
	 */
	public MailToPanel getMailToPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find a mail to panel...
			if ((null != w) && (w instanceof MailToPanel)) {
				// ...return it.
				return ((MailToPanel) w);
			}
		}
		
		// If we get here, a mail to panel isn't defined.  Return null.
		return null;
	}
	
	/**
	 * Returns the minimum height to used for a folder view's content.
	 * 
	 * Classes that extend this can override as needed.
	 * 
	 * @return
	 */
	public int getMinimumContentHeight() {
		return MINIMUM_CONTENT_HEIGHT;
	}

	/*
	 * Ensures an integer value is >= 0.
	 */
	private static int getNonNegativeInt(int value) {
		if (0 > value) {
			value = 0;
		}
		return value;
	}
	
	/**
	 * Returns the adjustment to used for a folder view's content so
	 * that it doesn't get a vertical scroll bar.
	 * 
	 * Classes that extend this can override as needed.
	 * 
	 * @return
	 */
	public int getNoVScrollAdjustment() {
		return NO_VSCROLL_ADJUST;
	}

	/**
	 * Scan the defined tool panels for a task graphs panel and returns
	 * it.
	 * 
	 * @return
	 */
	public TaskGraphsPanel getTaskGraphsPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find a task graphs panel...
			if ((null != w) && (w instanceof TaskGraphsPanel)) {
				// ...return it.
				return ((TaskGraphsPanel) w);
			}
		}
		
		// If we get here, a task graphs panel isn't defined.  Return
		// null.
		return null;
	}
	
	/**
	 * If a busy spinner exists, hide it.
	 */
	final public void hideBusySpinner() {
		// If we have a busy spinner...
		if (null != m_busySpinner) {
			// ...make sure that it's hidden.
			m_busySpinner.hide();
		}
	}

	/**
	 * Advance the progress indicator.
	 * 
	 * @param amount
	 * 
	 * Implements the Html5UploadCallback.incrProgress() method.
	 */
	@Override
	public void incrProgress(long amount) {
		// If we have an HTML5 upload popup...
		if (null != m_uploadPopup) {
			// ...increment its progress.
			m_uploadPopup.incrPerItemProgress(amount);
			m_uploadPopup.incrTotalProgress(  amount);
		}
	}

	/**
	 * Returns true if a panel should be loaded and false otherwise.
	 *
	 * Classes that extend this class can override this method to
	 * inhibit the inclusion of various panels as they see fit.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	protected boolean includePanel(FolderPanels folderPanel) {
		// Unless overridden, all panels except the mail to panel,
		// binder owner avatar panel, task graphs panel and calendar
		// navigation panel are included.
		boolean reply;
		switch (folderPanel) {
		case MAILTO:
		case BINDER_OWNER_AVATAR:
		case CALENDAR_NAVIGATION:
		case TASK_GRAPHS:   reply = false;             break;
		case USER_LIST:     reply = m_showUserList;    break;
		default:            reply = true;              break;
		}
		return reply;
	}
	
	/*
	 * Initializes various data members for the class.
	 */
	private void initDataMembers() {
		// Allocate an ArrayList<Widget> to track the panels we add
		// vertically to the view.
		m_verticalPanels = new ArrayList<Widget>();
	}

	/**
	 * Inserts a tool panel into the view.
	 * 
	 * @param tpb
	 * @param tpIndex
	 */
	final public void insertToolPanel(ToolPanelBase tpb, int tpIndex) {
		trackVerticalPanel(tpIndex, tpb);
	}
	
	/**
	 * Inserts a tool panel place holder into the view.
	 * 
	 * @param tpb
	 * @param tpIndex
	 */
	final public void insertToolPanelPlaceholder(int tpIndex) {
		trackVerticalPanel(tpIndex, buildToolPanelPlaceholder());
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads whether the user has rights to add entities to this
	 * folder.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}

	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads whether the user has rights to add entities to this
	 * folder.
	 */
	private void loadPart1Now() {
		GwtClientHelper.executeCommand(
				new GetCanAddEntitiesCmd(m_folderInfo),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetCanAddEntities(),
					m_folderInfo.getBinderIdAsLong());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the information about what rights the user has
				// to add things to the current folder.
				m_canAddEntities = ((CanAddEntitiesRpcResponseData) response.getResponseData());
				loadPart2Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 */
	private void loadPart2Async() {
		// Note that we always load the HTML5 helper, even if the
		// folder itself can't be a drop target.  The reason is that it
		// may contain nested folders that can be drop targets and this
		// HTML5 helper will be used for those, with this folder's
		// listing.
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
	}

	/*
	 * Synchronously loads the next part of the view.
	 */
	private void loadPart2Now() {
		// Does the browser support HTML5 uploads?
		if (GwtClientHelper.jsBrowserSupportsHtml5FileAPIs()) {
			// Yes!  Note that the implementation of
			// Html5UploadCallback.onSuccess() invokes part 3 of the
			// load process.  Simply create the HTML5 upload helper.
			Html5UploadHelper.createAsync(this);
		}
		
		else {
			// No, the browser doesn't support HTML5 uploads!  Continue
			// with the next part of the load.
			loadPart3Now();
		}
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
	 */
	private void loadPart3Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		});
	}

	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
	 */
	private void loadPart3Now() {
		// Are we loading a calendar folder?
		if (FolderType.CALENDAR == m_folderInfo.getFolderType()) {
			// Yes!  Then we don't need the folder display data as
			// it pertains to data on table based views.  We do,
			// however, need to know if we should show the
			// UserListPanel in the view.
			GwtClientHelper.executeCommand(
					new GetBinderHasOtherComponentsCmd(m_folderInfo),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetBinderHasOtherComponents(),
						m_folderInfo.getBinderIdAsLong());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the show user list flag and tell the view
					// to construct itself.
					HasOtherComponentsRpcResponseData reply = ((HasOtherComponentsRpcResponseData) response.getResponseData());
					m_showUserList    = reply.getShowUserList();
					loadPart4Async();
				}
			});
		}
		
		else {
			// No, we aren't loading a calendar folder!  Load the
			// display data for the folder.
			GwtClientHelper.executeCommand(
					new GetFolderDisplayDataCmd(m_folderInfo),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetFolderDisplayData(),
						m_folderInfo.getBinderIdAsLong());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the core folder display data and tell the view
					// to construct itself.
					m_folderDisplayData = ((FolderDisplayDataRpcResponseData) response.getResponseData());
					m_pinning           = m_folderDisplayData.getViewPinnedEntries();
					m_sharedFiles       = m_folderDisplayData.getViewSharedFiles();
					m_showUserList      = m_folderDisplayData.getShowUserList();
					loadPart4Async();
				}
			});
		}
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DownloadPanel.
	 */
	private void loadPart4Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.DOWNLOAD))) {
			// ...we don't show the submit panel.
			insertToolPanelPlaceholder(DOWNLOAD_PANEL_INDEX);
			loadPart5Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart4Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the DownloadPanel.
	 */
	private void loadPart4Now() {
		DownloadPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, DOWNLOAD_PANEL_INDEX);
				loadPart5Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the MailToPanel.
	 */
	private void loadPart5Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.MAILTO))) {
			// ...we don't show the submit panel.
			insertToolPanelPlaceholder(MAILTO_PANEL_INDEX);
			loadPart6Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart5Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the MailToPanel.
	 */
	private void loadPart5Now() {
		MailToPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, MAILTO_PANEL_INDEX);
				loadPart6Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart6Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.BREADCRUMB))) {
			// ...we don't show the bread crumbs.
			insertToolPanelPlaceholder(BREADCRUMB_PANEL_INDEX);
			loadPart7Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart6Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart6Now() {
		BreadCrumbPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, BREADCRUMB_PANEL_INDEX);
				loadPart7Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart7Async() {
		// If we're in Filr mode or a super class doesn't want it...
		if (GwtClientHelper.isLicenseFilr() || (!(includePanel(FolderPanels.ACCESSORIES)))) {
			// ...we don't show the accessories.
			insertToolPanelPlaceholder(ACCESSORY_PANEL_INDEX);
			loadPart8Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart7Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart7Now() {
		AccessoriesPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, ACCESSORY_PANEL_INDEX);
				loadPart8Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart8Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.DESCRIPTION))) {
			// ...we don't show the description.
			insertToolPanelPlaceholder(DESCRIPTION_PANEL_INDEX);
			loadPart9Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart8Now();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart8Now() {
		DescriptionPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, DESCRIPTION_PANEL_INDEX);
				loadPart9Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart9Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.TASK_GRAPHS))) {
			// ...we don't show the task graphs panel.
			insertToolPanelPlaceholder(TASK_GRAPHS_PANEL_INDEX);
			loadPart10Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart9Now();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart9Now() {
		TaskGraphsPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, TASK_GRAPHS_PANEL_INDEX);
				loadPart10Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	@SuppressWarnings("unused")
	private void loadPart10Async() {
		// For classes that don't want it...
		if ((!SHOW_LEGACY_FILTERS) || (!(includePanel(FolderPanels.FILTER)))) {
			// ...we don't show the filter.
			insertToolPanelPlaceholder(FILTER_PANEL_INDEX);
			loadPart11Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart10Now();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	private void loadPart10Now() {
		FilterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FILTER_PANEL_INDEX);
				loadPart11Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart11Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.ENTRY_MENU))) {
			// ...we don't show the entry menu.
			insertToolPanelPlaceholder(ENTRY_MENU_PANEL_INDEX);
			loadPart12Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart11Now();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart11Now() {
		EntryMenuPanel.createAsync(this, m_folderInfo, isPinning(), isSharedFiles(), m_allowColumnSizing, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, ENTRY_MENU_PANEL_INDEX);
				loadPart12Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart12Async() {
		// If we need to show the footer panel...
		boolean showFooter = includePanel(FolderPanels.FOOTER);
		if (showFooter) {
			if (!SHOW_FILR_FOOTER) {
				showFooter = (!(GwtClientHelper.isLicenseFilr()));
			}
		}
		
		if (showFooter) {
			// ...load it...
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					loadPart12Now();
				}
			});
		}
		
		else {
			// ...otherwise, insert a place holder for it.
			insertToolPanelPlaceholder(FOOTER_PANEL_INDEX);
			loadPart13Async();
		}
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart12Now() {
		FooterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FOOTER_PANEL_INDEX);
				loadPart13Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 */
	private void loadPart13Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.BINDER_OWNER_AVATAR))) {
			// ...we don't show the binder owner avatar.
			insertToolPanelPlaceholder(BINDER_OWNER_AVATAR_PANEL_INDEX);
			loadPart14Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart13Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 */
	private void loadPart13Now() {
		BinderOwnerAvatarPanel.createAsync(this, getFolderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FolderViewBase.BINDER_OWNER_AVATAR_PANEL_INDEX);
				loadPart14Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 */
	private void loadPart14Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.USER_LIST))) {
			// ...we don't show the calendar navigation panel.
			insertToolPanelPlaceholder(USER_LIST_PANEL_INDEX);
			loadPart15Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart14Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 */
	private void loadPart14Now() {
		UserListPanel.createAsync(this, getFolderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FolderViewBase.USER_LIST_PANEL_INDEX);
				loadPart15Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 */
	private void loadPart15Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.CALENDAR_NAVIGATION))) {
			// ...we don't show the calendar navigation panel.
			insertToolPanelPlaceholder(CALENDAR_NAVIGATION_PANEL_INDEX);
			constructViewAsync();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart15Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 */
	private void loadPart15Now() {
		CalendarNavigationPanel.createAsync(this, m_calendarDisplayDataProvider, getFolderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FolderViewBase.CALENDAR_NAVIGATION_PANEL_INDEX);
				constructViewAsync();
			}
		});
	}

	/**
	 * Called when the folder view is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		super.onAttach();
		GwtClientHelper.jsSetAllowNextPrevOnView(allowNextPrevOnEntryView());
	}
	
	/**
	 * Called when the folder view is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		GwtClientHelper.jsSetAllowNextPrevOnView(false);
	}
	
	/**
	 * Called when a something is dragged into the drag and drop popup.
	 * 
	 * Implements the DragEnterHandler.onDragEnter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragEnter(DragEnterEvent event) {
		if (null != m_uploadHelper) {
			if (!(m_uploadHelper.uploadsPending())) {
				setDnDHighlight(true);
			}
			event.stopPropagation();
			event.preventDefault();
		}
	}
	
	/**
	 * Called when a something is dragged out of the drag and drop
	 * popup.
	 * 
	 * Implements the DragEnterHandler.onDragLeave() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragLeave(DragLeaveEvent event) {
		if (null != m_uploadHelper) {
			if (!(m_uploadHelper.uploadsPending())) {
				setDnDHighlight(false);
			}
			event.stopPropagation();
			event.preventDefault();
		}
	}

	/**
	 * Called when a something is being dragged over the drag and drop
	 * popup.
	 * 
	 * Implements the DragEnterHandler.onDragOver() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragOver(DragOverEvent event) {
		if (null != m_uploadHelper) {
			// Mandatory handler, otherwise the default behavior will
			// kick in and onDrop will never be called.
			if (!(m_uploadHelper.uploadsPending())) {
				setDnDHighlight(true);
			}
			event.stopPropagation();
			event.preventDefault();
		}
	}

	/**
	 * Called when a something is dropped on the drag and drop popup.
	 * 
	 * Implements the DragEnterHandler.onDrop() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDrop(DropEvent event) {
		if (null != m_uploadHelper) {
			if (!(m_uploadHelper.uploadsPending())) {
				setDnDHighlight(false);
				
				// If the drop data doesn't contain any files...
				FileList fileList = event.getDataTransfer().<DataTransferExt>cast().getFiles();
				int files = ((null == fileList) ? 0 : fileList.getLength());
				if (0 == files) {
					// ...tell the user about the problem...
					String warning;
					if (GwtClientHelper.jsIsAnyIE())
					     warning = m_messages.html5Uploader_Warning_NoFilesIE();
					else warning = m_messages.html5Uploader_Warning_NoFiles();
					GwtClientHelper.deferredAlert(warning);
				}
				else {
					// ...otherwise, process the files that were
					// ...dropped...
					processFiles(fileList);
				}
			}
			event.stopPropagation();
			event.preventDefault();
		}
	}

	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class and do what we need to
		// do to resize the view.
		super.onResize();
		resizeViewAsync();
	}
	
	/**
	 * The helper was successfully loaded and is available for
	 * uploading files.
	 * 
	 * @param uploadHelper
	 * 
	 * Implements the Html5UploadCallback.onSuccess() method.
	 */
	@Override
	public void onSuccess(Html5UploadHelper uploadHelper) {
		// Store the helper and continue with the load process.
		m_uploadHelper = uploadHelper;
		loadPart3Async();
	}

	/**
	 * The helper failed to load.
	 * 
	 * Note that the user will have been told about the failure.
	 * 
	 * Implements the Html5UploadCallback.onUnavailable() method.
	 */
	@Override
	public void onUnavailable() {
		// The user will have been told about the error in the
		// createAsync() call.  Simply store null for the upload helper
		// and continue with the load process.
		m_uploadHelper = null;
		loadPart3Async();
	}

	/*
	 * Called when some files are dropped on the panel.
	 */
	private void processFiles(final FileList fileList) {
		// Are we uploading into the 'My Files' view?
		final List<File> files = Html5UploadHelper.getListFromFileList(fileList);
		if (m_folderInfo.isBinderCollection() && (CollectionType.MY_FILES.equals(m_folderInfo.getCollectionType()))) {
			// Yes!  Then we need to upload into the 'My Files Storage'
			// folder instead.
			final GetMyFilesContainerInfoCmd cmd = new GetMyFilesContainerInfoCmd();
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetMyFilesContainerInfo());
				}

				@Override
				public void onSuccess(final VibeRpcResponse result) {
					// Perform the upload to the 'My Files Storage'
					// folder.
					processFilesAsync((BinderInfo) result.getResponseData(), files);
				}
			});
		}
		
		else {
			// No, we aren't we uploading into the 'My Files' view!
			// Perform the upload to the folder directly.
			processFilesAsync(m_folderInfo, files);
		}
	}
	
	/*
	 * Asynchronously process files dropped on the panel.
	 */
	private void processFilesAsync(final BinderInfo fi, final List<File> files) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processFilesNow(fi, files);
			}
		});
	}
	
	/*
	 * Synchronously process files dropped on the panel.
	 */
	private void processFilesNow(BinderInfo fi, List<File> files) {
		// If we have an HTML5 upload popup...
		if ((null != m_uploadPopup) && (null != m_uploadHelper)) {
			// ...show it and start the upload.
			m_uploadPopup.setHtml5UploadHelper(m_uploadHelper);
			m_uploadPopup.setActive(           true          );
			Html5UploadHelper.uploadFiles(
				m_uploadHelper,
				fi,
				files,
				new FullUIReloadEvent());
		}
	}

	/**
	 * An error occurred uploading a file.
	 * 
	 * @param fileName
	 * @param errorDescription
	 * 
	 * Implements the Html5UploadCallback.readError() method.
	 */
	@Override
	public void readError(String fileName, String errorDescription) {
		GwtClientHelper.deferredAlert(
			m_messages.addFilesHtml5PopupReadError(
				fileName,
				errorDescription));
	}

	/**
	 * Does what's necessary to reset the view.
	 */
	final public void resetContent() {
		// Clear the flow panel's content...
		m_flowPanel.clear();
		
		// ...tell the tool panels to perform any resetting they need
		// ...to do...
		for (Widget w:  m_verticalPanels) {
			if ((null != w) && (w instanceof ToolPanelBase)) {
				((ToolPanelBase) w).resetPanel();
			}
		}
		
		// ...and reset any other information.
		m_readyComponents = 0;
		m_viewReady       = false;
	}
	
	/**
	 * Called to resize the view.
	 * 
	 * Implemented by classes that extend this base class so that they
	 * can take whatever action is necessary to resize their view.
	 */
	public abstract void resizeView();
	
	/*
	 * Asynchronously tells the view to resize itself.
	 */
	private void resizeViewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				resizeView();
			}
		});
	}

	/**
	 * Stores a CalendarDisplayDataProvider for use by folder view.
	 * 
	 * @param calendarDisplayDataProvider
	 */
	public void setCalendarDisplayDataProvider(CalendarDisplayDataProvider calendarDisplayDataProvider) {
		m_calendarDisplayDataProvider = calendarDisplayDataProvider;
	}

	/*
	 * Sets or clears the drag and drop highlighting on the view.
	 */
	private void setDnDHighlight(boolean highlight) {
		if (highlight) {
			m_rootPanel.addStyleName("vibe-folderViewBase-hover"        );
			m_dndPanel.addStyleName( "vibe-folderViewBase-dndPanelHover");
			m_dndHint.setVisible(     true                              );
		}
		
		else {
			m_rootPanel.removeStyleName("vibe-folderViewBase-hover"        );
			m_dndPanel.removeStyleName( "vibe-folderViewBase-dndPanelHover");
			m_dndHint.setVisible(        false                             );
		}
	}
	
	/**
	 * Sets the current files progress indicator.
	 * 
	 * @param min
	 * @param max
	 * 
	 * Implements the Html5UploadCallback.setPerItemProgress() method.
	 */
	@Override
	public void setPerItemProgress(long min, long max) {
		// If we have an HTML5 upload popup...
		if (null != m_uploadPopup) {
			// ...set its progress indicator.
			m_uploadPopup.setPerItemProgress(0, min, max);
		}
	}

	/**
	 * Sets the current pinning state.
	 * 
	 * @param pinning
	 */
	public void setPinning(boolean pinning) {
		m_pinning = pinning;
	}
	
	/**
	 * Sets the current shared files state.
	 * 
	 * @param sharedFiles
	 */
	public void setSharedFiles(boolean sharedFiles) {
		m_sharedFiles = sharedFiles;
	}
	
	/**
	 * Update the total progress to the specified value.
	 * 
	 * @param amount
	 * 
	 * Implements the Html5UploadCallback.setTotalCurrentProgress() method.
	 */
	@Override
	public void setTotalCurrentProgress(double amount) {
		// If we have an HTML5 upload popup...
		if (null != m_uploadPopup) {
			// ...set its total current progress.
			m_uploadPopup.setTotalCurrentProgress(amount);
		}
	}

	/**
	 * Set the total maximum progress value. 
	 * 
	 * @param max
	 * 
	 * Implements the Html5UploadCallback.setTotalMaxProgress() method.
	 */
	@Override
	public void setTotalMaxProgress(double max) {
		// If we have an HTML5 upload popup...
		if (null != m_uploadPopup) {
			// ...set its total progress.
			m_uploadPopup.setTotalProgress(0, 0, max);
		}
	}

	/**
	 * Sets the current state of the upload.
	 * 
	 * @param previousState
	 * @param newState
	 * 
	 * Implements the Html5UploadCallback.setUploadState() method.
	 */
	@Override
	public void setUploadState(Html5UploadState previousState, Html5UploadState newState) {
		// Do we have an HTML5 upload popup?
		if ((null != m_uploadPopup) && (null != m_uploadHelper)) {
			// What upload state is being set?
			switch (newState) {
			case UPLOADING:
				// We're uploading a file!  If we weren't previously
				// performing an upload...
				if (!(previousState.equals(Html5UploadState.UPLOADING))) {
					// ...update the upload popup to show that we are
					// ...now.
					m_uploadPopup.setAbortVisible(           true                             );
					m_uploadPopup.setProgressBarsVisible(    true                             );
					m_uploadPopup.setTotalProgressBarVisible(1 < m_uploadHelper.getReadTotal());
				}
				break;
				
			case INACTIVE:
				// We're waiting for user input!  Restore the upload
				// popup to its initial state.
				m_uploadPopup.setAbortVisible(       true                               );
				m_uploadPopup.setUploadHint(         m_messages.addFilesHtml5PopupHint());
				m_uploadPopup.setProgressBarsVisible(false                              );
				m_uploadPopup.setActive(             false                              );
				break;
				
			case VALIDATING:
				// We're validating the user's selections before
				// uploading them!
				m_uploadPopup.setAbortVisible(false                                    );
				m_uploadPopup.setUploadHint(  m_messages.addFilesHtml5PopupValidating());
				break;
			}
		}
	}

	/**
	 * Shows a busy spinner animation while an operation is going on.
	 */
	final public void showBusySpinner() {
		// If we haven't created a busy spinner yet...
		if (null == m_busySpinner) {
			// ...create one now...
			m_busySpinner = new SpinnerPopup();
		}

		// ...and show it.
		m_busySpinner.center();
	}
	
	/**
	 * Returns true if we should support this folder view as being a
	 * drop target and false otherwise.
	 * 
	 * @return
	 */
	final public boolean supportsDragAndDrop() {
		return (
			GwtClientHelper.jsBrowserSupportsHtml5FileAPIs() &&
			(null != m_canAddEntities)                       &&
			m_canAddEntities.canAddEntries());
	}
	
	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 */
	@Override
	public void toolPanelReady(ToolPanelBase toolPanel) {
		if (!m_viewReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else {
			GwtClientHelper.debugAlert("FolderViewBase.toolPanelReady( *Internal Error* ):  Unexpected call to toolPanelReady() method.");
		}
	}

	/*
	 * Ensures m_verticalPanels is large enough to hold an item at
	 * index and sets w as the widget at that index.
	 */
	private void trackVerticalPanel(int index, Widget w) {
		// Ensure the list is big enough...
		int c = m_verticalPanels.size();
		for (int i = c; i <= index; i += 1) {
			m_verticalPanels.add(i, null);
		}
		
		// ...and store the widget.
		m_verticalPanels.set(index, w);
	}
	
	/**
	 * The upload has completed.
	 * 
	 * @param aborted
	 * 
	 * Implements the Html5UploadCallback.uploadComplete() method.
	 */
	@Override
	public void uploadComplete(boolean aborted, VibeEventBase<?> completeEvent) {
		// If we have an HTML5 upload popup...
		if (null != m_uploadPopup) {
			// ...hide it...
			m_uploadPopup.setActive(false);
	
			// ...and if we were given an event to fire upon
			// ...completion...
			if (null != completeEvent) {
				// ...fire it.
				GwtTeaming.fireEventAsync(completeEvent);
			}
		}
	}

	/**
	 * We're now uploading the next file.
	 * 
	 * @param fileName
	 * @param thisFile
	 * @param totalFiles
	 * 
	 * Implements the Html5UploadCallback.uploadingNextFile() method.
	 */
	@Override
	public void uploadingNextFile(String fileName, int thisFile, int totalFiles) {
		// If we have an HTML5 upload popup...
		if (null != m_uploadPopup) {
			// ...set its hint.
			m_uploadPopup.setUploadHint(fileName, thisFile, totalFiles);
		}
	}

	/**
	 * Errors occurred while validating the upload request.
	 * 
	 * @param errors
	 * 
	 * Implements the Html5UploadCallback.validationErrors() method.
	 */
	@Override
	public void validationErrors(List<ErrorInfo> errors) {
		GwtClientHelper.displayMultipleErrors(
			m_messages.addFilesHtml5PopupUploadValidationError(),
			errors);
	}
	
	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * This method is defined for classes that extend this class to
	 * override so that they can do any processing that they require
	 * once their a view is complete.
	 */
	public void viewComplete() {
		// Nothing to do.
	}
	
	/**
	 * Called by classes that extend this base class so that it can
	 * inform the world that its view is ready to go.
	 * 
	 * Overrides the ViewBase.viewReady() method.
	 */
	@Override
	public void viewReady() {
		if (!m_viewReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else {
			GwtClientHelper.debugAlert("FolderViewBase.viewReady( *Internal Error* ):  Unexpected call to viewReady() method.");
		}
	}
}
