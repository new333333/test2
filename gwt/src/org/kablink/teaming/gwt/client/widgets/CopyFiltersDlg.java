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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FindControlBrowseEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderFilter;
import org.kablink.teaming.gwt.client.rpc.shared.FolderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderFiltersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderFiltersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.FindControlBrowserPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements a Copy Filters dialog.
 *  
 * @author drfoster@novell.com
 */
public class CopyFiltersDlg extends DlgBox
	implements EditSuccessfulHandler,
		// Event handlers implemented by this class.
		FindControlBrowseEvent.Handler,
		SearchFindResultsEvent.Handler
{
	private BinderInfo						m_folderInfo;				// BinderInfo of the folder filters are to be copied to.
	private Button							m_browseButton;				// Button used to connect a browse widget to the find control.
	private FindCtrl						m_findControl;				// The find control for selecting the source folder.
	private FolderFiltersRpcResponseData	m_folderFilters;			// The filters queried from m_sourceFolder.
	private GwtFolder						m_sourceFolder;				// The currently selected folder returned by the find control, if any.
	private GwtTeamingImageBundle			m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered for this dialog.
	private ScrollPanel						m_filtersScroller;			// The ScrollPanel that contains the filters from the source folder.
	private VibeFlowPanel					m_contentPanel;				// The panel containing the main content of the dialog.
	private VibeVerticalPanel				m_filtersPanel;				// The panel containing the filters themselves.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.FIND_CONTROL_BROWSE,
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CopyFiltersDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.OkCancel);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_images   = GwtTeaming.getImageBundle();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.copyFiltersDlgCaption(),	// The dialog's caption.
			this,								// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),			// The dialog's EditCanceledHandler.
			null);								// Create callback data.  Unused. 
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
		// Create the main panel for the dialog's content...
		VibeFlowPanel mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName("vibe-copyFiltersDlg-mainPanel");

		// ...create the constituent parts and add them to the main
		// ...panel...
		createContentPanel(mainPanel);

		// ...and return the main panel
		return mainPanel;
	}

	/*
	 * Create the controls needed in the content.
	 */
	private void createContentPanel(Panel mainPanel) {
		// Create the panel to be used for the dialog content (below
		// the header) and add it to the main panel.
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-copyFiltersDlg-contentPanel");
		mainPanel.add(m_contentPanel);
		
		// Add a ScrollPanel for the filters.
		m_filtersScroller = new ScrollPanel();
		m_filtersScroller.addStyleName("vibe-copyFiltersDlg-scrollPanel");
		mainPanel.add(m_filtersScroller);

		// Add a vertical panel for the ScrollPanel's content.
		m_filtersPanel = new VibeVerticalPanel(null, null);
		m_filtersPanel.addStyleName("vibe-copyFiltersDlg-filtersPanel");
		m_filtersScroller.add(m_filtersPanel);
		
		// Hide the panel we show the filters in until we have some
		// filters for the user to select.
		m_filtersScroller.setVisible(false);
		
		// Set the styles that enable scrolling.
		m_filtersPanel.removeStyleName("vibe-copyFiltersDlg-scrollLimit");	// Limit on the ScrollPanel...
		m_filtersScroller.addStyleName("vibe-copyFiltersDlg-scrollLimit");	// ...not the VerticalPanel.
	}
	
	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param unused - callbackData is unused.
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object unused) {
		// If ther's nothing to copy...
		int count = ((null == m_folderFilters) ? 0 : m_folderFilters.getTotalFiltersCount());
		if (0 == count) {
			// ...simply let the dialog close.
			return true;
		}

		// Create the GWT RPC command to save the filters to be copied.
		SaveFolderFiltersCmd saveCmd = new SaveFolderFiltersCmd(m_folderInfo, m_sourceFolder);

		// Add the selected global filters to the save command.
		count = 0;
		List<FolderFilter> globalFilters = m_folderFilters.getGlobalFilters();
		if (!(globalFilters.isEmpty())) {
			for (FolderFilter globalFilter:  globalFilters) {
				CheckBox cb = ((CheckBox) globalFilter.getUiData());
				if (cb.getValue()) {
					count += 1;
					saveCmd.addGlobalFilter(globalFilter);
				}
			}
		}

		// Add the selected personal filters to the save command.
		List<FolderFilter> personalFilters = m_folderFilters.getPersonalFilters();
		if (!(personalFilters.isEmpty())) {
			for (FolderFilter personalFilter:  personalFilters) {
				CheckBox cb = ((CheckBox) personalFilter.getUiData());
				if (cb.getValue()) {
					count += 1;
					saveCmd.addPersonalFilter(personalFilter);
				}
			}
		}
		
		// Were any filters selected to be copied?
		if (0 == count) {
			// No!  Tell the user and leave the dialog open.
			GwtClientHelper.deferredAlert(m_messages.copyFiltersDlg_Error_NothingSelected());
			setOkEnabled(true);
			return false;
		}

		// Send the GWT RPC request to save the selected filters.
		setOkEnabled(false);
	    showDlgBusySpinner();
		GwtClientHelper.executeCommand(saveCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				hideDlgBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveFolderFilters(),
					m_folderInfo.getBinderId());
				setOkEnabled(true);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Were any errors returned from the save?
				hideDlgBusySpinner();
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) result.getResponseData());
				if (responseData.hasErrors()) {
					// Yes!  Display them.
					GwtClientHelper.displayMultipleErrors(
						m_messages.copyFiltersDlg_Error_SaveErrors(),
						responseData.getErrorList());
				}

				// Were any of these actual errors (vs. simply
				// warnings)?
				setOkEnabled(true);
				if (0 == responseData.getErrorCount()) {
					// No!  Hide the dialog and force the UI to
					// refresh.
					hide();
					FullUIReloadEvent.fireOneAsync();
				}
			}
		});

		// Return false the leave the dialog open.  It will be closed
		// if the copied filters get successfully saved.
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

	/*
	 * Sends a GWT RPC request to the server for the filters defined on
	 * the selected folder.  Once they're returned, they're used to
	 * populate the filter list so the user can select which filters
	 * they want to copy.
	 */
	private void getFolderFilters() {
		GetFolderFiltersCmd cmd = new GetFolderFiltersCmd(m_sourceFolder);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_GetFolderFilters(),
					m_sourceFolder.getFolderId());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Save the filters from the selected folder and use
				// them to populate the filter list.
				m_folderFilters = ((FolderFiltersRpcResponseData) result.getResponseData());
				populateFilterListFromDataAsync();
			}
		});
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
		// Put the focus in the search widget.
		return ((null == m_findControl) ? null : m_findControl.getFocusWidget());
	}

	/*
	 * Asynchronously loads the find control.
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
	 * Synchronously loads the find control.
	 */
	private void loadPart1Now() {
		FindCtrl.createAsync(this, SearchType.FOLDERS, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				// Save the find control...
				m_findControl = findCtrl;
				m_findControl.addStyleName("vibe-copyFiltersDlg_findWidget");
				
				// ...and populate the dialog.
				populateDlgAsync();
			}
		});
	}
	
    /**
     * Called after the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingEnded() method.
     */
	@Override
    protected void okBtnProcessingEnded() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
    /**
     * Called before the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingStarted() method.
     */
	@Override
    protected void okBtnProcessingStarted() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
	/**
	 * Called when the dialog is attached.
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
	 * Called when the dialog is detached.
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
	 * Handles FindControlBrowseEvent's received by this class.
	 * 
	 * Implements the FindControlBrowseEvent.Handler.onFindControlBrowse()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onFindControlBrowse(FindControlBrowseEvent event) {
		// Simply invoke the find browser using the parameters from the
		// event.
		FindControlBrowserPopup.doBrowse(event.getFindControl(), event.getFindStart());
	}
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults()
	 * method.
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
			// Yes!  Is it the folder we're copying to?
			GwtFolder selectedFolder = ((GwtFolder) obj);
			if (Long.parseLong(selectedFolder.getFolderId()) == m_folderInfo.getBinderIdAsLong().longValue()) {
				// Yes!  Tell the user to select something else and
				// bail.
				GwtClientHelper.deferredAlert(m_messages.copyFiltersDlg_Error_CantCopyFromSelf());
				return;
			}
			
			// Save the selected folder it and use it to populate the
			// filters list.
			m_sourceFolder = selectedFolder;
			getFolderFilters();
		}
		else {
			// No, it's not a GwtFolder!  Whatever it is, we can't
			// handle it.
			GwtClientHelper.deferredAlert(m_messages.copyFiltersDlg_Error_InvalidSearchResult());
		}
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}

	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog (from a previous
		// usage.)
		m_contentPanel.clear();
		m_filtersPanel.clear();
		m_filtersScroller.setVisible(false);

		// Add a hint at the top of the dialog explaining what to do.
		Label hint = new Label(m_messages.copyFiltersDlg_Hint());
		hint.addStyleName("vibe-copyFiltersDlg-hint");
		m_contentPanel.add(hint);
				
		// Add the search widget...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-copyFiltersDlg_findPanel_Outer");
		m_contentPanel.add(fp);
		InlineLabel il = new InlineLabel(m_messages.copyFiltersDlgSelectSource());
		il.addStyleName("vibe-copyFiltersDlg_findLabel");
		il.setWordWrap(false);
		fp.add(il);
		VibeHorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-copyFiltersDlg_findPanel_Inner");
		hp.add(m_findControl);
		fp.add(hp);
		
		// ...and add a browse button next to the search widget.
		Image buttonImg = GwtClientHelper.buildImage(m_images.browseHierarchy(), m_messages.copyFiltersDlg_Alt_Browse());
		m_browseButton = new Button(GwtClientHelper.getWidgetHTML(buttonImg), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtTeaming.fireEventAsync(
					new FindControlBrowseEvent(
						m_findControl,
						m_sourceFolder));
			}
		});
		m_browseButton.addStyleName("vibe-copyFiltersDlg_BrowseButton");
		hp.add(m_browseButton);
		hp.setCellVerticalAlignment(m_browseButton, HasVerticalAlignment.ALIGN_MIDDLE);
		if (!(GwtClientHelper.getRequestInfo().hasRootDirAccess())) {
			m_browseButton.setVisible(false);
		}
		
		// ...and show the dialog centered on the screen.
		setCancelEnabled(true);
		setOkEnabled(    true);
		center();
	}

	/*
	 * Asynchronously populates the filter list with the data obtained
	 * from the server.
	 */
	private void populateFilterListFromDataAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateFilterListFromDataNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the filter list with the data obtained
	 * from the server.
	 */
	private void populateFilterListFromDataNow() {
		// We start with an empty filters panel.
		m_filtersPanel.clear();
		
		// Are there any filters that can be copied from the selected
		// folder?
		int count = ((null == m_folderFilters) ? 0 : m_folderFilters.getTotalFiltersCount());
		if (0 == count) {
			// No!  Tell the user about the problem and bail.
			m_filtersScroller.setVisible(false);
			GwtClientHelper.deferredAlert(m_messages.copyFiltersDlg_Error_NoFilters());
			return;
		}
		
		// We've got something that will need to be shown.
		m_filtersScroller.setVisible(true);

		// Do we have any global filters that can be copied?
		List<FolderFilter> globalFilters = m_folderFilters.getGlobalFilters();
		boolean hasGlobalFilters = (!(globalFilters.isEmpty()));
		if (hasGlobalFilters) {
			// Yes!  Add them to the filter list.
			Label il = new Label(m_messages.copyFiltersDlgCaptionGlobal());
			il.addStyleName("vibe-copyFiltersDlg_filterSectionLabel");
			m_filtersPanel.add(il);

			// Scan the global filters...
			for (FolderFilter globalFilter:  globalFilters) {
				// ...adding a checkbox selector for each.
				CheckBox selectFilterCB = new CheckBox(globalFilter.getFilterName());
				selectFilterCB.addStyleName("vibe-copyFiltersDlg-filterSelectCB");
				selectFilterCB.removeStyleName("gwt-CheckBox");
				m_filtersPanel.add(selectFilterCB);
				globalFilter.setUiData(selectFilterCB);
			}
		}
		
		// Do we have any personal filters that can be copied?
		List<FolderFilter> personalFilters = m_folderFilters.getPersonalFilters();
		if (!(personalFilters.isEmpty())) {
			// Yes!  Add them to the filter list.
			Label il = new Label(m_messages.copyFiltersDlgCaptionPersonal());
			il.addStyleName("vibe-copyFiltersDlg_filterSectionLabel");
			if (hasGlobalFilters) {
				il.addStyleName("marginTop10px");
			}
			m_filtersPanel.add(il);
			
			// Scan the private filters...
			for (FolderFilter personalFilter:  personalFilters) {
				// ...adding a checkbox selector for each.
				CheckBox selectFilterCB = new CheckBox(personalFilter.getFilterName());
				selectFilterCB.addStyleName("vibe-copyFiltersDlg-filterSelectCB");
				selectFilterCB.removeStyleName("gwt-CheckBox");
				m_filtersPanel.add(selectFilterCB);
				personalFilter.setUiData(selectFilterCB);
			}
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
	 * Asynchronously runs the given instance of the copy filters
	 * dialog.
	 */
	private static void runDlgAsync(final CopyFiltersDlg cfDlg, final BinderInfo folderInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cfDlg.runDlgNow(folderInfo);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the copy filters
	 * dialog.
	 */
	private void runDlgNow(BinderInfo folderInfo) {
		// Store the parameters...
		m_folderInfo = folderInfo;
		
		// ...make sure any previous filter list has been forgotten
		// ...about... 
		m_folderFilters = null;

		// ...and populate the dialog.
		loadPart1Async();
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
	/* the copy filters dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the copy filters dialog
	 * asynchronously after it loads. 
	 */
	public interface CopyFiltersDlgClient {
		void onSuccess(CopyFiltersDlg cfDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the CopyFiltersDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final CopyFiltersDlgClient cfDlgClient,
			
			// initAndShow parameters,
			final CopyFiltersDlg	cfDlg,
			final BinderInfo		folderInfo) {
		GWT.runAsync(CopyFiltersDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CopyFiltersDlg());
				if (null != cfDlgClient) {
					cfDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cfDlgClient) {
					// Yes!  Create it and return it via the callback.
					CopyFiltersDlg cfDlg = new CopyFiltersDlg();
					cfDlgClient.onSuccess(cfDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cfDlg, folderInfo);
				}
			}
		});
	}
	
	/**
	 * Loads the CopyFiltersDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cfDlgClient
	 */
	public static void createAsync(CopyFiltersDlgClient cfDlgClient) {
		doAsyncOperation(cfDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the copy filters dialog.
	 * 
	 * @param cfDlg
	 * @param folderInfo
	 */
	public static void initAndShow(CopyFiltersDlg cfDlg, BinderInfo folderInfo) {
		doAsyncOperation(null, cfDlg, folderInfo);
	}
}
