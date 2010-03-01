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
package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.OnSelectHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class CustomJspWidgetDlgBox extends DlgBox
	implements KeyPressHandler, OnSelectHandler
{
	private TextBox m_jspNameTxtBox = null;
	private CheckBox m_assocFolderCkBox = null;
	private CheckBox m_assocEntryCkBox = null;
	
	// The following data members are used if the user has checked the "Associate a folder with this custom jsp"
	private String m_folderId = null;
	private Panel m_selectFolderPanel = null;
	private FindCtrl m_folderFindCtrl = null;
	private CheckBox m_showFolderTitleCkBox = null;
	private TextBox m_numEntriesToShowTxtBox = null;
	private InlineLabel m_currentFolderNameLabel = null;
	
	// The following data members are used if the user has checked the "Associate an entry with this custom jsp"
	private String m_entryId = null;
	private Panel m_selectEntryPanel = null;
	private FindCtrl m_entryFindCtrl = null;
	private CheckBox m_showEntryTitleCkBox = null;
	private InlineLabel m_currentEntryNameLabel = null;
	
	/**
	 * 
	 */
	public CustomJspWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		CustomJspProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().customJspProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end CustomJspWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@SuppressWarnings("unchecked")
	public Panel createContent( PropertiesObj props )
	{
		CustomJspProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		FlexTable		table;
		
		properties = (CustomJspProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and edit control for "Custom Jsp Name"
		table = new FlexTable();
		table.setCellSpacing( 2 );
		label = new Label( GwtTeaming.getMessages().customJspName() );
		table.setWidget( 0, 0, label );
		m_jspNameTxtBox = new TextBox();
		m_jspNameTxtBox.setVisibleLength( 30 );
		table.setWidget( 1, 0, m_jspNameTxtBox );
		mainPanel.add( table );
		
		// Add the "Associate a folder with this custom jsp" checkbox.
		table = new FlexTable();
		table.setCellSpacing( 2 );
		m_assocFolderCkBox = new CheckBox( GwtTeaming.getMessages().customJspAssocFolder() );
		m_assocFolderCkBox.addClickHandler( this );
		table.setWidget( 0, 0, m_assocFolderCkBox );
		
		// Create the controls that will be visible if the user checks "Associate a folder with this custom jsp"
		m_selectFolderPanel = createSelectFolderPanel();
		table.setWidget( 1, 0, m_selectFolderPanel );
		
		// Add the "Associate a folder with this custom jsp" checkbox.
		m_assocEntryCkBox = new CheckBox( GwtTeaming.getMessages().customJspAssocEntry() );
		m_assocEntryCkBox.addClickHandler( this );
		table.setWidget( 2, 0, m_assocEntryCkBox );
		
		// Create the controls that will be visibe if the user checks "Associate an entry with this custom jsp"
		m_selectEntryPanel = createSelectEntryPanel();
		table.setWidget( 3, 0, m_selectEntryPanel );
		mainPanel.add( table );
		
		mainPanel.add( table );

		init( properties );
		
		return mainPanel;
	}// end createContent()
	

	/**
	 * Create the controls that will be needed if the user selects "Associate an entry with this custom jsp"
	 */
	public Panel createSelectEntryPanel()
	{
		VerticalPanel mainPanel;
		FlexTable table;
		FlowPanel flowPanel;
		InlineLabel inlineLabel;
		HTMLTable.CellFormatter cellFormatter;
		Label label;
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "lpeMarginLeft1Point5" );
		
		// Add a label that will say Current entry: name of the currently selected entry
		table = new FlexTable();
		table.setCellSpacing( 8 );
		flowPanel = new FlowPanel();
		inlineLabel = new InlineLabel( GwtTeaming.getMessages().currentEntry() );
		m_currentEntryNameLabel = new InlineLabel();
		m_currentEntryNameLabel.addStyleName( "bold" );
		m_currentEntryNameLabel.addStyleName( "marginLeftPoint25em" );
		flowPanel.add( inlineLabel );
		flowPanel.add( m_currentEntryNameLabel );
		table.setWidget( 0, 0, flowPanel );
		mainPanel.add( table );

		// Add label and a "find" control.
		table = new FlexTable();
		table.setCellSpacing( 8 );
		cellFormatter = table.getCellFormatter();
		cellFormatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
		label = new Label( GwtTeaming.getMessages().findEntry() );
		table.setWidget( 0, 0, label );
		m_entryFindCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.ENTRIES );
		table.setWidget( 0, 1, m_entryFindCtrl );
		mainPanel.add( table );
		
		// Add a checkbox for "Show title"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		m_showEntryTitleCkBox = new CheckBox( GwtTeaming.getMessages().showTitleBar() );
		table.setWidget( 0, 0, m_showEntryTitleCkBox );
		mainPanel.add( table );

		return mainPanel;
	}// end createSelectEntryPanel()
	
	
	/**
	 * Create the controls that will be needed if the user selects "Associate a folder with this custom jsp"
	 */
	public Panel createSelectFolderPanel()
	{
		VerticalPanel mainPanel;
		FlexTable table;
		FlowPanel flowPanel;
		InlineLabel inlineLabel;
		Label label;
		HTMLTable.CellFormatter cellFormatter;
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "lpeMarginLeft1Point5" );
		mainPanel.setVisible( false );

		// Add a label that will say Current folder: name of the currently selected folder
		table = new FlexTable();
		table.setCellSpacing( 8 );
		flowPanel = new FlowPanel();
		inlineLabel = new InlineLabel( GwtTeaming.getMessages().currentFolder() );
		m_currentFolderNameLabel = new InlineLabel();
		m_currentFolderNameLabel.addStyleName( "bold" );
		m_currentFolderNameLabel.addStyleName( "marginLeftPoint25em" );
		flowPanel.add( inlineLabel );
		flowPanel.add( m_currentFolderNameLabel );
		table.setWidget( 0, 0, flowPanel );
		mainPanel.add( table );

		// Add label "New folder"
		table = new FlexTable();
		table.setCellSpacing( 8 );

		// Add controls for the folder.
		label = new Label( GwtTeaming.getMessages().findFolderLabel() );
		cellFormatter = table.getCellFormatter();
		cellFormatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
		table.setWidget( 0, 0, label );
		m_folderFindCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.PLACES );
		m_folderFindCtrl.setSearchForFoldersOnly( true );
		table.setWidget( 0, 1, m_folderFindCtrl );
		mainPanel.add( table );

		// Add controls for "Number of entries to show"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		label = new Label( GwtTeaming.getMessages().numEntriesToShow() );
		table.setWidget( 0, 0, label );
		m_numEntriesToShowTxtBox = new TextBox();
		m_numEntriesToShowTxtBox.addKeyPressHandler( this );
		m_numEntriesToShowTxtBox.setVisibleLength( 2 );
		table.setWidget( 0, 1, m_numEntriesToShowTxtBox );
		mainPanel.add( table );
		
		// Add a checkbox for "Show title"
		table = new FlexTable();
		table.setCellSpacing( 0 );
		m_showFolderTitleCkBox = new CheckBox( GwtTeaming.getMessages().showTitleBar() );
		table.setWidget( 0, 0, m_showFolderTitleCkBox );
		mainPanel.add( table );
		
		return mainPanel;
	}// end createSelectFolderPanel()
	
	
	/**
	 * Show/hide the appropriate controls in the dialog based on whether the "Associate a folder..." or
	 * the "Associate an entry..." checkbox is checked.
	 */
	public void danceControls()
	{
		// Show/hide the select folder controls.
		m_selectFolderPanel.setVisible( m_assocFolderCkBox.getValue() );

		// Show/hide the select entry controls.
		m_selectEntryPanel.setVisible( m_assocEntryCkBox.getValue() );
	}// end danceControls()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		CustomJspProperties	properties;
		
		properties = new CustomJspProperties();
		
		// Save away the name of the custom jsp.
		properties.setJspName( getJspName() );

		// Is the "Associate a folder..." checkbox checked?
		if ( m_assocFolderCkBox.getValue() )
		{
			// Yes
			// Did the user select a folder?
			if ( m_folderId == null || m_folderId.length() == 0 )
			{
				// No, tell them they need to
				Window.alert( GwtTeaming.getMessages().pleaseSelectAFolder() );
				return null;
			}
			properties.setFolderId( m_folderId );

			// Save away the number of entries to show.
			properties.setNumEntriesToBeShownValue( getNumEntriesToShowValue() );
			
			// Save away the "show title bar" value.
			properties.setShowTitle( getShowFolderTitleValue() );
		}
		// Is this "Associate an entry..." checkbox checked?
		else if ( m_assocEntryCkBox.getValue() )
		{
			// Yes
			// Did the user select an entry?
			if ( m_entryId == null || m_entryId.length() == 0 )
			{
				// No, tell them they need to
				Window.alert( GwtTeaming.getMessages().pleaseSelectAnEntry() );
				return null;
			}
			properties.setEntryId( m_entryId );

			// Save away the "show border" value.
			properties.setShowTitle( getShowEntryTitleValue() );
		}
		
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_jspNameTxtBox;
	}// end getFocusWidget()
	
	
	/**
	 * Return the text found in the jsp name edit control.
	 */
	public String getJspName()
	{
		return m_jspNameTxtBox.getText();
	}// end getJspName()
	
	
	/**
	 * Return the number of entries to show.
	 */
	public int getNumEntriesToShowValue()
	{
		String txt;
		int numEntries;
		
		numEntries = 0;
		txt = m_numEntriesToShowTxtBox.getText();
		if ( txt != null && txt.length() > 0 )
		{
			try
			{
				numEntries = Integer.parseInt( txt );
			}
			catch ( NumberFormatException nfEx )
			{
				// This should never happen.  The data should be validated before we get to this point.
			}
		}
		
		return numEntries;
	}// end getNumEntriesToShowValue()
	
	
	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowEntryTitleValue()
	{
		return m_showEntryTitleCkBox.getValue().booleanValue();
	}// end getShowEntryTitleValue()
	
	
	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowFolderTitleValue()
	{
		return m_showFolderTitleCkBox.getValue().booleanValue();
	}// end getShowFolderTitleValue()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		CustomJspProperties properties;
		
		m_folderId = null;
		m_entryId = null;
		m_assocFolderCkBox.setValue( false );
		m_currentFolderNameLabel.setText( "" ); 
		m_numEntriesToShowTxtBox.setText( "" );
		m_showFolderTitleCkBox.setValue( false );
		m_folderFindCtrl.setInitialSearchString( "" );
		m_currentEntryNameLabel.setText( "" ); 
		m_showEntryTitleCkBox.setValue( false );
		m_entryFindCtrl.setInitialSearchString( "" );
		
		properties = (CustomJspProperties) props;

		m_jspNameTxtBox.setText( properties.getJspName() );

		m_assocFolderCkBox.setValue( false );
		m_assocEntryCkBox.setValue( false );

		m_folderId = properties.getFolderId();
		if ( m_folderId != null )
		{
			int num;
			
			// Check the "Associate a folder..." checkbox.
			m_assocFolderCkBox.setValue( true );
			
			// Update the name of the currently selected folder.
			m_currentFolderNameLabel.setText( properties.getFolderName() ); 

			m_showFolderTitleCkBox.setValue( properties.getShowTitleValue() );
			
			// Hide the search-results widget.
			m_folderFindCtrl.hideSearchResults();
			
			num = properties.getNumEntriesToBeShownValue();
			m_numEntriesToShowTxtBox.setText( String.valueOf( num ) );
		}
		else
		{
			m_entryId = properties.getEntryId();
			if ( m_entryId != null )
			{
				// Check the "Associate an entry..." checkbox.
				m_assocEntryCkBox.setValue( true );

				// Update the name of the currently selected entry.
				m_currentEntryNameLabel.setText( properties.getEntryName() ); 
				 
				m_showEntryTitleCkBox.setValue( properties.getShowTitleValue() );

				// Hide the search-results widget.
				m_entryFindCtrl.hideSearchResults();
			}
		}
		
		// Hide/show the appropriate controls on the page based on which checkbox is checked,
		// "Associate with folder" or "Associate with entry"
		danceControls();
	}// end init()

	
	/*
	 * This method gets called when the user clicks on the ok or cancel button.
	 */
	public void onClick( ClickEvent event )
	{
		Object	source;
		
		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on the "associate a folder with this custom jsp" checkbox?
		if ( source == m_assocFolderCkBox )
		{
			// Yes
			// A folder or an entry can be associated with a custom jsp but not both.
			if ( m_assocFolderCkBox.getValue() )
				m_assocEntryCkBox.setValue( false );
			
			// Show/hide the appopriate controls on the dialog.
			danceControls();
			return;
		}
		
		// Did the user click on the "associate an entry with this custom jsp" checkbox?
		if ( source == m_assocEntryCkBox )
		{
			// Yes
			// A folder or an entry can be associated with a custom jsp but not both.
			if ( m_assocEntryCkBox.getValue() )
				m_assocFolderCkBox.setValue( false );
			
			// Show/hide the appopriate controls on the dialog.
			danceControls();
			return;
		}
		
		// Let our parent handle everything else.
		super.onClick( event );
	}// end onClick()
	
	
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

	
	/**
	 * This method gets called when the user selects an item from the search results in the "find" control.
	 */
	public void onSelect( Object selectedObj )
	{
		// Are we dealing with a GwtFolder object?
		if ( selectedObj instanceof GwtFolder )
		{
			GwtFolder gwtFolder;
			
			gwtFolder = (GwtFolder) selectedObj;
			m_folderId = gwtFolder.getFolderId();
			
			// Hide the search-results widget.
			m_folderFindCtrl.hideSearchResults();
		}
		// Are we dealing with a GwtFolderEntry object?
		else if ( selectedObj instanceof GwtFolderEntry )
		{
			GwtFolderEntry gwtFolderEntry;
			
			gwtFolderEntry = (GwtFolderEntry) selectedObj;
			m_entryId = gwtFolderEntry.getEntryId();
			
			// Hide the search-results widget.
			m_entryFindCtrl.hideSearchResults();
		}
	}// end onSelect()
	
}// end CustomJspWidgetDlgBox
