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


import java.util.Date;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * 
 * @author jwootton
 *
 */
public class ShareExpirationDlg extends DlgBox
	implements KeyPressHandler
{
	private ListBox m_listbox;
	private TextBox m_expiresAfterTextBox;
	private TZDateBox m_dateBox;
	private VibeFlowPanel m_expiresOnPanel;
	private VibeFlowPanel m_expiresAfterPanel;

	private static long MILLISEC_IN_A_DAY = 86400000; 
	
	/**
	 * Callback interface to interact with the "Share expiration" dialog asynchronously after it loads. 
	 */
	public interface ShareExpirationDlgClient
	{
		void onSuccess( ShareExpirationDlg cbpDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ShareExpirationDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().shareExpirationDlg_caption(), null, null, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		VibeFlowPanel mainPanel;
		VibeFlowPanel expiresPanel;
		FlexTable mainTable;
		int col;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Give this dialog a fixed width so the dialog doesn't resize after the user
		// selects an expiration type.
		mainPanel.setWidth( "300px" );
		mainPanel.setHeight( "40px" );
		

		mainTable = new FlexTable();
		mainTable.addStyleName( "shareExpirationDlg_table" );
		mainPanel.add( mainTable );
		
		col = 0;
		
		mainTable.setText( 0, col, GwtTeaming.getMessages().shareExpirationDlg_expiresLabel() );
		++col;
		
		// Create a select control for specifying when the share expires
		{
			// Add the listbox where the user can select how the share expires, never, after or on
			m_listbox = new ListBox( false );
			m_listbox.setVisibleItemCount( 1 );
			
			m_listbox.addItem( 
						GwtTeaming.getMessages().shareExpirationDlg_expiresNever(),
						ShareExpirationType.NEVER.toString() );
			m_listbox.addItem(
						GwtTeaming.getMessages().shareExpirationDlg_expiresOn(),
						ShareExpirationType.ON_DATE.toString() );
			m_listbox.addItem(
						GwtTeaming.getMessages().shareExpirationDlg_expiresAfter(),
						ShareExpirationType.AFTER_DAYS.toString() );
			
			m_listbox.setSelectedIndex( 0 );

			m_listbox.addChangeHandler( new ChangeHandler()
			{
				@Override
				public void onChange( ChangeEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleExpirationTypeSelected();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			mainTable.setWidget( 0, col, m_listbox );
			++col;
		}
		
		expiresPanel = new VibeFlowPanel();
		mainTable.setWidget( 0, col, expiresPanel );
		++col;
		
		// Create the controls needed to specify "expires on"
		{
			FlexTable table;
			DateTimeFormat dateFormat;
			ImageResource imgResource;
			Image img;
			long offset;

			m_expiresOnPanel = new VibeFlowPanel();
			m_expiresOnPanel.addStyleName( "shareExpirationDlg_expiresOnPanel" );
			m_expiresOnPanel.setVisible( false );
			
			table = new FlexTable();
			table.addStyleName( "shareExpirationDlg_expiresOnTable" );
			m_expiresOnPanel.add( table );
			
			dateFormat = DateTimeFormat.getFormat( PredefinedFormat.DATE_SHORT );
			m_dateBox = new TZDateBox( new DatePicker(), (-1), new DateBox.DefaultFormat( dateFormat ) );
			offset = GwtTeaming.m_requestInfo.getTimeZoneOffsetHour() * 60 * 60 * 1000;
			m_dateBox.setTZOffset( offset );
			m_dateBox.getDateBox().getTextBox().setVisibleLength( 8 );
			table.setWidget( 0, 0, m_dateBox );
			
			imgResource = GwtTeaming.getImageBundle().calDatePicker();
			img = new Image( imgResource );
			img.addStyleName( "shareExpirationDlg_expiresOnImg" );
			img.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeDatePicker();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			table.setWidget( 0, 1, img );
			
			expiresPanel.add( m_expiresOnPanel );
		}
		
		// Create the controls needed to specify "expires after"
		{
			FlexTable table;
			
			m_expiresAfterPanel = new VibeFlowPanel();
			m_expiresAfterPanel.addStyleName( "shareExpirationDlg_expiresAfterPanel" );
			m_expiresAfterPanel.setVisible( false );
			
			table = new FlexTable();
			table.addStyleName( "shareExpirationDlg_expiresAfterTable" );
			m_expiresAfterTextBox = new TextBox();
			m_expiresAfterTextBox.setVisibleLength( 4 );
			m_expiresAfterTextBox.addStyleName( "shareExpirationDlg_expiresAfterTextBox" );
			m_expiresAfterTextBox.addKeyPressHandler( this );
			table.setWidget( 0, 0, m_expiresAfterTextBox );
			table.setText( 0, 1, GwtTeaming.getMessages().shareExpirationDlg_days() );
			m_expiresAfterPanel.add( table );
			
			expiresPanel.add( m_expiresAfterPanel );
		}

		return mainPanel;
	}
	
	
	/**
	 * Show/hide the appropriate controls based on the selected expiration type.
	 */
	private void danceDlg( boolean setFocus )
	{
		int selectedIndex;
		
		selectedIndex = m_listbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			ShareExpirationType type;
			
			// Get the selected expiration type;
			type = getSelectedExpirationType();

			m_expiresAfterPanel.setVisible( false );
			m_expiresOnPanel.setVisible( false );

			switch ( type )
			{
			case AFTER_DAYS:
				m_expiresAfterPanel.setVisible( true );
				if ( setFocus )
					m_expiresAfterTextBox.setFocus( true );
				
				break;
				
			case ON_DATE:
				m_expiresOnPanel.setVisible( true );
				if ( setFocus )
					invokeDatePicker();
				
				break;
				
			case NEVER:
			case UNKNOWN:
			default:
				break;
			}
		}
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	@Override
	public Object getDataFromDlg()
	{
		ShareExpirationType expirationType;
		ShareExpirationValue value;

		value = new ShareExpirationValue();
		expirationType = getSelectedExpirationType();
		
		if ( expirationType == ShareExpirationType.AFTER_DAYS )
		{
			Long days;
			
			// The user selected "after days".
			// Did they enter the number of days?
			days = getDaysFromDlg();
			if ( days == null )
			{
				// No, tell the user they need to enter a number of days.
				Window.alert( GwtTeaming.getMessages().shareExpirationDlg_noDaysEntered() );
				m_expiresAfterTextBox.setFocus( true );
				return null;
			}
			
			value.setValue( days );
		}
		else if ( expirationType == ShareExpirationType.ON_DATE )
		{
			Long date;
			Long today;
			
			// The user selected "on date".
			// Did they enter a date?
			date = getExpirationDateFromDlg();
			if ( date == null )
			{
				// No, tell the user they need to enter the expiration date.
				Window.alert( GwtTeaming.getMessages().shareExpirationDlg_noDateEntered() );
				return null;
			}
			
			// Did the user enter a date from the past?
			today = getToday();
			if ( date < today )
			{
				// Yes, tell them not to do that.
				Window.alert( GwtTeaming.getMessages().shareExpirationDlg_cantEnterPriorDate() );
				return null;
			}
			
			value.setValue( date );
		}
		
		value.setType( expirationType );
		
		return value;
	}
	
	/**
	 * Return the number of days entered by the user.
	 */
	private Long getDaysFromDlg()
	{
		String value;
		Long days = null;
		
		value = m_expiresAfterTextBox.getText();
		if ( value != null && value.length() > 0 )
		{
			days = Long.parseLong( value );
		}
		
		return days;
	}
	
	
	/**
	 * Return the expiration date entered by the user.
	 */
	private Long getExpirationDateFromDlg()
	{
		Long value;
		
		value = m_dateBox.getValue();
		if ( value == -1 )
			value = null;
		
		if ( value != null )
		{
			// Have the share expire at 23:59:59 on the selected day
			value += (MILLISEC_IN_A_DAY - 1000);

			// Convert the time to GMT
			value += (GwtTeaming.m_requestInfo.getTimeZoneOffsetHour() * 60 * 60 * 1000);
		}
		
		return value;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_listbox;
	}
	
	/**
	 * 
	 */
	private Long getToday()
	{
		Date today;
		Long value;
		
		today = new Date();
		value = today.getTime();
		
		// Convert the time to GMT
		value += (GwtTeaming.m_requestInfo.getTimeZoneOffsetHour() * 60 * 60 * 1000);
		
		return value;
	}

	/**
	 * Return the selected expiration type
	 */
	private ShareExpirationType getSelectedExpirationType()
	{
		int selectedIndex;
		
		selectedIndex = m_listbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			String value;

			value = m_listbox.getValue( selectedIndex );
			if ( value != null )
			{
				if ( value.equalsIgnoreCase( ShareExpirationType.AFTER_DAYS.toString() ) )
					return ShareExpirationType.AFTER_DAYS;
				
				if ( value.equalsIgnoreCase( ShareExpirationType.NEVER.toString() ) )
					return ShareExpirationType.NEVER;
				
				if ( value.equalsIgnoreCase( ShareExpirationType.ON_DATE.toString() ) )
					return ShareExpirationType.ON_DATE;
			}
		}
		
		return ShareExpirationType.UNKNOWN;
	}
	
	/**
	 * This method gets called when the user selects the expiration type, never, on, after.
	 * Show/hide the appropriate controls based on the selected expiration type.
	 */
	private void handleExpirationTypeSelected()
	{
		danceDlg( true );
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( ShareExpirationValue expirationValue, EditSuccessfulHandler editSuccessfulHandler )
	{
		ShareExpirationType expirationType;
		
		expirationType = expirationValue.getExpirationType();
		
		// Select the appropriate expiration type.
		GwtClientHelper.selectListboxItemByValue( m_listbox, expirationType.toString() );
		
		switch (expirationType)
		{
		case AFTER_DAYS:
			Long value;
			
			value = expirationValue.getValue();
			if ( value != null )
				m_expiresAfterTextBox.setText( value.toString() );
			break;
		
		case ON_DATE:
			m_dateBox.setValue( expirationValue.getValue() );
			break;
		
		case NEVER:
		case UNKNOWN:
		default:
			break;
		}
		
		danceDlg( false );
		
		// Set the "edit successful handler" we should use.
		initHandlers( editSuccessfulHandler, null );
	}
	
	/**
	 * Invoke the date picker control.
	 */
	private void invokeDatePicker()
	{
		m_dateBox.getDateBox().showDatePicker();
	}

	/**
	 * This method gets called when the user types in the "number of days after" text box.
	 * We only allow the user to enter numbers.
	 */
	@Override
	public void onKeyPress( KeyPressEvent event )
	{
    	final TextBox txtBox;
        int keyCode;
    	Object source;
    	
    	// Make sure we are dealing with a text box.
    	source = event.getSource();
    	if ( source instanceof TextBox )
    		txtBox = (TextBox) source;
    	else
    		txtBox = null;

        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();
        
        // Only let the user enter a valid digit.
        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
        {
        	// Make sure we are dealing with a text box.
        	if ( txtBox != null )
        	{
        		// Suppress the current keyboard event.
        		txtBox.cancelKey();
        	}
        }
	}

	/**
	 * Loads the ShareExpirationDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final ShareExpirationDlgClient seDlgClient )
	{
		GWT.runAsync( ShareExpirationDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ShareExpirationDlg() );
				if ( seDlgClient != null )
				{
					seDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ShareExpirationDlg seDlg;
				
				seDlg = new ShareExpirationDlg( autoHide, modal );
				seDlgClient.onSuccess( seDlg );
			}
		});
	}
}
