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
package org.kablink.teaming.gwt.client.datatable;

import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.FileConflictsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FileConflictsInfoRpcResponseData.DisplayInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileConflictsInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.UploadInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeHorizontalPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements Vibe's 'File Conflicts' dialog.
 *  
 * @author drfoster@novell.com
 */
public class FileConflictsDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private BinderInfo							m_folderInfo;			// The folder the file conflicts dialog is running against.
	private boolean								m_dialogReady;			// Set true once the dialog is ready for display.
	private boolean								m_emailTemplates;		// Set true if the conflicts are while uploading customized email templates.
	private boolean								m_isFilr;				// Set true if we're in Filr mode.
	private ConfirmCallback						m_confirmCallback;		// Callback interface to let the caller know about the user's choices in this dialog.
	private FileConflictsInfoRpcResponseData	m_fileConflictsInfo;	// In depth information about the file conflicts, once obtained from the server.
	private GwtTeamingFilrImageBundle			m_filrImages;			// Access to Filr's images.
	private GwtTeamingMessages					m_messages;				// Access to Vibe's messages.
	private List<UploadInfo>					m_fileConflicts;		// List<UploadInfo> of the files in conflict.
	private VerticalPanel						m_vp;					// Panel that holds the dialog's contents.
	
	private final static int	SCROLL_WHEN	= 5;	// Count of items in a ScrollPanel when scroll bars are enabled.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FileConflictsDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.OkCancel);

		// ...initialize everything else...
		m_isFilr     = GwtClientHelper.isLicenseFilr();
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_messages   = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		addStyleName("vibe-fileConflictsDlg");
		createAllDlgContent(
			m_messages.fileConflictsDlgHeader(),	// The dialog's header text.
			this,									// The dialog's OK     handler.
			this,									// The dialog's cancel handler.
			null);									// Create callback data.  null -> Unused. 
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
		// Create a panel to hold the dialog's content...
		m_vp = new VibeVerticalPanel(null, null);
		m_vp.addStyleName("vibe-fileConflictsDlg-panel");
		
		// ...and return the Panel that holds the dialog's contents.
		return m_vp;
	}

	/*
	 * Performs some final initializations, marks the dialog as being
	 * ready and shows it.
	 */
	private void doDialogReady() {		
		// Configure the OK and Cancel buttons appropriately for this
		// dialog...
		Button okBtn = getOkButton();
		okBtn.setText((m_isFilr || m_emailTemplates) ? m_messages.fileConflictsDlgBtnOverwrite() : m_messages.fileConflictsDlgBtnVersion());
		okBtn.addStyleName("vibe-fileConflictsDlg-ok");
		getCancelButton().setText(m_messages.fileConflictsDlgBtnCancel());
		
		// ...and hide the 'X' in the dialog's upper right corner.
		// ...This will force the user to use one of the footer push
		// ...buttons to address it.
		hideCloseImg();

		// Mark the dialog as being ready, show it and tell the caller
		// that we're ready.
		m_dialogReady = true;
		center();
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_confirmCallback.dialogReady();
			}
		});
	}
	
	/**
	 * Called if the user selects the dialog's Cancel button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() method.
	 */
	@Override
	public boolean editCanceled() {
		// Tell the caller the confirmation was rejected...
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_confirmCallback.rejected();
			}
		});
		
		// ...and return true to close the dialog.
		return true;
	}

	/**
	 * Called is the user selects the dialog's Ok button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() method.
	 * 
	 * @param callbackData (unused)
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Tell the caller the confirmation was accepted...
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_confirmCallback.accepted();
			}
		});
		
		// ...and return true to close the dialog.
		return true;
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
		GwtClientHelper.executeCommand(
				new GetFileConflictsInfoCmd(m_folderInfo, m_fileConflicts),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFileConflictsInfo());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the file conflicts information from the
				// response data and use it to populate the dialog.
				m_fileConflictsInfo = ((FileConflictsInfoRpcResponseData) response.getResponseData());
				populateDlgWithDataAsync();
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgWithDataAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgWithDataNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgWithDataNow() {
		// Clear the current contents of the dialog...
		m_vp.clear();

		// Add the banner label for the dialog.
		String lText;
		if (m_emailTemplates) {
			lText = m_messages.fileConflictsDlgConfirmEmailTemplatesOverwrite();
		}
		else {
			lText =
				(m_isFilr                                         ?
					m_messages.fileConflictsDlgConfirmOverwrite() :
					m_messages.fileConflictsDlgConfirmVersion());
		}
		Label l = new Label(lText);
		l.addStyleName("vibe-fileConflictsDlg-banner");
		m_vp.add(l);

		// Create a panel to hold the folder information...
		HorizontalPanel folderPanel = new VibeHorizontalPanel(null, null);
		folderPanel.addStyleName("vibe-fileConflictsDlg-folderPanel");
		m_vp.add(folderPanel);

		// ...add the folder's icon...
		String url;
		Image i;
		DisplayInfo folderDI = m_fileConflictsInfo.getFolderDisplay();
		if (m_folderInfo.isFolderHome()) {
			url = m_filrImages.folderHome_medium().getSafeUri().asString();
		}
		else {
			url = folderDI.getIconUrl();
			if (GwtClientHelper.hasString(url))
			     url = (GwtClientHelper.getRequestInfo().getImagesPath() + url);
			else url = m_filrImages.entry_medium().getSafeUri().asString();
		}
		i = GwtClientHelper.buildImage(url);
		i.addStyleName("vibe-fileConflictsDlg-folderImg");
		folderPanel.add(i);

		// ...name...
		VerticalPanel folderNamePanel = new VibeVerticalPanel(null, null);
		folderNamePanel.addStyleName("vibe-fileConflictsDlg-folderNamePanel");
		folderPanel.add(folderNamePanel);
		InlineLabel il = new InlineLabel(folderDI.getName());
		il.addStyleName("vibe-fileConflictsDlg-folderName");
		folderNamePanel.add(il);
		
		// ...and path.
		String path = folderDI.getPath();
		int slash = path.lastIndexOf('/');
		if (0 < slash) {
			path = path.substring(0, (slash + 1));
		}
		il = new InlineLabel(path);
		il.addStyleName("vibe-fileConflictsDlg-folderNamePath");
		folderNamePanel.add(il);

		// Add a label for the list of conflicts.
		il = new InlineLabel(m_messages.fileConflictsDlgConflictingFiles());
		il.addStyleName("vibe-fileConflictsDlg-scrollHeader");
		m_vp.add(il);
		
		// Add a ScrollPanel for the conflicts.
		ScrollPanel sp = new ScrollPanel();
		sp.addStyleName("vibe-fileConflictsDlg-scroll");
		m_vp.add(sp);
		List<DisplayInfo> conflicts = m_fileConflictsInfo.getFileConflictsDisplayList();
		if (conflicts.size() >= SCROLL_WHEN) {
			sp.addStyleName("vibe-fileConflictsDlg-scrollLimit");
		}
		VerticalPanel vp = new VibeVerticalPanel(null, null);
		sp.add(vp);
		
		// Scan the conflicts.
		for (DisplayInfo nf:  conflicts) {
			// Add a panel for each conflict...
			HorizontalPanel hp = new VibeHorizontalPanel(null, null);
			hp.addStyleName("vibe-fileConflictsDlg-conflicts");
			hp.setSpacing(1);
			vp.add(hp);

			// ...add the icon for the conflict...
			url = nf.getIconUrl();
			if (GwtClientHelper.hasString(url))
			     url = (GwtClientHelper.getRequestInfo().getImagesPath() + url);
			else url = m_filrImages.entry().getSafeUri().asString();
			i = GwtClientHelper.buildImage(url);
			i.addStyleName("vibe-fileConflictsDlg-conflictImg");
			hp.add(i);

			// ...and its name.
			il = new InlineLabel(nf.getName());
			il.addStyleName("vibe-fileConflictsDlg-conflictName");
			il.setWordWrap(false);
			hp.add(il);
			hp.setCellVerticalAlignment(il, HasVerticalAlignment.ALIGN_MIDDLE);
		}
		
		// Finally, mark the dialog as being ready and show it.
		doDialogReady();
	}
	
	/*
	 * Asynchronously runs the given instance of the file conflicts
	 * dialog.
	 */
	private static void runDlgAsync(final FileConflictsDlg fcDlg, final ConfirmCallback cCB, final BinderInfo fi, final List<UploadInfo> fileConflicts) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				fcDlg.runDlgNow(cCB, fi, fileConflicts);
			}
		});
	}
	
	/*
	 * Synchronously runs the file conflicts dialog.
	 */
	private void runDlgNow(ConfirmCallback cCB, BinderInfo fi, List<UploadInfo> fileConflicts) {
		// Store the parameters...
		m_folderInfo      = fi;
		m_fileConflicts   = fileConflicts;
		m_confirmCallback = cCB;
		
		// ...initialize everything else that requires it...
		m_emailTemplates = fi.isBinderEmailTemplates();
		
		// ...and populate the dialog.
		populateDlgAsync();
	}

	/**
	 * Shows the dialog if it's ready.
	 * 
	 * Overrides the DlgBox.show() method.
	 */
	@Override
	public void show() {
		// If the dialog is ready to be shown...
		if (m_dialogReady) {
			// ...pass this on to the super class.
			super.show();
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the file conflicts dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the file conflicts dialog
	 * asynchronously after it loads. 
	 */
	public interface FileConflictsDlgClient {
		void onSuccess(FileConflictsDlg fcDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the FileConflictsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync() parameters.
			final FileConflictsDlgClient fcDlgClient,
			
			// initAndShow() parameters,
			final FileConflictsDlg	fcDlg,
			final ConfirmCallback	cCB,
			final BinderInfo		fi,
			final List<UploadInfo>	fileConflicts) {
		GWT.runAsync(FileConflictsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FileConflictsDlg());
				if (null != fcDlgClient) {
					fcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != fcDlgClient) {
					// Yes!  Create it and return it via the callback.
					FileConflictsDlg fcDlg = new FileConflictsDlg();
					fcDlgClient.onSuccess(fcDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(fcDlg, cCB, fi, fileConflicts);
				}
			}
		});
	}
	
	/**
	 * Loads the FileConflictsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param fcDlgClient
	 */
	public static void createAsync(FileConflictsDlgClient fcDlgClient) {
		doAsyncOperation(fcDlgClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows an instance of the file conflicts dialog.
	 * 
	 * @param fcDlg
	 * @param cCB
	 * @param fi
	 * @param fileConflicts
	 */
	public static void initAndShow(FileConflictsDlg fcDlg, ConfirmCallback cCB, BinderInfo fi, List<UploadInfo> fileConflicts) {
		doAsyncOperation(null, fcDlg, cCB, fi, fileConflicts);
	}
}
