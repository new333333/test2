/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.lpe.EnhancedViewProperties.EnhancedViewType;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;
import org.kablink.teaming.gwt.client.widgets.SizeCtrl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
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
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author jwootton
 *
 */
public class EnhancedViewWidgetDlgBox extends DlgBox
	implements KeyPressHandler,
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private ListBox m_evListBox = null;
	private Label m_descLabel;
	private SizeCtrl m_sizeCtrl = null;
	private ArrayList<EnhancedViewInfo> m_views;
	private LandingPageEditor m_lpe;
	
	// The following data members are used if a folder is associated with the view.
	private String m_folderId = null;
	private Panel m_selectFolderPanel = null;
	private FlowPanel m_folderFindPanel;
	private FindCtrl m_folderFindCtrl = null;
	private CheckBox m_showFolderTitleCkBox = null;
	private Label m_numEntriesToShowLabel = null;
	private TextBox m_numEntriesToShowTxtBox = null;
	private InlineLabel m_currentFolderNameLabel = null;
	private Button m_folderEditBtn;
	
	// The following data members are used if an entry is associated with this view"
	private String m_entryId = null;
	private Panel m_selectEntryPanel = null;
	private FindCtrl m_entryFindCtrl = null;
	private FlowPanel m_entryFindPanel;
	private CheckBox m_showEntryTitleCkBox = null;
	private InlineLabel m_currentEntryNameLabel = null;
	private Button m_entryEditBtn;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
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
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this );
		
		EnhancedViewInfo evInfo;
		
		m_lpe = lpe;
		
		m_views = new ArrayList<EnhancedViewInfo>();
		evInfo = new EnhancedViewInfo( "landing_page_entry.jsp" );
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
		evInfo = new EnhancedViewInfo( "landing_page_my_calendar_events.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_task_folder.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_my_tasks.jsp" );
		m_views.add( evInfo );
		evInfo = new EnhancedViewInfo( "landing_page_survey.jsp" );
		m_views.add( evInfo );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().enhancedViewProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		EnhancedViewProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		FlexTable table;
		
		properties = (EnhancedViewProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and list box for the view
		table = new FlexTable();
		mainPanel.add( table );
		label = new Label( GwtTeaming.getMessages().enhancedViewLabel() );
		table.setWidget( 0, 0, label );
		
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
				@Override
				public void onChange( ChangeEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
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
			
			table.setWidget( 0, 1, m_evListBox );
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
		
		// Add the size control
		m_sizeCtrl = new SizeCtrl();
		mainPanel.add( m_sizeCtrl );

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
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
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
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
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
			
			final FlexTable findTable = new FlexTable();
			
			findLabel = new InlineLabel( GwtTeaming.getMessages().find() );
			findLabel.addStyleName( "findCtrlLabel" );
			findTable.setWidget( 0, 0, findLabel );
			
			FindCtrl.createAsync(
					this,
					GwtSearchCriteria.SearchType.ENTRIES,
					new FindCtrlClient() {				
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( FindCtrl findCtrl )
				{
					m_entryFindCtrl = findCtrl;
					m_entryFindCtrl.enableScope( m_lpe.getBinderId() );
					findTable.setWidget( 0, 1, m_entryFindCtrl );
				}// end onSuccess()
			} );
			
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
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
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
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
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
			
			final FlexTable findTable = new FlexTable();
			
			findLabel = new InlineLabel( GwtTeaming.getMessages().find() );
			findLabel.addStyleName( "findCtrlLabel" );
			findTable.setWidget( 0, 0, findLabel );
			
			FindCtrl.createAsync(
					this,
					GwtSearchCriteria.SearchType.PLACES,
					new FindCtrlClient() {				
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( FindCtrl findCtrl )
				{
					m_folderFindCtrl = findCtrl;
					m_folderFindCtrl.enableScope( m_lpe.getBinderId() );
					m_folderFindCtrl.setSearchForFoldersOnly( true );
					findTable.setWidget( 0, 1, m_folderFindCtrl );
				}// end onSuccess()
			} );
			
			m_folderFindPanel.add( findTable );
			mainPanel.add( m_folderFindPanel );
		}

		// Add controls for "Number of entries to show"
		table = new FlexTable();
		table.setCellSpacing( 8 );
		m_numEntriesToShowLabel = new Label( GwtTeaming.getMessages().numEntriesToShow() );
		table.setWidget( 0, 0, m_numEntriesToShowLabel );
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
			EnhancedViewType viewType;
			
			// Show the description of the view
			desc = evInfo.getDesc();
			if ( desc != null )
				m_descLabel.setText( desc );
			
			// Does the selected view require the user to select a folder?
			if ( evInfo.isFolderRequired() )
			{
				// Yes, show the ui for selecting a folder.
				m_selectFolderPanel.setVisible( true );
				
				// Show/hide the "Show title" checkbox.
				m_showFolderTitleCkBox.setVisible( evInfo.getTitleOptional() );
				
				// Is the selected view, "calendar"?
				if ( evInfo.getViewType() == EnhancedViewType.DISPLAY_CALENDAR )
				{
					// Yes, hide the "number of entries to show" controls.
					m_numEntriesToShowLabel.setVisible( false );
					m_numEntriesToShowTxtBox.setVisible( false );
				}
				else
				{
					// No, show the "number of entries to show" controls.
					m_numEntriesToShowLabel.setVisible( true );
					m_numEntriesToShowTxtBox.setVisible( true );
				}
			}
			
			// Does the selected view require the user to select an entry?
			if ( evInfo.isEntryRequired() )
			{
				// Yes, show the ui for selecting an entry.
				m_selectEntryPanel.setVisible( true );

				// Show/hide the "Show title" checkbox.
				m_showEntryTitleCkBox.setVisible( evInfo.getTitleOptional() );
			}

			// Is the "Display calendar" or the "Display my calendar events" option selected?
			viewType = evInfo.getViewType();
			if ( viewType == EnhancedViewType.DISPLAY_CALENDAR || viewType == EnhancedViewType.DISPLAY_MY_CALENDAR_EVENTS )
			{
				// Yes, hide the height controls.
				m_sizeCtrl.hideHeightControls();
			}
			else
			{
				// No, show the height controls.
				m_sizeCtrl.showHeightControls();
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
	@Override
	public PropertiesObj getDataFromDlg()
	{
		EnhancedViewProperties	properties;
		
		properties = new EnhancedViewProperties();
		
		// Save away the name of the jsp that the selected view uses.
		properties.setJspName( getJspName() );

		// Get the width and height values
		{
			int width;
			int height;
			Style.Unit units;
			
			// Get the width
			width = getWidth();
			units = getWidthUnits();
			if ( width == 0 )
			{
				// Default to 100%
				width = 100;
				units = Style.Unit.PCT;
			}
			properties.setWidth( width );
			properties.setWidthUnits( units );
			
			// Get the height
			height = getHeight();
			units = getHeightUnits();
			if ( height == 0 )
			{
				// Default to 100%
				height = 100;
				units = Style.Unit.PCT;
			}

			properties.setHeight( height );
			properties.setHeightUnits( units );
			properties.setOverflow( getOverflow() );
		}

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
		GetEntryCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
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
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				GwtFolderEntry gwtFolderEntry;
				
				gwtFolderEntry = (GwtFolderEntry) response.getResponseData();
				
				if ( gwtFolderEntry != null )
				{
					// Update the name of the selected entry.
					m_currentEntryNameLabel.setText( gwtFolderEntry.getEntryName() );
					m_currentEntryNameLabel.removeStyleName( "noEntrySelected" );
					m_currentEntryNameLabel.addStyleName( "bold" );
				}
			}
		};

		cmd = new GetEntryCmd( null, entryId );
		GwtClientHelper.executeCommand( cmd, callback );
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
	@Override
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
		GetFolderCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
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
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				GwtFolder gwtFolder;
				
				gwtFolder = (GwtFolder) response.getResponseData();
				
				if ( gwtFolder != null )
				{
					// Update the name of the selected folder.
					m_currentFolderNameLabel.setText( gwtFolder.getFolderName() );
					m_currentFolderNameLabel.removeStyleName( "noFolderSelected" );
					m_currentFolderNameLabel.addStyleName( "bold" );
				}
			}
		};

		cmd = new GetFolderCmd( null, folderId );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * 
	 */
	private int getHeight()
	{
		return m_sizeCtrl.getHeight();
	}
	
	/**
	 * 
	 */
	private Style.Unit getHeightUnits()
	{
		return m_sizeCtrl.getHeightUnits();
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
		
		numEntries = 5;
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
	 * 
	 */
	private Style.Overflow getOverflow()
	{
		return m_sizeCtrl.getOverflow();
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
	 * 
	 */
	private int getWidth()
	{
		return m_sizeCtrl.getWidth();
	}
	
	/**
	 * 
	 */
	private Style.Unit getWidthUnits()
	{
		return m_sizeCtrl.getWidthUnits();
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
		if ( m_entryFindCtrl != null )
			m_entryFindCtrl.hideSearchResults();
	}
	
	
	/**
	 * 
	 */
	private void hideFolderFindControl()
	{
		m_folderFindPanel.setVisible( false );
		if ( m_folderFindCtrl != null )
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
		if ( m_numEntriesToShowTxtBox != null )
			m_numEntriesToShowTxtBox.setText( "5" );
		m_showFolderTitleCkBox.setValue( false );
		m_showEntryTitleCkBox.setValue( false );
		m_descLabel.setText( "" );
		
		properties = (EnhancedViewProperties) props;

		// Select the appropriate view in the listbox.
		selectViewByJspName( properties.getJspName() );
		
		m_folderId = properties.getFolderId();
		m_entryId = properties.getEntryId();
		
		// Initialize the size control.
		m_sizeCtrl.init( properties.getWidth(), properties.getWidthUnits(), properties.getHeight(), properties.getHeightUnits(), properties.getOverflow() );

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
			if ( m_folderFindCtrl != null )
			{
				m_folderFindCtrl.hideSearchResults();
				m_folderFindCtrl.setInitialSearchString( "" );
			}

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
			if ( m_entryFindCtrl != null )
			{
				m_entryFindCtrl.hideSearchResults();
				m_entryFindCtrl.setInitialSearchString( "" );
			}
			
			// Hide the find control.
			hideEntryFindControl();
			
			// Show the edit button.
			m_entryEditBtn.setVisible( true );
		}
		
		// Hide/show the appropriate controls on the page based on the selected view.
		danceControls();
	}
	
	/**
	 * This method gets called when the user types in the "number of entries to show", "width" or "height" text box.
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
		FocusWidget focusWidget = null;

		m_entryFindPanel.setVisible( true );

		if ( m_entryFindCtrl != null )
			focusWidget = m_entryFindCtrl.getFocusWidget();
		
		if ( focusWidget != null )
			focusWidget.setFocus( true );
	}

	/**
	 * Show the folder find control and give it the focus.
	 */
	private void showFolderFindControl()
	{
		FocusWidget focusWidget = null;

		m_folderFindPanel.setVisible( true );

		if ( m_folderFindCtrl != null )
			focusWidget = m_folderFindCtrl.getFocusWidget();
		
		if ( focusWidget != null )
			focusWidget.setFocus( true );
	}
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults( SearchFindResultsEvent event )
	{
		// If the find results aren't for this widget...
		if ( !((Widget) event.getSource()).equals( this ) )
		{
			// ...ignore the event.
			return;
		}
		
		// Are we dealing with a GwtFolder object?
		GwtTeamingItem selectedObj = event.getSearchResults();
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
	}// end onSearchFindResults()
}
