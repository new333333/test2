/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements Vibe's move entries dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class MoveEntriesDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private FolderType				m_folderType;	// The type of folder that we're currently dealing with.
	private GwtTeamingImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingMessages		m_messages;		// Access to Vibe's messages.
	private Long					m_folderId;		// The folder whose entries are being moved.
	private List<Long>				m_entryIds;		// Current list of entry IDs to be moved.
	private VibeVerticalPanel		m_vp;			// The panel holding the dialog's content.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MoveEntriesDlg() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.moveEntriesDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage() {
		return new Image(m_images.spinner16());
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
		m_vp.addStyleName("vibe-moveEntriesDlgRootPanel");
		return m_vp;
	}

	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled() {
		// Simply return true to allow the dialog to close.
		return true;
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
		// Start the move...
		moveEntriesAsync();
		
		// ...and return false.  We'll close the dialog manually
		// ...if/when the move completes.
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
	 * Asynchronously performs the entry move.
	 */
	private void moveEntriesAsync() {
		ScheduledCommand doMove = new ScheduledCommand() {
			@Override
			public void execute() {
				moveEntriesNow();
			}
		};
		Scheduler.get().scheduleDeferred(doMove);
	}
	
	/*
	 * Synchronously performs the entry move.
	 */
	private void moveEntriesNow() {
//!		...this needs to be implemented...
		hide();
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
	}
	
	/*
	 * Asynchronously runs the given instance of the move entries
	 * dialog.
	 */
	private static void runDlgAsync(final MoveEntriesDlg moveEntriesDlg, final Long folderId, final FolderType folderType, final List<Long> entryIds) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				moveEntriesDlg.runDlgNow(folderId, folderType, entryIds);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the move entries
	 * dialog.
	 */
	private void runDlgNow(Long folderId, FolderType folderType, List<Long> entryIds) {
		// Store the parameters...
		m_folderId   = folderId;
		m_folderType = folderType;
		m_entryIds   = entryIds;

		// ...and populate the dialog and show it.
		populateDlgAsync();
		show(true);
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the move entries dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the move entries dialog
	 * asynchronously after it loads. 
	 */
	public interface MoveEntriesDlgClient {
		void onSuccess(MoveEntriesDlg moveEntriesDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the MoveEntriesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final MoveEntriesDlgClient moveEntriesDlgClient,
			
			// initAndShow parameters,
			final MoveEntriesDlg moveEntriesDlg,
			final Long folderId,
			final FolderType folderType,
			final List<Long> entryIds) {
		GWT.runAsync(MoveEntriesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_MoveEntriesDlg());
				if (null != moveEntriesDlgClient) {
					moveEntriesDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != moveEntriesDlgClient) {
					// Yes!  Create it and return it via the callback.
					MoveEntriesDlg moveEntriesDlg = new MoveEntriesDlg();
					moveEntriesDlgClient.onSuccess(moveEntriesDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(moveEntriesDlg, folderId, folderType, entryIds);
				}
			}
		});
	}
	
	/**
	 * Loads the MoveEntriesDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param moveEntriesDlgClient
	 */
	public static void createAsync(MoveEntriesDlgClient moveEntriesDlgClient) {
		doAsyncOperation(moveEntriesDlgClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the move entries dialog.
	 * 
	 * @param moveEntriesDlg
	 * @param folderId
	 * @param folderType
	 * @param entryIds
	 */
	public static void initAndShow(MoveEntriesDlg moveEntriesDlg, Long folderId, FolderType folderType, List<Long> entryIds) {
		doAsyncOperation(null, moveEntriesDlg, folderId, folderType, entryIds);
	}
}
