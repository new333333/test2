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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper.DeletePurgeEntriesCallback;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.util.DeleteSelectionsMode;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.SelectionDetails;
import org.kablink.teaming.gwt.client.util.SelectionDetailsHelper;
import org.kablink.teaming.gwt.client.util.SelectionDetailsHelper.SelectionDetailsCallback;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the delete selections dialog.
 *  
 * @author drfoster@novell.com
 */
public class DeleteSelectionsDlg extends DlgBox implements EditSuccessfulHandler {
	private DialogMode					m_dialogMode;		// The mode the dialog is running in.  Defines what's displayed on the dialog.
	private DeletePurgeEntriesCallback	m_dpeCallback;		// Callback used to interact with who called the dialog.
	private FlexCellFormatter			m_cellFormatter;	// The formatter to control how m_grid is laid out.
	private FlexTable					m_grid;				// The table holding the dialog's content.
	private GwtTeamingImageBundle		m_images;			// Access to the base images.
	private GwtTeamingMessages			m_messages;			// Access to our localized strings.
	private Image						m_warningImg;		// The <IMG>  on the 'can't be undone' warning. 
	private InlineLabel					m_warningTxt;		// The <SPAN> on the 'can't be undone' warning.
	private List<EntityId>				m_entityIds;		// The entities to be deleted.
	private RadioButton					m_purgeRB;			// The 'Delete from system' radio button.
	private RadioButton					m_trashRB;			// The 'Move to trash'      radio button.
	private SelectionDetails			m_selectionDetails;	// Populated via a GWT RPC call while constructing the dialog's contents.  Contains an analysis of what m_entityIds refers to.
	
	// The buttons displayed on this dialog.
	private final static DlgButtonMode	DLG_BUTTONS = DlgButtonMode.OkCancel; 

	/*
	 * Enumeration type used to define how the dialog prompts the user.
	 *  
	 * See the following for where the definitions of these come from:
	 *    https://teaming.innerweb.novell.com/ssf/a/c/p_name/ss_forum/p_action/1/action/view_permalink/entityType/folderEntry/entryId/422728/vibeonprem_url/1
	 */
	private enum DialogMode {
		Situation1,	// Selections contain only items from personal storage.
		Situation2,	// Selections contain only remote (i.e., Cloud Folder, Net Folder or Vibe Mirrored Folder) items.
		Situation3	// Selections contain a mixture of personal storage and remote items.
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DeleteSelectionsDlg() {
		// Initialize the superclass...
		super(false, true, DLG_BUTTONS);

		// ...initialize everything else...
		addStyleName("vibe-deleteSelectionsDlg");
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.deleteSelectionsDlgHeader(),	// The dialog's header.
			this,									// The dialog's EditSuccessfulHandler.
			DlgBox.getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);									// Create callback data.  Unused. 
	}

	/*
	 * Returns a Widget containing what's displayed for purge warnings.
	 */
	private Widget buildWarningWidget(String text) {
		HorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-deleteSelectionsDlg-warningPanel");
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		m_warningImg = GwtClientHelper.buildImage(m_images.warningIcon16());
		m_warningImg.addStyleName("vibe-deleteSelectionsDlg-warningImg");
		hp.add(m_warningImg);
		
		m_warningTxt = new InlineLabel(text);
		m_warningTxt.addStyleName("vibe-deleteSelectionsDlg-warningTxt");
		hp.add(m_warningTxt);
		
		return hp;
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
		// Create and return a table to hold the dialog's content.
		m_grid = new VibeFlexTable();
		m_grid.addStyleName("vibe-deleteSelectionsDlg-rootPanel");
		m_grid.setCellPadding(0);
		m_grid.setCellSpacing(0);
		m_cellFormatter = m_grid.getFlexCellFormatter();
		return m_grid;
	}

	/*
	 * Asynchronously deletes the selected items.
	 */
	private void doDeletesAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doDeletesNow();
			}
		});
	}
	
	/*
	 * Synchronously deletes the selected items.
	 */
	private void doDeletesNow() {
		// Determine how to perform the delete...
		DeleteSelectionsMode dsMode;
		switch (m_dialogMode) {
		default:
		case Situation1:
		case Situation3:
			if (m_purgeRB.getValue()) {
				dsMode = DeleteSelectionsMode.PURGE_ALL;
			}
			
			else {
				if (DialogMode.Situation1.equals(m_dialogMode))
				     dsMode = DeleteSelectionsMode.TRASH_ALL;
				else dsMode = DeleteSelectionsMode.TRASH_ADHOC_PURGE_OTHERS;
			}
			break;
			
		case Situation2:
			dsMode = DeleteSelectionsMode.PURGE_ALL;
			break;
		}

		// ...and do it.
		DeletePurgeEntriesHelper.deleteSelectedEntriesAsync(
			m_entityIds,
			dsMode,
			m_dpeCallback);
	}
	
	/**
	 * Called is the user selects the dialog's Ok button.
	 * 
	 * @param callbackData
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Asynchronously handle the Ok...
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				editSuccessfulNow();
			}
		});

		// ...and return false to keep the dialog open.  We'll close it
		// ...when we actually perform the delete.
		return false;
	}
	
	/*
	 * Called if the user selects the dialog's Ok button.
	 */
	private void editSuccessfulNow() {
		// Hide the dialog.
		hide();
		
		// Are we performing delete that needs a user confirmation?
		//
		// We need to confirm if:
		// 1) we have some purge confirmations; and
		// 2) We're in a mixed selection mode (situation 3) and the
		//    user has chosen to move some things to the trash and
		//    purge others.
		boolean needsConfirmaiton =
			(m_selectionDetails.hasPurgeConfirmations() &&
			(m_dialogMode.equals(DialogMode.Situation3) && m_trashRB.getValue()));
		
		if (needsConfirmaiton) {
			// Yes!  Does the user accept what's going to be purged?
			GwtClientHelper.displayMultipleErrors(
						m_messages.deleteSelectionsDlgConfirm(),
						m_selectionDetails.getPurgeConfirmations(),
						new ConfirmCallback() {
					@Override
					public void dialogReady() {
						// Ignored.  We don't care when the dialog is
						// ready.
					}
	
					@Override
					public void accepted() {
						// Yes, the user said do it!  Start deleting the
						// selections.
						doDeletesAsync();
					}
	
					@Override
					public void rejected() {
						// No, the user said don't do it!  Reopen the
						// dialog.
						show();
					}
				},
				DLG_BUTTONS);
		}
		
		else {
			// No, we're not performing a delete that needs a user
			// confirmation!  Start deleting the selections.
			doDeletesAsync();
		}
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
	 * Asynchronously populates the contents of the dialog.
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
	 * Synchronously populates the contents of the dialog.
	 */
	private void loadPart1Now() {
		SelectionDetailsHelper.getSelectionDetails(m_entityIds, new SelectionDetailsCallback() {
			@Override
			public void onFailure() {
				// Nothing to do.  getSelectionDetails() will have told
				// the user about it's problems.
			}

			@Override
			public void onSuccess(SelectionDetails selectionDetails) {
				// Store the details and continue populating the
				// dialog.
				m_selectionDetails = selectionDetails;
				setDialogMode();
				populateDlgAsync();
			}
		});
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
		// usage, ...)
		m_grid.removeAllRows();
		
		// ...forget any previous widgets...
		m_trashRB =
		m_purgeRB = null;

		// ...and repopulate the dialog.
		switch (m_dialogMode) {
		case Situation1:  populateSituation1(); break;
		case Situation2:  populateSituation2(); break;
		case Situation3:  populateSituation3(); break;
		}
		
		// ...and show the dialog.
		center();
	}

	/*
	 * Dialog has two radio buttons:
	 *    - Move to trash (default); and
	 *    - Delete from system (and a warning that the operation cannot
	 *      be undone.)
	 */
	private void populateSituation1() {
		// Create a ValueChangeHandler we can use to tweak the dialog
		// when the user changes the selected radio button.
		ValueChangeHandler<Boolean> rbChangedHandler = new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setPurgeWarningActive(m_purgeRB.getValue());
			}
		};
		
		// Add the 'Move to trash' radio button...
		m_trashRB = new RadioButton("deleteMode");
		m_trashRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_trashRB.addValueChangeHandler(rbChangedHandler);
		m_trashRB.setValue(true);
		m_grid.setWidget(            0, 0, m_trashRB);
		m_cellFormatter.addStyleName(0, 0, "vibe-deleteSelectionsDlg-trashRadio");
		
		m_grid.setText(              0, 1, m_messages.deleteSelectionsDlgLabel_Trash());
		m_cellFormatter.addStyleName(0, 1, "vibe-deleteSelectionsDlg-trashRadioLabel");
		m_cellFormatter.setWordWrap( 0, 1, false);
		
		// ...add the 'Delete from system' radio button...
		m_purgeRB = new RadioButton("deleteMode");
		m_purgeRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_purgeRB.addValueChangeHandler(rbChangedHandler);
		m_grid.setWidget(            1, 0, m_purgeRB);
		m_cellFormatter.addStyleName(1, 0, "vibe-deleteSelectionsDlg-purgeRadio");
		
		m_grid.setText(              1, 1, m_messages.deleteSelectionsDlgLabel_Purge());
		m_cellFormatter.addStyleName(1, 1, "vibe-deleteSelectionsDlg-purgeRadioLabel");
		m_cellFormatter.setWordWrap( 1, 1, false);
		
		// ...and add the warning about the delete.
		m_grid.setWidget(            2, 1, buildWarningWidget(m_messages.deleteSelectionsDlgWarning_CantUndo()));
		m_cellFormatter.addStyleName(2, 1, "vibe-deleteSelectionsDlg-warning vibe-deleteSelectionsDlg-warningSituation1");
	}
	
	/*
	 * Dialog has no options.
	 * 
	 * Contains a note that everything will be deleted from the system
	 * and a warning that the operation cannot be undone.
	 */
	private void populateSituation2() {
		// Add a note that everything will be deleted from the
		// system...
		m_grid.setText(              0, 0, m_messages.deleteSelectionsDlgLabel_PurgeOnly());
		m_cellFormatter.addStyleName(0, 0, "vibe-deleteSelectionsDlg-purgeNote");
		
		// ...and add a warning about the delete.
		m_grid.setWidget(            1, 0, buildWarningWidget(m_messages.deleteSelectionsDlgWarning_CantUndo()));
		m_cellFormatter.addStyleName(1, 0, "vibe-deleteSelectionsDlg-warning vibe-deleteSelectionsDlg-warningSituation2");
		setPurgeWarningActive(true);
	}
	
	/*
	 * Dialog has two radio buttons:
	 *    - Move personal storage items to trash and delete others from
	 *      system (default); and
	 *    - Delete everything from system.
	 *
	 * Both options will have a warning that deleting items from the
	 * system cannot be undone.
	 */
	private void populateSituation3() {
		// Add the 'Move items in personal storage to trash and delete
		// everything else from system' radio button...
		m_trashRB = new RadioButton("deleteMode");
		m_trashRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_trashRB.setValue(true);
		m_grid.setWidget(            0, 0, m_trashRB);
		m_cellFormatter.addStyleName(0, 0, "vibe-deleteSelectionsDlg-trashRadio");
		
		m_grid.setText(              0, 1, m_messages.deleteSelectionsDlgLabel_TrashAdHoc());
		m_cellFormatter.addStyleName(0, 1, "vibe-deleteSelectionsDlg-trashRadioLabel");
		m_cellFormatter.setWordWrap( 0, 1, false);
		
		// ...add the 'Delete everything from system' radio button...
		m_purgeRB = new RadioButton("deleteMode");
		m_purgeRB.addStyleName("vibe-deleteSelectionsDlg-radio");
		m_grid.setWidget(            1, 0, m_purgeRB);
		m_cellFormatter.addStyleName(1, 0, "vibe-deleteSelectionsDlg-purgeRadio");
		
		m_grid.setText(              1, 1, m_messages.deleteSelectionsDlgLabel_PurgeAll());
		m_cellFormatter.addStyleName(1, 1, "vibe-deleteSelectionsDlg-purgeRadioLabel");
		m_cellFormatter.setWordWrap( 1, 1, false);
		
		// ...and add the warning about the deletes.
		m_grid.setWidget(            2, 0, buildWarningWidget(m_messages.deleteSelectionsDlgWarning_CantUndo()));
		m_cellFormatter.addStyleName(2, 0, "vibe-deleteSelectionsDlg-warning vibe-deleteSelectionsDlg-warningSituation3");
		m_cellFormatter.setColSpan(  2, 0, 2);
		setPurgeWarningActive(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the delete selections
	 * dialog.
	 */
	private static void runDlgAsync(final DeleteSelectionsDlg dsDlg, final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				dsDlg.runDlgNow(entityIds, dpeCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the delete selections
	 * dialog.
	 */
	private void runDlgNow(final List<EntityId> entityIds, final DeletePurgeEntriesCallback dpeCallback) {
		// Store the parameters and populate the dialog.
		m_entityIds   = entityIds;
		m_dpeCallback = dpeCallback;
		loadPart1Async();
	}

	/*
	 * Maps the information in the dialog's SelectionDetails to a
	 * DialogMode.
	 *
	 * Situation1 -> Selections contain only items from personal storage.
	 * Situation2 -> Selections contain only remote (i.e., Cloud Folder, Net Folder or Vibe Mirrored Folder) items.
	 * Situation3 -> Selections contain a mixture of personal storage and remote items.
	 * 
	 * See the following for where the definitions of these come from:
	 *     https://teaming.innerweb.novell.com/ssf/a/c/p_name/ss_forum/p_action/1/action/view_permalink/entityType/folderEntry/entryId/422728/vibeonprem_url/1
	 */
	private void setDialogMode() {
		// Is anything remote selected?
		if (m_selectionDetails.hasRemoteSelections()) {
			// Yes!  Are there any selections from personal storage?
			if (m_selectionDetails.hasAdHocSelections()) {
				// Yes!  We have a mixture.  This is situation 3.
			    m_dialogMode = DialogMode.Situation3;
			}
			
			else {
				// No, we only have remote selections!  This is
				// situation 2.
				m_dialogMode = DialogMode.Situation2;
			}
		}
		
		else {
			// No, nothing remote is selected!  This is situation 1.
			m_dialogMode = DialogMode.Situation1;
		}
	}

	/*
	 * Sets the purge warning as being active or inactive.
	 */
	private void setPurgeWarningActive(boolean active) {
		if (active) {
			if (null != m_warningImg) m_warningImg.addStyleName("vibe-deleteSelectionsDlg-warningImgActive");
			if (null != m_warningTxt) m_warningTxt.addStyleName("vibe-deleteSelectionsDlg-warningTxtActive");
		}
		
		else {
			if (null != m_warningImg) m_warningImg.removeStyleName("vibe-deleteSelectionsDlg-warningImgActive");
			if (null != m_warningTxt) m_warningTxt.removeStyleName("vibe-deleteSelectionsDlg-warningTxtActive");
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the delete selections dialog and perform some operation on    */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the delete selections dialog
	 * asynchronously after it loads. 
	 */
	public interface DeleteSelectionsDlgClient {
		void onSuccess(DeleteSelectionsDlg dsDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the DeleteSelectionsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final DeleteSelectionsDlgClient	dsDlgClient,
			
			// initAndShow parameters,
			final DeleteSelectionsDlg			dsDlg,
			final List<EntityId>				entityIds,
			final DeletePurgeEntriesCallback	dpeCallback) {
		GWT.runAsync(DeleteSelectionsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DeleteSelectionsDlg());
				if (null != dsDlgClient) {
					dsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != dsDlgClient) {
					// Yes!  Create it and return it via the callback.
					DeleteSelectionsDlg dsDlg = new DeleteSelectionsDlg();
					dsDlgClient.onSuccess(dsDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(dsDlg, entityIds, dpeCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the DeleteSelectionsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param dsDlgClient
	 */
	public static void createAsync(DeleteSelectionsDlgClient dsDlgClient) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(dsDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the delete selections dialog.
	 * 
	 * @param dsDlg
	 * @param entityIds
	 * @param dpeCallback
	 */
	public static void initAndShow(DeleteSelectionsDlg dsDlg, List<EntityId> entityIds, DeletePurgeEntriesCallback dpeCallback) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(null, dsDlg, entityIds, dpeCallback);
	}
}
