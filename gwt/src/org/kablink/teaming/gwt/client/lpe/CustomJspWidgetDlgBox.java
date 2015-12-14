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
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.SizeCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author jwootton
 *
 */
public class CustomJspWidgetDlgBox extends DlgBox
	implements KeyPressHandler,
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private TextBox m_jspNameTxtBox = null;
	private CheckBox m_assocFolderCkBox = null;
	private CheckBox m_assocEntryCkBox = null;
	private LandingPageEditor m_lpe;
	private SizeCtrl m_sizeCtrl = null;
	
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
	private FlowPanel m_entryFindPanel;
	private FindCtrl m_entryFindCtrl = null;
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
	public CustomJspWidgetDlgBox(
		LandingPageEditor lpe,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		CustomJspProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this );
		
		m_lpe = lpe;
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().customJspProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end CustomJspWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
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

		// Add an empty div that is as wide as the find control.  We do this so when we
		// show/hide the find control the size of the dialog doesn't change width.
		{
			Label spacer;
			
			spacer = new Label();
			spacer.getElement().getStyle().setWidth( 440, Unit.PX );
			spacer.getElement().getStyle().setHeight( 2, Unit.PX );
			mainPanel.add( spacer );
		}

		// Add the size control
		m_sizeCtrl = new SizeCtrl();
		mainPanel.add( m_sizeCtrl );

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
		FlowPanel panel;
		InlineLabel inlineLabel;
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "lpeMarginLeft1Point5" );
		
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
	}// end createSelectEntryPanel()
	
	/**
	 * Create the controls that will be needed if the user selects "Associate a folder with this custom jsp"
	 */
	public Panel createSelectFolderPanel()
	{
		VerticalPanel mainPanel;
		FlexTable table;
		FlowPanel panel;
		InlineLabel inlineLabel;
		Label label;
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "lpeMarginLeft1Point5" );
		mainPanel.setVisible( false );

		table = new FlexTable();
		table.setCellSpacing( 8 );

		mainPanel.add( table );

		// Add a label that will say Folder:
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
	@Override
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

		return properties;
	}// end getDataFromDlg()
	
	
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
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_jspNameTxtBox;
	}// end getFocusWidget()
	
	
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
	 * 
	 */
	private Style.Overflow getOverflow()
	{
		return m_sizeCtrl.getOverflow();
	}

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
		CustomJspProperties properties;
		
		m_folderId = null;
		m_entryId = null;
		m_assocFolderCkBox.setValue( false );
		m_numEntriesToShowTxtBox.setText( "" );
		m_showFolderTitleCkBox.setValue( false );
		m_showEntryTitleCkBox.setValue( false );
		
		properties = (CustomJspProperties) props;

		m_jspNameTxtBox.setText( properties.getJspName() );

		m_assocFolderCkBox.setValue( false );
		m_assocEntryCkBox.setValue( false );

		m_folderId = properties.getFolderId();
		m_entryId = properties.getEntryId();

		// Initialize the size control.
		m_sizeCtrl.init( properties.getWidth(), properties.getWidthUnits(), properties.getHeight(), properties.getHeightUnits(), properties.getOverflow() );

		// Do we have a folder?
		if ( m_folderId != null && m_folderId.length() > 0 )
		{
			int num;
			
			// Yes
			// Check the "Associate a folder..." checkbox.
			m_assocFolderCkBox.setValue( true );
			
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

		hideFolderFindControl();
		
		// Show the edit button.
		m_folderEditBtn.setVisible( true );
		
		// Do we have an entry?
		if ( m_entryId != null && m_entryId.length() > 0 )
		{
			// Check the "Associate an entry..." checkbox.
			m_assocEntryCkBox.setValue( true );

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

		// Hide the find control.
		hideEntryFindControl();
		
		// Show the edit button.
		m_entryEditBtn.setVisible( true );
		
		// Hide the search-results widget.
		if ( m_folderFindCtrl != null )
		{
			m_folderFindCtrl.hideSearchResults();
			m_folderFindCtrl.setInitialSearchString( "" );
		}

		// Hide the search-results widget.
		if ( m_entryFindCtrl != null )
		{
			m_entryFindCtrl.hideSearchResults();
			m_entryFindCtrl.setInitialSearchString( "" );
		}
		
		// Hide/show the appropriate controls on the page based on which checkbox is checked,
		// "Associate with folder" or "Associate with entry"
		danceControls();
	}// end init()

	
	/*
	 * This method gets called when the user clicks on the ok or cancel button.
	 */
	@Override
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
}// end CustomJspWidgetDlgBox
