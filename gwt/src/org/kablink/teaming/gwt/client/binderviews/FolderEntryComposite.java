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

import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryCookies.Cookie;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper.DeleteEntitiesCallback;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.CopyPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EditPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EmailPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.EventsHandledBySourceMarker;
import org.kablink.teaming.gwt.client.event.FolderEntryActionCompleteEvent;
import org.kablink.teaming.gwt.client.event.ForceFilesUnlockEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditInPlaceEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MailToPublicLinkEntityEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ShowViewPermalinksEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadSelectedFilesEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.FolderEntryDetailsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntryDetailsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNextPreviousFolderEntryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ViewFolderEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntityRights;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class that holds the folder entry viewer.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntryComposite extends ResizeComposite	
	implements EventsHandledBySourceMarker, FolderEntryCallback, ToolPanelReady,
		// Event handlers implemented by this class.
		ChangeEntryTypeSelectedEntitiesEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		CopyPublicLinkSelectedEntitiesEvent.Handler,
		CopySelectedEntitiesEvent.Handler,
		DeleteSelectedEntitiesEvent.Handler,
		EditPublicLinkSelectedEntitiesEvent.Handler,
		EmailPublicLinkSelectedEntitiesEvent.Handler,
		FolderEntryActionCompleteEvent.Handler,
		ForceFilesUnlockEvent.Handler,
		InvokeEditInPlaceEvent.Handler,
		LockSelectedEntitiesEvent.Handler,
		MailToPublicLinkEntityEvent.Handler,
		MarkReadSelectedEntitiesEvent.Handler,
		MarkUnreadSelectedEntitiesEvent.Handler,
		MoveSelectedEntitiesEvent.Handler,
		ShareSelectedEntitiesEvent.Handler,
		ShowViewPermalinksEvent.Handler,
		SubscribeSelectedEntitiesEvent.Handler,
		UnlockSelectedEntitiesEvent.Handler,
		ZipAndDownloadSelectedFilesEvent.Handler
{
	private boolean							m_blockHeaderInducedResize;	// Used to control recursive onResize() calls while resizing the header.
	private boolean							m_compositeReady;			// Set true once the composite and all its components are ready.
	private boolean							m_isDialog;					// true -> The composite is hosted in a dialog.  false -> It's hosted in a view.
	private boolean							m_sidebarVisible;			// true -> The sidebar is visible.  false -> It's not.
	private FlexCellFormatter				m_contentGridFCF;			// Used to format cells in m_contentGridFCF.
	private FolderEntryDetails				m_fed;						// Details about the folder entry being viewed.
	private FolderEntryDocument				m_documentArea;				// The document portion of the view.
	private FolderEntryHeader				m_headerArea;				// The header   portion of the view.
	private FolderEntryMenu					m_menuArea;					// The menu     portion of the view.
	private FolderEntrySidebar				m_sidebarArea;				// The sidebar  portion of the view.
	private FooterPanel						m_footerPanel;				// Footer at the bottom of the view with the permalink, ...
	private FormPanel						m_downloadForm;				// A <FORM> used for download URLs.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private int								m_readyComponents;			// Components that are ready, incremented as they callback.
	private Label							m_captionLabel;				// The label on the left of the caption bar. 
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private PermalinksDlg					m_permalinksDlg;			// The dialog used to display entry permalinks.
	private Panel							m_captionRightPanel;		// The panel at the right end of the caption containing the navigation arrows and close button.
	private Panel							m_captionRootPanel;			// Root panel holding the caption widget.
	private ViewReady						m_viewReady;				// Stores a ViewReady created for the classes that extends it.
	private VibeFlexTable					m_contentGrid;				// A <TABLE> containing the content portions of the view (everything but the sidebar.)
	private VibeFlowPanel 					m_captionImagePanel;		// A panel holding an image in the caption, if one is required.
	private VibeFlowPanel					m_contentPanel;				// The panel containing the composite's main content.
	private VibeFlowPanel					m_rootPanel;				// The panel containing everything about the composite.
	private ViewFolderEntryInfo				m_vfei;						// The view information for the folder entry being viewed.

	private final static int CAPTION_LABEL_OVERHEAD			=  25;	// Pixels the width of the caption label is reduced by to account for overhead, ...
	private final static int CAPTION_LABEL_MINIMUM			= 100;	// Minimum size, in pixels of the caption label.
	private final static int CONTENT_AREA_VERTICAL_PADDING	=  20;	// Padding (top + bottom) as defined in the vibe-feComposite-contentPanel style.
	private final static int MINIMUM_CONTENT_HEIGHT			= 150;	// The minimum height (in pixels) of the composite's content  panel.
	private final static int MINIMUM_DOCUMENT_HEIGHT		=  50;	// The minimum height (in pixels) of the composite's document area.
	public  final static int MINIMUM_SHARING_HEIGHT			= 100;	// The minimum height (in pixels) of the sidebar's sharing area.
	private final static int FOOTER_ADJUST_DLG				=  20;	// Height adjustment required for adequate spacing below the footer when hosted in a dialog.
	private final static int FOOTER_ADJUST_VIEW				=  30;	// Height adjustment required for adequate spacing below the footer when hosted in a view.
	
	private final static int BLOCK_HEADER_FORCED_RESIZE_DURATION	= 500;	// Milliseconds we ignore onResize() calls after resizing the header.
	
	private final static int CONTENT_ROW	= 0;	// Row    index in m_contentGrid for the content.
	private final static int CONTENT_CELL	= 0;	// Column index in m_contentGrid for the content.
	private final static int SIDEBAR_CELL	= 1;	// Column index in m_contentGrid for the sidebar.
	
	// Number of components to coordinate during construction:
	// - Header;
	// - Menu;
	// - Document;
	// - Sidebar;
	// - Comments Manager (within sidebar);
	// - Sharing  Manager (within sidebar); and
	// - Footer.
	private final static int FOLDER_ENTRY_COMPONENTS = 7;	//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTITIES,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.COPY_PUBLIC_LINK_SELECTED_ENTITIES,
		TeamingEvents.COPY_SELECTED_ENTITIES,
		TeamingEvents.DELETE_SELECTED_ENTITIES,
		TeamingEvents.EDIT_PUBLIC_LINK_SELECTED_ENTITIES,
		TeamingEvents.EMAIL_PUBLIC_LINK_SELECTED_ENTITIES,
		TeamingEvents.FOLDER_ENTRY_ACTION_COMPLETE,
		TeamingEvents.FORCE_FILES_UNLOCK,
		TeamingEvents.INVOKE_EDIT_IN_PLACE,
		TeamingEvents.LOCK_SELECTED_ENTITIES,
		TeamingEvents.MAILTO_PUBLIC_LINK_ENTITY,
		TeamingEvents.MARK_READ_SELECTED_ENTITIES,
		TeamingEvents.MARK_UNREAD_SELECTED_ENTITIES,
		TeamingEvents.MOVE_SELECTED_ENTITIES,
		TeamingEvents.SHARE_SELECTED_ENTITIES,
		TeamingEvents.SHOW_VIEW_PERMALINKS,
		TeamingEvents.SUBSCRIBE_SELECTED_ENTITIES,
		TeamingEvents.UNLOCK_SELECTED_ENTITIES,
		TeamingEvents.ZIP_AND_DOWNLOAD_SELECTED_FILES,
	};
	
	/*
	 * Dialog used to show the view's permalinks. 
	 */
	private class PermalinksDlg extends DlgBox {
		/**
		 * Constructor method.
		 * 
		 * @param permalinksPanel
		 */
		public PermalinksDlg(Panel permalinksPanel) {
			// Initialize the super class...
			super(false, true, DlgButtonMode.Close);

			// ...add this dialog's specific style...
			addStyleName("vibe-feComposite-permalinksDlg");

			// ...and create the dialog's content.
			createAllDlgContent(
				m_messages.folderEntry_Permalinks(),	// The dialog's header.
				getSimpleSuccessfulHandler(),			// The dialog's EditSuccessfulHandler.
				getSimpleCanceledHandler(),				// The dialog's EditCanceledHandler.
				permalinksPanel);						// Create callback data.
		}

		/**
		 * Called to create the dialog's contents.
		 * 
		 * Implements the DlgBox.createContent() method.
		 * 
		 * @param propertiesObj
		 * 
		 * @return
		 */
		@Override
		public Panel createContent(Object propertiesObj) {
			// The dialog's content is simply the Panel passed through
			// as the parameter.
			return ((Panel) propertiesObj);
		}

		/**
		 * Unused.
		 * 
		 * Implements the Dlgo.getDataFromDlg() method.
		 * 
		 * @return
		 */
		@Override
		public Object getDataFromDlg() {
			return "";
		}

		/**
		 * Unused.
		 * 
		 * Implements the Dlgo.getFocusWidget() method.
		 * 
		 * @return
		 */
		@Override
		public FocusWidget getFocusWidget() {
			// There are no focusable widgets in the dialog.
			return null;
		}
	}
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FolderEntryComposite(ViewFolderEntryInfo vfei, DlgBox dialog, ViewReady viewReady) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_vfei      = vfei;
		m_viewReady = viewReady;
		
		// ...initialize the data members requiring it...
		m_isDialog       = (null != dialog);
		m_images         = GwtTeaming.getDataTableImageBundle();
		m_messages       = GwtTeaming.getMessages();
		m_sidebarVisible = FolderEntryCookies.getBooleanCookieValue(Cookie.SIDEBAR_VISIBLE, true);	// true -> Defaults to visible.

		// ...create the base content panels...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-feComposite-rootPanel");
		m_rootPanel.add(BinderViewsHelper.createEditInPlaceFrame());
		createCaption(m_rootPanel, ((null == dialog) ? null : dialog.getHeaderPanel()));
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-feComposite-contentPanel");
		m_contentGrid = new VibeFlexTable();
		m_contentGrid.setCellPadding(0);
		m_contentGrid.setCellSpacing(0);
		m_contentGridFCF = m_contentGrid.getFlexCellFormatter();
		m_contentGrid.setWidget(              CONTENT_ROW, CONTENT_CELL, m_contentPanel                );
		m_contentGridFCF.setWidth(            CONTENT_ROW, CONTENT_CELL, "100%"                        );
		m_contentGridFCF.setVerticalAlignment(CONTENT_ROW, CONTENT_CELL, HasVerticalAlignment.ALIGN_TOP);
		m_rootPanel.add(m_contentGrid);
		m_downloadForm = DownloadPanel.createDownloadForm();
		m_contentPanel.add(m_downloadForm);
		initWidget(m_rootPanel);

		// ...and continue building the composite.
		loadPart1Async(false);	// false -> Not part of a refresh.
	}

	/*
	 * Checks the components as they callback and finishes things
	 * once everybody is ready.
	 */
	private void checkReadyness() {
		// Is everything ready?
		if (FOLDER_ENTRY_COMPONENTS == m_readyComponents) {
			// Yes!  Tell the view and resize as appropriate.
			m_compositeReady = true;
			m_viewReady.viewReady();
			onResizeAsync(FolderViewBase.INITIAL_RESIZE_DELAY);
		}
	}

	/*
	 * Closes the viewer and reloads the underlying content.
	 */ 
	private void closeFolderEntryViewer() {
		// To close it, we simply pop the previous URL and goto it.
		// Note that we have to protect against the case where the URL
		// is a permalink to the entry as navigating to it again, would
		// simply re-launch the entry viewer.  Hence the ignore.
		String url = ContentControl.getContentHistoryUrl(-1);	// Pops the previous URL.
		url = GwtClientHelper.appendUrlParam(url, "ignoreEntryView", "1");
		OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(url, Instigator.GOTO_CONTENT_URL);
		if (GwtClientHelper.validateOSBI(osbInfo)) {
			GwtTeaming.fireEventAsync(
				new ChangeContextEvent(osbInfo));
		}
	}
	
	/*
	 * Creates the dialog/view caption for the composite.
	 */
	private void createCaption(VibeFlowPanel rootPanel, Panel container) {
		// Do we have the header from a dialog container?
		if (null != container) {
			// Yes!  Clear its contents so we can create our own.
			container.clear();
		}
		
		else {
			// No, we don't have the header from a dialog!  Create one
			// using the styles used in a dialog's header.
			container = new VibeFlowPanel();
			container.setStyleName("teamingDlgBoxHeader");
			if (GwtClientHelper.jsIsIE())
			     container.addStyleName("teamingDlgBoxHeaderBG_IE"   );
			else container.addStyleName("teamingDlgBoxHeaderBG_NonIE");
		}

		// Save the panel that contains the caption widgets.
		m_captionRootPanel = container;

		// Create a panel to hold an image displayed in the caption, if
		// there will be one.
		m_captionImagePanel = new VibeFlowPanel();
		m_captionImagePanel.setStyleName("teamingDlgBoxHeader-captionImagePanel");
		m_captionRootPanel.add(m_captionImagePanel);
		
		// Create the label with the entry's title.
		m_captionLabel = new Label(m_vfei.getTitle());
		m_captionLabel.setStyleName("teamingDlgBoxHeader-captionLabel vibe-feComposite-captionLabel");
		m_captionRootPanel.add(m_captionLabel);

		// Create the widgets that appear at the right end of the caption.
		createCaptionRight();

		// Finally, add the caption panel to the root panel.
		rootPanel.add(m_captionRootPanel);
	}

	/*
	 * Creates a close button in the caption for non-dialog hosts.
	 */
	private void createCaptionClose(Panel container) {
		// Dialog's hosting this composite already have a close.  Is
		// this one hosted by a dialog?
		if (!m_isDialog) {
			// No!  Create a panel to hold the close widgets.
			VibeFlowPanel closePanel = new VibeFlowPanel();
			closePanel.addStyleName("vibe-feComposite-closePanel");
			closePanel.setTitle(m_messages.folderEntry_Alt_Close());
			container.add(closePanel);

			// Create a handler to close the view when clicked.
			ClickHandler closeClick = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					closeFolderEntryViewer();
				}
			};

			// Create a close label...
			InlineLabel close = new InlineLabel(m_messages.folderEntry_Close());
			close.addStyleName("vibe-feComposite-closeLabel");
			close.addClickHandler(closeClick);
			closePanel.add(close);
			
			// ...and an 'X' image to close it.
			Image closeX = GwtClientHelper.buildImage(m_images.closeBorder().getSafeUri().asString());
			closeX.addStyleName("vibe-feComposite-closeImg");
			closeX.addClickHandler(closeClick);
			closePanel.add(closeX);
		}
	}
	
	/*
	 * Creates the widgets used to navigate between entries being
	 * viewed. 
	 */
	private void createCaptionNavigation(Panel container) {
		// Create a panel to hold the navigation buttons.
		VibeFlowPanel navPanel = new VibeFlowPanel();
		navPanel.addStyleName("vibe-feComposite-navPanel");
		if (!m_isDialog) {
			navPanel.addStyleName("padding15R");
		}
		container.add(navPanel);

		// Create the previous button.
		Image button = GwtClientHelper.buildImage(m_images.previousTeal());
		button.addStyleName("vibe-feComposite-navPrevImg");
		button.setTitle(m_messages.folderEntry_Alt_Previous());
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doNavigateAsync(true);	// true -> Previous
			}
		});
		navPanel.add(button);
		
		// Create the next button.
		button = GwtClientHelper.buildImage(m_images.nextTeal());
		button.addStyleName("vibe-feComposite-navNextImg");
		button.setTitle(m_messages.folderEntry_Alt_Next());
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doNavigateAsync(false);	// false -> Next.
			}
		});
		navPanel.add(button);
	}

	/*
	 * Creates a panel that lives at the right edge of the caption.
	 */
	private void createCaptionRight() {
		// Create the right aligned panel
		m_captionRightPanel = new VibeFlowPanel();
		m_captionRightPanel.addStyleName("vibe-feComposite-rightPanel");
		m_captionRootPanel.add(m_captionRightPanel);

		//
		createCaptionNavigation(m_captionRightPanel);
		
		// Add a close button when necessary.
		createCaptionClose(m_captionRightPanel);
	}

	/**
	 * Navigate the entry viewer to the given ViewFolderEntryInfo.
	 * 
	 * Implements the FolderEntryCallback.doNavigate() method.
	 * 
	 * @param vfei
	 */
	@Override
	public void doNavigate(ViewFolderEntryInfo vfei) {
		m_vfei = vfei;
		m_captionLabel.setText(m_vfei.getTitle());
		m_sidebarArea.doNavigate(m_vfei.getEntityId());
		reloadFolderEntryViewer(false);	// false -> Not part of a refresh.
	}
	
	/*
	 * Asynchronously navigates to the previous/next entry.
	 */
	private void doNavigateAsync(final boolean previous) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doNavigateNow(previous);
			}
		});
	}
	
	/*
	 * Synchronously navigates to the previous/next entry.
	 */
	private void doNavigateNow(final boolean previous) {
		// Can we get the previous/next entry navigate to?
		GetNextPreviousFolderEntryInfoCmd cmd = new GetNextPreviousFolderEntryInfoCmd(m_vfei.getEntityId(), previous);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				String error;
				if ((t instanceof GwtTeamingException) && ExceptionType.ACCESS_CONTROL_EXCEPTION.equals(((GwtTeamingException) t).getExceptionType())) {
					if (previous)
					     error = m_messages.rpcFailure_GetPreviousFolderEntryInfo_NoAccess();
					else error = m_messages.rpcFailure_GetNextFolderEntryInfo_NoAccess();
				}
				else {
					if (previous)
					     error = m_messages.rpcFailure_GetPreviousFolderEntryInfo();
					else error = m_messages.rpcFailure_GetNextFolderEntryInfo();
				}
				GwtClientHelper.handleGwtRPCFailure(t, error);
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				ViewFolderEntryInfoRpcResponseData responseData = ((ViewFolderEntryInfoRpcResponseData) response.getResponseData());
				ViewFolderEntryInfo vfei = responseData.getViewFolderEntryInfo();
				if (null == vfei) {
					// No!  Tell the user about the problem.
					String error;
					if (previous)
					     error = m_messages.folderEntry_Error_NoPrevious();
					else error = m_messages.folderEntry_Error_NoNext();
					GwtClientHelper.deferredAlert(error);
				}
				
				else {
					// Yes!  We've got the previous/next entry.
					// Load it.
					doNavigate(vfei);
				}
			}
		});
	}

	/**
	 * Returns the EntityId of folder entry being viewed.
	 * 
	 * Implements the FolderEntryCallback.getEntityId() method.
	 * 
	 * @param vfei
	 */
	@Override
	public EntityId getEntityId() {
		return m_vfei.getEntityId();
	}

	/**
	 * Returns the EntityRights of folder entry being viewed.
	 * 
	 * Implements the FolderEntryCallback.getEntityRights() method.
	 * 
	 * @param vfei
	 */
	@Override
	public EntityRights getEntityRights() {
		return m_fed.getEntityRights();
	}

	/*
	 * Returns the footer adjustment to use for the content panel.
	 */
	private int getFooterAdjust() {
		int reply;
		if (m_isDialog)
		     reply = FOOTER_ADJUST_DLG;
		else reply = FOOTER_ADJUST_VIEW;
		return reply;
	}

	/*
	 * Returns true if a List<EntityId> contains a single EntityId that
	 * matches the one loaded in the entry viewer and false otherwise.
	 */
	private boolean isCompositeEntry(EntityId eid) {
		return eid.equalsEntityId(m_vfei.getEntityId());
	}
	
	private boolean isCompositeEntry(List<EntityId> eidList) {
		return (
			GwtClientHelper.hasItems(eidList) &&
			(1 == eidList.size()) &&
			isCompositeEntry(eidList.get(0)));
	}
	
	/**
	 * Implements the FolderEntryCallback.isSidebarVisible()
	 * method.
	 */
	@Override
	public boolean isSidebarVisible() {
		return m_sidebarVisible;
	}
	
	/*
	 * Asynchronously loads the next part of the composite.
	 */
	private void loadPart1Async(final boolean refresh) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(refresh);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the composite.
	 */
	private void loadPart1Now(final boolean refresh) {
		FooterPanel.createAsync(
				this,
				m_vfei.getEntityId(),
				this,
				new ToolPanelClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in asynchronous
						// provider.
					}
					
					@Override
					public void onSuccess(ToolPanelBase tpb) {
						m_footerPanel = ((FooterPanel) tpb);
						m_footerPanel.addStyleName("vibe-feComposite-footerPanel");
						m_rootPanel.add(m_footerPanel);
						loadPart2Async(refresh);
					}
				},
				new FooterPanelVisibility() {
					@Override
					public void footerPanelVisible(boolean show) {
						// Are we showing the permalinks?
						boolean havePermalinksDlg   = (null != m_permalinksDlg);
						Panel   permalinksDataPanel = m_footerPanel.getDataPanel();
						if (show) {
							// Yes!  Have we create a permalinks dialog
							// yet?
							if (!havePermalinksDlg) {
								// No!  Create one now...
								permalinksDataPanel.removeFromParent();
								permalinksDataPanel.addStyleName("vibe-feComposite-permalinksData");
								m_footerPanel.hideCloser();
								m_permalinksDlg = new PermalinksDlg(permalinksDataPanel);
							}
							
							// ...and show it.
							m_permalinksDlg.center();
						}
						
						// No, we aren't showing the permalinks!  If we
						// have a permalinks dialog...
						else if (havePermalinksDlg) {
							// ...hide it.
							m_permalinksDlg.hide();
						}
					}
				}
			);
	}

	/*
	 * Asynchronously loads the next part of the composite.
	 */
	private void loadPart2Async(final boolean refresh) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now(refresh);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the composite.
	 */
	private void loadPart2Now(final boolean refresh) {
		// Can we get the previous/next entry navigate to?
		GetFolderEntryDetailsCmd cmd = new GetFolderEntryDetailsCmd(m_vfei.getEntityId(), (!refresh));
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetFolderEntryDetails());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				FolderEntryDetailsRpcResponseData responseData = ((FolderEntryDetailsRpcResponseData) response.getResponseData());
				m_fed = responseData.getFolderEntryDetails();
				loadPart3Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the composite.
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
	 * Synchronously loads the next part of the composite.
	 */
	private void loadPart3Now() {
		// Create and add the various components to the composite.
		m_headerArea     = new FolderEntryHeader(  this, m_fed                  ); m_contentPanel.add(m_headerArea  );
		m_menuArea       = new FolderEntryMenu(    this, m_fed.getToolbarItems()); m_contentPanel.add(m_menuArea    );
		m_documentArea   = new FolderEntryDocument(this, m_fed                  ); m_contentPanel.add(m_documentArea);
		m_sidebarArea    = new FolderEntrySidebar( this, m_fed                  );
		m_contentGrid.setWidget(              CONTENT_ROW, SIDEBAR_CELL, m_sidebarArea                 );
		m_contentGridFCF.setHeight(           CONTENT_ROW, SIDEBAR_CELL, "100%"                        );
		m_contentGridFCF.setVerticalAlignment(CONTENT_ROW, SIDEBAR_CELL, HasVerticalAlignment.ALIGN_TOP);
	}

	/**
	 * Called when the composite is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest(final ContributorIdsRequestEvent event) {
		// Convert the contributor IDs from a String[] to a
		// List<Long>...
		final List<Long> contributorIds = new ArrayList<Long>();
		String[] contributors = m_fed.getContributors();
		if ((null != contributors) && (0 < contributors.length)) {
			for (String contributor:  contributors) {
				contributorIds.add(new Long(Long.parseLong(contributor)));
			}
		}
		
		// ...and asynchronously fire the corresponding reply event it.
		GwtTeaming.fireEventAsync(
			new ContributorIdsReplyEvent(
				event.getBinderId(),
				contributorIds));
	}
	
	/**
	 * Handles CopySelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntitiesEvent.Handler.onCopySelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntities(CopySelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> copiedEntities = event.getSelectedEntities();
		if (isCompositeEntry(copiedEntities)) {
			// Yes!  Run the copy on it.
			onCopySelectedEntitiesAsync(copiedEntities);
		}
	}
	
	/*
	 * Asynchronously handles copying the folder entry.
	 */
	private void onCopySelectedEntitiesAsync(final List<EntityId> copiedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onCopySelectedEntitiesNow(copiedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles copying the folder entry.
	 */
	private void onCopySelectedEntitiesNow(List<EntityId> copiedEntities) {
		BinderViewsHelper.copyEntries(
			copiedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				true));
	}
	
	/**
	 * Handles ChangeEntryTypeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ChangeEntryTypeSelectedEntitiesEvent.Handler.onChangeEntryTypeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeEntryTypeSelectedEntities(ChangeEntryTypeSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> changedEntities = event.getSelectedEntities();
		if (isCompositeEntry(changedEntities)) {
			// Yes!  Run the change on it.
			onChangeEntryTypeSelectedEntitiesAsync(changedEntities);
		}
	}
	
	/*
	 * Asynchronously handles changing the folder entry type.
	 */
	private void onChangeEntryTypeSelectedEntitiesAsync(final List<EntityId> changedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onChangeEntryTypeSelectedEntitiesNow(changedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles changing the folder entry type.
	 */
	private void onChangeEntryTypeSelectedEntitiesNow(List<EntityId> changedEntities) {
		BinderViewsHelper.changeEntryTypes(
			changedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				true));
	}
	
	/**
	 * Handles CopyPublicLinkSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the CopyPublicLinkSelectedEntitiesEvent.Handler.onCopyPublicLinkSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopyPublicLinkSelectedEntities(CopyPublicLinkSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> sharedEntities = event.getSelectedEntities();
		if (isCompositeEntry(sharedEntities)) {
			// Yes!  Run the copy public link on it.
			onCopyPublicLinkSelectedEntitiesAsync(sharedEntities);
		}
	}

	/*
	 * Asynchronously handles copying the public link of the folder
	 * entry.
	 */
	private void onCopyPublicLinkSelectedEntitiesAsync(final List<EntityId> sharedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onCopyPublicLinkSelectedEntitiesNow(sharedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles copying the public link of the folder
	 * entry.
	 */
	private void onCopyPublicLinkSelectedEntitiesNow(List<EntityId> sharedEntities) {
		BinderViewsHelper.copyEntitiesPublicLink(sharedEntities);
	}
	
	/**
	 * Called when the composite is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}

	/**
	 * Handles DeleteSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntitiesEvent.Handler.onDeleteSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntities(DeleteSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> deletedEntities = event.getSelectedEntities();
		if (isCompositeEntry(deletedEntities)) {
			// Yes!  Run the delete on it.
			onDeleteSelectedEntitiesAsync(deletedEntities);
		}
	}
	
	/*
	 * Asynchronously handles deleting the folder entry.
	 */
	private void onDeleteSelectedEntitiesAsync(final List<EntityId> deletedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onDeleteSelectedEntitiesNow(deletedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles deleting the folder entry.
	 */
	private void onDeleteSelectedEntitiesNow(List<EntityId> deletedEntities) {
		BinderViewsHelper.deleteSelections(deletedEntities, new DeleteEntitiesCallback() {
			@Override
			public void operationCanceled() {
				// Nothing to do.
			}

			@Override
			public void operationComplete() {
				GwtTeaming.fireEventAsync(
					new FolderEntryActionCompleteEvent(
						m_vfei.getEntityId(),
						true));
			}
			
			@Override
			public void operationFailed() {
				// Nothing to do.  The purge call will have told the
				// user about the failure.
			}
		});
	}
	
	/**
	 * Handles EditPublicLinkSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the EditPublicLinkSelectedEntitiesEvent.Handler.onEditPublicLinkSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEditPublicLinkSelectedEntities(EditPublicLinkSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> sharedEntities = event.getSelectedEntities();
		if (isCompositeEntry(sharedEntities)) {
			// Yes!  Run the edit public link on it.
			onEditPublicLinkSelectedEntitiesAsync(sharedEntities);
		}
	}

	/*
	 * Asynchronously handles editing the public link of the folder
	 * entry.
	 */
	private void onEditPublicLinkSelectedEntitiesAsync(final List<EntityId> sharedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onEditPublicLinkSelectedEntitiesNow(sharedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles editing the public link of the folder
	 * entry.
	 */
	private void onEditPublicLinkSelectedEntitiesNow(List<EntityId> sharedEntities) {
		BinderViewsHelper.editEntitiesPublicLink(sharedEntities);
	}
	
	/**
	 * Handles EmailPublicLinkSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the EmailPublicLinkSelectedEntitiesEvent.Handler.onEmailPublicLinkSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEmailPublicLinkSelectedEntities(EmailPublicLinkSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> sharedEntities = event.getSelectedEntities();
		if (isCompositeEntry(sharedEntities)) {
			// Yes!  Run the email public link on it.
			onEmailPublicLinkSelectedEntitiesAsync(sharedEntities);
		}
	}

	/*
	 * Asynchronously handles emailing the public link of the folder
	 * entry.
	 */
	private void onEmailPublicLinkSelectedEntitiesAsync(final List<EntityId> sharedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onEmailPublicLinkSelectedEntitiesNow(sharedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles emailing the public link of the folder
	 * entry.
	 */
	private void onEmailPublicLinkSelectedEntitiesNow(List<EntityId> sharedEntities) {
		BinderViewsHelper.emailEntitiesPublicLink(sharedEntities);
	}
	
	/**
	 * Handles FolderEntryActionCompleteEvent's received by this class.
	 * 
	 * Implements the FolderEntryActionCompleteEvent.Handler.onFolderEntryActionComplete() method.
	 * 
	 * @param event
	 */
	@Override
	public void onFolderEntryActionComplete(FolderEntryActionCompleteEvent event) {
		// Is the event targeted to this entry?
		if (isCompositeEntry(event.getEntityid())) {
			// Yes!  Close/refresh the folder entry viewer, as
			// required.
			if (event.exitViewer())
			     closeFolderEntryViewer();
			else reloadFolderEntryViewer(true);	// true -> Part of a refresh.
		}
	}
	
	/**
	 * Handles ForceFilesUnlockEvent's received by this class.
	 * 
	 * Implements the ForceFilesUnlockEvent.Handler.onForceFilesUnlock() method.
	 * 
	 * @param event
	 */
	@Override
	public void onForceFilesUnlock(ForceFilesUnlockEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> entityIds = event.getSelectedEntities();
		if (isCompositeEntry(entityIds)) {
			// Yes!  Force the file to be unlocked.
			onForceFilesUnlockAsync(entityIds);
		}
	}

	/*
	 * Asynchronously forces the selected files to be unlocked.
	 */
	private void onForceFilesUnlockAsync(final List<EntityId> entityIds) {
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Force the files to
							// be unlocked.
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									onForceFilesUnlockNow(entityIds);
								}
							});
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.folderEntry_Confirm_ForceFileUnlock());
			}
		});
	}
	
	/*
	 * Synchronously forces the selected files to be unlocked.
	 */
	private void onForceFilesUnlockNow(final List<EntityId> entityIds) {
		BinderViewsHelper.forceUnlockSelectedFiles(
			entityIds,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles InvokeEditInPlaceEvent's received by this class.
	 * 
	 * Implements the InvokeEditInPlaceEvent.Handler.onInvokeEditInPlace() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeEditInPlace(InvokeEditInPlaceEvent event) {
		// Is the event targeted to this entry?
		EntityId editThisEntity = event.getEntityid();
		if (isCompositeEntry(editThisEntity)) {
			// Yes!  Run the edit on it.
			BinderViewsHelper.invokeEditInPlace(event);
		}
	}
	
	/**
	 * Handles LockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the LockSelectedEntitiesEvent.Handler.onLockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLockSelectedEntities(LockSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> lockedEntities = event.getSelectedEntities();
		if (isCompositeEntry(lockedEntities)) {
			// Yes!  Run the lock on it.
			onLockSelectedEntitiesAsync(lockedEntities);
		}
	}
	
	/*
	 * Asynchronously handles locking the folder entry.
	 */
	private void onLockSelectedEntitiesAsync(final List<EntityId> lockedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onLockSelectedEntitiesNow(lockedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles locking the folder entry.
	 */
	private void onLockSelectedEntitiesNow(List<EntityId> lockedEntities) {
		BinderViewsHelper.lockEntries(
			lockedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles MailToPublicLinkEntityEvent's received by this class.
	 * 
	 * Implements the MailToPublicLinkEntityEvent.Handler.onMailToPublicLinkEntity() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMailToPublicLinkEntity(MailToPublicLinkEntityEvent event) {
		// Is the event targeted to this entry?
		EntityId entityId = event.getEntityId();
		if (isCompositeEntry(entityId)) {
			// Yes!  Run the mail public link on it.
			onMailToPublicLinkEntityAsync(entityId);
		}
	}

	/*
	 * Asynchronously handles mailing the public link of the folder
	 * entry.
	 */
	private void onMailToPublicLinkEntityAsync(final EntityId entityId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onMailToPublicLinkEntityNow(entityId);
			}
		});
	}
	
	/*
	 * Synchronously handles mailing the public link of the folder
	 * entry.
	 */
	private void onMailToPublicLinkEntityNow(EntityId entityId) {
		BinderViewsHelper.mailToPublicLink(entityId);
	}
	
	/**
	 * Handles MarkReadSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MarkReadSelectedEntitiesEvent.Handler.onMarkReadSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkReadSelectedEntities(MarkReadSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> markedReadEntities = event.getSelectedEntities();
		if (isCompositeEntry(markedReadEntities)) {
			// Yes!  Run the mark read on it.
			onMarkReadSelectedEntitiesAsync(markedReadEntities);
		}
	}
	
	/*
	 * Asynchronously handles marking the folder entry as being read
	 */
	private void onMarkReadSelectedEntitiesAsync(final List<EntityId> markedReadEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onMarkReadSelectedEntitiesNow(markedReadEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles marking the folder entry as being read.
	 */
	private void onMarkReadSelectedEntitiesNow(List<EntityId> markedReadEntities) {
		BinderViewsHelper.markEntriesRead(
			markedReadEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles MarkUnreadSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MarkUnreadSelectedEntitiesEvent.Handler.onMarkUnreadSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkUnreadSelectedEntities(MarkUnreadSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> markedUnreadEntities = event.getSelectedEntities();
		if (isCompositeEntry(markedUnreadEntities)) {
			// Yes!  Run the mark unread on it.
			onMarkUnreadSelectedEntitiesAsync(markedUnreadEntities);
		}
	}
	
	/*
	 * Asynchronously handles marking the folder entry as being read
	 */
	private void onMarkUnreadSelectedEntitiesAsync(final List<EntityId> markedUnreadEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onMarkUnreadSelectedEntitiesNow(markedUnreadEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles marking the folder entry as being read.
	 */
	private void onMarkUnreadSelectedEntitiesNow(List<EntityId> markedUnreadEntities) {
		BinderViewsHelper.markEntriesUnread(
			markedUnreadEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles MoveSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntitiesEvent.Handler.onMoveSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntities(MoveSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> movedEntities = event.getSelectedEntities();
		if (isCompositeEntry(movedEntities)) {
			// Yes!  Run the move on it.
			onMoveSelectedEntitiesAsync(movedEntities);
		}
	}
	
	/*
	 * Asynchronously handles moving the folder entry.
	 */
	private void onMoveSelectedEntitiesAsync(final List<EntityId> movedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onMoveSelectedEntitiesNow(movedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles moving the folder entry.
	 */
	private void onMoveSelectedEntitiesNow(List<EntityId> movedEntities) {
		BinderViewsHelper.moveEntries(
			movedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				true));
	}
	
	/**
	 * Synchronously sets the size of the composite based on its
	 * position in the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// If we're blocking resizes while resizing the header...
		if (m_blockHeaderInducedResize) {
			// ...ignore this onResize() call.
			return;
		}
		
		// Pass the resize on to the super class...
		super.onResize();
		
		// ...and do what we need to do locally.
		onResizeImpl();
	}
	
	/*
	 * Asynchronously sets the size of the composite based on its
	 * position in the view.
	 */
	private void onResizeAsync(int delay) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onResize();
			}
		},
		delay);
	}

	/*
	 * Re-sizes / re-lays out the widgets in the component.
	 */
	private void onResizeImpl() {
		Widget	container    = getParent();									// Widget (dialog or view) containing the composite.
		int		cHeight      = container.getOffsetHeight();					// Height       of the container.
		int		cTop         = container.getAbsoluteTop();					// Absolute top of the container.		
		int		contentTop   = (m_contentPanel.getAbsoluteTop() - cTop);	// Top of the composite's main content panel relative to the top of its container.		
		int		footerHeight = m_footerPanel.getOffsetHeight();				// Height of the composite's footer panel.

		// What's the optimal height for the content panel?
		boolean addContainerScroll = false;
		int contentHeight = (((cHeight - contentTop) - footerHeight) - getFooterAdjust());
		if (MINIMUM_CONTENT_HEIGHT > contentHeight) {
			// Too small!  Use the minimum even though this may result
			// in a vertical scroll bar.
			contentHeight      = MINIMUM_CONTENT_HEIGHT;
			addContainerScroll = true;
		}

		// Calculate the height we can give to the document area.
		int headerHeight = m_headerArea.getOffsetHeight();
		int menuHeight   = m_menuArea.getOffsetHeight();
		int docAbove     = (headerHeight  + menuHeight + 30);	// Adjust for padding...
		int docHeight    = (contentHeight - docAbove       );
		if (MINIMUM_DOCUMENT_HEIGHT > docHeight) {
			docHeight          = MINIMUM_DOCUMENT_HEIGHT;
			contentHeight      = (docAbove + docHeight);
			addContainerScroll = true;
		}

		// Add/remove the vertical scroll bar from the container as
		// necessary.
		if (addContainerScroll)
		     container.addStyleName(   "vibe-verticalScroll");
		else container.removeStyleName("vibe-verticalScroll");
		
		// Set the height of the content panel.
		m_documentArea.setHeight(      docHeight     + "px"                         );
		m_contentPanel.setHeight(      contentHeight + "px"                         );
		m_sidebarArea.setSidebarHeight(contentHeight + CONTENT_AREA_VERTICAL_PADDING);	// Sidebar height needs to account for the vertical padding in the content area.
		
		// Adjust the width of the caption label so that it doesn't
		// overlap the right panel.
		int captionWidth      = m_captionRootPanel.getOffsetWidth();
		int captionRightWidth = m_captionRightPanel.getOffsetWidth();
		int captionLabelWidth = ((captionWidth - captionRightWidth) - CAPTION_LABEL_OVERHEAD);
		if (CAPTION_LABEL_MINIMUM > captionLabelWidth) {
			captionLabelWidth = CAPTION_LABEL_MINIMUM;
		}
		m_captionLabel.setWidth(captionLabelWidth + "px");

		// Finally, tell the header to set its size based on where
		// we're at.  Note that while the header is resizing, we
		// ignore additional onResize() requests.  This is done to
		// stop recursive onResize() calls as when the header resizes,
		// it causes its parent (i.e., this composite) to resize, ...
		m_blockHeaderInducedResize = true;
		m_headerArea.setHeaderSize();
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					m_blockHeaderInducedResize = false;
				}
			},
			BLOCK_HEADER_FORCED_RESIZE_DURATION);
	}

	/**
	 * Handles ShareSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ShareSelectedEntitiesEvent.Handler.onShareSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareSelectedEntities(ShareSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> sharedEntities = event.getSelectedEntities();
		if (isCompositeEntry(sharedEntities)) {
			// Yes!  Run the share on it.
			onShareSelectedEntitiesAsync(sharedEntities);
		}
	}

	/*
	 * Asynchronously handles sharing the folder entry.
	 */
	private void onShareSelectedEntitiesAsync(final List<EntityId> sharedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onShareSelectedEntitiesNow(sharedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles sharing the folder entry.
	 */
	private void onShareSelectedEntitiesNow(List<EntityId> sharedEntities) {
		BinderViewsHelper.shareEntities(sharedEntities);
	}
	
	/**
	 * Handles ShowViewPermalinksEvent's received by this class.
	 * 
	 * Implements the ShowViewPermalinksEvent.Handler.onShowViewPermalinks() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowViewPermalinks(ShowViewPermalinksEvent event) {
		FooterPanel.expandFooter(m_footerPanel);
	}

	/**
	 * Handles SubscribeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the SubscribeSelectedEntitiesEvent.Handler.onSubscribeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSubscribeSelectedEntities(SubscribeSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> subscribedEntities = event.getSelectedEntities();
		if (isCompositeEntry(subscribedEntities)) {
			// Yes!  Run the subscribe on it.
			onSubscribeSelectedEntitiesAsync(subscribedEntities);
		}
	}

	/*
	 * Asynchronously handles subscribing to the folder entry.
	 */
	private void onSubscribeSelectedEntitiesAsync(final List<EntityId> subscribedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onSubscribeSelectedEntitiesNow(subscribedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles subscribing to the folder entry.
	 */
	private void onSubscribeSelectedEntitiesNow(List<EntityId> subscribedEntities) {
		BinderViewsHelper.subscribeToEntries(subscribedEntities);
	}
	
	/**
	 * Handles UnlockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the UnlockSelectedEntitiesEvent.Handler.onUnlockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUnlockSelectedEntities(UnlockSelectedEntitiesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> unlockedEntities = event.getSelectedEntities();
		if (isCompositeEntry(unlockedEntities)) {
			// Yes!  Run the unlock on it.
			onUnlockSelectedEntitiesAsync(unlockedEntities);
		}
	}
	
	/*
	 * Asynchronously handles unlocking the folder entry.
	 */
	private void onUnlockSelectedEntitiesAsync(final List<EntityId> unlockedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onUnlockSelectedEntitiesNow(unlockedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles unlocking the folder entry.
	 */
	private void onUnlockSelectedEntitiesNow(List<EntityId> unlockedEntities) {
		BinderViewsHelper.unlockEntries(
			unlockedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles ZipAndDownloadSelectedFilesEvent's received by this class.
	 * 
	 * Implements the ZipAndDownloadSelectedFilesEvent.Handler.onZipAndDownloadSelectedFiles() method.
	 * 
	 * @param event
	 */
	@Override
	public void onZipAndDownloadSelectedFiles(ZipAndDownloadSelectedFilesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> zipAndDownloadEntities = event.getSelectedEntities();
		if (isCompositeEntry(zipAndDownloadEntities)) {
			// Yes!  Run the zip and download on it.
			onZipAndDownloadSelectedFilesAsync(zipAndDownloadEntities);
		}
	}
	
	/*
	 * Asynchronously handles zipping and downloading the file.
	 */
	private void onZipAndDownloadSelectedFilesAsync(final List<EntityId> zipAndDownloadEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onZipAndDownloadSelectedFilesNow(zipAndDownloadEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles zipping and downloading the file.
	 */
	private void onZipAndDownloadSelectedFilesNow(List<EntityId> zipAndDownloadEntities) {
		BinderViewsHelper.zipAndDownloadFiles(
			m_downloadForm,
			zipAndDownloadEntities,
			true,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/*
	 * Coordinates things as the components of the composite become ready.
	 */
	private void partReady() {
		if (!m_compositeReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else {
			GwtClientHelper.debugAlert("FolderEntryComposite.partReady( *Internal Error* ):  Unexpected call to partReady() method.");
		}
	}

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Reloads the contents of the folder entry viewer.
	 */
	private void reloadFolderEntryViewer(final boolean refresh) {
		// Remove the existing content...
		m_contentPanel.clear();
		m_rootPanel.remove(m_footerPanel);

		// ...prepare for new content to be created...
		m_compositeReady  = false;
		m_readyComponents = 0;

		// ...and start loading the new content. 
		loadPart1Async(refresh);
	}

	/**
	 * Called by one of the view components to tell the composite that
	 * things needs to be resized.
	 * 
	 * Implements the FolderEntryCallback.resizeView() method.
	 */
	@Override
	public void resizeView() {
		onResizeAsync(0);
	}
	
	/*
	 * Updates the caption's image.
	 */
	@SuppressWarnings("unused")
	private void setCaptionImage(Image captionImg) {
		if (null != captionImg) {
			m_captionImagePanel.clear();
			m_captionImagePanel.add(captionImg);
			m_captionImagePanel.addStyleName("padding5R");
		}
	}

	/**
	 * Implements the FolderEntryCallback.toggleSidebarVisibility()
	 * method.
	 */
	@Override
	public void toggleSidebarVisibility() {
		// Toggle the state of the sidebar's visibility...
		m_sidebarVisible = (!m_sidebarVisible);
		String sidebarWidth;
		if (m_sidebarVisible)
		     sidebarWidth = "";
		else sidebarWidth = "1px";
		m_contentGridFCF.setWidth(CONTENT_ROW, SIDEBAR_CELL, sidebarWidth);
		onResizeAsync(0);
		
		// ...and store the current state in a cookie.
		if (m_sidebarVisible)
		     FolderEntryCookies.removeCookieValue(    Cookie.SIDEBAR_VISIBLE       );
		else FolderEntryCookies.setBooleanCookieValue(Cookie.SIDEBAR_VISIBLE, false);
	}
	
	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 * 
	 * @param toolPanel
	 */
	@Override
	public void toolPanelReady(ToolPanelBase toolPanel) {
		partReady();
	}

	/**
	 * Implements the FolderEntryCallback.viewComponentReady() method.
	 */
	@Override
	public void viewComponentReady() {
		partReady();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the folder entry composite and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface used to interact with the composite asynchronously
	 * after it loads. 
	 */
	public interface FolderEntryCompositeClient {
		void onSuccess(FolderEntryComposite fec);
		void onUnavailable();
	}
	
	/**
	 * Loads the FolderEntryComposite split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param fecClient
	 * @param dialog
	 * @param vfei
	 * @param viewReady
	 */
	public static void createAsync(final FolderEntryCompositeClient fecClient, final DlgBox dialog, final ViewFolderEntryInfo vfei, final ViewReady viewReady) {
		GWT.runAsync(FolderEntryComposite.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FolderEntryComposite());
				fecClient.onUnavailable();
			}

			@Override
			public void onSuccess() {
				FolderEntryComposite fec = new FolderEntryComposite(vfei, dialog, viewReady);
				fecClient.onSuccess(fec);
			}
		});
	}
}
