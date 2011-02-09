/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class LandingPageExtWidgetDlgBox extends DlgBox
	implements KeyPressHandler, ActionHandler
{
	private ListBox m_extListBox = null;
	private Label m_descLabel;
	private ArrayList<LPExtensionInfo> m_extensions;
	
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
	public LandingPageExtWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		LandingPageExtProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		LPExtensionInfo extInfo;
		
		m_extensions = new ArrayList<LPExtensionInfo>();
		extInfo = new LPExtensionInfo( "landing_page_entry.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_full_entry.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_folder.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_folder_list.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_folder_list_sorted.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_folder_list_sorted_files.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_calendar.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_task_folder.jsp" );
		m_extensions.add( extInfo );
		extInfo = new LPExtensionInfo( "landing_page_survey.jsp" );
		m_extensions.add( extInfo );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().landingPageExtProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		LandingPageExtProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		
		properties = (LandingPageExtProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and list box for the landing page extension
		label = new Label( GwtTeaming.getMessages().landingPageExtNameLabel() );
		mainPanel.add( label );
		
		// Create a listbox that holds the names of all the landing page extensions.
		{
			ChangeHandler changeHandler;
			
			m_extListBox = new ListBox( false );
			m_extListBox.setVisibleItemCount( 1 );
			
			changeHandler = new ChangeHandler()
			{
				/**
				 * 
				 */
				public void onChange( ChangeEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						public void execute()
						{
							handleExtensionSelected();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_extListBox.addChangeHandler( changeHandler );
			
			for ( LPExtensionInfo extInfo : m_extensions )
			{
				m_extListBox.addItem( extInfo.getDisplayName(), extInfo.getJspName() );
			}
			
			mainPanel.add( m_extListBox );
		}
		
		// Create a panel where we will display the description of the selected extension.
		{
			m_descLabel = new Label();
			m_descLabel.addStyleName( "lpExtDescLabel" );
			
			mainPanel.add( m_descLabel );
		}
		
		// Create the controls that will be visible if the user selects an extension that requires
		// a folder to be selected.
		m_selectFolderPanel = createSelectFolderPanel();
		m_selectFolderPanel.setVisible( false );
		mainPanel.add( m_selectFolderPanel );
		
		// Create the controls that will be visibe if the user selects an extension that requires
		// an entry to be selected.
		m_selectEntryPanel = createSelectEntryPanel();
		m_selectEntryPanel.setVisible( false );
		mainPanel.add( m_selectEntryPanel );

		init( properties );
		
		return mainPanel;
	}
	

	/**
	 * Create the controls that will be needed if the user selects an extension that
	 * requires a folder to be selected.
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
		
		// Add a label that will say Current entry: name of the currently selected entry
		table = new FlexTable();
		table.setCellSpacing( 8 );
		flowPanel = new FlowPanel();
		inlineLabel = new InlineLabel( GwtTeaming.getMessages().currentEntry() );
		m_currentEntryNameLabel = new InlineLabel();
		m_currentEntryNameLabel.addStyleName( "bold" );
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
	}
	
	
	/**
	 * Create the controls that will be needed if the user selects an extension that requires
	 * a folder to be selected.
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
		mainPanel.setVisible( false );

		// Add a label that will say Current folder: name of the currently selected folder
		table = new FlexTable();
		table.setCellSpacing( 8 );
		flowPanel = new FlowPanel();
		inlineLabel = new InlineLabel( GwtTeaming.getMessages().currentFolder() );
		m_currentFolderNameLabel = new InlineLabel();
		m_currentFolderNameLabel.addStyleName( "bold" );
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
	}
	
	
	/**
	 * Show/hide the appropriate controls in the dialog based on whether the selected
	 * extension requires a folder or an entry to be selected.
	 */
	public void danceControls()
	{
		LPExtensionInfo extInfo;
		
		// Hide the ui dealing with selecting a folder.
		m_selectFolderPanel.setVisible( false );
		
		// Hide the ui dealing with selecting an entry.
		m_selectEntryPanel.setVisible( false );
		
		// Get the selected extension.
		extInfo = getSelectedExtension();
		if ( extInfo != null )
		{
			String desc;
			
			// Show the description of the extension
			desc = extInfo.getDesc();
			if ( desc != null )
				m_descLabel.setText( desc );
			
			// Does the selected extension require the user to select a folder?
			if ( extInfo.isFolderRequired() )
			{
				// Yes, show the ui for selecting a folder.
				m_selectFolderPanel.setVisible( true );
			}
			
			// Does the selected extension require the user to select an entry?
			if ( extInfo.isEntryRequired() )
			{
				// Yes, show the ui for selecting an entry.
				m_selectEntryPanel.setVisible( true );
			}
		}
	}
	
	/**
	 * Does the selected extension require the user to select an entry?
	 */
	private boolean doesSelectedExtensionRequireEntry()
	{
		LPExtensionInfo extInfo;
		
		// Get the selected extension information.
		extInfo = getSelectedExtension();
		if ( extInfo != null )
			return extInfo.isEntryRequired();
		
		return false;
	}
	
	
	/**
	 * Does the selected extension require the user to select a folder?
	 */
	private boolean doesSelectedExtensionRequireFolder()
	{
		LPExtensionInfo extInfo;
		
		// Get the selected extension information.
		extInfo = getSelectedExtension();
		if ( extInfo != null )
			return extInfo.isFolderRequired();
		
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		LandingPageExtProperties	properties;
		
		properties = new LandingPageExtProperties();
		
		// Save away the name of the jsp that the selected extension uses.
		properties.setJspName( getJspName() );

		// Does the selected extension require a folder to be selected?
		if ( doesSelectedExtensionRequireFolder() )
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
		
		// Does the selected extension require an entry to be selected.
		if ( doesSelectedExtensionRequireEntry() )
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
	}
	
	
	/**
	 * Return an LPExtensionInfo object for the given jsp name.
	 */
	private LPExtensionInfo getExtensionByJspName( String jspName )
	{
		if ( jspName != null )
		{
			for ( LPExtensionInfo extInfo : m_extensions )
			{
				String nextJspName;
				
				nextJspName = extInfo.getJspName();
				if ( nextJspName != null && jspName.equalsIgnoreCase( nextJspName ) )
					return extInfo;
			}
		}
		
		return null;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_extListBox;
	}
	
	
	/**
	 * Return the jsp name of the selected extension
	 */
	public String getJspName()
	{
		LPExtensionInfo extInfo;
		
		// Get the selected extension.
		extInfo = getSelectedExtension();
		if ( extInfo != null )
			return extInfo.getJspName();
		
		return "";
	}
	
	
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
	}
	
	
	/**
	 * Return the selected extension.
	 */
	private LPExtensionInfo getSelectedExtension()
	{
		int selectedIndex;
		
		// Get the selected index from the listbox that holds the list of extensions.
		selectedIndex = m_extListBox.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			String jspName;
			
			jspName = m_extListBox.getValue( selectedIndex );
			return getExtensionByJspName( jspName );
		}
		
		return null;
	}
	
	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowEntryTitleValue()
	{
		return m_showEntryTitleCkBox.getValue().booleanValue();
	}
	
	
	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowFolderTitleValue()
	{
		return m_showFolderTitleCkBox.getValue().booleanValue();
	}
	
	
	/**
	 * This method gets called when the user selects an item from the search results in the "find" control.
	 */
	public void handleAction( TeamingAction ta, Object selectedObj )
	{
		if ( TeamingAction.SELECTION_CHANGED == ta )
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
		}
	}
	
	/**
	 * This method gets called when the user selects an extension in the listbox.
	 */
	private void handleExtensionSelected()
	{
		// Dance the ui based on the selected extension
		danceControls();
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		LandingPageExtProperties properties;
		
		m_folderId = null;
		m_entryId = null;
		m_currentFolderNameLabel.setText( "" ); 
		m_numEntriesToShowTxtBox.setText( "" );
		m_showFolderTitleCkBox.setValue( false );
		m_folderFindCtrl.setInitialSearchString( "" );
		m_currentEntryNameLabel.setText( "" ); 
		m_showEntryTitleCkBox.setValue( false );
		m_entryFindCtrl.setInitialSearchString( "" );
		m_descLabel.setText( "" );
		
		properties = (LandingPageExtProperties) props;

		// Select the appropriate extension in the listbox.
		selectExtensionByJspName( properties.getJspName() );
		
		m_folderId = properties.getFolderId();
		if ( m_folderId != null )
		{
			int num;
			
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
				// Update the name of the currently selected entry.
				m_currentEntryNameLabel.setText( properties.getEntryName() ); 
				 
				m_showEntryTitleCkBox.setValue( properties.getShowTitleValue() );

				// Hide the search-results widget.
				m_entryFindCtrl.hideSearchResults();
			}
		}
		
		// Hide/show the appropriate controls on the page based on the selected extension.
		danceControls();
	}

	
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
	}

	/**
	 * Select an extension in the list box for the given jsp name.
	 */
	private int selectExtensionByJspName( String jspName )
	{
		if ( jspName != null && jspName.length() > 0 )
		{
			int i;
			
			// Go through the list box and select the extension whose jsp name matches the given jsp name.
			for (i = 0; i < m_extListBox.getItemCount(); ++i)
			{
				String nextJspName;
				
				nextJspName = m_extListBox.getValue( i );
				if ( nextJspName != null )
				{
					if ( nextJspName.equalsIgnoreCase( jspName ) )
					{
						m_extListBox.setSelectedIndex( i );
						return i;
					}
				}
			}
		}
		else
			m_extListBox.setSelectedIndex( 0 );
		
		return -1;
	}
}
