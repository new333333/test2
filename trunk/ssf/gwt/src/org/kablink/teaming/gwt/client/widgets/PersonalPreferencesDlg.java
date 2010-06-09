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
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author jwootton
 *
 */
public class PersonalPreferencesDlg extends DlgBox
	implements KeyPressHandler
{
	private ListBox m_entryDisplayStyleListbox;
	private TextBox m_numEntriesPerPageTxtBox;
	private CheckBox m_showToolTips;
	private Anchor m_editorOverridesAnchor;
	

	/**
	 * 
	 */
	public PersonalPreferencesDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().personalPreferencesDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}// end PersonalPreferencesDlg()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
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
		
		// Create the controls for "Entry display style"
		{
			table.setText( nextRow, 0, messages.entryDisplayStyleLabel() );
			
			// Create a listbox that will hold all the possible values for the "Entry Display Style".
			m_entryDisplayStyleListbox = new ListBox( false );
			m_entryDisplayStyleListbox.setVisibleItemCount( 1 );
			m_entryDisplayStyleListbox.addItem( messages.showEntriesAsAnOverlay(), "iframe" );
			m_entryDisplayStyleListbox.addItem( messages.showEntriesInNewPage(), "newpage" );
			m_entryDisplayStyleListbox.addItem( messages.showEntriesInPopupWnd(), "popup" );

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
			table.setWidget( nextRow, 1, m_numEntriesPerPageTxtBox );
			++nextRow;
		}
		
		// Create the controls for "show tooltips".
		{
			m_showToolTips = new CheckBox( messages.showToolTips() );
			table.setWidget( nextRow, 0, m_showToolTips );
			++nextRow;
		}
		
		// Create a link the user can click on to invoke the "Define editor overrides" dialog.
		{
			ClickHandler clickHandler;
			MouseOverHandler mouseOverHandler;
			MouseOutHandler mouseOutHandler;
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
			m_editorOverridesAnchor.addStyleName( "subhead-control-bg1" );
			m_editorOverridesAnchor.addStyleName( "roundcornerSM" );
			
			// Add a clickhandler to the "Clear branding" link.  When the user clicks on the link we
			// will clear all branding information.
			clickHandler = new ClickHandler()
			{
				/**
				 * Clear all branding information.
				 */
				public void onClick( ClickEvent event )
				{
					invokeEditorOverridesDlg();
				}//end onClick()
			};
			m_editorOverridesAnchor.addClickHandler( clickHandler );
			
			// Add a mouse-over handler
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
				public void onMouseOver( MouseOverEvent event )
				{
					Widget widget;
					
					widget = (Widget)event.getSource();
					widget.removeStyleName( "subhead-control-bg1" );
					widget.addStyleName( "subhead-control-bg2" );
				}// end onMouseOver()
			};
			m_editorOverridesAnchor.addMouseOverHandler( mouseOverHandler );

			// Add a mouse-out handler
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				public void onMouseOut( MouseOutEvent event )
				{
					Widget widget;
					
					// Remove the background color we added to the anchor when the user moved the mouse over the anchor.
					widget = (Widget)event.getSource();
					widget.removeStyleName( "subhead-control-bg2" );
					widget.addStyleName( "subhead-control-bg1" );
				}// end onMouseOut()
			};
			m_editorOverridesAnchor.addMouseOutHandler( mouseOutHandler );

			table.setWidget( nextRow, 0, m_editorOverridesAnchor );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtPersonalPreferences obj.
	 */
	public Object getDataFromDlg()
	{
		GwtPersonalPreferences personalPrefs;
		String displayStyle;
		Boolean value;
		
		personalPrefs = new GwtPersonalPreferences();
		
		// Get the entry display style from the dialog.
		displayStyle = getEntryDisplayStyleFromDlg();
		personalPrefs.setDisplayStyle( displayStyle );
		
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
		
		// Get the value of "Show tooltips".
		value = m_showToolTips.getValue();
		personalPrefs.setShowToolTips( value.booleanValue() );
		
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
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
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
		
		m_numEntriesPerPageTxtBox.setValue( String.valueOf( personalPrefs.getNumEntriesPerPage() ) );
		
		m_showToolTips.setValue( personalPrefs.getShowToolTips() );
		
		// Show/hide the "Define editor overrides..." button depending on whether or not
		// "editor overrides" are supported.
		m_editorOverridesAnchor.setVisible( personalPrefs.isEditorOverrideSupported() );
	}// end init()
	
	
	/**
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


	/**
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
	public void onKeyPress( KeyPressEvent event )
	{
        int keyCode;

        // Get the key the user pressed
        keyCode = event.getCharCode();
        
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
	}// end onKeyPress()

}// end PersonalPreferencesDlg
