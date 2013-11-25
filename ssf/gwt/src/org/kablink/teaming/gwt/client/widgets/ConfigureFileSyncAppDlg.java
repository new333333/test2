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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class ConfigureFileSyncAppDlg extends DlgBox
	implements KeyPressHandler
{
	private RadioButton m_enableFileSyncRB;
	private RadioButton m_disableFileSyncRB;
	private TextBox m_syncIntervalTextBox;
	private TextBox m_autoUpdateUrlTextBox;
	

	/**
	 * 
	 */
	public ConfigureFileSyncAppDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().fileSyncAppDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlexTable table;
		Label spacer;
		int nextRow;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		nextRow = 0;
		
		// Add the controls for enable/disable File Sync App
		{
			FlowPanel rbPanel;
			
			table.setText( nextRow, 0, messages.fileSyncAppOnOffLabel() );
			++nextRow;
			
			rbPanel = new FlowPanel();
			
			m_enableFileSyncRB = new RadioButton( "fileSyncEnabled", messages.fileSyncAppOn() );
			m_enableFileSyncRB.setValue( Boolean.FALSE );
			rbPanel.add( m_enableFileSyncRB );
			
			m_disableFileSyncRB = new RadioButton( "fileSyncEnabled", messages.fileSyncAppOff() );
			m_disableFileSyncRB.setValue( Boolean.TRUE );
			rbPanel.add( m_disableFileSyncRB );
			
			table.setWidget( nextRow, 0, rbPanel );
			++nextRow;
		}
		
		// Add an empty row to add some space.
		spacer = new Label( " " );
		spacer.addStyleName( "marginTop10px" );
		table.setWidget( nextRow, 0, spacer );
		++nextRow;
		
		// Create the controls for File Sync interval
		{
			HorizontalPanel hPanel;
			Label intervalLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			
			intervalLabel = new Label( messages.fileSyncAppIntervalLabel() );
			hPanel.add( intervalLabel );
			
			m_syncIntervalTextBox = new TextBox();
			m_syncIntervalTextBox.addKeyPressHandler( this );
			m_syncIntervalTextBox.setVisibleLength( 3 );
			hPanel.add( m_syncIntervalTextBox );
			
			intervalLabel = new Label( messages.fileSyncAppMinutesLabel() );
			hPanel.add( intervalLabel );
			
			table.setWidget( nextRow, 0, hPanel );
			++nextRow;
		}
		
		// Add an empty row to add some space.
		spacer = new Label( " " );
		spacer.addStyleName( "marginTop10px" );
		table.setWidget( nextRow, 0, spacer );
		++nextRow;
		
		// Create the controls for auto-update url.
		{
			table.setText( nextRow, 0, messages.fileSyncAppAutoUpdateUrlLabel() );
			++nextRow;
			
			// Create a textbox for the user to enter the auto-update url.
			m_autoUpdateUrlTextBox = new TextBox();
			table.setWidget( nextRow, 0, m_autoUpdateUrlTextBox );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		return mainPanel;
	}
	
	
	/**
	 * Return the string entered by the user for the auto-update url
	 */
	private String getAutoUpdateUrl()
	{
		return m_autoUpdateUrlTextBox.getText();
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtFileSyncAppConfiguration object.
	 */
	public Object getDataFromDlg()
	{
		GwtFileSyncAppConfiguration fileSyncAppConfig;
		
		fileSyncAppConfig = new GwtFileSyncAppConfiguration();
		
		// Get whether the File Sync App is enabled.
		fileSyncAppConfig.setIsFileSyncAppEnabled( getIsFileSyncAppEnabled() );
		
		// Get the sync interval from the dialog.
		fileSyncAppConfig.setSyncInterval( getIntervalInt() );
		
		// Get the auto-update url from the dialog.
		fileSyncAppConfig.setAutoUpdateUrl( getAutoUpdateUrl() );
		
		return fileSyncAppConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_syncIntervalTextBox;
	}
	
	/**
	 * Return the interval entered by the user.
	 */
	private int getIntervalInt()
	{
		String intervalStr;
		int interval = 0;
		
		intervalStr = m_syncIntervalTextBox.getText();
		if ( intervalStr != null && intervalStr.length() > 0 )
			interval = Integer.parseInt( intervalStr );
		
		return interval;
	}
	
	/**
	 * Return whether the File Sync App is enabled.
	 */
	private boolean getIsFileSyncAppEnabled()
	{
		if ( m_enableFileSyncRB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	public void init( GwtFileSyncAppConfiguration fileSyncAppConfiguration )
	{
		int interval;
		
		// Initialize the on/off radio buttons.
		if ( fileSyncAppConfiguration.getIsFileSyncAppEnabled() )
		{
			m_enableFileSyncRB.setValue( true );
			m_disableFileSyncRB.setValue( false );
		}
		else
		{
			m_enableFileSyncRB.setValue( false );
			m_disableFileSyncRB.setValue( true );
		}
		
		// Initialize the interval textbox
		interval = fileSyncAppConfiguration.getSyncInterval();
		m_syncIntervalTextBox.setText( String.valueOf( interval ) );
		
		// Initialize the auto-update url.
		m_autoUpdateUrlTextBox.setText( fileSyncAppConfiguration.getAutoUpdateUrl() );
	}
	
	
	/**
	 * This method gets called when the user types in the "number of entries to show" text box.
	 * We only allow the user to enter numbers.
	 */
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
}
