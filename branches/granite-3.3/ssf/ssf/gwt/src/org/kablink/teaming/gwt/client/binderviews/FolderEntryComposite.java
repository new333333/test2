/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.binderviews.FolderEntryCookies.Cookie;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper.DeletePurgeEntriesCallback;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.EventsHandledBySourceMarker;
import org.kablink.teaming.gwt.client.event.FolderEntryActionCompleteEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditInPlaceEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ShowViewPermalinksEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.FolderEntryDetailsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntryDetailsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNextPreviousFolderEntryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ViewFolderEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.NamedFrame;
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
	implements CommentAddedCallback, EventsHandledBySourceMarker, FolderEntryCallback, ToolPanelReady,
		// Event handlers implemented by this class.
		ChangeEntryTypeSelectedEntriesEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		CopySelectedEntriesEvent.Handler,
		DeleteSelectedEntriesEvent.Handler,
		FolderEntryActionCompleteEvent.Handler,
		InvokeEditInPlaceEvent.Handler,
		LockSelectedEntriesEvent.Handler,
		MarkReadSelectedEntriesEvent.Handler,
		MarkUnreadSelectedEntriesEvent.Handler,
		MoveSelectedEntriesEvent.Handler,
		PurgeSelectedEntriesEvent.Handler,
		ShareSelectedEntriesEvent.Handler,
		ShowViewPermalinksEvent.Handler,
		SubscribeSelectedEntriesEvent.Handler,
		UnlockSelectedEntriesEvent.Handler
{
	private boolean							m_commentsVisible;			//
	private boolean							m_compositeReady;			// Set true once the composite and all its components are ready.
	private boolean							m_isDialog;					// true -> The composite is hosted in a dialog.  false -> It's hosted in a view.
	private FolderEntryComments				m_commentsArea;				//
	private FolderEntryDetails				m_fed;						// Details about the folder entry being viewed.
	private FolderEntryDocument				m_documentArea;				//
	private FolderEntryHeader				m_headerArea;				//
	private FolderEntryMenu					m_menuArea;					//
	private FooterPanel						m_footerPanel;				// Footer at the bottom of the view with the permalink, ...
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private Image							m_commentsSliderImg;		//
	private InlineLabel 					m_commentsHeader;			//
	private int								m_readyComponents;			// Components that are ready, incremented as they callback.
	private Label							m_caption;					// The text on the left of the caption bar. 
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ViewReady						m_viewReady;				// Stores a ViewReady created for the classes that extends it.
	private VibeFlowPanel 					m_captionImagePanel;		// A panel holding an image in the caption, if one is required.
	private VibeFlowPanel					m_contentPanel;				// The panel containing the composite's main content.
	private VibeFlowPanel					m_rootPanel;				// The panel containing everything about the composite.
	private ViewFolderEntryInfo				m_vfei;						// The view information for the folder entry being viewed.

	private final static int MINIMUM_CONTENT_HEIGHT		= 150;	// The minimum height (in pixels) of the composite's content panel.
	private final static int MINIMUM_DOCUMENT_HEIGHT	=  50;
	private final static int FOOTER_ADJUST_DLG			=  20;	// Height adjustment required for adequate spacing below the footer when hosted in a dialog.
	private final static int FOOTER_ADJUST_VIEW			=  30;	// Height adjustment required for adequate spacing below the footer when hosted in a view.
	
	// Number of components to coordinate with during construction:
	// - Header;
	// - Menu;
	// - Document;
	// - Comments; and
	// - Footer.
	private final static int FOLDER_ENTRY_COMPONENTS = 5;	//
	
	// The following is the ID/name of the <IFRAME> used to run the
	// edit-in-place editor via an applet.
	private final String EDIT_IN_PLACE_DIV_ID	= "ss_div_fileopen_GWT";
	private final String EDIT_IN_PLACE_FRAME_ID	= "ss_iframe_fileopen_GWT";
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTRIES,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.COPY_SELECTED_ENTRIES,
		TeamingEvents.DELETE_SELECTED_ENTRIES,
		TeamingEvents.FOLDER_ENTRY_ACTION_COMPLETE,
		TeamingEvents.INVOKE_EDIT_IN_PLACE,
		TeamingEvents.LOCK_SELECTED_ENTRIES,
		TeamingEvents.MARK_READ_SELECTED_ENTRIES,
		TeamingEvents.MARK_UNREAD_SELECTED_ENTRIES,
		TeamingEvents.MOVE_SELECTED_ENTRIES,
		TeamingEvents.PURGE_SELECTED_ENTRIES,
		TeamingEvents.SHARE_SELECTED_ENTRIES,
		TeamingEvents.SHOW_VIEW_PERMALINKS,
		TeamingEvents.SUBSCRIBE_SELECTED_ENTRIES,
		TeamingEvents.UNLOCK_SELECTED_ENTRIES,
	};
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
		m_isDialog = (null != dialog);
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();

		// ...create the base content panels...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-feComposite-rootPanel");
		createEditInPlaceFrame(m_rootPanel);
		createCaption(m_rootPanel, ((null == dialog) ? null : dialog.getHeaderPanel()));
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-feComposite-contentPanel");
		m_rootPanel.add(m_contentPanel);
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
			onResizeAsync();
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

		// Create a panel to hold an image displayed in the caption, if
		// there will be one.
		m_captionImagePanel = new VibeFlowPanel();
		m_captionImagePanel.setStyleName("teamingDlgBoxHeader-captionImagePanel");
		container.add(m_captionImagePanel);
		
		// Create the label with the entry's title.
		m_caption = new Label(m_vfei.getTitle());
		m_caption.setStyleName("teamingDlgBoxHeader-captionLabel");
		container.add(m_caption);

		// Create the widgets that appear at the right end of the caption.
		createCaptionComments(container);
		createCaptionRight(   container);

		// Finally, add the caption panel to the root panel.
		rootPanel.add(container);
	}

	/**
	 * Called when a comment is added to the entry.
	 * 
	 * Implements the CommentAddedCallback.commentAdded() method.
	 */
	@Override
	public void commentAdded(Object callbackData) {
		// Update the count in the header.
		int commentsCount = (m_fed.getComments().getCommentsCount() + 1);
		m_fed.getComments().setCommentsCount(commentsCount);
		setCommentsCount(commentsCount);

		// If the comments widget is not currently visible...
		if (!m_commentsVisible) {
			// ...show it.
			toggleCommentsVisibility();
		}
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
			Image closeX = GwtClientHelper.buildImage(m_images.closeX());
			closeX.addStyleName("vibe-feComposite-closeImg");
			closeX.addClickHandler(closeClick);
			closePanel.add(closeX);
		}
	}
	
	/*
	 * Creates a panel that contains the comments portion of the caption.
	 */
	private void createCaptionComments(Panel container) {
		// What's the visibility state for comments on this entry?
		m_commentsVisible = FolderEntryCookies.getBooleanCookieValue(Cookie.COMMENTS_VISIBLE, m_vfei.getEntityId(), false);

		// Create the outer panel for the comment caption...
		VibeFlowPanel outerPanel = new VibeFlowPanel();
		outerPanel.addStyleName("vibe-feComposite-commentsHeadOuter");
		container.add(outerPanel);

		// ...add an anchor to it so that we can make it clickable...
		Anchor commentsAnchor = new Anchor();
		commentsAnchor.addStyleName("cursorPointer");
		commentsAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleCommentsVisibility();
			}
		});
		outerPanel.add(commentsAnchor);

		// ...add the inner panel to the anchor...
		VibeFlowPanel innerPanel = new VibeFlowPanel();
		innerPanel.addStyleName("vibe-feComposite-commentsHeadInner blend2-c");
		commentsAnchor.getElement().appendChild(innerPanel.getElement());

		// ...add the title panel to the inner panel...
		VibeFlowPanel titlePanel = new VibeFlowPanel();
		titlePanel.addStyleName("vibe-feComposite-commentsHeadTitle");
		innerPanel.add(titlePanel);

		// ...add the slider image to the title panel...
		ImageResource sliderImg = (m_commentsVisible ? m_images.slideUp() : m_images.slideDown());
		m_commentsSliderImg = GwtClientHelper.buildImage(sliderImg.getSafeUri().asString());
		m_commentsSliderImg.addStyleName("vibe-feComposite-commentsHeadSlider");
		titlePanel.add(m_commentsSliderImg);

		// ...and finally, add the label to the title panel.  Note that
		// ...its content will be set once we're fully initialized.
		m_commentsHeader = new InlineLabel();
		titlePanel.add(m_commentsHeader);
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
	private void createCaptionRight(Panel container) {
		// Create the right aligned panel
		VibeFlowPanel rightPanel = new VibeFlowPanel();
		rightPanel.addStyleName("vibe-feComposite-rightPanel");
		container.add(rightPanel);

		//
		createCaptionNavigation(rightPanel);
		
		// Add a close button when necessary.
		createCaptionClose(rightPanel);
	}

	/*
	 * Creates the IFRAME, ... required to run a edit-in-place editor
	 * on a file using the applet.
	 */
	private void createEditInPlaceFrame(Panel container) {
		// Create the outer <DIV>...
		VibeFlowPanel outer = new VibeFlowPanel();
		outer.addStyleName("vibe-feView-editInPlaceOuter");
		outer.getElement().setId(               EDIT_IN_PLACE_DIV_ID);
		outer.getElement().setAttribute("name", EDIT_IN_PLACE_DIV_ID);
		
		// ...create the inner <DIV>...
		VibeFlowPanel inner = new VibeFlowPanel();
		inner.addStyleName("vibe-feView-editInPlaceInner");
		inner.getElement().setAttribute("align", "right");

		// ...create the <IFRAME>...
		NamedFrame eipFrame = new NamedFrame(EDIT_IN_PLACE_FRAME_ID);
		eipFrame.getElement().setId(         EDIT_IN_PLACE_FRAME_ID);
		eipFrame.setPixelSize(0, 0);
		eipFrame.setUrl(GwtClientHelper.getRequestInfo().getJSPath() + "forum/null.html");
		eipFrame.setTitle(GwtClientHelper.isLicenseFilr() ? m_messages.novellFilr() : m_messages.novellTeaming());

		// ...and tie it all together.
		inner.add(    eipFrame);
		outer.add(    inner   );
		container.add(outer   );
	}
	
	/**
	 * Navigate the entry viewer to the given ViewFolderEntryInfo.
	 * 
	 * Implements the FolderEntryCallback.doNavigate() method.
	 */
	@Override
	public void doNavigate(ViewFolderEntryInfo vfei) {
		m_vfei = vfei;
		m_caption.setText(m_vfei.getTitle());
		if (m_commentsVisible != FolderEntryCookies.getBooleanCookieValue(Cookie.COMMENTS_VISIBLE, m_vfei.getEntityId(), false)) {
			toggleCommentsVisibility();
		}
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
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetNextPreviousFolderEntryInfo());
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
		FooterPanel.createAsync(this, m_vfei.getEntityId(), this, new ToolPanelClient() {			
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
		});
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
		// Store the current comment count in the comments header...
		setCommentsCount(m_fed.getComments().getCommentsCount());
		
		// ...and create and add the various components to the
		// ...composite.
		m_headerArea   = new FolderEntryHeader(  this, m_fed                    ); m_contentPanel.add(m_headerArea  );
		m_menuArea     = new FolderEntryMenu(    this, m_fed.getToolbarItems()  ); m_contentPanel.add(m_menuArea    );
		m_documentArea = new FolderEntryDocument(this, m_fed                    ); m_contentPanel.add(m_documentArea);
		m_commentsArea = new FolderEntryComments(this, m_fed.getComments(), this); m_contentPanel.add(m_commentsArea);
		if (!m_commentsVisible) {
			m_commentsArea.setCommentsVisible(false);
			Timer timer = new Timer() {
				@Override
				public void run() {
					// We do this again 1/2 second later to ensure it
					// gets hidden after its fully initialized.  On the
					// first display, sometimes it doesn't stay hidden.
					m_commentsArea.setCommentsVisible(false);
				}
			};
			timer.schedule(500);
		}
	}

	/**
	 * Called when the data table is attached.
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
	 * Handles CopySelectedEntriesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntriesEvent.Handler.onCopySelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntries(CopySelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> copiedEntities = event.getSelectedEntities();
		if (isCompositeEntry(copiedEntities)) {
			// Yes!  Run the copy on it.
			onCopySelectedEntriesAsync(copiedEntities);
		}
	}
	
	/*
	 * Asynchronously handles copying the folder entry.
	 */
	private void onCopySelectedEntriesAsync(final List<EntityId> copiedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onCopySelectedEntriesNow(copiedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles copying the folder entry.
	 */
	private void onCopySelectedEntriesNow(List<EntityId> copiedEntities) {
		BinderViewsHelper.copyEntries(
			copiedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				true));
	}
	
	/**
	 * Handles ChangeEntryTypeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the ChangeEntryTypeSelectedEntriesEvent.Handler.onChangeEntryTypeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeEntryTypeSelectedEntries(ChangeEntryTypeSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> changedEntities = event.getSelectedEntities();
		if (isCompositeEntry(changedEntities)) {
			// Yes!  Run the change on it.
			onChangeEntryTypeSelectedEntriesAsync(changedEntities);
		}
	}
	
	/*
	 * Asynchronously handles changing the folder entry type.
	 */
	private void onChangeEntryTypeSelectedEntriesAsync(final List<EntityId> changedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onChangeEntryTypeSelectedEntriesNow(changedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles changing the folder entry type.
	 */
	private void onChangeEntryTypeSelectedEntriesNow(List<EntityId> changedEntities) {
		BinderViewsHelper.changeEntryTypes(
			changedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				true));
	}
	
	/**
	 * Called when the data table is detached.
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
	 * Handles DeleteSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntriesEvent.Handler.onDeleteSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntries(DeleteSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> deletedEntities = event.getSelectedEntities();
		if (isCompositeEntry(deletedEntities)) {
			// Yes!  Run the delete on it.
			onDeleteSelectedEntriesAsync(deletedEntities);
		}
	}
	
	/*
	 * Asynchronously handles deleting the folder entry.
	 */
	private void onDeleteSelectedEntriesAsync(final List<EntityId> deletedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onDeleteSelectedEntriesNow(deletedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles deleting the folder entry.
	 */
	private void onDeleteSelectedEntriesNow(List<EntityId> deletedEntities) {
		BinderViewsHelper.deleteFolderEntries(deletedEntities, new DeletePurgeEntriesCallback() {
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
			onInvokeEditInPlaceAsync(event);
		}
	}
	
	/*
	 * Asynchronously handles editing the folder entry.
	 */
	private void onInvokeEditInPlaceAsync(final InvokeEditInPlaceEvent event) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeEditInPlaceNow(event);
			}
		});
	}
	
	/*
	 * Synchronously handles editing the folder entry.
	 */
	private void onInvokeEditInPlaceNow(InvokeEditInPlaceEvent event) {
		// How are we launching the edit-in-place editor?
		String		et  = event.getEditorType(); if (null == et) et = "";
		EntityId	eid = event.getEntityid();
		if ("applet".equals(et)) {
			// Via an applet!  Launch it.
			GwtClientHelper.jsEditInPlace_Applet(
				eid.getBinderId(),
				eid.getEntityId(),
				"_GWT",
				event.getOperatingSystem(),
				event.getAttachmentId());
		}
		
		else if ("webdav".equals(et)) {
			// Via a WebDAV URL!  Launch it.
			GwtClientHelper.jsEditInPlace_WebDAV(event.getAttachmentUrl());
		}
		
		else {
			// Unknown!  Tell the user about the problem.
			GwtClientHelper.deferredAlert(m_messages.eventHandling_UnknownEditInPlaceEditorType(et));
		}
	}

	/**
	 * Handles LockSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the LockSelectedEntriesEvent.Handler.onLockSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLockSelectedEntries(LockSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> lockedEntities = event.getSelectedEntities();
		if (isCompositeEntry(lockedEntities)) {
			// Yes!  Run the lock on it.
			onLockSelectedEntriesAsync(lockedEntities);
		}
	}
	
	/*
	 * Asynchronously handles locking the folder entry.
	 */
	private void onLockSelectedEntriesAsync(final List<EntityId> lockedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onLockSelectedEntriesNow(lockedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles locking the folder entry.
	 */
	private void onLockSelectedEntriesNow(List<EntityId> lockedEntities) {
		BinderViewsHelper.lockEntries(
			lockedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles MarkReadSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the MarkReadSelectedEntriesEvent.Handler.onMarkReadSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkReadSelectedEntries(MarkReadSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> markedReadEntities = event.getSelectedEntities();
		if (isCompositeEntry(markedReadEntities)) {
			// Yes!  Run the mark read on it.
			onMarkReadSelectedEntriesAsync(markedReadEntities);
		}
	}
	
	/*
	 * Asynchronously handles marking the folder entry as being read
	 */
	private void onMarkReadSelectedEntriesAsync(final List<EntityId> markedReadEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onMarkReadSelectedEntriesNow(markedReadEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles marking the folder entry as being read.
	 */
	private void onMarkReadSelectedEntriesNow(List<EntityId> markedReadEntities) {
		BinderViewsHelper.markEntriesRead(
			markedReadEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles MarkUnreadSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the MarkUnreadSelectedEntriesEvent.Handler.onMarkUnreadSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkUnreadSelectedEntries(MarkUnreadSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> markedUnreadEntities = event.getSelectedEntities();
		if (isCompositeEntry(markedUnreadEntities)) {
			// Yes!  Run the mark unread on it.
			onMarkUnreadSelectedEntriesAsync(markedUnreadEntities);
		}
	}
	
	/*
	 * Asynchronously handles marking the folder entry as being read
	 */
	private void onMarkUnreadSelectedEntriesAsync(final List<EntityId> markedUnreadEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onMarkUnreadSelectedEntriesNow(markedUnreadEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles marking the folder entry as being read.
	 */
	private void onMarkUnreadSelectedEntriesNow(List<EntityId> markedUnreadEntities) {
		BinderViewsHelper.markEntriesUnread(
			markedUnreadEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				false));
	}
	
	/**
	 * Handles MoveSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntriesEvent.Handler.onMoveSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntries(MoveSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> movedEntities = event.getSelectedEntities();
		if (isCompositeEntry(movedEntities)) {
			// Yes!  Run the move on it.
			onMoveSelectedEntriesAsync(movedEntities);
		}
	}
	
	/*
	 * Asynchronously handles moving the folder entry.
	 */
	private void onMoveSelectedEntriesAsync(final List<EntityId> movedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onMoveSelectedEntriesNow(movedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles moving the folder entry.
	 */
	private void onMoveSelectedEntriesNow(List<EntityId> movedEntities) {
		BinderViewsHelper.moveEntries(
			movedEntities,
			new FolderEntryActionCompleteEvent(
				m_vfei.getEntityId(),
				true));
	}
	
	/**
	 * Handles PurgeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the PurgeSelectedEntriesEvent.Handler.onPurgeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onPurgeSelectedEntries(PurgeSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> purgedEntities = event.getSelectedEntities();
		if (isCompositeEntry(purgedEntities)) {
			// Yes!  Run the purge on it.
			onPurgeSelectedEntriesAsync(purgedEntities);
		}
	}
	
	/*
	 * Asynchronously handles purging the folder entry.
	 */
	private void onPurgeSelectedEntriesAsync(final List<EntityId> purgedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onPurgeSelectedEntriesNow(purgedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles purging the folder entry.
	 */
	private void onPurgeSelectedEntriesNow(List<EntityId> purgedEntities) {
		BinderViewsHelper.purgeFolderEntries(purgedEntities, new DeletePurgeEntriesCallback() {
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
	 * Synchronously sets the size of the composite based on its
	 * position in the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class...
		super.onResize();
		
		// ...and do what we need to do locally.
		onResizeImpl();
	}
	
	/*
	 * Asynchronously sets the size of the data table based on its
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
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void onResizeAsync() {
		onResizeAsync(FolderViewBase.INITIAL_RESIZE_DELAY);
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
		boolean addConainerScroll = false;;
		int contentHeight = (((cHeight - contentTop) - footerHeight) - getFooterAdjust());
		if (MINIMUM_CONTENT_HEIGHT > contentHeight) {
			// Too small!  Use the minimum even though this may result
			// in a vertical scroll bar.
			contentHeight     = MINIMUM_CONTENT_HEIGHT;
			addConainerScroll = true;
		}

		// Calculate the height we can give to the document area.
		int headerHeight = m_headerArea.getOffsetHeight();
		int menuHeight   = m_menuArea.getOffsetHeight();
		int docAbove     = (headerHeight  + menuHeight + 30);	// Adjust for padding...
		int docHeight    = (contentHeight - docAbove       );
		if (MINIMUM_DOCUMENT_HEIGHT > docHeight) {
			docHeight         = MINIMUM_DOCUMENT_HEIGHT;
			contentHeight     = (docAbove + docHeight);
			addConainerScroll = true;
		}

		// Add/remove the vertical scroll bar from the container as
		// necessary.
		if (addConainerScroll)
		     container.addStyleName(   "vibe-verticalScroll");
		else container.removeStyleName("vibe-verticalScroll");
		
		// Set the height of the content panel.
		m_documentArea.setHeight(docHeight     + "px");
		m_contentPanel.setHeight(contentHeight + "px");
	}

	/**
	 * Handles ShareSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the ShareSelectedEntriesEvent.Handler.onShareSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareSelectedEntries(ShareSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> sharedEntities = event.getSelectedEntities();
		if (isCompositeEntry(sharedEntities)) {
			// Yes!  Run the share on it.
			onShareSelectedEntriesAsync(sharedEntities);
		}
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

	/*
	 * Asynchronously handles sharing the folder entry.
	 */
	private void onShareSelectedEntriesAsync(final List<EntityId> sharedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onShareSelectedEntriesNow(sharedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles sharing the folder entry.
	 */
	private void onShareSelectedEntriesNow(List<EntityId> sharedEntities) {
		BinderViewsHelper.shareEntities(sharedEntities);
	}
	
	/**
	 * Handles SubscribeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the SubscribeSelectedEntriesEvent.Handler.onSubscribeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSubscribeSelectedEntries(SubscribeSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> subscribedEntities = event.getSelectedEntities();
		if (isCompositeEntry(subscribedEntities)) {
			// Yes!  Run the subscribe on it.
			onSubscribeSelectedEntriesAsync(subscribedEntities);
		}
	}

	/*
	 * Asynchronously handles subscribing to the folder entry.
	 */
	private void onSubscribeSelectedEntriesAsync(final List<EntityId> subscribedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onSubscribeSelectedEntriesNow(subscribedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles subscribing to the folder entry.
	 */
	private void onSubscribeSelectedEntriesNow(List<EntityId> subscribedEntities) {
		BinderViewsHelper.subscribeToEntries(subscribedEntities);
	}
	
	/**
	 * Handles UnlockSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the UnlockSelectedEntriesEvent.Handler.onUnlockSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUnlockSelectedEntries(UnlockSelectedEntriesEvent event) {
		// Is the event targeted to this entry?
		List<EntityId> unlockedEntities = event.getSelectedEntities();
		if (isCompositeEntry(unlockedEntities)) {
			// Yes!  Run the unlock on it.
			onUnlockSelectedEntriesAsync(unlockedEntities);
		}
	}
	
	/*
	 * Asynchronously handles unlocking the folder entry.
	 */
	private void onUnlockSelectedEntriesAsync(final List<EntityId> unlockedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onUnlockSelectedEntriesNow(unlockedEntities);
			}
		});
	}
	
	/*
	 * Synchronously handles unlocking the folder entry.
	 */
	private void onUnlockSelectedEntriesNow(List<EntityId> unlockedEntities) {
		BinderViewsHelper.unlockEntries(
			unlockedEntities,
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
				m_registeredEvents,
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

	/*
	 * Updates the comments count in the header.
	 */
	private void setCommentsCount(int commentCount) {
		m_commentsHeader.setText(m_messages.folderEntry_Comments(commentCount));
	}

	/*
	/* Toggle the state of the comment widgets visibility.
	 */
	private void toggleCommentsVisibility() {
		// Toggle the state...
		m_commentsVisible = (!m_commentsVisible);
		ImageResource sliderRes;
		if (m_commentsVisible)
		     sliderRes = m_images.slideUp();
		else sliderRes = m_images.slideDown();
		m_commentsSliderImg.setUrl(sliderRes.getSafeUri().asString());
		m_commentsArea.setCommentsVisible(m_commentsVisible);
		
		// ...and store the current state in a cookie.
		if (m_commentsVisible)
		     FolderEntryCookies.setBooleanCookieValue(Cookie.COMMENTS_VISIBLE, m_vfei.getEntityId(), true);
		else FolderEntryCookies.removeCookieValue(    Cookie.COMMENTS_VISIBLE, m_vfei.getEntityId()      );
	}
	
	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
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
