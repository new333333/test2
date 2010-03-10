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

import org.kablink.teaming.gwt.client.ActionHandler;
import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
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
public class FolderWidgetDlgBox extends DlgBox
	implements KeyPressHandler, ActionHandler
{
	private CheckBox m_showTitleCkBox = null;
	private CheckBox m_showDescCkBox = null;
	private CheckBox m_showEntriesOpenedCkBox = null;
	private TextBox m_numEntriesToShowTxtBox;
	private FindCtrl m_findCtrl = null;
	private String m_folderId = null;
	private InlineLabel m_currentFolderNameLabel = null;

	/**
	 * 
	 */
	public FolderWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		FolderProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().folderProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end FolderWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FolderProperties properties;
		Label			label;
		InlineLabel inlineLabel;
		VerticalPanel	mainPanel;
		FlexTable		table;
		HTMLTable.CellFormatter cellFormatter;
		FlowPanel flowPanel;
		
		properties = (FolderProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

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

		// Add label and edit control for "Folder id"
		table = new FlexTable();
		table.setCellSpacing( 8 );

		// Add controls for the folder.
		label = new Label( GwtTeaming.getMessages().findFolderLabel() );
		cellFormatter = table.getCellFormatter();
		cellFormatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
		table.setWidget( 0, 0, label );
		m_findCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.PLACES );
		m_findCtrl.setSearchForFoldersOnly( true );
		table.setWidget( 0, 1, m_findCtrl );
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
		m_showTitleCkBox = new CheckBox( GwtTeaming.getMessages().showTitleBar() );
		table.setWidget( 0, 0, m_showTitleCkBox );
		
		// Add a checkbox for "Show the folder description"
		m_showDescCkBox = new CheckBox( GwtTeaming.getMessages().showFolderDesc() );
		table.setWidget( 1, 0, m_showDescCkBox );

		// Add a checkbox for "Show entries opened"
		m_showEntriesOpenedCkBox = new CheckBox( GwtTeaming.getMessages().showEntriesOpened() );
		table.setWidget( 2, 0, m_showEntriesOpenedCkBox );
		mainPanel.add( table );

		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		FolderProperties	properties;
		String folderId;
		
		properties = new FolderProperties();
		
		// Save away the "show title bar" value.
		properties.setShowTitle( getShowTitleValue() );
		
		// Save away the "show the folder description" value.
		properties.setShowDescValue( getShowDescValue() );
		
		// Save away the "show entries opened" value.
		properties.setShowEntriesOpenedValue( getShowEntriesOpenedValue() );
		
		// Save away the folder id.
		// Did the user select a folder?
		folderId = getFolderIdValue();
		if ( folderId == null || folderId.length() == 0 )
		{
			// No, tell them they need to
			Window.alert( GwtTeaming.getMessages().pleaseSelectAFolder() );
			return null;
		}
		properties.setFolderId( folderId );
		
		// Save away the number of entries to show.
		properties.setNumEntriesToBeShownValue( getNumEntriesToShowValue() );
		
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_findCtrl.getFocusWidget();
	}// end getFocusWidget()
	
	
	/**
	 * Return folder id of the selected folder.
	 */
	public String getFolderIdValue()
	{
		return m_folderId;
	}// end getFolderIdValue()
	

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
	 * Return true if the "show the folder description" checkbox is checked.
	 */
	public boolean getShowDescValue()
	{
		return m_showDescCkBox.getValue().booleanValue();
	}// end getShowDescValue()
	
	
	/**
	 * Return true if the "show entries opened" checkbox is checked.
	 */
	public boolean getShowEntriesOpenedValue()
	{
		return m_showEntriesOpenedCkBox.getValue().booleanValue();
	}// end getShowEntriesOpenedValue()
	
	
	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitleCkBox.getValue().booleanValue();
	}// end getShowTitleValue()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		FolderProperties properties;
		String value;
		int num;
		
		properties = (FolderProperties) props;

		// Update the name of the currently selected folder.
		m_currentFolderNameLabel.setText( properties.getFolderName() ); 

		m_showTitleCkBox.setValue( properties.getShowTitleValue() );
		m_showDescCkBox.setValue( properties.getShowDescValue() );
		m_showEntriesOpenedCkBox.setValue( properties.getShowEntriesOpenedValue() );
		
		// Hide the search-results widget.
		m_findCtrl.hideSearchResults();
		
		// Populate the find control's text box with the name of the selected folder.
		m_findCtrl.setInitialSearchString( "" );
		
		// Remember the entry id that was passed to us.
		m_folderId = properties.getFolderId();

		num = properties.getNumEntriesToBeShownValue();
		m_numEntriesToShowTxtBox.setText( String.valueOf( num ) );
	}// end init()
	

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
	public void handleAction( TeamingAction ta, Object selectedObj )
	{
		if ( TeamingAction.SELECTION_CHANGED == ta )
		{
			// Make sure we are dealing with a GwtFolder object.
			if ( selectedObj instanceof GwtFolder )
			{
				GwtFolder gwtFolder;
				
				gwtFolder = (GwtFolder) selectedObj;
				m_folderId = gwtFolder.getFolderId();
				
				// Hide the search-results widget.
				m_findCtrl.hideSearchResults();
			}
		}
	}// end handleAction()
	
}// end FolderWidgetDlgBox
