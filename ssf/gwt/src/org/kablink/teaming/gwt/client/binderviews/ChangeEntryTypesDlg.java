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

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData.EntryType;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryTypesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
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


/**
 * Implements Vibe's change entry types dialog.
 *  
 * @author drfoster@novell.com
 */
//@SuppressWarnings("unused")
public class ChangeEntryTypesDlg extends DlgBox implements EditSuccessfulHandler {
	private EntryType			m_entryType;	// The EntryType of an individual item, if all that was requested.
	private GwtTeamingMessages	m_messages;		// Access to Vibe's messages.
	private List<EntryId>		m_entryIds;		// Current list of entry IDs whose entry types are to be changed.
	private List<EntryType>		m_entryTypes;	// List<EntryType> for the binder's referred to by m_entryIds.
	private VibeVerticalPanel	m_vp;			// The panel holding the dialog's content.

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
			m_messages.changeEntryTypesHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	@SuppressWarnings("unused")
	private Image buildSpinnerImage(String style) {
		Image reply = new Image(GwtTeaming.getImageBundle().spinner16());
		reply.getElement().setAttribute("align", "absmiddle");
		if (GwtClientHelper.hasString(style)) {
			reply.addStyleName(style);
		}
		return reply;
	}

	/*
	 * Asynchronously performs the change entry types.
	 */
	private void changeEntryTypesAsync() {
		ScheduledCommand doChange = new ScheduledCommand() {
			@Override
			public void execute() {
				changeEntryTypesNow();
			}
		};
		Scheduler.get().scheduleDeferred(doChange);
	}
	
	/*
	 * Synchronously performs the change entry types.
	 */
	private void changeEntryTypesNow() {
//!		...this needs to be implemented...
		hide();
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
		m_vp.addStyleName("vibe-changeEntryTypesRootPanel");
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
		// Start the change...
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
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the entry types the user can select from.
	 */
	private void loadPart1Now() {
		// Collect the unique binder IDs that are participating in the
		// change entry types...
		GetEntryTypesCmd getCmd = new GetEntryTypesCmd();
		List<Long> binderIds = getCmd.getBinderIds();
		for (EntryId entryId:  m_entryIds) {
			GwtClientHelper.addLongToListLongIfUnique(binderIds, entryId.getBinderId());
		}
		if (1 == m_entryIds.size()) {
			getCmd.setEntryId(m_entryIds.get(0));
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
				m_entryType  = etResponse.getEntryType();
				m_entryTypes = etResponse.getEntryTypes();
				populateDlgAsync();
			}
		});
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
		
//!		...this needs to be implemented...
		m_vp.add(new InlineLabel("...this needs to be implemented..."));
		m_vp.add(new InlineLabel("- - - - -"));
		if (null != m_entryType) {
			m_vp.add(new InlineLabel("id:" + m_entryType.getDefId() + ", key:" + m_entryType.getDefKey() + ", local:" + m_entryType.isLocalDef()));
			m_vp.add(new InlineLabel("- - - - -"));
		}
		for (EntryType et:  m_entryTypes) {
			m_vp.add(new InlineLabel("id:" + et.getDefId() + ", key:" + et.getDefKey() + ", local:" + et.isLocalDef()));
		}
		m_vp.add(new InlineLabel("- - - - -"));
		m_vp.add(new InlineLabel("...this needs to be implemented..."));
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the change entry types
	 * dialog.
	 */
	private static void runDlgAsync(final ChangeEntryTypesDlg cetDlg, final List<EntryId> entryIds) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				cetDlg.runDlgNow(entryIds);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the change entry types
	 * dialog.
	 */
	private void runDlgNow(List<EntryId> entryIds) {
		// Store the parameter and populate the dialog.
		m_entryIds = entryIds;
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
			final ChangeEntryTypesDlg cetDlg,
			final List<EntryId> entryIds) {
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
					runDlgAsync(cetDlg, entryIds);
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
		doAsyncOperation(cetDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the change entry types dialog.
	 * 
	 * @param cetDlg
	 * @param entryIds
	 */
	public static void initAndShow(ChangeEntryTypesDlg cetDlg, List<EntryId> entryIds) {
		doAsyncOperation(null, cetDlg, entryIds);
	}
}
