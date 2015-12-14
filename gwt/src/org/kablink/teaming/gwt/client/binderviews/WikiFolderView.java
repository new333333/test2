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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.DownloadFolderAsCSVFileEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeCopyFiltersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsReadEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsUnreadEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetWikiDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.WikiDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Wiki folder view.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class WikiFolderView extends FolderViewBase
	implements
		// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler,
		DownloadFolderAsCSVFileEvent.Handler,
		InvokeDropBoxEvent.Handler,
		InvokeCopyFiltersDlgEvent.Handler,
		QuickFilterEvent.Handler,
		MarkFolderContentsReadEvent.Handler,
		MarkFolderContentsUnreadEvent.Handler
{
	public final static boolean SHOW_GWT_WIKI	= false;	//! DRF (20150326):  Leave false on checkin until it's all working.

	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private WikiDisplayDataRpcResponseData	m_wikiDisplayData;			// The wiki display data read from the server.
	private String							m_quickFilter;				// Any quick filter that's active.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.DOWNLOAD_FOLDER_AS_CSV_FILE,
		TeamingEvents.INVOKE_COPY_FILTERS_DLG,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.MARK_FOLDER_CONTENTS_READ,
		TeamingEvents.MARK_FOLDER_CONTENTS_UNREAD,
		TeamingEvents.QUICK_FILTER,
	};
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public WikiFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the super class.
		super(folderInfo, viewReady, "vibe-wikiFolderView", false);
	}
	
	/**
	 * Returns true if the entry viewer should include next/previous
	 * buttons and false otherwise. 
	 *
	 * Overrides the FolderViewBase.allowNextPrevOnEntryView() method.
	 * 
	 * @return
	 */
	@Override
	protected boolean allowNextPrevOnEntryView() {
		// By default, the wiki folder doesn't allow next/previous.
		return false;
	}
	
	/**
	 * Called to construct the view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		loadPart1Async();
	}

	/**
	 * Returns the adjustment to used for a folder view's content so
	 * that it doesn't get a vertical scroll bar.
	 * 
	 * Overrides the FolderViewBase.getNoVScrollAdjustment() method.
	 * 
	 * @return
	 */
	@Override
	public int getNoVScrollAdjustment() {
		return (super.getNoVScrollAdjustment() + 10);
	}

	/**
	 * Returns true for panels that are to be included and false
	 * otherwise.
	 * 
	 * Overrides the FolderViewBase.includePanel() method.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	@Override
	protected boolean includePanel(FolderPanels folderPanel) {
		// In the wiki folder view, we show the default panels.
		boolean reply;
		switch (folderPanel) {
		default:  reply = super.includePanel(folderPanel);  break;
		}
		return reply;
	}

	/*
	 * Asynchronously loads the wiki display data.
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
	 * Synchronously loads the wiki display data.
	 */
	private void loadPart1Now() {
		GetWikiDisplayDataCmd cmd = new GetWikiDisplayDataCmd(getFolderInfo().getBinderIdAsLong());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetWikiDisplayData();
				GwtClientHelper.handleGwtRPCFailure(caught, error);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_wikiDisplayData = ((WikiDisplayDataRpcResponseData) result.getResponseData());
				populateViewAsync();
			}			
		});
	}

	/**
	 * Called when the wiki folder view is attached.
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
	public void onContributorIdsRequest(ContributorIdsRequestEvent event) {
//!		...this needs to be implemented...
		
		// Is the event targeted to this folder?
		final Long eventBinderId = event.getBinderId();
		if (eventBinderId.equals(getFolderInfo().getBinderId())) {
			// Yes!  Asynchronously fire the corresponding reply event
			// with the contributor IDs.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(
						new ContributorIdsReplyEvent(
							eventBinderId,
							new ArrayList<Long>()));
				}
			});
		}
	}
	
	/**
	 * Called when the wiki folder view is detached.
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
	 * Handles InvokeCopyFiltersDlgEvent's received by this class.
	 * 
	 * Implements the InvokeCopyFiltersDlgEvent.Handler.onInvokeCopyFiltersDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeCopyFiltersDlg(InvokeCopyFiltersDlgEvent event) {
		// Is the event targeted to this folder?
		BinderInfo eventFolderInfo = event.getFolderInfo();
		if (eventFolderInfo.isEqual(getFolderInfo())) {
			// Yes!  Invoke the copy filters dialog on the folder.
			onInvokeCopyFiltersDlgAsync(eventFolderInfo);
		}
	}

	/*
	 * Asynchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgAsync(final BinderInfo folderInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeCopyFiltersDlgNow(folderInfo);
			}
		} );
	}
	
	/*
	 * Synchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgNow(final BinderInfo folderInfo) {
		BinderViewsHelper.invokeCopyFiltersDlg(folderInfo);
	}
		
	/**
	 * Handles InvokeDropBoxEvent's received by this class.
	 * 
	 * Implements the InvokeDropBoxEvent.Handler.onInvokeDropBox() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeDropBox(InvokeDropBoxEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the files drop box on the folder.
			BinderViewsHelper.invokeDropBox(
				getFolderInfo(),
				getEntryMenuPanel().getAddFilesMenuItem());
		}
	}
	
	/**
	 * Handles DownloadFolderAsCSVFileEvent's received by this class.
	 * 
	 * Implements the DownloadFolderAsCSVFileEvent.Handler.onDownloadFolderAsCSVFile() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDownloadFolderAsCSVFile(DownloadFolderAsCSVFileEvent event) {
		// Is the event targeted to this folder?
		Long dlFolderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = dlFolderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the download.
			BinderViewsHelper.downloadFolderAsCSVFile(
				getDownloadPanel().getForm(),
				dlFolderId);
		}
	}
	
	/**
	 * Handles MarkFolderContentsReadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsReadEvent.Handler.onMarkFolderContentsRead() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsRead(MarkFolderContentsReadEvent event) {
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = folderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Mark the folder contents as having been read.
			BinderViewsHelper.markFolderContentsRead(folderId);
		}
	}
	
	/**
	 * Handles MarkFolderContentsUnreadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsUnreadEvent.Handler.onMarkFolderContentsUnread() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsUnread(MarkFolderContentsUnreadEvent event) {
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = folderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Mark the folder contents as having been unread.
			BinderViewsHelper.markFolderContentsUnread(folderId);
		}
	}
	
	/**
	 * Handles QuickFilterEvent's received by this class.
	 * 
	 * Implements the QuickFilterEvent.Handler.onQuickFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onQuickFilter(QuickFilterEvent event) {
		// Is the event is targeted to the folder we're viewing?
		if (event.getFolderId().equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!
//!			...this needs to be implemented...
			m_quickFilter = event.getQuickFilter();
			GwtClientHelper.deferredAlert("WikiFolderView.onQuickFilter():  ...this needs to be implemented...");
		}
	}

	/*
	 * Asynchronously populates the the wiki view.
	 */
	private void populateViewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateViewNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the the wiki view.
	 */
	private void populateViewNow() {
//!		...this needs to be implemented...
		getFlowPanel().add(new Label("WikiFolderView():  ...this needs to be implemented..."));
		viewReady();
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

	/**
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		getFlowPanel().clear();
		populateViewAsync();
	}
	
	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
//!		...this needs to be implemented...
	}

	/*
	 * Asynchronously sets the size of the wiki based on its position
	 * in the view.
	 */
	private void resizeViewAsync(int delay) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				resizeView();
			}
		},
		delay);
	}

	/*
	 * Asynchronously sets the size of wiki the based on its position
	 * in the view.
	 */
	private void resizeViewAsync() {
		resizeViewAsync(INITIAL_RESIZE_DELAY);
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * Overrides the FolderViewBase.viewComplete() method.
	 */
	@Override
	public void viewComplete() {
		// Tell the wiki to resize itself now that it can determine how
		// big everything is.
		resizeViewAsync();
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the wiki folder view and perform some operation on it.        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Loads the WikiFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(WikiFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				WikiFolderView dfView = new WikiFolderView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_WikiFolderView());
				vClient.onUnavailable();
			}
		});
	}
}
