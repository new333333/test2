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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
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
public class EnhancedViewWidgetDlgBox extends DlgBox
	implements KeyPressHandler, ActionHandler
{
	private ListBox m_evListBox = null;
	private Label m_descLabel;
	private ArrayList<EnhancedViewInfo> m_views;
	private LandingPageEditor m_lpe;
	
	// The following data members are used if the user has checked the "Associate a folder with this custom jsp"
	private String m_folderId = null;
	private Panel m_selectFolderPanel = null;
	private FlowPanel m_folderFindPanel;
	private FindCtrl m_folderFindCtrl = null;
	private CheckBox m_showFolderTitleCkBox = null;
	private TextBox m_numEntriesToShowTxtBox = null;
	private InlineLabel m_currentFolderNameLabel = null;
	private Button m_folderEditBtn;
	
	// The following data members are used if the user has checked the "Associate an entry with this custom jsp"
	private String m_entryId = null;
	private Panel m_selectEntryPanel = null;
	private FindCtrl m_entryFindCtrl = null;
	private FlowPanel m_entryFindPanel;
	private CheckBox m_showEntryTitleCkBox = null;
	private InlineLabel m_currentEntryNameLabel = null;
	private Button m_entryEditBtn;
	
	/**
	 * 
	 */
	public EnhancedViewWidgetDlgBox(
		LandingPageEditor lpe,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EnhancedViewProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		EnhancedViewInfo evInfo;
		
		m_lpe = lpe;
		
		m_views = new ArrayList<EnhancedViewInfo>();
		evInfo = new EnhancedViewInfo( "landing_page_entry.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_full_entry.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_folder.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_folder_list.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_folder_list_sorted.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_folder_list_sorted_files.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_calendar.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_task_folder.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_survey.jsp" );
		m_views.add( evInfo );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().enhancedViewProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		EnhancedViewProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		
		properties = (EnhancedViewProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and list box for the view
		label = new Label( GwtTeaming.getMessages().enhancedViewLabel() );
		mainPanel.add( label );
		
		// Create a listbox that holds the names of all the view.
		{
			ChangeHandler changeHandler;
			
			m_evListBox = new ListBox( false );
			m_evListBox.setVisibleItemCount( 1 );
			
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
							handleViewSelected();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_evListBox.addChangeHandler( changeHandler );
			
			for ( EnhancedViewInfo evInfo : m_views )
			{
				m_evListBox.addItem( evInfo.getDisplayName(), evInfo.getJspName() );
			}
			
			mainPanel.add( m_evListBox );
		}
		
		// Create a panel where we will display the description of the selected view.
		{
			m_descLabel = new Label();
			m_descLabel.addStyleName( "enhancedViewDescLabel" );
			
			mainPanel.add( m_descLabel );
		}
		
		// Create the controls that will be visible if the user selects a view that requires
		// a folder to be selected.
		m_selectFolderPanel = createSelectFolderPanel();
		m_selectFolderPanel.setVisible( false );
		mainPanel.add( m_selectFolderPanel );
		
		// Create the controls that will be visibe if the user selects a view that requires
		// an entry to be selected.
		m_selectEntryPanel = createSelectEntryPanel();
		m_selectEntryPanel.setVisible( false );
		mainPanel.add( m_selectEntryPanel );

		init( properties );
		
		return mainPanel;
	}
	

	/**
	 * Create the controls that will be needed if the user selects a view that
	 * requires a folder to be selected.
	 */
	public Panel createSelectEntryPanel()
	{
		VerticalPanel mainPanel;
		FlexTable table;
		FlowPanel panel;
		InlineLabel inlineLabel;
		
		mainPanel = new VerticalPanel();
		
		table = new FlexTable();
		table.setCellSpacing( 8 );

		mainPanel.add( table );
		
		// Add a label that will say Entry:
		inlineLabel = new InlineLabel( GwtTeaming.getMessages().entryLabel() );
		table.setWidget( 0, 0, inlineLabel );
		
		// Add a label to hold the name of the selected entry.
		m_currentEntryNameLabel = new InlineLabel( GwtTeaming.getMessages().noEntrySelected() );
		m_currentEntryNameLabel.addStyleName( "noEntrySelected" );
		m_currentEntryNameLabel.addStyleName( "marginLeftPoint25em" );
		m_currentEntryNameLabel.addStyleName( "marginright10px" );
		panel = new FlowPanel();
		panel.add( m_currentEntryNameLabel );
		
		// Add an "Edit" button
		{
			ClickHandler clickHandler;
			
			m_entryEditBtn = new Button( GwtTeaming.getMessages().edit() );
			m_entryEditBtn.addStyleName( "teamingButton" );
			panel.add( m_entryEditBtn );
			
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						public void execute()
						{
							// Make the find control visible.
							showEntryFindControl();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			};
			m_entryEditBtn.addClickHandler( clickHandler );
		}

		table.setWidget( 0, 1, panel );
		
		// Add a "find" control.
		{
			InlineLabel findLabel;
			FlexTable findTable;
			
			m_entryFindPanel = new FlowPanel();
			m_entryFindPanel.addStyleName( "findCtrlPanel" );
			m_entryFindPanel.setVisible( false );
			
			// Add an image the user can click on to close the find panel.
			{
				Image img;
				ImageResource imageResource;
				ClickHandler clickHandler;
				
				imageResource = GwtTeaming.getImageBundle().closeX();
				img = new Image( imageResource );
				img.addStyleName( "findCtrlCloseImg" );
				img.getElement().setAttribute( "title", GwtTeaming.getMessages().close() );
				m_entryFindPanel.add( img );
		
				// Add a click handler to the "close" image.
				clickHandler = new ClickHandler()
				{
					public void onClick( ClickEvent clickEvent )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							public void execute()
							{
								// Close the panel that holds find controls.
								hideEntryFindControl();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				img.addClickHandler( clickHandler );
			}
			
			findTable = new FlexTable();
			
			findLabel = new InlineLabel( GwtTeaming.getMessages().find() );
			findLabel.addStyleName( "findCtrlLabel" );
			findTable.setWidget( 0, 0, findLabel );
			
			m_entryFindCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.ENTRIES );
			m_entryFindCtrl.enableScope( m_lpe.getBinderId() );
			findTable.setWidget( 0, 1, m_entryFindCtrl );
			
			m_entryFindPanel.add( findTable );
			mainPanel.add( m_entryFindPanel );
		}
		
		// Add a checkbox for "Show title"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		m_showEntryTitleCkBox = new CheckBox( GwtTeaming.getMessages().showTitleBar() );
		table.setWidget( 0, 0, m_showEntryTitleCkBox );
		mainPanel.add( table );

		return mainPanel;
	}
	
	
	/**
	 * Create the controls that will be needed if the user selects a view that requires
	 * a folder to be selected.
	 */
	public Panel createSelectFolderPanel()
	{
		VerticalPanel mainPanel;
		FlexTable table;
		FlowPanel panel;
		InlineLabel inlineLabel;
		Label label;
		
		mainPanel = new VerticalPanel();
		mainPanel.setVisible( false );

		table = new FlexTable();
		table.setCellSpacing( 8 );

		mainPanel.add( table );
		
		// Add a label that will say Current folder:
		inlineLabel = new InlineLabel( GwtTeaming.getMessages().folderLabel() );
		table.setWidget( 0, 0, inlineLabel );
		
		// Add a label to hold the name of the selected folder.
		m_currentFolderNameLabel = new InlineLabel( GwtTeaming.getMessages().noFolderSelected() );
		m_currentFolderNameLabel.addStyleName( "noFolderSelected" );
		m_currentFolderNameLabel.addStyleName( "marginLeftPoint25em" );
		m_currentFolderNameLabel.addStyleName( "marginright10px" );
		panel = new FlowPanel();
		panel.add( m_currentFolderNameLabel );

		// Add an "Edit" button
		{
			ClickHandler clickHandler;
			
			m_folderEditBtn = new Button( GwtTeaming.getMessages().edit() );
			m_folderEditBtn.addStyleName( "teamingButton" );
			panel.add( m_folderEditBtn );
			
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						public void execute()
						{
							// Make the find control visible.
							showFolderFindControl();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			};
			m_folderEditBtn.addClickHandler( clickHandler );
		}

		table.setWidget( 0, 1, panel );


		// Add a "find" control
		{
			InlineLabel findLabel;
			FlexTable findTable;
			
			m_folderFindPanel = new FlowPanel();
			m_folderFindPanel.addStyleName( "findCtrlPanel" );
			m_folderFindPanel.setVisible( false );
			
			// Add an image the user can click on to close the find panel.
			{
				Image img;
				ImageResource imageResource;
				ClickHandler clickHandler;
				
				imageResource = GwtTeaming.getImageBundle().closeX();
				img = new Image( imageResource );
				img.addStyleName( "findCtrlCloseImg" );
				img.getElement().setAttribute( "title", GwtTeaming.getMessages().close() );
				m_folderFindPanel.add( img );
		
				// Add a click handler to the "close" image.
				clickHandler = new ClickHandler()
				{
					public void onClick( ClickEvent clickEvent )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							public void execute()
							{
								// Close the panel that holds find controls.
								hideFolderFindControl();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				img.addClickHandler( clickHandler );
			}
			
			findTable = new FlexTable();
			
			findLabel = new InlineLabel( GwtTeaming.getMessages().find() );
			findLabel.addStyleName( "findCtrlLabel" );
			findTable.setWidget( 0, 0, findLabel );
			
			m_folderFindCtrl = new FindCtrl( this, GwtSearchCriteria.SearchType.PLACES );
			m_folderFindCtrl.enableScope( m_lpe.getBinderId() );
			m_folderFindCtrl.setSearchForFoldersOnly( true );
			findTable.setWidget( 0, 1, m_folderFindCtrl );
			
			m_folderFindPanel.add( findTable );
			mainPanel.add( m_folderFindPanel );
		}

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
	 * view requires a folder or an entry to be selected.
	 */
	public void danceControls()
	{
		EnhancedViewInfo evInfo;
		
		// Hide the ui dealing with selecting a folder.
		m_selectFolderPanel.setVisible( false );
		
		// Hide the ui dealing with selecting an entry.
		m_selectEntryPanel.setVisible( false );
		
		// Get the selected view.
		evInfo = getSelectedView();
		if ( evInfo != null )
		{
			String desc;
			
			// Show the description of the view
			desc = evInfo.getDesc();
			if ( desc != null )
				m_descLabel.setText( desc );
			
			// Does the selected view require the user to select a folder?
			if ( evInfo.isFolderRequired() )
			{
				// Yes, show the ui for selecting a folder.
				m_selectFolderPanel.setVisible( true );
			}
			
			// Does the selected view require the user to select an entry?
			if ( evInfo.isEntryRequired() )
			{
				// Yes, show the ui for selecting an entry.
				m_selectEntryPanel.setVisible( true );
			}
		}
	}
	
	/**
	 * Does the selected view require the user to select an entry?
	 */
	private boolean doesSelectedViewRequireEntry()
	{
		EnhancedViewInfo evInfo;
		
		// Get the selected view information.
		evInfo = getSelectedView();
		if ( evInfo != null )
			return evInfo.isEntryRequired();
		
		return false;
	}
	
	
	/**
	 * Does the selected view require the user to select a folder?
	 */
	private boolean doesSelectedViewRequireFolder()
	{
		EnhancedViewInfo evInfo;
		
		// Get the selected view information.
		evInfo = getSelectedView();
		if ( evInfo != null )
			return evInfo.isFolderRequired();
		
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		EnhancedViewProperties	properties;
		
		properties = new EnhancedViewProperties();
		
		// Save away the name of the jsp that the selected view uses.
		properties.setJspName( getJspName() );

		// Does the selected view require a folder to be selected?
		if ( doesSelectedViewRequireFolder() )
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
		
		// Does the selected view require an entry to be selected.
		if ( doesSelectedViewRequireEntry() )
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
	 * Issue an ajax request to get the entry for the given id.  After we get the entry
	 * we will update the name of the selected entry.
	 */
	private void getEntry( final String entryId )
	{
		AsyncCallback<GwtFolderEntry> callback;
		
		callback = new AsyncCallback<GwtFolderEntry>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetFolderEntry(),
					entryId );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtFolderEntry gwtFolderEntry )
			{
				if ( gwtFolderEntry != null )
				{
					// Update the name of the selected entry.
					m_currentEntryNameLabel.setText( gwtFolderEntry.getEntryName() );
					m_currentEntryNameLabel.removeStyleName( "noEntrySelected" );
					m_currentEntryNameLabel.addStyleName( "bold" );
				}
			}
		};

		GwtTeaming.getRpcService().getEntry( HttpRequestInfo.createHttpRequestInfo(), null, entryId, callback );
	}
	

	/**
	 * Return an EnhancedViewInfo object for the given jsp name.
	 */
	private EnhancedViewInfo getViewByJspName( String jspName )
	{
		if ( jspName != null )
		{
			for ( EnhancedViewInfo evInfo : m_views )
			{
				String nextJspName;
				
				nextJspName = evInfo.getJspName();
				if ( nextJspName != null && jspName.equalsIgnoreCase( nextJspName ) )
					return evInfo;
			}
		}
		
		return null;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_evListBox;
	}
	
	
	/**
	 * Issue an ajax request to get the folder for the given id.  After we get the folder
	 * we will update the name of the selected folder.
	 */
	private void getFolder( final String folderId )
	{
		AsyncCallback<GwtFolder> callback;
		
		callback = new AsyncCallback<GwtFolder>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetFolder(),
					folderId );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtFolder gwtFolder )
			{
				if ( gwtFolder != null )
				{
					// Update the name of the selected folder.
					m_currentFolderNameLabel.setText( gwtFolder.getFolderName() );
					m_currentFolderNameLabel.removeStyleName( "noFolderSelected" );
					m_currentFolderNameLabel.addStyleName( "bold" );
				}
			}
		};

		GwtTeaming.getRpcService().getFolder( HttpRequestInfo.createHttpRequestInfo(), null, folderId, callback );
	}
	

	/**
	 * Return the jsp name of the selected view
	 */
	public String getJspName()
	{
		EnhancedViewInfo evInfo;
		
		// Get the selected view.
		evInfo = getSelectedView();
		if ( evInfo != null )
			return evInfo.getJspName();
		
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
	 * Return the selected view.
	 */
	private EnhancedViewInfo getSelectedView()
	{
		int selectedIndex;
		
		// Get the selected index from the listbox that holds the list of views.
		selectedIndex = m_evListBox.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			String jspName;
			
			jspName = m_evListBox.getValue( selectedIndex );
			return getViewByJspName( jspName );
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
				
				// Hide the find control.
				hideFolderFindControl();
				
				// Issue an ajax request to get information about the selected folder.
				getFolder( m_folderId );
			}
			// Are we dealing with a GwtFolderEntry object?
			else if ( selectedObj instanceof GwtFolderEntry )
			{
				GwtFolderEntry gwtFolderEntry;
				
				gwtFolderEntry = (GwtFolderEntry) selectedObj;
				m_entryId = gwtFolderEntry.getEntryId();

				// Hide the find control.
				hideEntryFindControl();
				
				// Issue an ajax request to get information about the selected entry.
				getEntry( m_entryId );
			}
		}
	}
	
	/**
	 * This method gets called when the user selects a view in the listbox.
	 */
	private void handleViewSelected()
	{
		// Dance the ui based on the selected view
		danceControls();
	}
	
	/**
	 * 
	 */
	private void hideEntryFindControl()
	{
		m_entryFindPanel.setVisible( false );
		m_entryFindCtrl.hideSearchResults();
	}
	
	
	/**
	 * 
	 */
	private void hideFolderFindControl()
	{
		m_folderFindPanel.setVisible( false );
		m_folderFindCtrl.hideSearchResults();
	}
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		EnhancedViewProperties properties;
		
		m_folderId = null;
		m_entryId = null;
		m_numEntriesToShowTxtBox.setText( "" );
		m_showFolderTitleCkBox.setValue( false );
		m_showEntryTitleCkBox.setValue( false );
		m_descLabel.setText( "" );
		
		properties = (EnhancedViewProperties) props;

		// Select the appropriate view in the listbox.
		selectViewByJspName( properties.getJspName() );
		
		m_folderId = properties.getFolderId();
		m_entryId = properties.getEntryId();
		
		// Initialize the controls when a folder is required.
		{
			// Do we have a folder?
			if ( m_folderId != null && m_folderId.length() > 0 )
			{
				int num;
				
				// Yes
				// Update the name of the currently selected folder.
				m_currentFolderNameLabel.setText( properties.getFolderName() ); 
				m_currentFolderNameLabel.removeStyleName( "noFolderSelected" );
				m_currentFolderNameLabel.addStyleName( "bold" );

				m_showFolderTitleCkBox.setValue( properties.getShowTitleValue() );
				
				num = properties.getNumEntriesToBeShownValue();
				m_numEntriesToShowTxtBox.setText( String.valueOf( num ) );
			}
			else
			{
				// No
				m_currentFolderNameLabel.setText( GwtTeaming.getMessages().noFolderSelected() );
				m_currentFolderNameLabel.addStyleName( "noFolderSelected" );
				m_currentFolderNameLabel.removeStyleName( "bold" );
			}

			// Hide the search-results widget.
			m_folderFindCtrl.hideSearchResults();
			m_folderFindCtrl.setInitialSearchString( "" );

			hideFolderFindControl();
			
			// Show the edit button.
			m_folderEditBtn.setVisible( true );
		}

		// Initialize the controls when an entry is required.
		{
			// Do we have an entry?
			if ( m_entryId != null && m_entryId.length() > 0 )
			{
				// Update the name of the currently selected entry.
				m_currentEntryNameLabel.setText( properties.getEntryName() );
				m_currentEntryNameLabel.removeStyleName( "noEntrySelected" );
				m_currentEntryNameLabel.addStyleName( "bold" );
				 
				m_showEntryTitleCkBox.setValue( properties.getShowTitleValue() );
			}
			else
			{
				// No
				m_currentEntryNameLabel.setText( GwtTeaming.getMessages().noEntrySelected() );
				m_currentEntryNameLabel.addStyleName( "noEntrySelected" );
				m_currentEntryNameLabel.removeStyleName( "bold" );
			}

			// Hide the search-results widget.
			m_entryFindCtrl.hideSearchResults();
			m_entryFindCtrl.setInitialSearchString( "" );
			
			// Hide the find control.
			hideEntryFindControl();
			
			// Show the edit button.
			m_entryEditBtn.setVisible( true );
		}
		
		// Hide/show the appropriate controls on the page based on the selected view.
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
	 * Select a view in the list box for the given jsp name.
	 */
	private int selectViewByJspName( String jspName )
	{
		if ( jspName != null && jspName.length() > 0 )
		{
			int i;
			
			// Go through the list box and select the view whose jsp name matches the given jsp name.
			for (i = 0; i < m_evListBox.getItemCount(); ++i)
			{
				String nextJspName;
				
				nextJspName = m_evListBox.getValue( i );
				if ( nextJspName != null )
				{
					if ( nextJspName.equalsIgnoreCase( jspName ) )
					{
						m_evListBox.setSelectedIndex( i );
						return i;
					}
				}
			}
		}
		else
			m_evListBox.setSelectedIndex( 0 );
		
		return 0;
	}

	/**
	 * Show the find control and give it the focus.
	 */
	private void showEntryFindControl()
	{
		FocusWidget focusWidget;

		m_entryFindPanel.setVisible( true );

		focusWidget = m_entryFindCtrl.getFocusWidget();
		if ( focusWidget != null )
			focusWidget.setFocus( true );
	}

	/**
	 * Show the folder find control and give it the focus.
	 */
	private void showFolderFindControl()
	{
		FocusWidget focusWidget;

		m_folderFindPanel.setVisible( true );

		focusWidget = m_folderFindCtrl.getFocusWidget();
		if ( focusWidget != null )
			focusWidget.setFocus( true );
	}

}
