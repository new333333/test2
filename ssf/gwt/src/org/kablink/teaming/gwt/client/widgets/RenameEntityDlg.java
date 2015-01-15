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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.RenameEntityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Implements Vibe's rename an entity dialog.
 *  
 * @author drfoster@novell.com
 */
public class RenameEntityDlg extends DlgBox implements EditSuccessfulHandler {
	private EntityId			m_entityId;			// The entity to be renamed.
	private FlowPanel			m_dlgPanel;			// The panel holding the dialog's content.
	private GwtTeamingMessages	m_messages;			// Access to Vibe's messages.
	private RenameEntity		m_renameEntity;		// Set to the type of entity being renamed when the dialog is in operation.
	private String				m_originalName;		// The original name of the entity being renamed.
	private TextBox 			m_entityNameInput;	// The <INPUT> widget the user enters the new name into.

	/*
	 * Enumeration used to easily work with the type of entity being
	 * renamed.
	 */
	private enum RenameEntity {
		FILE,
		FOLDER,
		WORKSPACE,
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private RenameEntityDlg() {
		// Initialize the super class...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// The dialog's caption is set when the dialog is shown.
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
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
		// Create and return a FlowPanel to hold the dialog's content.
		m_dlgPanel = new VibeFlowPanel();
		m_dlgPanel.addStyleName("vibe-renameEntityDlg_Panel");
		return m_dlgPanel;
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
		// Disable the OK button so the user doesn't repeatedly press
		// it...
		setButtonsEnabled(false);
		
		// ...start the entity rename process...
		renameEntityAsync();
		
		// ...and always return false to leave the dialog open.  If its
		// ...contents validate and the entity gets renamed, the dialog
		// ...will then be closed.
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
		return m_entityNameInput;
	}

	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		super.onAttach();
		m_entityNameInput.setSelectionRange(0, m_entityNameInput.getValue().length());
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
		// What type of entity is being renamed?
		String eidType = m_entityId.getEntityType();
		if      (EntityId.FOLDER_ENTRY.equals(eidType)) m_renameEntity = RenameEntity.FILE;
		else if (EntityId.FOLDER.equals(      eidType)) m_renameEntity = RenameEntity.FOLDER;
		else if (EntityId.WORKSPACE.equals(   eidType)) m_renameEntity = RenameEntity.WORKSPACE;
		else {
			GwtClientHelper.deferredAlert(m_messages.renameEntityDlgError_BogusEntity(eidType));
			return;
		}

		// Set an appropriate caption for the dialog.
		String dlgCaption;
		switch (m_renameEntity) {
		default:         dlgCaption = m_messages.renameEntityDlgHeader_Unknown();   break;
		case FILE:       dlgCaption = m_messages.renameEntityDlgHeader_File();      break;
		case FOLDER:     dlgCaption = m_messages.renameEntityDlgHeader_Folder();    break;
		case WORKSPACE:  dlgCaption = m_messages.renameEntityDlgHeader_Workspace(); break;
		}
		setCaption(dlgCaption);
		
		// Clear anything already in the dialog's panel.
		m_dlgPanel.clear();

		// Create a horizontal panel to hold the name input widgets...
		HorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-renameEntityDlg_NamePanel");
		m_dlgPanel.add(hp);

		// ...add a label...
		InlineLabel il = new InlineLabel(m_messages.renameEntityDlgName());
		il.addStyleName("vibe-renameEntityDlg_NameLabel");
		hp.add(il);
		hp.setCellVerticalAlignment(il, HasVerticalAlignment.ALIGN_MIDDLE);

		// ...and add an input widget.
		m_entityNameInput = new TextBox();
		m_entityNameInput.setText(m_originalName);
		m_entityNameInput.addStyleName("vibe-renameEntityDlg_NameInput");
		m_entityNameInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				// What key is being pressed?
				switch (event.getNativeEvent().getKeyCode()) {
				case KeyCodes.KEY_ESCAPE:
					// Escape!  Simply hide the dialog.
					hide();
					break;
					
				case KeyCodes.KEY_ENTER:
					// Enter!  Treat it like the user pressed OK.
					editSuccessful(null);
					break;
				}
			}
		});
		hp.add(m_entityNameInput);
		hp.setCellVerticalAlignment(m_entityNameInput, HasVerticalAlignment.ALIGN_MIDDLE);
		GwtClientHelper.setFocusDelayed(m_entityNameInput);
		
		// Finally, show the dialog centered on the screen.
		setButtonsEnabled(true);
		center();
	}
	
	/*
	 * Asynchronously runs the rename process.
	 */
	private void renameEntityAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				renameEntityNow();
			}
		});
	}
	
	/*
	 * Synchronously runs the rename process.
	 */
	private void renameEntityNow() {
		// Did the user supply the new name for the entity?
		String fn = m_entityNameInput.getValue();
		final String entityName = ((null == fn) ? "" : fn.trim());
		int enLength = entityName.length();
		if (0 == enLength) {
			// No!  Tell them about the error and bail.
			String errorMsg;
			switch (m_renameEntity) {
			default:         errorMsg = m_messages.renameEntityDlgError_NoName_Unknown();   break;
			case FILE:       errorMsg = m_messages.renameEntityDlgError_NoName_File();      break;
			case FOLDER:     errorMsg = m_messages.renameEntityDlgError_NoName_Folder();    break;
			case WORKSPACE:  errorMsg = m_messages.renameEntityDlgError_NoName_Workspace(); break;
			}
			GwtClientHelper.deferredAlert(errorMsg);
			setButtonsEnabled(true);
			return;
		}

		// Is the name the supplied too long for the entity?
		int maxNameLength;
		if (RenameEntity.FILE.equals(m_renameEntity))
		     maxNameLength = GwtConstants.MAX_FILE_NAME_LENGTH;
		else maxNameLength = GwtConstants.MAX_BINDER_NAME_LENGTH;
		if (maxNameLength < enLength) {
			// Yes!  Tell them about the error and bail.
			String errorMsg;
			switch (m_renameEntity) {
			default:         errorMsg = m_messages.renameEntityDlgError_NameTooLong_Unknown(  maxNameLength); break;
			case FILE:       errorMsg = m_messages.renameEntityDlgError_NameTooLong_File(     maxNameLength); break;
			case FOLDER:     errorMsg = m_messages.renameEntityDlgError_NameTooLong_Folder(   maxNameLength); break;
			case WORKSPACE:  errorMsg = m_messages.renameEntityDlgError_NameTooLong_Workspace(maxNameLength); break;
			}
			GwtClientHelper.deferredAlert(errorMsg);
			setButtonsEnabled(true);
			return;
		}

		// Can we rename the entity?
		RenameEntityCmd cmd = new RenameEntityCmd(m_entityId, entityName);
		GwtClientHelper.executeCommand(
				cmd,
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				String errorMsg;
				switch (m_renameEntity) {
				default:         errorMsg = m_messages.rpcFailure_RenameEntity_Unknown();   break;
				case FILE:       errorMsg = m_messages.rpcFailure_RenameEntity_File();      break;
				case FOLDER:     errorMsg = m_messages.rpcFailure_RenameEntity_Folder();    break;
				case WORKSPACE:  errorMsg = m_messages.rpcFailure_RenameEntity_Workspace(); break;
				}
				GwtClientHelper.handleGwtRPCFailure(t, errorMsg);
				setButtonsEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Perhaps!  Was there an error returned from the
				// rename?
				StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
				String errorMsg = responseData.getStringValue();
				if (GwtClientHelper.hasString(errorMsg)) {
					// Yes!  Display it.
					GwtClientHelper.deferredAlert(errorMsg);
					setButtonsEnabled(true);
				}
				
				else {
					// No, there wasn't an error!  Force things to
					// refresh...
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
					GwtTeaming.fireEvent(new FullUIReloadEvent());
					
					// ...and close the dialog.
					setButtonsEnabled(true);
					hide();
				}
			}
		});
	}
	
	/*
	 * Asynchronously runs the given instance of the rename entity
	 * dialog.
	 */
	private static void runDlgAsync(final RenameEntityDlg reDlg, final EntityId entityId, final String originalName) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				reDlg.runDlgNow(entityId, originalName);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the rename entity
	 * dialog.
	 */
	private void runDlgNow(EntityId entityId, String originalName) {
		// Store the parameters...
		m_entityId     = entityId;
		m_originalName = originalName;

		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Enables/disables the buttons on the dialog.
	 */
	private void setButtonsEnabled(boolean enabled) {
		setOkEnabled(    enabled);
		setCancelEnabled(enabled);
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the rename entity dialog and perform some operation on it.    */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the rename entity dialog
	 * asynchronously after it loads. 
	 */
	public interface RenameEntityDlgClient {
		void onSuccess(RenameEntityDlg reDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the RenameEntityDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final RenameEntityDlgClient reDlgClient,
			
			// initAndShow parameters,
			final RenameEntityDlg	reDlg,
			final EntityId			entityId,
			final String			originalName) {
		GWT.runAsync(RenameEntityDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_RenameEntityDlg());
				if (null != reDlgClient) {
					reDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != reDlgClient) {
					// Yes!  Create it and return it via the callback.
					RenameEntityDlg reDlg = new RenameEntityDlg();
					reDlgClient.onSuccess(reDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(reDlg, entityId, originalName);
				}
			}
		});
	}
	
	/**
	 * Loads the RenameEntityDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param reDlgClient
	 */
	public static void createAsync(RenameEntityDlgClient reDlgClient) {
		doAsyncOperation(reDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the rename entity dialog.
	 * 
	 * @param reDlg
	 * @param entityId
	 * @param originalName
	 */
	public static void initAndShow(RenameEntityDlg reDlg, EntityId entityId, String originalName) {
		doAsyncOperation(null, reDlg, entityId, originalName);
	}
}
