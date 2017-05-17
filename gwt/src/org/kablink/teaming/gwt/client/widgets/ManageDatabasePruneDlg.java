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
import org.kablink.teaming.gwt.client.GwtDatabasePruneConfiguration;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.SaveDatabasePruneConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.AlertDlg.AlertDlgCallback;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ?
 *  
 * @author phurley
 */
public class ManageDatabasePruneDlg extends DlgBox implements KeyPressHandler, EditSuccessfulHandler {
	private boolean				m_isFilr;						//
	private boolean				m_isVibe;						//
	private CheckBox			m_changeLogEnabledCB;			//
	private CheckBox			m_fileArchivingEnabledCB;		//
	private GwtTeamingMessages	m_messages;						//
	private TextBox				m_auditTrailPruneAgeTextBox;	//
	private TextBox				m_changeLogPruneAgeTextBox;		//
	
	// The following controls the minimum number of a days that may be
	// specified for pruning the SS_AuditTrail table.
	private static int	MINIMUM_AUDIT_TRAIL_PRUNE_AGE	= 30;	// 0 -> No minimum.
	
	// The following controls the minimum number of a days that may be
	// specified for pruning the SS_ChangeLogs table.
	private static int	MINIMUM_CHANGE_LOG_PRUNE_AGE	= 0;	// 0 -> No minimum.
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageDatabasePruneDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height) {
		// Initialize the super class...
		super(autoHide, modal, xPos, yPos, width, height, DlgButtonMode.OkCancel);

		// ...initialize the data members that require it....
		m_isFilr   = (GwtClientHelper.isLicenseFilr() || GwtClientHelper.isLicenseFilrAndVibe());
		m_isVibe   = (GwtClientHelper.isLicenseVibe() || GwtClientHelper.isLicenseFilrAndVibe());
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the header, content and footer of this dialog.
		String header;
		if (m_isVibe)
		     header = m_messages.databasePruneDlgHeader_Vibe();
		else header = m_messages.databasePruneDlgHeader_Filr();
		createAllDlgContent(header, this, null, null); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param props
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object props) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("teamingDlgBoxContent");
		
		CaptionPanel captionPanel = new CaptionPanel(m_messages.databasePruneDlgHeader1());
		FlowPanel captionFlowPanel = new FlowPanel();
		captionPanel.add(captionFlowPanel);
		mainPanel.add(captionPanel);

		String h2;
		if (m_isVibe)
		     h2 = m_messages.databasePruneDlgHeader2_Vibe();
		else h2 = m_messages.databasePruneDlgHeader2_Filr();
		Label label = new Label(h2);
		label.addStyleName("marginbottom3");
		captionFlowPanel.add(label);

		// Create the controls for AuditTrail prune age.
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(6);
		
		Label intervalLabel = new Label(m_messages.databasePruneDlgRemoveAuditTrailEntries());
		hPanel.add(intervalLabel);
		
		m_auditTrailPruneAgeTextBox = new TextBox();
		m_auditTrailPruneAgeTextBox.addKeyPressHandler(this);
		m_auditTrailPruneAgeTextBox.setVisibleLength(3);
		hPanel.add(m_auditTrailPruneAgeTextBox);
		
		intervalLabel = new Label(m_messages.databasePruneDlgAgeUnits());
		hPanel.add(intervalLabel);

		captionFlowPanel.add(hPanel);
		
		// Create the controls for ChangeLog prune age.
		FlowPanel ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName("margintop2");

		// Create the "Enable Change Log" checkbox
		m_changeLogEnabledCB = new CheckBox(m_messages.databasePruneDlgEnableChangeLog());
		FlowPanel tmpPanel = new FlowPanel();
		tmpPanel.add(m_changeLogEnabledCB);
		ckboxPanel.add(tmpPanel);
		captionFlowPanel.add(ckboxPanel);
		if (!m_isVibe) {
			ckboxPanel.setVisible(false);
		}

		hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(6);
		hPanel.addStyleName("marginleft2");
		
		intervalLabel = new Label(m_messages.databasePruneDlgRemoveChangeLogEntries());
		hPanel.add(intervalLabel);
		
		m_changeLogPruneAgeTextBox = new TextBox();
		m_changeLogPruneAgeTextBox.addKeyPressHandler(this);
		m_changeLogPruneAgeTextBox.setVisibleLength(3);
		hPanel.add(m_changeLogPruneAgeTextBox);
		
		intervalLabel = new Label(m_messages.databasePruneDlgAgeUnits());
		hPanel.add(intervalLabel);

		captionFlowPanel.add(hPanel);
		if (!m_isVibe) {
			hPanel.setVisible(false);
		}
		
		// Create the warning messages.
		hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(6);
		hPanel.addStyleName("margintop3");
		Label warningLabel = new Label(m_messages.databasePruneDlgCautionIrrevocable());
		hPanel.add(warningLabel);
		captionFlowPanel.add(hPanel);
		
		hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(6);
		warningLabel = new Label(m_messages.databasePruneDlgCautionAuditTrail());
		hPanel.add(warningLabel);
		captionFlowPanel.add(hPanel);
		
		if (m_isVibe) {
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			hPanel.setSpacing(6);
			hPanel.addStyleName("marginbottom3");
			warningLabel = new Label(m_messages.databasePruneDlgCautionChangeLog());
			hPanel.add(warningLabel);
			captionFlowPanel.add(hPanel);
		}

		// Create the controls for enabling file archiving.
		captionPanel = new CaptionPanel(m_messages.databasePruneDlgHeader3());
		captionFlowPanel = new FlowPanel();
		captionPanel.add(captionFlowPanel);
		mainPanel.add(captionPanel);
		
		ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName("margintop3");

		// Create the 'Enable File Archiving' check box.
		m_fileArchivingEnabledCB = new CheckBox(m_messages.databasePruneDlgEnableFileArchiving());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_fileArchivingEnabledCB);
		ckboxPanel.add(tmpPanel);
		captionFlowPanel.add(ckboxPanel);

		// Create the warning messages.
		hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(6);
		hPanel.addStyleName("margintop1");
		String warning;
		if      (m_isFilr && m_isVibe) warning = m_messages.databasePruneDlgCautionFileArchiving_Both();
		else if (m_isFilr)             warning = m_messages.databasePruneDlgCautionFileArchiving_Filr();
		else                           warning = m_messages.databasePruneDlgCautionFileArchiving_Vibe();
		warningLabel = new Label(warning);
		hPanel.add(warningLabel);
		captionFlowPanel.add(hPanel);
		
		if (!m_isVibe) {
			captionPanel.setVisible(false);
		}
		
		return mainPanel;
	}
	
	/**
	 * This method gets called when user user presses OK.
	 * 
	 * Implements the EditSuccessful.editSuccessful() method.
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		String pruneAgeS = m_auditTrailPruneAgeTextBox.getValue();
		if (!(GwtClientHelper.isValidInt(pruneAgeS, true))) {
			GwtClientHelper.deferredAlert(m_messages.databasePruneDlg_Error_AuditLogInvalid(), new AlertDlgCallback() {
				@Override
				public void closed() {
					setButtonsEnabled(true);
					GwtClientHelper.setFocusDelayed(m_auditTrailPruneAgeTextBox);
				}
			});
			return false;
		}
		boolean validateMinimum = (0 != MINIMUM_AUDIT_TRAIL_PRUNE_AGE);
		if (validateMinimum && GwtClientHelper.hasString(pruneAgeS)) {
			int pruneAge = Integer.parseInt(pruneAgeS);
			if ((0 < pruneAge) && (MINIMUM_AUDIT_TRAIL_PRUNE_AGE > pruneAge)) {
				GwtClientHelper.deferredAlert(m_messages.databasePruneDlg_Error_AuditLogTooSmall(MINIMUM_AUDIT_TRAIL_PRUNE_AGE), new AlertDlgCallback() {
					@Override
					public void closed() {
						setButtonsEnabled(true);
						GwtClientHelper.setFocusDelayed(m_auditTrailPruneAgeTextBox);
					}
				});
				return false;
			}
		}
		
		if (m_isVibe) {
			pruneAgeS = m_changeLogPruneAgeTextBox.getValue();
			if (!(GwtClientHelper.isValidInt(pruneAgeS, true))) {
				GwtClientHelper.deferredAlert(m_messages.databasePruneDlg_Error_ChangeLogInvalid(), new AlertDlgCallback() {
					@Override
					public void closed() {
						setButtonsEnabled(true);
						GwtClientHelper.setFocusDelayed(m_changeLogPruneAgeTextBox);
					}
				});
				return false;
			}
			validateMinimum = (0 != MINIMUM_CHANGE_LOG_PRUNE_AGE);
			if (validateMinimum && GwtClientHelper.hasString(pruneAgeS)) {
				int pruneAge = Integer.parseInt(pruneAgeS);
				if ((0 < pruneAge) && (MINIMUM_CHANGE_LOG_PRUNE_AGE > pruneAge)) {
					GwtClientHelper.deferredAlert(m_messages.databasePruneDlg_Error_ChangeLogTooSmall(MINIMUM_CHANGE_LOG_PRUNE_AGE), new AlertDlgCallback() {
						@Override
						public void closed() {
							setButtonsEnabled(true);
							GwtClientHelper.setFocusDelayed(m_changeLogPruneAgeTextBox);
						}
					});
					setButtonsEnabled(true);
					return false;
				}
			}
		}
		
		// Issue a GWT RPC request to save the prune configuration to
		// the DB.
		GwtDatabasePruneConfiguration databasePruneConfig = ((GwtDatabasePruneConfiguration) obj);
		SaveDatabasePruneConfigurationCmd cmd = new SaveDatabasePruneConfigurationCmd(databasePruneConfig);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Get the panel that holds the errors.
				FlowPanel errorPanel = getErrorPanel();
				errorPanel.clear();
				
				String errMsg = m_messages.databasePruneDlg_OnSaveUnknownException(caught.toString());
				Label label = new Label(errMsg);
				label.addStyleName("dlgErrorLabel");
				errorPanel.add(label);
				
				setButtonsEnabled(true);
				showErrorPanel();
				GwtClientHelper.setFocusDelayed(m_auditTrailPruneAgeTextBox);
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Close this dialog.
				hide();
			}
		});
		
		// Returning false will prevent the dialog from closing.  We
		// will close the dialog after we successfully save the
		// configuration.
		return false;
	}

	/**
	 * Get the data from the controls in the dialog box and store the
	 * data in a GwtDatabasePruneConfiguration object.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		GwtDatabasePruneConfiguration databasePruneConfig = new GwtDatabasePruneConfiguration();

		// Get the auditTrail prune age from the dialog.
		databasePruneConfig.setAuditTrailPruneAgeDays(getAuditTrailPruneAge());
		databasePruneConfig.setAuditTrailEnabled(     getAuditTrailEnabled() );
		
		// Get the changeLog prune age from the dialog.
		databasePruneConfig.setChangeLogPruneAgeDays(getChangeLogPruneAge());
		databasePruneConfig.setChangeLogEnabled(     getChangeLogEnabled() );
		
		// Get the file archiving setting.
		databasePruneConfig.setFileArchivingEnabled(getFileArchivingEnabled());
		
		return databasePruneConfig;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is
	 * shown.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return m_auditTrailPruneAgeTextBox;
	}
	
	/**
	 * Returns information about running help for the dialog.
	 * 
	 * Overrides the DlgBox.getHelperData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("databaseprune");
		return helpData;
	}

	/*
	 * Return if the file archiving is enabled.
	 */
	private boolean getFileArchivingEnabled() {
		if (m_isVibe && m_fileArchivingEnabledCB.getValue()) {
			return true;
		}
		return false;
	}
	
	/*
	 * Return if the audit trail is enabled.
	 */
	private boolean getAuditTrailEnabled() {
		return true;
	}
	
	/*
	 * Return the audit trail prune age entered by the user.
	 */
	private int getAuditTrailPruneAge() {
		String pruneAgeStr = m_auditTrailPruneAgeTextBox.getText();
		int interval = 0;
		if (GwtClientHelper.isValidInt(pruneAgeStr, false)) {
			interval = Integer.parseInt(pruneAgeStr);
		}
		
		return interval;
	}
	
	/*
	 * Return if the change log is enabled.
	 */
	private boolean getChangeLogEnabled() {
		if (m_isVibe && m_changeLogEnabledCB.getValue()) {
			return true;
		}

		return false;
	}
	
	/*
	 * Return the change log prune age entered by the user.
	 */
	private int getChangeLogPruneAge() {
		int interval = 0;
		if (m_isVibe) {
			String pruneAgeStr = m_changeLogPruneAgeTextBox.getText();
			if (GwtClientHelper.isValidInt(pruneAgeStr, false)) {
				interval = Integer.parseInt(pruneAgeStr);
			}
		}
		
		return interval;
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the
	 * given values.
	 * 
	 * @param databasePruneConfiguration
	 */
	public void init(GwtDatabasePruneConfiguration databasePruneConfiguration) {
		// Initialize the audit trail prune age text box
		String value = "";
		int interval = databasePruneConfiguration.getAuditTrailPruneAgeDays();
		if (interval > 0) value = String.valueOf(interval);
		m_auditTrailPruneAgeTextBox.setText(value);
		
		// Initialize the change log prune age text box and enabled check box
		m_changeLogEnabledCB.setValue(databasePruneConfiguration.isChangeLogEnabled());
		value = "";
		interval = databasePruneConfiguration.getChangeLogPruneAgeDays();
		if (interval > 0) value = String.valueOf(interval);
		m_changeLogPruneAgeTextBox.setText(value);

		// Initialize the file archiving enabled check box
		m_fileArchivingEnabledCB.setValue(databasePruneConfiguration.isFileArchivingEnabled());

		setButtonsEnabled(true);
		hideErrorPanel();
	}
	
	/**
	 * This method gets called when the user types in one of the entry
	 * field text boxes.  We only allow the user to enter numbers.
	 * 
	 * Implements the KeyPressHandler.onKeyPress() method.
	 * 
	 * @param event
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
        // Get the key the user pressed.
        int keyCode = event.getNativeEvent().getKeyCode();
        if (!(GwtClientHelper.isKeyValidForNumericField(event.getCharCode(), keyCode))) {
        	// Make sure we are dealing with a text box.
        	Object source = event.getSource();
        	if (source instanceof TextBox) {
        		// Suppress the current keyboard event.
            	TextBox txtBox = ((TextBox) source);
        		txtBox.cancelKey();
        	}
        }
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
	/* the database prune dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the 'Prune DataBase Tables'
	 * dialog asynchronously after it loads. 
	 */
	public interface ManageDatabasePruneDlgClient {
		void onSuccess(ManageDatabasePruneDlg mdbpDlg);
		void onUnavailable();
	}

	/**
	 * Loads the ManageDatabasePruneDlg split point and returns an
	 * instance of the dialog via the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param mdpdDlgClient
	 */
	public static void createAsync(
		final boolean						autoHide,
		final boolean						modal,
		final int							left,
		final int							top,
		final int							width,
		final int							height,
		final ManageDatabasePruneDlgClient	mdpdDlgClient)
	{
		GWT.runAsync(ManageDatabasePruneDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageDatabasePruneDlg());
				if (mdpdDlgClient != null) {
					mdpdDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				ManageDatabasePruneDlg mdbpDlg = new ManageDatabasePruneDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height);
				mdpdDlgClient.onSuccess(mdbpDlg);
			}
		});
	}
}
