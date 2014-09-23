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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtFileLinkAction;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ?
 *  
 * @author jwootton
 */
public class PersonalPreferencesDlg extends DlgBox
	implements KeyPressHandler
{
	private boolean	m_isFilr;
	private boolean m_publicSharesActive;
	private ListBox m_entryDisplayStyleListbox;
	private ListBox m_fileLinkActionListbox;
	private TextBox m_numEntriesPerPageTxtBox;
	private Anchor m_editorOverridesAnchor;
	private CheckBox m_hidePublicCollectionCkbox;
	

	/**
	 * 
	 */
	public PersonalPreferencesDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );
		
		m_isFilr = GwtClientHelper.isLicenseFilr();
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().personalPreferencesDlgHeader(), editSuccessfulHandler, editCanceledHandler, null );
	}// end PersonalPreferencesDlg()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlexTable table;
		int nextRow;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		nextRow = 0;

		{
			// Create the controls for "Entry display style".  (Note
			// that they're not shown for Filr.)
			if ( ! m_isFilr )
			{
				table.setText( nextRow, 0, messages.entryDisplayStyleLabel() );
			}
			
			// Create a listbox that will hold all the possible values for the "Entry Display Style".
			m_entryDisplayStyleListbox = new ListBox( false );
			m_entryDisplayStyleListbox.setVisibleItemCount( 1 );
			m_entryDisplayStyleListbox.addItem( messages.showEntriesInNewPage(), "newpage" );
			m_entryDisplayStyleListbox.addItem( messages.showEntriesAsAnOverlay(), "iframe" );
			m_entryDisplayStyleListbox.setVisible( ! m_isFilr );

			table.setWidget( nextRow, 1, m_entryDisplayStyleListbox );
			++nextRow;
		}
		
		// Create the controls for "Number of entries per page to display"
		{
			table.setText( nextRow, 0, messages.numEntriesPerPageLabel() );
			
			// Create a textbox for the user to enter the number of entries.
			m_numEntriesPerPageTxtBox = new TextBox();
			m_numEntriesPerPageTxtBox.addKeyPressHandler( this );
			m_numEntriesPerPageTxtBox.setVisibleLength( 3 );
			m_numEntriesPerPageTxtBox.setMaxLength( 6 );
			table.setWidget( nextRow, 1, m_numEntriesPerPageTxtBox );
			++nextRow;
		}
		
		// Create the controls for "File Link Action"
		if ( GwtClientHelper.isLicenseFilr() )
		{
			table.setText( nextRow, 0, messages.fileLinkActionLabel() );
			
			// Create a select widget the user can select the options from.
			m_fileLinkActionListbox = new ListBox();
			m_fileLinkActionListbox.setVisibleItemCount( 1 );

			table.setWidget( nextRow, 1, m_fileLinkActionListbox );
			++nextRow;
		}
		
		// Create the checkbox for hiding the public collection.
		if ( ( ! ( GwtClientHelper.isGuestUser() ) ) && ( ! ( GwtClientHelper.isExternalUser() ) ) )
		{
			FlowPanel panel = new FlowPanel();
			m_hidePublicCollectionCkbox = new CheckBox( messages.hidePublicCollectionLabel() );
			panel.add( m_hidePublicCollectionCkbox );
			table.getFlexCellFormatter().setColSpan( nextRow, 0, 2 );
			table.setWidget( nextRow, 0, panel );
			++nextRow;
		}
		
		// Create a link the user can click on to invoke the "Define editor overrides" dialog.
		{
			ClickHandler clickHandler;
			Label spacer;
			
			// Add an empty row to add some space between the "use advanced branding" radio button and the "background image" listbox.
			spacer = new Label( " " );
			spacer.addStyleName( "marginTop5px" );
			table.setWidget( nextRow, 0, spacer );
			++nextRow;
			
			m_editorOverridesAnchor = new Anchor( messages.editorOverridesLabel() );
			m_editorOverridesAnchor.setTitle( messages.editorOverridesLabel() );
			m_editorOverridesAnchor.addStyleName( "editorOverridesLink1" );
			m_editorOverridesAnchor.addStyleName( "editorOverridesLink2" );
			m_editorOverridesAnchor.addStyleName( "roundcornerSM" );
			
			// Add a clickhandler to the "Clear branding" link.  When the user clicks on the link we
			// will clear all branding information.
			clickHandler = new ClickHandler()
			{
				/**
				 * Clear all branding information.
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					invokeEditorOverridesDlg();
				}//end onClick()
			};
			m_editorOverridesAnchor.addClickHandler( clickHandler );
			
//			// Add a mouse-over handler
//			mouseOverHandler = new MouseOverHandler()
//			{
//				/**
//				 * 
//				 */
//				public void onMouseOver( MouseOverEvent event )
//				{
//					Widget widget;
//					
//					widget = (Widget)event.getSource();
//					widget.removeStyleName( "subhead-control-bg1" );
//					widget.addStyleName( "subhead-control-bg2" );
//				}// end onMouseOver()
//			};
//			m_editorOverridesAnchor.addMouseOverHandler( mouseOverHandler );
//
//			// Add a mouse-out handler
//			mouseOutHandler = new MouseOutHandler()
//			{
//				/**
//				 * 
//				 */
//				public void onMouseOut( MouseOutEvent event )
//				{
//					Widget widget;
//					
//					// Remove the background color we added to the anchor when the user moved the mouse over the anchor.
//					widget = (Widget)event.getSource();
//					widget.removeStyleName( "subhead-control-bg2" );
//					widget.addStyleName( "subhead-control-bg1" );
//				}// end onMouseOut()
//			};
//			m_editorOverridesAnchor.addMouseOutHandler( mouseOutHandler );

			table.getFlexCellFormatter().setColSpan( nextRow, 0, 2 );
			table.setWidget( nextRow, 0, m_editorOverridesAnchor );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtPersonalPreferences obj.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtPersonalPreferences personalPrefs;
		String displayStyle;
		GwtFileLinkAction fla;
		
		personalPrefs = new GwtPersonalPreferences();
		
		// Get the entry display style from the dialog.
		displayStyle = getEntryDisplayStyleFromDlg();
		personalPrefs.setDisplayStyle( displayStyle );
		
		// Get the file link action from the dialog.
		fla = getFileLinkActionFromDlg();
		personalPrefs.setFileLinkAction(fla);
		
		// Get the hide public collection from the dialog.
		personalPrefs.setPublicSharesActive( m_publicSharesActive );
		personalPrefs.setHidePublicCollection( getHidePublicCollectionFromDlg() );
		
		// Get the value of "number of entries per page"
		{
			String tmpValue;
			int num;
			
			tmpValue = m_numEntriesPerPageTxtBox.getValue();
			if ( tmpValue == null || tmpValue.length() == 0 )
			{
				// Tell the user to enter the number of entries per page.
				Window.alert( GwtTeaming.getMessages().numEntriesPerPageCannotBeBlank() );
				m_numEntriesPerPageTxtBox.setFocus( true );
				
				return null;
			}
			
			try
			{
				num = Integer.parseInt( tmpValue );
				
				// Make sure the number is greater than 0.
				if ( num > 0 )
					personalPrefs.setNumEntriesPerPage( num );
				else
				{
					Window.alert( GwtTeaming.getMessages().numEntriesPerPageInvalidNum() );
					m_numEntriesPerPageTxtBox.setFocus( true );

					return null;
				}
				
			}
			catch (NumberFormatException nfe)
			{
				Window.alert( GwtTeaming.getMessages().numEntriesPerPageInvalidNum() );
				m_numEntriesPerPageTxtBox.setFocus( true );

				return null;
			}
		}
		
		return personalPrefs;
	}// end getDataFromDlg()
	
	
	/**
	 * Get the selected value for "entry display style"
	 */
	private String getEntryDisplayStyleFromDlg()
	{
		int index;
		String displayStyle;
		
		index = m_entryDisplayStyleListbox.getSelectedIndex();
		if ( index == -1 )
			index = 0;
		
		displayStyle = m_entryDisplayStyleListbox.getValue( index );
		return displayStyle;
	}// end getEntryDisplayStyleFromDlg()
	
	
	/**
	 * Get the selected value for "file link action"
	 */
	private GwtFileLinkAction getFileLinkActionFromDlg()
	{
		if ( GwtClientHelper.isLicenseFilr() )
		{
			int index;
			GwtFileLinkAction reply;
			
			index = m_fileLinkActionListbox.getSelectedIndex();
			if ( index == -1 )
				index = 0;
			
			reply = GwtFileLinkAction.getEnum( Integer.parseInt( m_fileLinkActionListbox.getValue( index ) ) );
			return reply;
		}
		
		return GwtFileLinkAction.VIEW_DETAILS;
	}// end getEntryDisplayStyleFromDlg()

	/*
	 * Returns a Boolean indicating the state of the user's public
	 * collection.
	 */
	private Boolean getHidePublicCollectionFromDlg() {
		Boolean reply;
		if ( ( null != m_hidePublicCollectionCkbox ) && m_hidePublicCollectionCkbox.isVisible() )
		     reply = m_hidePublicCollectionCkbox.getValue();
		else reply = null;
		return reply;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given personal preferences.
	 */
	public void init( GwtPersonalPreferences personalPrefs )
	{
		initEntryDisplayStyleControls( personalPrefs );
		initFileLinkActionControls( personalPrefs );
		initHidePublicCollectionControls( personalPrefs );
		
		m_numEntriesPerPageTxtBox.setValue( String.valueOf( personalPrefs.getNumEntriesPerPage() ) );
		
		// Show/hide the "Define editor overrides..." button depending on whether or not
		// "editor overrides" are supported.
		m_editorOverridesAnchor.setVisible( personalPrefs.isEditorOverrideSupported() );

	}// end init()
	
	
	/*
	 * Initialize the controls used with "Entry display style"
	 */
	private void initEntryDisplayStyleControls( GwtPersonalPreferences personalPrefs )
	{
		int index;
		
		m_entryDisplayStyleListbox.setSelectedIndex( -1 );
		
		// Select the appropriate item in the "display style" listbox.
		index = GwtClientHelper.selectListboxItemByValue( m_entryDisplayStyleListbox, personalPrefs.getDisplayStyle() );
		
		// Did we select an item in the listbox?
		if ( index == -1 )
		{
			// No
			m_entryDisplayStyleListbox.setSelectedIndex( 0 );
		}
	}// end initEntryDisplayStyleControls()


	/*
	 * Initialize the controls used with "File Link Action"
	 */
	private void initFileLinkActionControls( GwtPersonalPreferences personalPrefs )
	{
		if ( GwtClientHelper.isLicenseFilr() )
		{
			m_fileLinkActionListbox.clear();
			GwtTeamingMessages messages = GwtTeaming.getMessages();
			boolean canDownload = personalPrefs.canDownload();
			if (canDownload) m_fileLinkActionListbox.addItem( messages.fileLinkActionOption_Download(),             String.valueOf( GwtFileLinkAction.DOWNLOAD.ordinal()                ) );
			                 m_fileLinkActionListbox.addItem( messages.fileLinkActionOption_ViewDetails(),          String.valueOf( GwtFileLinkAction.VIEW_DETAILS.ordinal()            ) );
			                 m_fileLinkActionListbox.addItem( messages.fileLinkActionOption_ViewHtmlElseDetails(),  String.valueOf( GwtFileLinkAction.VIEW_HTML_ELSE_DETAILS.ordinal()  ) );
			if (canDownload) m_fileLinkActionListbox.addItem( messages.fileLinkActionOption_ViewHtmlElseDownload(), String.valueOf( GwtFileLinkAction.VIEW_HTML_ELSE_DOWNLOAD.ordinal() ) );
			
			m_fileLinkActionListbox.setSelectedIndex( -1 );
			
			// Select the appropriate item in the "file link action" listbox.
			int index = GwtClientHelper.selectListboxItemByValue( m_fileLinkActionListbox, String.valueOf( personalPrefs.getFileLinkAction().ordinal() ) );
			
			// Did we select an item in the listbox?
			if ( index == -1 )
			{
				// No
				m_fileLinkActionListbox.setSelectedIndex( 0 );
			}
		}
	}// end initFileLinkActionControls()
	
	
	/*
	 * Initialize the controls used with "Hide Public Collection"
	 */
	private void initHidePublicCollectionControls( GwtPersonalPreferences personalPrefs )
	{
		m_publicSharesActive = personalPrefs.publicSharesActive();
		
		if ( null == m_hidePublicCollectionCkbox )
		{
			return;
		}
		
		if ( ! m_publicSharesActive )
		{
			m_hidePublicCollectionCkbox.setVisible( false );
		}
		
		Boolean hidePublicCollection = personalPrefs.getHidePublicCollection();
		m_hidePublicCollectionCkbox.setValue( (null != hidePublicCollection ) && hidePublicCollection );
	}


	/*
	 * Invoke the "Editor Overrides" dialog
	 */
	private void invokeEditorOverridesDlg()
	{
		GwtClientHelper.jsInvokeDefineEditorOverridesDlg();
	}// end invokeEditorOverridesDlg()
	
	
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
        
        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
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
	}// end onKeyPress()
}// end PersonalPreferencesDlg
