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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteUsersHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteUsersHelper.DeleteUsersCallback;
import org.kablink.teaming.gwt.client.util.DeleteSelectedUsersMode;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.SelectedUsersDetails;
import org.kablink.teaming.gwt.client.util.SelectedUsersDetailsHelper;
import org.kablink.teaming.gwt.client.util.SelectedUsersDetailsHelper.SelectedUsersDetailsCallback;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
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
 * Implements the delete selected user dialog.
 *  
 * @author drfoster@novell.com
 */
public class DeleteSelectedUsersDlg extends DlgBox implements EditSuccessfulHandler {
	private CheckBox				m_purgeWorkspaceCB;		// The 'Delete users from system whose workspaces are deleted' checkbox.
	private DialogMode				m_dialogMode;			// The mode the dialog is running in.  Defines what's displayed on the dialog.
	private DeleteUsersCallback		m_duCallback;			// Callback used to interact with who called the dialog.
	private FlexCellFormatter		m_cellFormatter;		// The formatter to control how m_grid is laid out.
	private FlexTable				m_grid;					// The table holding the dialog's content.
	private GwtTeamingImageBundle	m_images;				// Access to the base images.
	private GwtTeamingMessages		m_messages;				// Access to our localized strings.
	private List<Long>				m_userIds;				// The users to be deleted.
	private RadioButton				m_purgeRB;				// The 'Delete from system' radio button.
	private RadioButton				m_trashRB;				// The 'Move to trash'      radio button.
	private SelectedUsersDetails	m_selectedUsersDetails;	// Populated via a GWT RPC call while constructing the dialog's contents.  Contains an analysis of what m_userIds refers to.
	private Widget					m_purgeWarning;			// The Widget containing the 'can't be undone' warning. 
	
	// The buttons displayed on this dialog.
	private final static DlgButtonMode	DLG_BUTTONS = DlgButtonMode.OkCancel; 

	/*
	 * Enumeration type used to define how the dialog prompts the user.
	 *  
	 * See the following for where the definitions of these come from:
	 *    https://teaming.innerweb.novell.com/ssf/a/c/p_name/ss_forum/p_action/1/action/view_permalink/entityType/folderEntry/entryId/422728/vibeonprem_url/1
	 */
	private enum DialogMode {
		Situation1,	// Selections contain only users with workspaces containing items from personal storage.
		Situation2,	// Selections contain only users with workspaces containing remote (i.e., Cloud Folder, Net Folder or Vibe Mirrored Folder) items.
		Situation3	// Selections contain a mixture of workspaces containing personal storage and remote items.
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DeleteSelectedUsersDlg() {
		// Initialize the superclass...
		super(false, true, DLG_BUTTONS);

		// ...initialize everything else...
		addStyleName("vibe-deleteSelectedUsersDlg");
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.deleteSelectedUsersDlgHeader(),	// The dialog's header.
			this,										// The dialog's EditSuccessfulHandler.
			DlgBox.getSimpleCanceledHandler(),			// The dialog's EditCanceledHandler.
			null);										// Create callback data.  Unused. 
	}

	/*
	 * Returns a Widget containing what's displayed for purge warnings.
	 */
	private Widget buildPurgeWarningWidget(String text) {
		HorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-deleteSelectedUsersDlg-warningPanel");
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Image img = GwtClientHelper.buildImage(m_images.warningIcon16());
		img.addStyleName("vibe-deleteSelectedUsersDlg-warningImg");
		hp.add(img);
		
		InlineLabel txt = new InlineLabel(text);
		txt.addStyleName("vibe-deleteSelectedUsersDlg-warningTxt");
		hp.add(txt);
		
		return hp;
	}
	
	/*
	 * Returns a CheckBox for selecting to purge users whose workspace
	 * is being purged.
	 */
	private CheckBox buildPurgeWorkspaceCheckBox(String cbText) {
		CheckBox reply = new CheckBox(cbText);
		reply.addStyleName("vibe-deleteSelectedUsersDlg-purgeCheckBoxWS");
		reply.removeStyleName("gwt-CheckBox");
		return reply;
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
		m_grid.addStyleName("vibe-deleteSelectedUsersDlg-rootPanel");
		m_grid.setCellPadding(0);
		m_grid.setCellSpacing(0);
		m_cellFormatter = m_grid.getFlexCellFormatter();
		return m_grid;
	}

	/*
	 * Asynchronously deletes the selected users.
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
	 * Synchronously deletes the selected users.
	 */
	private void doDeletesNow() {
		// Determine how to perform the delete...
		boolean workspacesTrashed;
		DeleteSelectedUsersMode dsuMode;
		switch (m_dialogMode) {
		default:
		case Situation1:
		case Situation3:
			if (m_purgeRB.getValue()) {
				dsuMode = DeleteSelectedUsersMode.PURGE_ALL_WORKSPACES;
				workspacesTrashed = false;
			}
			
			else {
				if (DialogMode.Situation1.equals(m_dialogMode))
				     dsuMode = DeleteSelectedUsersMode.TRASH_ALL_WORKSPACES;
				else dsuMode = DeleteSelectedUsersMode.TRASH_ADHOC_WORKSPACES_PURGE_OTHERS;
				workspacesTrashed = true;
			}
			break;
			
		case Situation2:
			dsuMode = DeleteSelectedUsersMode.PURGE_ALL_WORKSPACES;
			workspacesTrashed = false;
			break;
		}

		// If we trashed any user workspaces, wrap the callback so that
		// we can tell the user that we disabled the user's too. 
		DeleteUsersCallback duCallbackWrapper;
		if (workspacesTrashed) {
			final String trashDisabledMsg = m_messages.deleteSelectedUsersDlgLabel_TrashDisabled();
			duCallbackWrapper = new DeleteUsersCallback() {
				@Override
				public void operationCanceled() {
					GwtClientHelper.deferredAlert(trashDisabledMsg);
					if (null != m_duCallback) {
						m_duCallback.operationCanceled();
					}
				}
	
				@Override
				public void operationComplete() {
					GwtClientHelper.deferredAlert(trashDisabledMsg);
					if (null != m_duCallback) {
						m_duCallback.operationComplete();
					}
				}
	
				@Override
				public void operationFailed() {
					if (null != m_duCallback) {
						m_duCallback.operationFailed();
					}
				}
			};
		}
		else {
			duCallbackWrapper = m_duCallback;
		}

		// ...and do it.
		DeleteUsersHelper.deleteSelectedUsersAsync(
			m_userIds,
			dsuMode,
			((null == m_purgeWorkspaceCB) ?
				false                     :
				m_purgeWorkspaceCB.getValue()),
			duCallbackWrapper);
	}
	
	/**
	 * Called is the user selects the dialog's Ok button.
	 * 
	 * @param callbackData
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Asynchronously handle the Ok...
		setOkEnabled(false);
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
			(m_selectedUsersDetails.hasPurgeConfirmations() &&
			(m_dialogMode.equals(DialogMode.Situation3) && m_trashRB.getValue()));
		
		if (needsConfirmaiton) {
			// Yes!  Does the user accept what's going to be purged?
			String confirm;
			if ((null != m_purgeWorkspaceCB) && m_purgeWorkspaceCB.getValue())
			     confirm = m_messages.deleteSelectedUsersDlgConfirmWSAndUsers();
			else confirm = m_messages.deleteSelectedUsersDlgConfirmWS();
			GwtClientHelper.displayMultipleErrors(
						confirm,
						m_selectedUsersDetails.getPurgeConfirmations(),
						new ConfirmCallback() {
					@Override
					public void dialogReady() {
						// Ignored.  We don't care when the dialog is
						// ready.
					}
	
					@Override
					public void accepted() {
						// Yes, the user said do it!  Start deleting
						// the users.
						doDeletesAsync();
					}
	
					@Override
					public void rejected() {
						// No, the user said don't do it!  Reopen the
						// dialog.
						setOkEnabled(true);
						show();
					}
				},
				DLG_BUTTONS);
		}
		
		else {
			// No, we're not performing a delete that needs a user
			// confirmation!  Start deleting the users.
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
		SelectedUsersDetailsHelper.getSelectedUsersDetails(m_userIds, new SelectedUsersDetailsCallback() {
			@Override
			public void onFailure() {
				// Nothing to do.  getSelectedUsersDetails() will have
				// told the user about it's problems.
			}

			@Override
			public void onSuccess(SelectedUsersDetails selectedUsersDetails) {
				// Store the details and continue populating the
				// dialog.
				m_selectedUsersDetails = selectedUsersDetails;
				setDialogMode();
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
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_grid.removeAllRows();
		
		// ...forget any previous widgets...
		m_trashRB          =
		m_purgeRB          = null;
		m_purgeWarning     = null;
		m_purgeWorkspaceCB = null;

		// ...and repopulate the dialog.
		switch (m_dialogMode) {
		case Situation1:  populateSituation1(); break;
		case Situation2:  populateSituation2(); break;
		case Situation3:  populateSituation3(); break;
		}
		
		// ...and show the dialog.
		setCancelEnabled(true);
		setOkEnabled(    true);
		center();
	}

	/*
	 * Dialog has two radio buttons:
	 *    - Move user workspaces to trash (default); and
	 *    - Delete user workspaces from system (and a warning that the
	 *      operation cannot be undone.)
	 */
	private void populateSituation1() {
		// Create the purge workspace checkbox we can associate with
		// the purge radio buttons.
		final CheckBox purgeCB = buildPurgeWorkspaceCheckBox(m_messages.deleteSelectedUsersDlgLabel_PurgeUsers1());
		m_purgeWorkspaceCB = null;
		
		// Create a ValueChangeHandler we can use to tweak the dialog
		// when the user changes the selected radio button.
		ValueChangeHandler<Boolean> rbChangedHandler = new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (m_purgeRB.getValue()) {
					setPurgeWarningActive(true);
					m_purgeWorkspaceCB = purgeCB;
					purgeCB.setVisible(true);
				}
				else {
					setPurgeWarningActive(false);
					m_purgeWorkspaceCB = null;
					purgeCB.setVisible(false);
				}
			}
		};
		
		// Add the 'Move to trash' radio button...
		m_trashRB = new RadioButton("deleteMode", m_messages.deleteSelectedUsersDlgLabel_Trash());
		m_trashRB.addStyleName("vibe-deleteSelectedUsersDlg-radio");
		m_trashRB.removeStyleName("gwt-RadioButton");
		m_trashRB.addValueChangeHandler(rbChangedHandler);
		m_trashRB.setValue(true);
		m_grid.setWidget(            0, 0, m_trashRB);
		m_cellFormatter.addStyleName(0, 0, "vibe-deleteSelectedUsersDlg-trashRadio");
		m_cellFormatter.setWordWrap( 0, 0, false);
		
		// ...add the 'Delete from system' radio button...
		m_purgeRB = new RadioButton("deleteMode", m_messages.deleteSelectedUsersDlgLabel_Purge());
		m_purgeRB.addStyleName("vibe-deleteSelectedUsersDlg-radio");
		m_purgeRB.removeStyleName("gwt-RadioButton");
		m_purgeRB.addValueChangeHandler(rbChangedHandler);
		m_grid.setWidget(            1, 0, m_purgeRB);
		m_cellFormatter.addStyleName(1, 0, "vibe-deleteSelectedUsersDlg-purgeRadio");
		m_cellFormatter.setWordWrap( 1, 0, false);
		m_grid.setWidget(            2, 0, purgeCB);
		m_cellFormatter.addStyleName(2, 0, "vibe-deleteSelectedUsersDlg-purgeCheckBox vibe-deleteSelectedUsersDlg-indent");
		purgeCB.setVisible(false);
		
		// ...and add the warning about the delete.
		m_purgeWarning = buildPurgeWarningWidget(m_messages.deleteSelectedUsersDlgWarning_CantUndo());
		m_grid.setWidget(            3, 0, m_purgeWarning);
		m_cellFormatter.addStyleName(3, 0, "vibe-deleteSelectedUsersDlg-warning vibe-deleteSelectedUsersDlg-warningSituation1 vibe-deleteSelectedUsersDlg-indent");
		setPurgeWarningActive(false);
	}
	
	/*
	 * Dialog has no options.
	 * 
	 * Contains a note that all user workspaces will be deleted from
	 * the system and a warning that the operation cannot be undone.
	 */
	private void populateSituation2() {
		// Add a note that everything will be deleted from the
		// system...
		m_grid.setText(              0, 0, m_messages.deleteSelectedUsersDlgLabel_PurgeOnly());
		m_cellFormatter.addStyleName(0, 0, "vibe-deleteSelectedUsersDlg-purgeNote");
		
		// ...add a checkbox to purge users when purging workspaces...
		m_purgeWorkspaceCB = buildPurgeWorkspaceCheckBox(m_messages.deleteSelectedUsersDlgLabel_PurgeUsers2());
		m_grid.setWidget(            1, 0, m_purgeWorkspaceCB);
		m_cellFormatter.addStyleName(1, 0, "vibe-deleteSelectedUsersDlg-purgeCheckBox");
		
		// ...and add a warning about the delete.
		m_purgeWarning = buildPurgeWarningWidget(m_messages.deleteSelectedUsersDlgWarning_CantUndo());
		m_grid.setWidget(            2, 0, m_purgeWarning);
		m_cellFormatter.addStyleName(2, 0, "vibe-deleteSelectedUsersDlg-warning vibe-deleteSelectedUsersDlg-warningSituation2");
		setPurgeWarningActive(true);
	}
	
	/*
	 * Dialog has two radio buttons:
	 *    - Move user workspaces with only personal storage items to
	 *      trash and delete others from system (default); and
	 *    - Delete all user workspaces from system.
	 *
	 * Both options will have a warning that deleting items from the
	 * system cannot be undone.
	 */
	private void populateSituation3() {
		// Create the purge workspace checkboxes we can associate with
		// the trash and purge radio buttons.
		final CheckBox purgeCB = buildPurgeWorkspaceCheckBox(m_messages.deleteSelectedUsersDlgLabel_PurgeUsers3());
		final CheckBox trashCB = buildPurgeWorkspaceCheckBox(m_messages.deleteSelectedUsersDlgLabel_PurgeUsers4());
		m_purgeWorkspaceCB = trashCB;
		
		// Create a ValueChangeHandler we can use to tweak the dialog
		// when the user changes the selected radio button.
		ValueChangeHandler<Boolean> rbChangedHandler = new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (m_purgeRB.getValue()) {
					m_purgeWorkspaceCB = purgeCB;
					trashCB.setVisible(false);
					purgeCB.setVisible(true );
				}
				else {
					m_purgeWorkspaceCB = trashCB;
					trashCB.setVisible(true );
					purgeCB.setVisible(false);
				}
			}
		};
		
		// Add the 'Move items in personal storage to trash and delete
		// everything else from system' radio button...
		m_trashRB = new RadioButton("deleteMode", m_messages.deleteSelectedUsersDlgLabel_TrashAdHoc());
		m_trashRB.addStyleName("vibe-deleteSelectedUsersDlg-radio");
		m_trashRB.removeStyleName("gwt-RadioButton");
		m_trashRB.addValueChangeHandler(rbChangedHandler);
		m_trashRB.setValue(true);
		m_grid.setWidget(            0, 0, m_trashRB);
		m_cellFormatter.addStyleName(0, 0, "vibe-deleteSelectedUsersDlg-trashRadio");
		m_cellFormatter.setWordWrap( 0, 0, false);
		m_grid.setWidget(            1, 0, trashCB);
		m_cellFormatter.addStyleName(1, 0, "vibe-deleteSelectedUsersDlg-purgeCheckBox vibe-deleteSelectedUsersDlg-indent");
		
		// ...add the 'Delete everything from system' radio button...
		m_purgeRB = new RadioButton("deleteMode", m_messages.deleteSelectedUsersDlgLabel_PurgeAll());
		m_purgeRB.addStyleName("vibe-deleteSelectedUsersDlg-radio");
		m_purgeRB.removeStyleName("gwt-RadioButton");
		m_purgeRB.addValueChangeHandler(rbChangedHandler);
		m_grid.setWidget(            2, 0, m_purgeRB);
		m_cellFormatter.addStyleName(2, 0, "vibe-deleteSelectedUsersDlg-purgeRadio");
		m_cellFormatter.setWordWrap( 2, 0, false);
		m_grid.setWidget(            3, 0, purgeCB);
		m_cellFormatter.addStyleName(3, 0, "vibe-deleteSelectedUsersDlg-purgeCheckBox vibe-deleteSelectedUsersDlg-indent");
		purgeCB.setVisible(false);
		
		// ...and add the warning about the deletes.
		m_purgeWarning = buildPurgeWarningWidget(m_messages.deleteSelectedUsersDlgWarning_CantUndo());
		m_grid.setWidget(            4, 0, m_purgeWarning);
		m_cellFormatter.addStyleName(4, 0, "vibe-deleteSelectedUsersDlg-warning vibe-deleteSelectedUsersDlg-warningSituation3");
		setPurgeWarningActive(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the delete select
	 * users dialog.
	 */
	private static void runDlgAsync(final DeleteSelectedUsersDlg dsuDlg, final List<Long> userIds, final DeleteUsersCallback duCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				dsuDlg.runDlgNow(userIds, duCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the delete selected
	 * users dialog.
	 */
	private void runDlgNow(final List<Long> userIds, final DeleteUsersCallback duCallback) {
		// Store the parameters and populate the dialog.
		m_userIds    = userIds;
		m_duCallback = duCallback;
		loadPart1Async();
	}

	/*
	 * Maps the information in the dialog's SelectedUsersDetails to a
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
		// Are there any purge only selections?
		if (m_selectedUsersDetails.hasPurgeOnlySelections()) {
			// Yes!  Are there any users from personal storage?
			if (m_selectedUsersDetails.hasAdHocUserWorkspaces()) {
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
		if (null != m_purgeWarning) {
			if (active)
			     m_purgeWarning.addStyleName(   "vibe-deleteSelectedUsersDlg-warningPanelActive");
			else m_purgeWarning.removeStyleName("vibe-deleteSelectedUsersDlg-warningPanelActive");
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
	public interface DeleteSelectedUsersDlgClient {
		void onSuccess(DeleteSelectedUsersDlg dsuDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the DeleteSelectedUsersDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final DeleteSelectedUsersDlgClient	dsuDlgClient,
			
			// initAndShow parameters,
			final DeleteSelectedUsersDlg	dsuDlg,
			final List<Long>				userIds,
			final DeleteUsersCallback		duCallback) {
		GWT.runAsync(DeleteSelectedUsersDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DeleteSelectedUsersDlg());
				if (null != dsuDlgClient) {
					dsuDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != dsuDlgClient) {
					// Yes!  Create it and return it via the callback.
					DeleteSelectedUsersDlg dsuDlg = new DeleteSelectedUsersDlg();
					dsuDlgClient.onSuccess(dsuDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(dsuDlg, userIds, duCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the DeleteSelectedUsersDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param dsuDlgClient
	 */
	public static void createAsync(DeleteSelectedUsersDlgClient dsuDlgClient) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(dsuDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the delete selected users dialog.
	 * 
	 * @param dsuDlg
	 * @param userIds
	 * @param duCallback
	 */
	public static void initAndShow(DeleteSelectedUsersDlg dsuDlg, List<Long> userIds, DeleteUsersCallback duCallback) {
		// Invoke the appropriate asynchronous operation.
		doAsyncOperation(null, dsuDlg, userIds, duCallback);
	}
}
