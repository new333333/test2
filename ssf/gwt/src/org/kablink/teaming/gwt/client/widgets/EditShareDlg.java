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


import java.util.ArrayList;
import java.util.Date;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * 
 * @author jwootton
 *
 */
public class EditShareDlg extends DlgBox
	implements EditSuccessfulHandler, KeyPressHandler
{
	private VibeFlowPanel m_rightsPanelForMultiEdit;
	private VibeFlowPanel m_rightsPanelForSingleEdit;
	
	// Data members used with access rights
	private ListBox m_accessRightsListbox;
	private RadioButton m_viewerRb;
	private RadioButton m_editorRb;
	private RadioButton m_contributorRb;

	// Data members used with reshare
	private VerticalPanel m_resharePanelForMultiEdit;
	private ListBox m_canReshareInternalListbox;
	private ListBox m_canReshareExternalListbox;
	private ListBox m_canResharePublicListbox;
	private Label m_canShareLabel;
	private CheckBox m_canShareExternalCkbox;
	private CheckBox m_canShareInternalCkbox;
	private CheckBox m_canSharePublicCkbox;
	private Label m_canReshareInternalLabel;
	private Label m_canReshareExternalLabel;
	private Label m_canResharePublicLabel;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private ArrayList<GwtShareItem> m_listOfShareItems;
	
	// Data members used with share expiration
	private ListBox m_expiresListbox;
	private TextBox m_expiresAfterTextBox;
	private TZDateBox m_dateBox;
	private VibeFlowPanel m_expiresOnPanel;
	private VibeFlowPanel m_expiresAfterPanel;
	
	// Data members used with share note
	private TextArea m_noteTextArea;

	
	private static long MILLISEC_IN_A_DAY = 86400000;
	private static String VIEWER = "viewer";
	private static String EDITOR = "editor";
	private static String CONTRIBUTOR = "contributor";
	private static String LEAVE_UNCHANGED = "leave-unchanged";
	private static String RESHARE_YES = "Reshare-Yes";
	private static String RESHARE_NO = "Reshare-No";
	


	/**
	 * Callback interface to interact with the "Edit Share" dialog asynchronously after it loads. 
	 */
	public interface EditShareDlgClient
	{
		void onSuccess( EditShareDlg esDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditShareDlg(
		boolean autoHide,
		boolean modal,
		int left,
		int top )
	{
		super( autoHide, modal, left, top );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", this, null, null ); 
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "editShareRightsDlg_MainPanel" );
		
		createRightsContent( mainPanel );
		createExpirationContent( mainPanel );
		createNoteContent( mainPanel );
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	private void createExpirationContent( VibeFlowPanel panel )
	{
		VibeFlowPanel mainPanel;
		VibeFlowPanel expiresPanel;
		FlexTable mainTable;
		int col;

		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "editShareDlg_expirationPanel" );

		// Give this panel a fixed width so the dialog doesn't resize after the user
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
			m_expiresListbox = new ListBox( false );
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

		panel.add( mainPanel );
	}
	
	/**
	 * Create the controls needed for the share note.
	 */
	public void createNoteContent( VibeFlowPanel mainPanel )
	{
		FlexTable mainTable;
		int col;
		
		mainTable = new FlexTable();
		mainTable.addStyleName( "editShareNoteDlg_table" );
		mainTable.getRowFormatter().setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
		mainPanel.add( mainTable );
		
		col = 0;
		
		mainTable.setText( 0, col, GwtTeaming.getMessages().editShareNoteDlg_noteLabel() );
		++col;
		
		m_noteTextArea = new TextArea();
		m_noteTextArea.addStyleName( "editShareNoteDlg_TextArea" );
		m_noteTextArea.addStyleName( "editShareNoteDlg_TextAreaBorder" );
		mainTable.setWidget( 0, col, m_noteTextArea );
	}
	
	/**
	 * Create the ui controls needed to edit rights for multiple shares
	 */
	private VibeFlowPanel createRightsContentForMultiEdit()
	{
		GwtTeamingMessages messages;
		HorizontalPanel hPanel;
		Label label;
		VibeFlowPanel panel;
		
		messages = GwtTeaming.getMessages();
		
		panel = new VibeFlowPanel();
		
		hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
		hPanel.setSpacing( 4 );
		
		label = new Label( messages.editShareDlg_accessRightsLabel() );
		hPanel.add( label );
		m_accessRightsListbox = new ListBox( false );
		m_accessRightsListbox.setVisibleItemCount( 1 );
		hPanel.add( m_accessRightsListbox );
		panel.add( hPanel );
		
		// Add the controls needed to define re-share rights
		{
			m_resharePanelForMultiEdit = new VerticalPanel();
			m_resharePanelForMultiEdit.addStyleName( "margintop2" );
			panel.add( m_resharePanelForMultiEdit );
			
			// Add a "Allow the recipient to re-share this item with:" label
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			label = new Label( messages.editShareRightsDlg_CanShareLabel() );
			hPanel.add( label );
			m_resharePanelForMultiEdit.add( hPanel );
			
			// Add the "Reshare with internal users" listbox
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			hPanel.addStyleName( "marginleft1" );
			m_canReshareInternalLabel = new Label( messages.editShareDlg_canReshareInternalLabel() );
			hPanel.add( m_canReshareInternalLabel );
			m_canReshareInternalListbox = new ListBox( false );
			m_canReshareInternalListbox.setVisibleItemCount( 1 );
			m_canReshareInternalListbox.addItem( messages.editShareDlg_yes(), RESHARE_YES );
			m_canReshareInternalListbox.addItem( messages.editShareDlg_no(), RESHARE_NO );
			m_canReshareInternalListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			hPanel.add( m_canReshareInternalListbox );
			m_resharePanelForMultiEdit.add( hPanel );
			
			// Add the "Reshare with external users" listbox
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			hPanel.addStyleName( "marginleft1" );
			m_canReshareExternalLabel = new Label( messages.editShareDlg_canReshareExternalLabel() );
			hPanel.add( m_canReshareExternalLabel );
			m_canReshareExternalListbox = new ListBox( false );
			m_canReshareExternalListbox.setVisibleItemCount( 1 );
			m_canReshareExternalListbox.addItem( messages.editShareDlg_yes(), RESHARE_YES );
			m_canReshareExternalListbox.addItem( messages.editShareDlg_no(), RESHARE_NO );
			m_canReshareExternalListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			hPanel.add( m_canReshareExternalListbox );
			m_resharePanelForMultiEdit.add( hPanel );
			
			// Add the "Reshare with public" listbox
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			hPanel.addStyleName( "marginleft1" );
			m_canResharePublicLabel = new Label( messages.editShareDlg_canResharePublicLabel() );
			hPanel.add( m_canResharePublicLabel );
			m_canResharePublicListbox = new ListBox( false );
			m_canResharePublicListbox.setVisibleItemCount( 1 );
			m_canResharePublicListbox.addItem( messages.editShareDlg_yes(), RESHARE_YES );
			m_canResharePublicListbox.addItem( messages.editShareDlg_no(), RESHARE_NO );
			m_canResharePublicListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			hPanel.add( m_canResharePublicListbox );
			m_resharePanelForMultiEdit.add( hPanel );
		}
		
		return panel;
	}
	
	/**
	 * Create the ui controls needed to edit rights for a single share
	 */
	private VibeFlowPanel createRightsContentForSingleEdit()
	{
		GwtTeamingMessages messages;
		VibeFlowPanel mainPanel;
		VibeFlowPanel rbPanel;
		VibeFlowPanel tmpPanel;
		Label label;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VibeFlowPanel();
		
		// Add an "Access Rights" heading
		label = new Label( messages.editShareDlg_accessRightsLabel() );
		label.addStyleName( "smalltext" );
		mainPanel.add( label );
		
		// Create a panel for the radio buttons to live in.
		rbPanel = new VibeFlowPanel();
		rbPanel.addStyleName( "editShareRightsDlg_RbPanel" );
		
		m_viewerRb = new RadioButton( "shareRights", messages.editShareRightsDlg_ViewerLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_viewerRb );
		rbPanel.add( tmpPanel );

		m_editorRb = new RadioButton( "shareRights", messages.editShareRightsDlg_EditorLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_editorRb );
		rbPanel.add( tmpPanel );
		
		m_contributorRb = new RadioButton( "shareRights", messages.editShareRightsDlg_ContributorLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_contributorRb );
		rbPanel.add( tmpPanel );
		
		mainPanel.add( rbPanel );
		
		rbPanel = new VibeFlowPanel();
		rbPanel.addStyleName( "editShareRightsDlg_RbPanel" );
		
		// Add the "Allow the recipient to re-share this item with:"
		m_canShareLabel = new Label( messages.editShareRightsDlg_CanShareLabel() );
		m_canShareLabel.addStyleName( "margintop2" );
		m_canShareLabel.addStyleName( "smalltext" );
		mainPanel.add( m_canShareLabel );
		
		// Add the "allow share internal checkbox.
		m_canShareInternalCkbox = new CheckBox( messages.editShareRightsDlg_CanShareInternalLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_canShareInternalCkbox );
		rbPanel.add( tmpPanel );
		
		// Add the "allow share external" checkbox.
		m_canShareExternalCkbox = new CheckBox( messages.editShareRightsDlg_CanShareExternalLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_canShareExternalCkbox );
		rbPanel.add( tmpPanel );
		
		// Add the "allow share public" checkbox.
		m_canSharePublicCkbox = new CheckBox( messages.editShareRightsDlg_CanSharePublicLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_canSharePublicCkbox );
		rbPanel.add( tmpPanel );
		
		mainPanel.add( rbPanel );
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	public void createRightsContent( VibeFlowPanel panel )
	{
		m_rightsPanelForMultiEdit = createRightsContentForMultiEdit();
		m_rightsPanelForMultiEdit.setVisible( false );
		panel.add( m_rightsPanelForMultiEdit );
		
		m_rightsPanelForSingleEdit = createRightsContentForSingleEdit();
		panel.add( m_rightsPanelForSingleEdit );
	}
	
	/**
	 * Show/hide the appropriate controls based on the selected expiration type.
	 */
	private void danceDlg( boolean setFocus )
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
	 * 
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		// Do we have a share item we are working with?
		if ( m_listOfShareItems != null )
		{
			// Yes
			for ( GwtShareItem nextShareItem : m_listOfShareItems )
			{
				saveShareRights( nextShareItem );
				saveExpirationValue( nextShareItem );
				saveNote( nextShareItem );
				
				nextShareItem.setIsDirty( true );
			}
			
			// Do we have a handler we should call?
			if ( m_editSuccessfulHandler != null )
				m_editSuccessfulHandler.editSuccessful( Boolean.TRUE );
		}

		return true;
	}

	/**
	 * Get the text entered by the user.
	 */
	@Override
	public Object getDataFromDlg()
	{
		if ( validateExpirationValue() == false )
			return null;
		
		return Boolean.TRUE;
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
		return null;
	}

	/**
	 * Return the list of GwtShareItems we are working with.
	 */
	public ArrayList<GwtShareItem> getListOfShareItems()
	{
		return m_listOfShareItems;
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
		danceDlg( true );
	}

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		// Update the caption
		{
			// Are we editing more than 1 share?
			if ( listOfShareItems.size() == 1 )
			{
				GwtShareItem shareItem;
				
				// No
				shareItem = listOfShareItems.get( 0 );
				setCaption( GwtTeaming.getMessages().editShareDlg_captionEdit1( shareItem.getRecipientName() ) );
			}
			else
			{
				setCaption( GwtTeaming.getMessages().editShareDlg_captionEditMultiple( listOfShareItems.size() ) );
			}
		}
		
		m_listOfShareItems = listOfShareItems;
		m_editSuccessfulHandler = editSuccessfulHandler;

		initRightsControls( listOfShareItems, highestRightsPossible );
		initExpirationControls( listOfShareItems );
		initNoteControls( listOfShareItems );

		danceDlg( false );
	}
	
	/**
	 * Initialize the controls dealing with expiration
	 */
	private void initExpirationControls( ArrayList<GwtShareItem> listOfShares )
	{
		GwtTeamingMessages messages;
		
		if ( listOfShares == null || listOfShares.size() == 0 )
			return;

		messages = GwtTeaming.getMessages();
		
		m_expiresListbox.clear();
		
		m_expiresListbox.addItem( 
							messages.shareExpirationDlg_expiresNever(),
							ShareExpirationType.NEVER.toString() );
		m_expiresListbox.addItem(
							messages.shareExpirationDlg_expiresOn(),
							ShareExpirationType.ON_DATE.toString() );
		m_expiresListbox.addItem(
							messages.shareExpirationDlg_expiresAfter(),
							ShareExpirationType.AFTER_DAYS.toString() );
	
		if ( listOfShares.size() > 1 )
		{
			m_expiresListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			m_expiresListbox.setSelectedIndex( m_expiresListbox.getItemCount()-1 );
		}
		else
		{
			ShareExpirationValue expirationValue;
			ShareExpirationType expirationType;
			
			expirationValue = listOfShares.get( 0 ).getShareExpirationValue();
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
	 * Initialize the controls dealing with rights
	 */
	public void initRightsControls(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible )
	{
		if ( highestRightsPossible == null )
			highestRightsPossible = new ShareRights();
		
		// Are we only dealing with 1 share item?
		if ( listOfShareItems.size() == 1 )
		{
			m_rightsPanelForMultiEdit.setVisible( false );
			m_rightsPanelForSingleEdit.setVisible( true );
			initRightsControlsForSingleEdit( listOfShareItems.get( 0 ), highestRightsPossible );
		}
		else
		{
			m_rightsPanelForSingleEdit.setVisible( false );
			m_rightsPanelForMultiEdit.setVisible( true );
			initRightsControlsForMultiEdit( listOfShareItems, highestRightsPossible );
		}
	}
	
	/**
	 * Initialize the controls dealing with rights for multiple shares
	 */
	private void initRightsControlsForMultiEdit(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible )
	{
		GwtTeamingMessages messages;
		boolean entityIsBinder;

		messages = GwtTeaming.getMessages();
		
		if ( highestRightsPossible == null )
			highestRightsPossible = new ShareRights();
		
		entityIsBinder = true;
		
		// See if every entity is a binder
		for ( GwtShareItem nextShareItem : listOfShareItems )
		{
			entityIsBinder = nextShareItem.getEntityId().isBinder();
			if ( entityIsBinder == false )
				break;
		}

		// Add the appropriate options to the "access rights" listbox.
		{
			
			m_accessRightsListbox.clear();
			
			switch ( highestRightsPossible.getAccessRights() )
			{
			case CONTRIBUTOR:
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_ViewerLabel(), VIEWER );
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_EditorLabel(), EDITOR );
				
				// Add "contributor" only if we are dealing with a binder.
				if ( entityIsBinder )
					m_accessRightsListbox.addItem( messages.editShareRightsDlg_ContributorLabel(), CONTRIBUTOR );
	
				break;
				
			case EDITOR:
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_ViewerLabel(), VIEWER );
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_EditorLabel(), EDITOR );
				break;
				
			case VIEWER:
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_ViewerLabel(), VIEWER );
				break;
				
			default:
				break;
			}
	
			// Add an "Leave unchanged" option.
			m_accessRightsListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			GwtClientHelper.selectListboxItemByValue( m_accessRightsListbox, LEAVE_UNCHANGED );
		}
		
		// Update the controls dealing with re-share
		{
			boolean canShareForward;

			canShareForward = highestRightsPossible.getCanShareForward();
			
			m_resharePanelForMultiEdit.setVisible( canShareForward );
			
			if ( canShareForward )
			{
				boolean canShare;
				
				// Show/hide the "share internal" listbox depending on whether the user has "share internal" rights.
				canShare = highestRightsPossible.getCanShareWithInternalUsers();
				m_canReshareInternalLabel.setVisible( canShare );
				m_canReshareInternalListbox.setVisible( canShare );
				if ( canShare )
					GwtClientHelper.selectListboxItemByValue( m_canReshareInternalListbox, LEAVE_UNCHANGED );

				// Show/hide the "share external" listbox depending on whether the user has "share external" rights.
				canShare = highestRightsPossible.getCanShareWithExternalUsers();
				m_canReshareExternalLabel.setVisible( canShare );
				m_canReshareExternalListbox.setVisible( canShare );
				if ( canShare )
					GwtClientHelper.selectListboxItemByValue( m_canReshareExternalListbox, LEAVE_UNCHANGED );

				// Show/hide the "share public" listbox depending on whether the user has "share public" rights.
				canShare = highestRightsPossible.getCanShareWithPublic();
				m_canResharePublicLabel.setVisible( canShare );
				m_canResharePublicListbox.setVisible( canShare );
				if ( canShare )
					GwtClientHelper.selectListboxItemByValue( m_canResharePublicListbox, LEAVE_UNCHANGED );
			}
		}
	}
	
	/**
	 * Initialize the controls dealing with rights for a single share
	 */
	private void initRightsControlsForSingleEdit(
		GwtShareItem shareItem,
		ShareRights highestRightsPossible )
	{
		ShareRights shareRights;
		boolean entityIsBinder;
		boolean canShareForward;

		if ( highestRightsPossible == null )
			highestRightsPossible = new ShareRights();
		
		// Get the share rights from the one share item we are working with.
		shareRights = shareItem.getShareRights();
		entityIsBinder = shareItem.getEntityId().isBinder();

		m_viewerRb.setVisible( false );
		m_editorRb.setVisible( false );
		m_contributorRb.setVisible( false );
		
		m_viewerRb.setValue( false );
		m_editorRb.setValue( false );
		m_contributorRb.setValue( false );
		
		switch ( shareRights.getAccessRights() )
		{
		case CONTRIBUTOR:
			m_contributorRb.setValue( true );
			break;
		
		case EDITOR:
			m_editorRb.setValue( true );
			break;
			
		case VIEWER:
		default:
			m_viewerRb.setValue( true );
			break;
		}
		
		// Hide/show the controls for the rights the user can/cannot give
		switch ( highestRightsPossible.getAccessRights() )
		{
		case CONTRIBUTOR:
			m_viewerRb.setVisible( true );
			m_editorRb.setVisible( true );
			
			// Show the "contributor" radio button only if we are dealing with a binder.
			m_contributorRb.setVisible( entityIsBinder );
			break;
			
		case EDITOR:
			m_viewerRb.setVisible( true );
			m_editorRb.setVisible( true );
			m_contributorRb.setVisible( false );
			break;
			
		case VIEWER:
		default:
			m_viewerRb.setVisible( true );
			m_editorRb.setVisible( false );
			m_contributorRb.setVisible( false );
			break;
		}
		
		canShareForward = highestRightsPossible.getCanShareForward();
		
		m_canShareLabel.setVisible( canShareForward );
		
		// Show/hide the "share internal" checkbox depending on whether the user has "share internal" rights.
		m_canShareInternalCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithInternalUsers() );
		m_canShareInternalCkbox.setValue( shareRights.getCanShareWithInternalUsers() );
		
		// Show/hide the "share external" checkbox depending on whether the user has "share external" rights.
		m_canShareExternalCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithExternalUsers() );
		m_canShareExternalCkbox.setValue( shareRights.getCanShareWithExternalUsers() );
		
		// Show/hide the "share public" checkbox depending on whether the user has "share public" rights.
		m_canSharePublicCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithPublic() );
		m_canSharePublicCkbox.setValue( shareRights.getCanShareWithPublic() );
	}
	
	/**
	 * Initialize the controls that deal with the share note.
	 */
	private void initNoteControls( ArrayList<GwtShareItem> listOfShareItems )
	{
		String note;
		
		// Are we only dealing with 1 share item?
		if ( listOfShareItems.size() == 1 )
		{
			GwtShareItem shareItem;
			
			// Get the share rights from the one share item we are working with.
			shareItem = listOfShareItems.get( 0 );
			note = shareItem.getComments();
		}
		else
		{
			note = GwtTeaming.getMessages().editShareDlg_undefinedNote();
		}

		m_noteTextArea.setValue( note );
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
	 * Update the GwtShareItem with the expiration value from the dialog
	 */
	private void saveExpirationValue( GwtShareItem shareItem )
	{
		ShareExpirationType expirationType;

		expirationType = getSelectedExpirationType();
		
		if ( expirationType != null )
		{
			ShareExpirationValue value;

			value = new ShareExpirationValue();

			if ( expirationType == ShareExpirationType.AFTER_DAYS )
			{
				Long days;
				
				// The user selected "after days".
				// Did they enter the number of days?
				days = getDaysFromDlg();
				value.setValue( days );
			}
			else if ( expirationType == ShareExpirationType.ON_DATE )
			{
				Long date;
				
				// The user selected "on date".
				// Did they enter a date?
				date = getExpirationDateFromDlg();
				value.setValue( date );
			}
			
			value.setType( expirationType );
			shareItem.setShareExpirationValue( value );
		}
	}
		
	
	/**
	 * Update the GwtShareItem with the share rights from the dialog
	 */
	private void saveShareRights( GwtShareItem shareItem )
	{
		// Are the multi-edit controls visible?
		if ( m_rightsPanelForMultiEdit.isVisible() )
		{
			// Yes
			saveShareRightsForMultiEdit( shareItem );
		}
		else
		{
			// No
			saveShareRightsForSingleEdit( shareItem );
		}
	}
	
	/**
	 * Update the GwtShareItem with the share rights from multi-edit controls
	 */
	private void saveShareRightsForMultiEdit( GwtShareItem shareItem )
	{
		ShareRights shareRights;
		
		shareRights = shareItem.getShareRights();
		
		// Save the access rights.
		{
			int selectedIndex;

			selectedIndex = m_accessRightsListbox.getSelectedIndex();
			if ( selectedIndex >= 0 )
			{
				String value;
				
				value = m_accessRightsListbox.getValue( selectedIndex );
				if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
				{
					AccessRights accessRights;

					accessRights = ShareRights.AccessRights.UNKNOWN;

					if ( value.equalsIgnoreCase( VIEWER ) )
						accessRights = ShareRights.AccessRights.VIEWER;
					else if ( value.equalsIgnoreCase( EDITOR ) )
						accessRights = ShareRights.AccessRights.EDITOR;
					else if ( value.equalsIgnoreCase( CONTRIBUTOR ) )
						accessRights = ShareRights.AccessRights.CONTRIBUTOR;

					shareRights.setAccessRights( accessRights );
				}
			}
		}

		// Save the re-share rights
		if ( m_resharePanelForMultiEdit.isVisible() )
		{
			int selectedIndex;
			boolean canShareForward;
			boolean setCanShareForward;
			
			setCanShareForward = false;
			canShareForward = false;
			
			if ( m_canReshareInternalListbox.isVisible() )
			{
				selectedIndex = m_canReshareInternalListbox.getSelectedIndex();
				if ( selectedIndex >= 0 )
				{
					String value;
					
					value = m_canReshareInternalListbox.getValue( selectedIndex );
					if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
					{
						setCanShareForward = true;

						if ( value.equalsIgnoreCase( RESHARE_YES ) )
						{
							canShareForward = true;
							shareRights.setCanShareWithInternalUsers( true );
						}
						else
							shareRights.setCanShareWithInternalUsers( false );
					}
				}
			}
	
			if ( m_canReshareExternalListbox.isVisible() )
			{
				selectedIndex = m_canReshareExternalListbox.getSelectedIndex();
				if ( selectedIndex >= 0 )
				{
					String value;
					
					value = m_canReshareExternalListbox.getValue( selectedIndex );
					if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
					{
						setCanShareForward = true;

						if ( value.equalsIgnoreCase( RESHARE_YES ) )
						{
							canShareForward = true;
							shareRights.setCanShareWithExternalUsers( true );
						}
						else
							shareRights.setCanShareWithExternalUsers( false );
					}
				}
			}
	
			if ( m_canResharePublicListbox.isVisible() )
			{
				selectedIndex = m_canResharePublicListbox.getSelectedIndex();
				if ( selectedIndex >= 0 )
				{
					String value;
					
					value = m_canResharePublicListbox.getValue( selectedIndex );
					if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
					{
						setCanShareForward = true;

						if ( value.equalsIgnoreCase( RESHARE_YES ) )
						{
							canShareForward = true;
							shareRights.setCanShareWithPublic( true );
						}
						else
							shareRights.setCanShareWithPublic( false );
					}
				}
			}
	
			if ( setCanShareForward )
				shareRights.setCanShareForward( canShareForward );
		}
	}
	
	/**
	 * Update the GwtShareItem with the share rights from single-edit controls
	 */
	private void saveShareRightsForSingleEdit( GwtShareItem shareItem )
	{
		AccessRights accessRights;
		ShareRights shareRights;
		boolean canShareForward;
		
		shareRights = shareItem.getShareRights();
		accessRights = ShareRights.AccessRights.UNKNOWN;
		
		if ( m_viewerRb.isVisible() && m_viewerRb.getValue() == true )
			accessRights = ShareRights.AccessRights.VIEWER;
		else if ( m_editorRb.isVisible() && m_editorRb.getValue() == true )
			accessRights = ShareRights.AccessRights.EDITOR;
		else if ( m_contributorRb.isVisible() && m_contributorRb.getValue() == true )
			accessRights = ShareRights.AccessRights.CONTRIBUTOR;
		
		shareRights.setAccessRights( accessRights );

		canShareForward = false;
		
		if ( m_canShareInternalCkbox.isVisible() && m_canShareInternalCkbox.getValue() == true )
		{
			canShareForward = true;
			shareRights.setCanShareWithInternalUsers( true );
		}
		else
			shareRights.setCanShareWithInternalUsers( false );

		if ( m_canShareExternalCkbox.isVisible() && m_canShareExternalCkbox.getValue() == true )
		{
			canShareForward = true;
			shareRights.setCanShareWithExternalUsers( true );
		}
		else
			shareRights.setCanShareWithExternalUsers( false );

		if ( m_canSharePublicCkbox.isVisible() && m_canSharePublicCkbox.getValue() == true )
		{
			canShareForward = true;
			shareRights.setCanShareWithPublic( true );
		}
		else
			shareRights.setCanShareWithPublic( false );

		shareRights.setCanShareForward( canShareForward );
		
		shareItem.setIsDirty( true );
	}
	
	/**
	 * Update the GwtShareItem with the note from the dialog
	 */
	private void saveNote( GwtShareItem shareItem )
	{
		String newNote;
		
		newNote = m_noteTextArea.getValue();
		if ( newNote != null && newNote.equalsIgnoreCase( GwtTeaming.getMessages().editShareDlg_undefinedNote() ) == false )
			shareItem.setComments( m_noteTextArea.getValue() );
	}

	/**
	 * Validate the expiration value entered by the user.
	 */
	private boolean validateExpirationValue()
	{
		ShareExpirationType expirationType;

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
				return false;
			}
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
	
	/**
	 * Loads the EditShareRightsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final EditShareDlgClient esDlgClient )
	{
		GWT.runAsync( EditShareDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditShareRightsDlg() );
				if ( esDlgClient != null )
				{
					esDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditShareDlg esDlg;
				
				esDlg = new EditShareDlg( autoHide, modal, left, top );
				esDlgClient.onSuccess( esDlg );
			}
		});
	}
}
