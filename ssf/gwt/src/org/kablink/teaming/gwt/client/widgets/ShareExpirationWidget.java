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

import java.util.Date;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;

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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;



/**
 * This widget is used to define when a share expires
 * 
 * @author jwootton@novell.com
 */
public class ShareExpirationWidget extends Composite
	implements KeyPressHandler
{
	private ListBox m_expiresListbox;
	private VibeFlowPanel m_expiresOnPanel;
	private TZDateBox m_dateBox;
	private VibeFlowPanel m_expiresAfterPanel;
	private TextBox m_expiresAfterTextBox;

	private static long MILLISEC_IN_A_DAY = 86400000;
	
	/**
	 * 
	 */
	public ShareExpirationWidget()
	{
		GwtTeamingMessages messages;
		VibeFlowPanel mainPanel;
		VibeFlowPanel expiresPanel;
		FlexTable mainTable;
		int col;

		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "shareExpirationWidget_expirationPanel" );

		// Give this panel a fixed width so the dialog doesn't resize after the user
		// selects an expiration type.
		mainPanel.setWidth( "300px" );
		mainPanel.setHeight( "40px" );

		mainTable = new FlexTable();
		mainTable.addStyleName( "shareExpirationWidget_table" );
		mainPanel.add( mainTable );
		
		col = 0;
		
		mainTable.setText( 0, col, GwtTeaming.getMessages().shareExpirationDlg_expiresLabel() );
		++col;
		
		messages = GwtTeaming.getMessages();
		
		// Create a select control for specifying when the share expires
		{
			// Add the listbox where the user can select how the share expires, never, after or on
			m_expiresListbox = new ListBox( false );
			m_expiresListbox.addItem( 
								messages.shareExpirationDlg_expiresNever(),
								ShareExpirationType.NEVER.toString() );
			
			m_expiresListbox.addItem(
								messages.shareExpirationDlg_expiresOn(),
								ShareExpirationType.ON_DATE.toString() );
			
			m_expiresListbox.addItem(
								messages.shareExpirationDlg_expiresAfter(),
								ShareExpirationType.AFTER_DAYS.toString() );

			m_expiresListbox.setVisibleItemCount( 1 );
			
			m_expiresListbox.addChangeHandler( new ChangeHandler()
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
			mainTable.setWidget( 0, col, m_expiresListbox );
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
			m_expiresOnPanel.addStyleName( "shareExpirationWidget_expiresOnPanel" );
			m_expiresOnPanel.setVisible( false );
			
			table = new FlexTable();
			table.addStyleName( "shareExpirationWidget_expiresOnTable" );
			m_expiresOnPanel.add( table );
			
			dateFormat = DateTimeFormat.getFormat( PredefinedFormat.DATE_SHORT );
			m_dateBox = new TZDateBox( new DatePicker(), (-1), new DateBox.DefaultFormat( dateFormat ) );
			offset = GwtTeaming.m_requestInfo.getTimeZoneOffsetHour() * 60 * 60 * 1000;
			m_dateBox.setTZOffset( offset );
			m_dateBox.getDateBox().getTextBox().setVisibleLength( 8 );
			table.setWidget( 0, 0, m_dateBox );
			
			imgResource = GwtTeaming.getImageBundle().calDatePicker();
			img = new Image( imgResource );
			img.addStyleName( "shareExpirationWidget_expiresOnImg" );
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
			m_expiresAfterPanel.addStyleName( "shareExpirationWidget_expiresAfterPanel" );
			m_expiresAfterPanel.setVisible( false );
			
			table = new FlexTable();
			table.addStyleName( "shareExpirationWidget_expiresAfterTable" );
			m_expiresAfterTextBox = new TextBox();
			m_expiresAfterTextBox.setVisibleLength( 4 );
			m_expiresAfterTextBox.addStyleName( "shareExpirationWidget_expiresAfterTextBox" );
			m_expiresAfterTextBox.addKeyPressHandler( this );
			table.setWidget( 0, 0, m_expiresAfterTextBox );
			table.setText( 0, 1, GwtTeaming.getMessages().shareExpirationDlg_days() );
			m_expiresAfterPanel.add( table );
			
			expiresPanel.add( m_expiresAfterPanel );
		}

		initWidget( mainPanel );
	}

	/**
	 * Show/hide the appropriate controls based on the selected expiration type.
	 */
	private void danceWidget( boolean setFocus )
	{
		int selectedIndex;
		
		selectedIndex = m_expiresListbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			ShareExpirationType type;
			
			// Get the selected expiration type;
			type = getSelectedExpirationType();

			m_expiresAfterPanel.setVisible( false );
			m_expiresOnPanel.setVisible( false );

			if ( type != null )
			{
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
	}
	
	/**
	 * Return the number of days entered by the user.
	 */
	private Long getDays()
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
	private Long getExpirationDate()
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
	 * Get the expiration value entered by the user
	 */
	public ShareExpirationValue getExpirationValue()
	{
		ShareExpirationType expirationType;
		ShareExpirationValue value = null;

		expirationType = getSelectedExpirationType();
		
		if ( expirationType != null )
		{
			value = new ShareExpirationValue();

			if ( expirationType == ShareExpirationType.AFTER_DAYS )
			{
				Long days;
				
				// The user selected "after days".
				// Did they enter the number of days?
				days = getDays();
				value.setValue( days );
			}
			else if ( expirationType == ShareExpirationType.ON_DATE )
			{
				Long date;
				
				// The user selected "on date".
				// Did they enter a date?
				date = getExpirationDate();
				value.setValue( date );
			}
			
			value.setType( expirationType );
		}
		
		return value;
	}
		
	/**
	 * Return the selected expiration type
	 */
	private ShareExpirationType getSelectedExpirationType()
	{
		int selectedIndex;
		
		selectedIndex = m_expiresListbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			String value;

			value = m_expiresListbox.getValue( selectedIndex );
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
		
		return null;
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
	 * This method gets called when the user selects the expiration type, never, on, after.
	 * Show/hide the appropriate controls based on the selected expiration type.
	 */
	private void handleExpirationTypeSelected()
	{
		danceWidget( true );
	}

	/**
	 * Initialize the controls dealing with expiration
	 */
	public void init( ShareExpirationValue expirationValue )
	{
		ShareExpirationType expirationType;

		if ( expirationValue != null )
		{
			expirationType = expirationValue.getExpirationType();
			
			// Select the appropriate expiration type.
			GwtClientHelper.selectListboxItemByValue( m_expiresListbox, expirationType.toString() );
			
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
		}
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
	 * Validate the expiration value entered by the user.
	 */
	public boolean validateExpirationValue()
	{
		ShareExpirationType expirationType;

		expirationType = getSelectedExpirationType();
		
		if ( expirationType == ShareExpirationType.AFTER_DAYS )
		{
			Long days;
			
			// The user selected "after days".
			// Did they enter the number of days?
			days = getDays();
			if ( days == null )
			{
				// No, tell the user they need to enter a number of days.
				Window.alert( GwtTeaming.getMessages().shareExpirationDlg_noDaysEntered() );
				m_expiresAfterTextBox.setFocus( true );
				return false;
			}
		}
		else if ( expirationType == ShareExpirationType.ON_DATE )
		{
			Long date;
			Long today;
			
			// The user selected "on date".
			// Did they enter a date?
			date = getExpirationDate();
			if ( date == null )
			{
				// No, tell the user they need to enter the expiration date.
				Window.alert( GwtTeaming.getMessages().shareExpirationDlg_noDateEntered() );
				return false;
			}
			
			// Did the user enter a date from the past?
			today = getToday();
			if ( date < today )
			{
				// Yes, tell them not to do that.
				Window.alert( GwtTeaming.getMessages().shareExpirationDlg_cantEnterPriorDate() );
				return false;
			}
		}
		
		// If we get here everything is valid.
		return true;
	}
}
