/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.CopyEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CopyMoveEntriesCmdBase;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.MoveEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryId;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * Implements Vibe's copy/move entries dialog.
 *  
 * @author drfoster@novell.com
 */
public class CopyMoveEntriesDlg extends DlgBox
	implements EditSuccessfulHandler,
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private boolean						m_doCopy;					// true -> The dialog is doing a copy.  false -> It's doing a move.
	private FindCtrl					m_findControl;				// The search widget.
	private GwtFolder					m_currentDest;				// Tracks the last destination used.  If a new folder isn't selected, this will be used.
	private GwtFolder					m_selectedFolder;			// The currently selected folder returned by the search widget.
	private GwtTeamingImageBundle		m_images;					// Access to Vibe's images.
	private List<EntryId>				m_entryIds;					// Current list of entry IDs to be moved.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeVerticalPanel			m_vp;						// The panel holding the dialog's content.

	// The following manage the strings used by the dialog.  The map is
	// loaded with the appropriate strings from the resource bundle for
	// the dialog based on whether it's in copy or move mode each time
	// the dialog is run.  See initDlgStrings().
	private enum StringIds{
		CAPTION1,
		CAPTION2,
		CURRENT_DEST,
		CURRENT_DEST_NONE,
		ERROR_INVALID_SEARCH,
		ERROR_OP_FAILURE,
		HEADER,
		RPC_FAILURE,
		SELECT_DEST,
		WARNING_NO_DEST,
	}
	private Map<StringIds, String> m_strMap;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CopyMoveEntriesDlg() {
		// Initialize the superclass...
		super(false, true);
		
		// ...initialize everything else...
		m_images = GwtTeaming.getImageBundle();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// No caption yet.  It's set appropriately when the dialog runs.
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCancledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	@SuppressWarnings("unused")
	private Image buildSpinnerImage() {
		return new Image(m_images.spinner16());
	}

	/*
	 * Asynchronously performs the copy/move of the entries.
	 */
	private void copyMoveEntriesAsync(final CopyMoveEntriesCmdBase cmd, final GwtFolder target) {
		ScheduledCommand doCopy = new ScheduledCommand() {
			@Override
			public void execute() {
				copyMoveEntriesNow(cmd, target);
			}
		};
		Scheduler.get().scheduleDeferred(doCopy);
	}
	
	/*
	 * Synchronously performs the copy/move of the entries.
	 */
	private void copyMoveEntriesNow(final CopyMoveEntriesCmdBase cmd, final GwtFolder target) {
		// Send a request to copy/move the entries.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_strMap.get(StringIds.RPC_FAILURE));
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did everything we ask get copied/moved?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<String> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 < count) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(m_strMap.get(StringIds.ERROR_OP_FAILURE), errors);
				}

				// If anything was copied/moved...
				if (count != m_entryIds.size()) {
					// ...force the content to refreshed to reflect
					// ...what was copied/moved.
					FullUIReloadEvent.fireOne();
				}
				
				// Finally, close the dialog.
				m_currentDest = target;
				hide();
			}
		});
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create create an return a vertical panel to hold the
		// dialog's content.
		m_vp = new VibeVerticalPanel();
		m_vp.addStyleName("vibe-cmeDlg_RootPanel");
		return m_vp;
	}

	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Has the user selected a folder yet?
		if ((null == m_selectedFolder) && (null == m_currentDest)) {
			// No!  They must before they can press OK.
			GwtClientHelper.deferredAlert(m_strMap.get(StringIds.WARNING_NO_DEST));
		}
		
		else {
			// Yes, the user has selected a destination folder!  Start
			// the copy/move.
			GwtFolder target = ((null == m_selectedFolder) ? m_currentDest : m_selectedFolder);
			Long targetFolderId = Long.parseLong(target.getFolderId());
			CopyMoveEntriesCmdBase cmd;
			if (m_doCopy)
			     cmd = new CopyEntriesCmd(targetFolderId, m_entryIds);
			else cmd = new MoveEntriesCmd(targetFolderId, m_entryIds);
			copyMoveEntriesAsync(cmd, target);
		}
		
		// Return false.  We'll close the dialog manually if/when the
		// move completes.
		return false;
	}

	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return ((null == m_findControl) ? null : m_findControl.getFocusWidget());
	}

	/*
	 * Initialize the Map of the strings used by the dialog based its
	 * current mode.
	 */
	private void initDlgStrings() {
		if (null == m_strMap)
		     m_strMap = new HashMap<StringIds, String>();
		else m_strMap.clear();
		
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		if (m_doCopy) {
			m_strMap.put(StringIds.CAPTION1,             messages.copyEntriesDlgCaption1());
			m_strMap.put(StringIds.CAPTION2,             messages.copyEntriesDlgCaption2());
			m_strMap.put(StringIds.CURRENT_DEST,         messages.copyEntriesDlgCurrentDestination());
			m_strMap.put(StringIds.CURRENT_DEST_NONE,    messages.copyEntriesDlgCurrentDestinationNone());
			m_strMap.put(StringIds.ERROR_INVALID_SEARCH, messages.copyEntriesDlgErrorInvalidSearchResult());
			m_strMap.put(StringIds.ERROR_OP_FAILURE,     messages.copyEntriesDlgErrorCopyFailures());
			m_strMap.put(StringIds.HEADER,               messages.copyEntriesDlgHeader());
			m_strMap.put(StringIds.RPC_FAILURE,          messages.rpcFailure_CopyEntries());
			m_strMap.put(StringIds.SELECT_DEST,          messages.copyEntriesDlgSelectDestination());
			m_strMap.put(StringIds.WARNING_NO_DEST,      messages.copyEntriesDlgWarningNoSelection());
		}
		
		else {
			m_strMap.put(StringIds.CAPTION1,             messages.moveEntriesDlgCaption1());
			m_strMap.put(StringIds.CAPTION2,             messages.moveEntriesDlgCaption2());
			m_strMap.put(StringIds.CURRENT_DEST,         messages.moveEntriesDlgCurrentDestination());
			m_strMap.put(StringIds.CURRENT_DEST_NONE,    messages.moveEntriesDlgCurrentDestinationNone());
			m_strMap.put(StringIds.ERROR_INVALID_SEARCH, messages.moveEntriesDlgErrorInvalidSearchResult());
			m_strMap.put(StringIds.ERROR_OP_FAILURE,     messages.moveEntriesDlgErrorMoveFailures());
			m_strMap.put(StringIds.HEADER,               messages.moveEntriesDlgHeader());
			m_strMap.put(StringIds.RPC_FAILURE,          messages.rpcFailure_MoveEntries());
			m_strMap.put(StringIds.SELECT_DEST,          messages.moveEntriesDlgSelectDestination());
			m_strMap.put(StringIds.WARNING_NO_DEST,      messages.moveEntriesDlgWarningNoSelection());
		}
	}
	
	/*
	 * Asynchronously loads the find control.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the find control.
	 */
	private void loadPart1Now() {
		FindCtrl.createAsync(this, GwtSearchCriteria.SearchType.FOLDERS, 30, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				m_findControl = findCtrl;
				m_findControl.addStyleName("vibe-cmeDlg_FindWidget");
				
				populateDlgAsync();
			}
		});
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
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(SearchFindResultsEvent event) {
		// If the find results aren't for the move entries dialog...
		if (!(((Widget) event.getSource()).equals(this))) {
			// ...ignore the event.
			return;
		}
		
		// Hide the search results list.
		m_findControl.hideSearchResults();

		// Is the search result a GwtFolder?
		GwtTeamingItem obj = event.getSearchResults();
		if (obj instanceof GwtFolder) {
			// Yes!  Save it for when the user selects OK.
			m_selectedFolder = ((GwtFolder) obj);
		}
		else {
			// No, it's not a GwtFolder!  Whatever it is, we can't
			// handle it.
			Window.alert(m_strMap.get(StringIds.ERROR_INVALID_SEARCH));
		}
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_vp.clear();
		
		// Add a caption...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-cmeDlg_CaptionPanel");
		m_vp.add(fp);
		InlineLabel il = new InlineLabel(m_strMap.get(StringIds.CAPTION1));
		il.addStyleName("vibe-cmeDlg_CaptionLeft");
		il.setWordWrap(false);
		fp.add(il);
		il = new InlineLabel(m_strMap.get(StringIds.CAPTION2));
		il.addStyleName("vibe-cmeDlg_CaptionRight");
		il.setWordWrap(false);
		fp.add(il);

		// ...add the search widgets...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-cmeDlg_FindPanel");
		m_vp.add(fp);
		il = new InlineLabel(m_strMap.get(StringIds.SELECT_DEST));
		il.addStyleName("vibe-cmeDlg_FindLabel");
		il.setWordWrap(false);
		fp.add(il);
		fp.add(m_findControl);

		// ...add information about any current destination...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-cmeDlg_DestPanel");
		m_vp.add(fp);
		il = new InlineLabel(m_strMap.get(StringIds.CURRENT_DEST));
		il.addStyleName("vibe-cmeDlg_DestLabel");
		il.setWordWrap(false);
		fp.add(il);
		il = new InlineLabel((null != m_currentDest) ? m_currentDest.getFolderName() : m_strMap.get(StringIds.CURRENT_DEST_NONE));
		il.addStyleName("vibe-cmeDlg_Dest");
		il.setWordWrap(false);
		if (null != m_currentDest) {
			String pName = m_currentDest.getParentBinderName();
			if (GwtClientHelper.hasString(pName)) {
				il.setTitle(pName);
			}
		}
		fp.add(il);

		// ...and finally, show the dialog.
		show(true);
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
	 * Asynchronously runs the given instance of the move entries
	 * dialog.
	 */
	private static void runDlgAsync(final CopyMoveEntriesDlg cmeDlg, final boolean doCopy, final FolderType folderType, final List<EntryId> entryIds) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				cmeDlg.runDlgNow(doCopy, folderType, entryIds);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the move entries
	 * dialog.
	 */
	private void runDlgNow(boolean doCopy, FolderType folderType, List<EntryId> entryIds) {
		// Store the parameters...
		m_doCopy   = doCopy;
		m_entryIds = entryIds;
		
		// ...initialize any other data members...
		m_selectedFolder = null;
		initDlgStrings();
		setCaption(m_strMap.get(StringIds.HEADER));

		// ...and populate the dialog and show it.
		loadPart1Async();
		show(true);
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
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the move entries dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the move entries dialog
	 * asynchronously after it loads. 
	 */
	public interface CopyMoveEntriesDlgClient {
		void onSuccess(CopyMoveEntriesDlg cmeDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the CopyMoveEntriesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final CopyMoveEntriesDlgClient cmeDlgClient,
			
			// initAndShow parameters,
			final CopyMoveEntriesDlg cmeDlg,
			final boolean doCopy,
			final FolderType folderType,
			final List<EntryId> entryIds) {
		GWT.runAsync(CopyMoveEntriesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CopyMoveEntriesDlg());
				if (null != cmeDlgClient) {
					cmeDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cmeDlgClient) {
					// Yes!  Create it and return it via the callback.
					CopyMoveEntriesDlg cmeDlg = new CopyMoveEntriesDlg();
					cmeDlgClient.onSuccess(cmeDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cmeDlg, doCopy, folderType, entryIds);
				}
			}
		});
	}
	
	/**
	 * Loads the CopyMoveEntriesDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param cmeDlgClient
	 */
	public static void createAsync(CopyMoveEntriesDlgClient cmeDlgClient) {
		doAsyncOperation(cmeDlgClient, null, false, null, null);
	}
	
	/**
	 * Initializes and shows the move entries dialog.
	 * 
	 * @param cmeDlg
	 * @param doCopy
	 * @param folderType
	 * @param entryIds
	 */
	public static void initAndShow(CopyMoveEntriesDlg cmeDlg, boolean doCopy, FolderType folderType, List<EntryId> entryIds) {
		doAsyncOperation(null, cmeDlg, doCopy, folderType, entryIds);
	}
}
