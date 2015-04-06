/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.rpc.shared.ChangeEntryTypesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData.EntryType;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryTypesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements Vibe's change entry types dialog.
 *  
 * @author drfoster@novell.com
 */
public class ChangeEntryTypesDlg extends DlgBox implements EditSuccessfulHandler {
	private EntryType			m_baseEntryType;	// The EntryType of an individual item, if all that was requested.
	private GwtTeamingMessages	m_messages;			// Access to Vibe's messages.
	private ListBox				m_entryTypeLB;		// The list of entry types the user can choose from.
	private List<EntityId>		m_entityIds;		// Current list of entity IDs whose entry types are to be changed.
	private List<EntryType>		m_entryTypes;		// List<EntryType> for the binder's referred to by m_entityIds.
	private String				m_baseEntryTitle;	// The title of an individual item, if all that was requested.
	private VibeEventBase<?>	m_reloadEvent;		//	
	private VibeFlowPanel		m_fp;				// The panel holding the dialog's content.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ChangeEntryTypesDlg() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// The actual header will be supplied when the dialog is populated.
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	@SuppressWarnings("unused")
	private Image buildSpinnerImage(String style) {
		Image reply = GwtClientHelper.buildImage(GwtTeaming.getImageBundle().spinner16());
		if (GwtClientHelper.hasString(style)) {
			reply.addStyleName(style);
		}
		return reply;
	}

	/*
	 * Asynchronously performs the change entry types.
	 */
	private void changeEntryTypesAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				changeEntryTypesNow();
			}
		});
	}
	
	/*
	 * Synchronously performs the change entry types.
	 */
	private void changeEntryTypesNow() {
		// Has the user selected something other than the 'select an
		// entry type' option?
		int selIndex = m_entryTypeLB.getSelectedIndex();
		if (0 == selIndex) {
			// No!  Tell them they must and bail.
			GwtClientHelper.deferredAlert(m_messages.changeEntryTypesDlgErrorNoSelection());
			hideDlgBusySpinner();
			setOkEnabled(true);
			return;
		}
		
		// Can we perform the change?
		String defId = m_entryTypeLB.getValue(selIndex);
		ChangeEntryTypesCmd cetCmd = new ChangeEntryTypesCmd(defId, m_entityIds);
		GwtClientHelper.executeCommand(cetCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_ChangeEntryTypes());
				hideDlgBusySpinner();
				setOkEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Perhaps!  Did we get any errors back?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData());
				List<ErrorInfo> changeErrors = responseData.getErrorList();
				if ((null != changeErrors) && (!(changeErrors.isEmpty()))) {
					// Yes!  Display them to the user.
					GwtClientHelper.displayMultipleErrors(
						m_messages.changeEntryTypesDlgErrorChangeFailures(),
						changeErrors);
				}

				// If the caller requested notification when we're
				// done...
				if (null != m_reloadEvent) {
					// ...tell them.
					GwtTeaming.fireEventAsync(m_reloadEvent);
				}
				
				// Simply close the dialog.
				hideDlgBusySpinner();
				setOkEnabled(true);
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
		// Create and return a panel to hold the dialog's content.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-changeEntryTypesRootPanel");
		return m_fp;
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
		// Start the change...
		showDlgBusySpinner();
		setOkEnabled(false);
		changeEntryTypesAsync();
		
		// ...and return false.  We'll close the dialog manually
		// ...if/when the changes complete.
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
		return null;
	}

	/*
	 * Asynchronously loads the entry types the user can select from.
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
	 * Synchronously loads the entry types the user can select from.
	 */
	private void loadPart1Now() {
		// Collect the unique binder IDs that are participating in the
		// change entry types...
		GetEntryTypesCmd getCmd = new GetEntryTypesCmd();
		List<Long> binderIds = getCmd.getBinderIds();
		for (EntityId entityId:  m_entityIds) {
			GwtClientHelper.addLongToListLongIfUnique(binderIds, entityId.getBinderId());
		}
		if (1 == m_entityIds.size()) {
			getCmd.setEntityId(m_entityIds.get(0));
		}
		
		// ...get their available entry types...
		GwtClientHelper.executeCommand(getCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetEntryTypes());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and use them to run the dialog.
				EntryTypesRpcResponseData etResponse = ((EntryTypesRpcResponseData) response.getResponseData());
				m_baseEntryType  = etResponse.getBaseEntryType();
				m_baseEntryTitle = etResponse.getBaseEntryTitle();
				m_entryTypes     = etResponse.getEntryTypes();
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
		// Set an appropriate title for the dialog.
		setCaption(
			GwtClientHelper.hasString(m_baseEntryTitle) ?
				m_messages.changeEntryTypesDlgHeaderFor(m_baseEntryTitle) :
				m_messages.changeEntryTypesDlgHeader());
		
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_fp.clear();

		// If we have a base entry type...
		InlineLabel il;
		VibeFlowPanel fp;
		if (null != m_baseEntryType) {
			// ...display it at the top of the dialog...
			fp = new VibeFlowPanel();
			fp.addStyleName("vibe-changeEntryTypesCurPanel");
			il = new InlineLabel(m_messages.changeEntryTypesDlgCurrent());
			il.addStyleName("vibe-changeEntryTypesCurLabel");
			il.setWordWrap(false);
			fp.add(il);
			il = new InlineLabel(m_baseEntryType.getDefKey());
			il.addStyleName("vibe-changeEntryTypesCurDef");
			il.setWordWrap(false);
			fp.add(il);
			m_fp.add(fp);
		}

		// ...add the <SELECT> for the new entry type...
		il = new InlineLabel(m_messages.changeEntryTypesDlgNew());
		il.addStyleName("vibe-changeEntryTypesNewLabel");
		il.setWordWrap(false);
		m_fp.add(il);
		m_entryTypeLB = new ListBox();
		m_entryTypeLB.addStyleName("vibe-changeEntryTypesNewSelect");
		m_fp.add(m_entryTypeLB);
		m_entryTypeLB.addItem(m_messages.changeEntryTypesDlgSelect(), "");
		for (EntryType et:  m_entryTypes) {
			m_entryTypeLB.addItem(et.getDefKey(), et.getDefId());
		}
		m_entryTypeLB.setSelectedIndex(0);

		// ...add a note about new/removed data elements...
		Label l = new Label(m_messages.changeEntryTypesDlgNote());
		l.addStyleName("vibe-changeEntryTypesNote");
		m_fp.add(l);

		// ...add a hint about local definitions...
		l = new Label(m_messages.changeEntryTypesDlgLocal());
		l.addStyleName("vibe-changeEntryTypesFooter");
		l.setWordWrap(false);
		m_fp.add(l);

		// ...and show the dialog.
		setCancelEnabled(true);
		setOkEnabled(    true);
		show(            true);
	}
	
	/*
	 * Asynchronously runs the given instance of the change entry types
	 * dialog.
	 */
	private static void runDlgAsync(final ChangeEntryTypesDlg cetDlg, final List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cetDlg.runDlgNow(entityIds, reloadEvent);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the change entry types
	 * dialog.
	 */
	private void runDlgNow(List<EntityId> entityIds, VibeEventBase<?> reloadEvent) {
		// Store the parameter...
		m_entityIds   = entityIds;
		m_reloadEvent = reloadEvent;
		
		// ...initialize any other data members...
		m_baseEntryType  = null;
		m_baseEntryTitle = null;
		
		// ...and populate the dialog.
		loadPart1Async();
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the change entry types dialog and perform some operation on   */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the change entry types
	 * dialog asynchronously after it loads. 
	 */
	public interface ChangeEntryTypesDlgClient {
		void onSuccess(ChangeEntryTypesDlg cetDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ChangeEntryTypesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ChangeEntryTypesDlgClient cetDlgClient,
			
			// initAndShow parameters,
			final ChangeEntryTypesDlg	cetDlg,
			final List<EntityId>		entityIds,
			final VibeEventBase<?>		reloadEvent) {
		GWT.runAsync(ChangeEntryTypesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ChangeEntryTypesDlg());
				if (null != cetDlgClient) {
					cetDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cetDlgClient) {
					// Yes!  Create it and return it via the callback.
					ChangeEntryTypesDlg cetDlg = new ChangeEntryTypesDlg();
					cetDlgClient.onSuccess(cetDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cetDlg, entityIds, reloadEvent);
				}
			}
		});
	}
	
	/**
	 * Loads the ChangeEntryTypesDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cetDlgClient
	 */
	public static void createAsync(ChangeEntryTypesDlgClient cetDlgClient) {
		doAsyncOperation(cetDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the change entry types dialog.
	 * 
	 * @param cetDlg
	 * @param entityIds
	 */
	public static void initAndShow(ChangeEntryTypesDlg cetDlg, List<EntityId> entityIds, final VibeEventBase<?> reloadEvent) {
		doAsyncOperation(null, cetDlg, entityIds, reloadEvent);
	}
	
	public static void initAndShow(ChangeEntryTypesDlg cetDlg, List<EntityId> entityIds) {
		// Always use the initial form of the method.
		initAndShow(cetDlg, entityIds, null);
	}
}
