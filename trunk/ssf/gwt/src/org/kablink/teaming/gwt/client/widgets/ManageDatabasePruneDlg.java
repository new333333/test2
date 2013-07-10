/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.SaveDatabasePruneConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author phurley
 *
 */
public class ManageDatabasePruneDlg extends DlgBox
	implements KeyPressHandler, EditSuccessfulHandler
{
	private CheckBox m_enableFileSyncAccessCB;
	private CheckBox m_enableDeployCB;
	private CheckBox m_allowPwdCacheCB;
	private CheckBox m_fileArchivingEnabledCB;
	private CheckBox m_auditTrailEnabledCB;
	private CheckBox m_changeLogEnabledCB;
	private TextBox m_auditTrailPruneAgeTextBox;
	private TextBox m_changeLogPruneAgeTextBox;
	private TextBox m_autoUpdateUrlTextBox;
	private TextBox m_maxFileSizeTextBox;
	
	/**
	 * Callback interface to interact with the "Prune DataBase Tables" dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageDatabasePruneDlgClient
	{
		void onSuccess( ManageDatabasePruneDlg cfsaDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private ManageDatabasePruneDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().databasePruneDlgHeader(), this, null, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlowPanel captionFlowPanel;
		FlowPanel tmpPanel;
		FlowPanel ckboxPanel;
		CaptionPanel captionPanel;
		ClickHandler clickHandler;
		Label label;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		captionPanel = new CaptionPanel( messages.databasePruneDlgHeader1() );
		captionFlowPanel = new FlowPanel();
		captionPanel.add(captionFlowPanel);
		mainPanel.add(captionPanel);

		label = new Label( messages.databasePruneDlgHeader2() );
		captionFlowPanel.add( label );

		// Create the controls for AuditTrail prune age
		{
			ckboxPanel = new FlowPanel();
			ckboxPanel.addStyleName( "margintop3" );

			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
				}
			};

			// Create the "Enable Audit Trail" check box
			m_auditTrailEnabledCB = new CheckBox( messages.databasePruneDlgEnableAuditTrail() );
			m_auditTrailEnabledCB.addClickHandler( clickHandler );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_auditTrailEnabledCB );
			ckboxPanel.add( tmpPanel );
			captionFlowPanel.add( ckboxPanel );

			HorizontalPanel hPanel;
			Label intervalLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 6 );
			hPanel.addStyleName("marginleft2");
			
			intervalLabel = new Label( messages.databasePruneDlgRemoveAuditTrailEntries() );
			hPanel.add( intervalLabel );
			
			m_auditTrailPruneAgeTextBox = new TextBox();
			m_auditTrailPruneAgeTextBox.addKeyPressHandler( this );
			m_auditTrailPruneAgeTextBox.setVisibleLength( 3 );
			hPanel.add( m_auditTrailPruneAgeTextBox );
			
			intervalLabel = new Label( messages.databasePruneDlgAgeUnits() );
			hPanel.add( intervalLabel );

			captionFlowPanel.add( hPanel );
		}
		
		// Create the controls for ChangeLog prune age
		{
			ckboxPanel = new FlowPanel();
			ckboxPanel.addStyleName( "margintop2" );

			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
				}
			};

			// Create the "Enable Change Log" checkbox
			m_changeLogEnabledCB = new CheckBox( messages.databasePruneDlgEnableChangeLog() );
			m_changeLogEnabledCB.addClickHandler( clickHandler );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_changeLogEnabledCB );
			ckboxPanel.add( tmpPanel );
			captionFlowPanel.add( ckboxPanel );

			HorizontalPanel hPanel;
			Label intervalLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 6 );
			hPanel.addStyleName("marginleft2");
			
			intervalLabel = new Label( messages.databasePruneDlgRemoveChangeLogEntries() );
			hPanel.add( intervalLabel );
			
			m_changeLogPruneAgeTextBox = new TextBox();
			m_changeLogPruneAgeTextBox.addKeyPressHandler( this );
			m_changeLogPruneAgeTextBox.setVisibleLength( 3 );
			hPanel.add( m_changeLogPruneAgeTextBox );
			
			intervalLabel = new Label( messages.databasePruneDlgAgeUnits() );
			hPanel.add( intervalLabel );

			captionFlowPanel.add( hPanel );
		}
		
		// Create the warning messages
		{
			HorizontalPanel hPanel;
			Label warningLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 6 );
			hPanel.addStyleName("margintop3");
			warningLabel = new Label( messages.databasePruneDlgCautionIrrevocable() );
			hPanel.add( warningLabel );
			captionFlowPanel.add( hPanel );
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 6 );
			warningLabel = new Label( messages.databasePruneDlgCautionAuditTrail() );
			hPanel.add( warningLabel );
			captionFlowPanel.add( hPanel );
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 6 );
			hPanel.addStyleName("marginbottom3");
			warningLabel = new Label( messages.databasePruneDlgCautionChangeLog() );
			hPanel.add( warningLabel );
			captionFlowPanel.add( hPanel );
		}

		// Create the controls for enabling file archiving
		{
			captionPanel = new CaptionPanel( messages.databasePruneDlgHeader3() );
			captionFlowPanel = new FlowPanel();
			captionPanel.add(captionFlowPanel);
			mainPanel.add(captionPanel);
			
			ckboxPanel = new FlowPanel();
			ckboxPanel.addStyleName( "margintop3" );

			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
				}
			};

			// Create the "Enable File Archiving" check box
			m_fileArchivingEnabledCB = new CheckBox( messages.databasePruneDlgEnableFileArchiving() );
			m_fileArchivingEnabledCB.addClickHandler( clickHandler );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_fileArchivingEnabledCB );
			ckboxPanel.add( tmpPanel );
			captionFlowPanel.add( ckboxPanel );

			// Create the warning messages
			HorizontalPanel hPanel;
			Label warningLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 6 );
			hPanel.addStyleName("margintop1");
			warningLabel = new Label( messages.databasePruneDlgCautionFileArchiving() );
			hPanel.add( warningLabel );
			captionFlowPanel.add( hPanel );
		}
		
		return mainPanel;
	}
	
	/**
	 * This method gets called when user user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
		GwtDatabasePruneConfiguration databasePruneConfig;
		SaveDatabasePruneConfigurationCmd cmd;

		databasePruneConfig = (GwtDatabasePruneConfiguration) obj;
		
		// Create the callback that will be used when we issue an ajax request to save the file sync app configuration.
		rpcSaveCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable caught )
			{
				FlowPanel errorPanel;
				Label label;
				String errMsg = null;
				
				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
				}
				
				if ( errMsg == null )
				{
					errMsg = GwtTeaming.getMessages().databasePruneDlg_OnSaveUnknownException( caught.toString() );
				}
				
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
				m_autoUpdateUrlTextBox.setFocus( true );
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// Close this dialog.
				hide();
			}
		};

		// Issue an ajax request to save the File Sync App configuration to the db.  rpcSaveCallback will
		// be called when we get the response back.
		cmd = new SaveDatabasePruneConfigurationCmd( databasePruneConfig );
		GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the configuration.
		return false;
	}

	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtDatabasePruneConfiguration object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtDatabasePruneConfiguration databasePruneConfig;
		
		databasePruneConfig = new GwtDatabasePruneConfiguration();

		// Get the auditTrail prune age from the dialog.
		databasePruneConfig.setAuditTrailPruneAge( getAuditTrailPruneAge() );
		databasePruneConfig.setAuditTrailEnabled(getAuditTrailEnabled());
		
		// Get the changeLog prune age from the dialog.
		databasePruneConfig.setChangeLogPruneAge( getChangeLogPruneAge() );
		databasePruneConfig.setChangeLogEnabled(getChangeLogEnabled());
		
		// Get the file archiving setting
		databasePruneConfig.setFileArchivingEnabled(getFileArchivingEnabled());
		
		return databasePruneConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_auditTrailPruneAgeTextBox;
	}
	
	/**
	 * 
	 */
	@Override
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.ADMIN_GUIDE );
		helpData.setPageId( "databasePrune" );
		
		return helpData;
	}

	/**
	 * Return if the file archiving is enabled.
	 */
	private boolean getFileArchivingEnabled()
	{
		if ( m_fileArchivingEnabledCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * Return if the audit trail is enabled.
	 */
	private boolean getAuditTrailEnabled()
	{
		if ( m_auditTrailEnabledCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * Return the audit trail prune age entered by the user.
	 */
	private int getAuditTrailPruneAge()
	{
		String pruneAgeStr;
		int interval = 0;
		
		pruneAgeStr = m_auditTrailPruneAgeTextBox.getText();
		if ( pruneAgeStr != null && pruneAgeStr.length() > 0 )
			interval = Integer.parseInt( pruneAgeStr );
		
		return interval;
	}
	
	/**
	 * Return if the change log is enabled.
	 */
	private boolean getChangeLogEnabled()
	{
		if ( m_changeLogEnabledCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * Return the change log prune age entered by the user.
	 */
	private int getChangeLogPruneAge()
	{
		String pruneAgeStr;
		int interval = 0;
		
		pruneAgeStr = m_changeLogPruneAgeTextBox.getText();
		if ( pruneAgeStr != null && pruneAgeStr.length() > 0 )
			interval = Integer.parseInt( pruneAgeStr );
		
		return interval;
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	public void init( GwtDatabasePruneConfiguration databasePruneConfiguration )
	{
		int interval;
		int size;
		String value;
		
		// Initialize the audit trail prune age text box and enabled check box
		m_auditTrailEnabledCB.setValue(databasePruneConfiguration.getAuditTrailEnabled());
		value = "";
		interval = databasePruneConfiguration.getAuditTrailPruneAge();
		if (interval > 0) value = String.valueOf( interval );
		m_auditTrailPruneAgeTextBox.setText( value );
		
		// Initialize the change log prune age text box and enabled check box
		m_changeLogEnabledCB.setValue(databasePruneConfiguration.getChangeLogEnabled());
		value = "";
		interval = databasePruneConfiguration.getChangeLogPruneAge();
		if (interval > 0) value = String.valueOf( interval );
		m_changeLogPruneAgeTextBox.setText( value );

		// Initialize the file archiving enabled check box
		m_fileArchivingEnabledCB.setValue(databasePruneConfiguration.getFileArchivingEnabled());

		hideErrorPanel();
	}
	
	
	/**
	 * This method gets called when the user types in the "number of entries to show" text box.
	 * We only allow the user to enter numbers.
	 */
	@Override
	public void onKeyPress( KeyPressEvent event )
	{
        int keyCode;

        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();
        
        if ( (!Character.isDigit(event.getCharCode())) && (keyCode != KeyCodes.KEY_TAB) && (keyCode != KeyCodes.KEY_BACKSPACE)
            && (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) && (keyCode != KeyCodes.KEY_HOME)
            && (keyCode != KeyCodes.KEY_END) && (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
            && (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN))
        {
        	TextBox txtBox;
        	Object source;
        	
        	// Make sure we are dealing with a text box.
        	source = event.getSource();
        	if ( source instanceof TextBox )
        	{
        		// Suppress the current keyboard event.
        		txtBox = (TextBox) source;
        		txtBox.cancelKey();
        	}
        }
	}

	/**
	 * Loads the ManageDatabasePruneDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final int width,
							final int height,
							final ManageDatabasePruneDlgClient cfsaDlgClient )
	{
		GWT.runAsync( ManageDatabasePruneDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ManageDatabasePruneDlg() );
				if ( cfsaDlgClient != null )
				{
					cfsaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ManageDatabasePruneDlg cfsaDlg;
				
				cfsaDlg = new ManageDatabasePruneDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				cfsaDlgClient.onSuccess( cfsaDlg );
			}
		});
	}
}
