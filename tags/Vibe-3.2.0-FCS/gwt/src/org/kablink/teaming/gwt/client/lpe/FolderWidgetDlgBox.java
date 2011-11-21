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
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.Scheduler;
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
public class FolderWidgetDlgBox extends DlgBox
	implements KeyPressHandler,
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private CheckBox m_showTitleCkBox = null;
	private CheckBox m_showDescCkBox = null;
	private CheckBox m_showEntriesOpenedCkBox = null;
	private TextBox m_numEntriesToShowTxtBox;
	private FindCtrl m_findCtrl = null;
	private FlowPanel m_findPanel;
	private String m_folderId = null;
	private InlineLabel m_currentFolderNameLabel = null;
	private Button m_editBtn;
	private LandingPageEditor m_lpe;

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
	public FolderWidgetDlgBox(
		LandingPageEditor lpe,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		FolderProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this );
		
		m_lpe = lpe;
		
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
		FlowPanel panel;
		
		properties = (FolderProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

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
			
			m_editBtn = new Button( GwtTeaming.getMessages().edit() );
			m_editBtn.addStyleName( "teamingButton" );
			panel.add( m_editBtn );
			
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
							showFindControl();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			};
			m_editBtn.addClickHandler( clickHandler );
		}

		table.setWidget( 0, 1, panel );

		// Add a "find" control
		{
			InlineLabel findLabel;
			
			m_findPanel = new FlowPanel();
			m_findPanel.addStyleName( "findCtrlPanel" );
			m_findPanel.setVisible( false );
			
			// Add an image the user can click on to close the find panel.
			{
				Image img;
				ImageResource imageResource;
				ClickHandler clickHandler;
				
				imageResource = GwtTeaming.getImageBundle().closeX();
				img = new Image( imageResource );
				img.addStyleName( "findCtrlCloseImg" );
				img.getElement().setAttribute( "title", GwtTeaming.getMessages().close() );
				m_findPanel.add( img );
		
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
								hideFindControl();
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
					m_findCtrl = findCtrl;
					m_findCtrl.enableScope( m_lpe.getBinderId() );
					m_findCtrl.setSearchForFoldersOnly( true );
					findTable.setWidget( 0, 1, m_findCtrl );
				}// end onSuccess()
			} );
			
			m_findPanel.add( findTable );
			mainPanel.add( m_findPanel );
		}
		
		// Add controls for "Number of entries to show"
		table = new FlexTable();
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

		// Add an empty div that is as wide as the find control.  We do this so when we
		// show/hide the find control the size of the dialog doesn't change width.
		{
			Label spacer;
			
			spacer = new Label();
			spacer.getElement().getStyle().setWidth( 440, Unit.PX );
			spacer.getElement().getStyle().setHeight( 2, Unit.PX );
			mainPanel.add( spacer );
		}

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
	 * 
	 */
	private void hideFindControl()
	{
		m_findPanel.setVisible( false );
		m_findCtrl.hideSearchResults();
	}
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		FolderProperties properties;
		int num;
		
		properties = (FolderProperties) props;

		// Remember the folder id that was passed to us.
		m_folderId = properties.getFolderId();

		// Do we have a folder?
		if ( m_folderId != null && m_folderId.length() > 0 )
		{
			//Yes
			// Update the name of the currently selected folder.
			m_currentFolderNameLabel.setText( properties.getFolderName() ); 

			m_currentFolderNameLabel.removeStyleName( "noFolderSelected" );
			m_currentFolderNameLabel.addStyleName( "bold" );
		}
		else
		{
			// No
			m_currentFolderNameLabel.setText( GwtTeaming.getMessages().noFolderSelected() );
			m_currentFolderNameLabel.addStyleName( "noFolderSelected" );
			m_currentFolderNameLabel.removeStyleName( "bold" );
		}
		
		// Hide the find control.
		hideFindControl();
		
		// Show the edit button.
		m_editBtn.setVisible( true );
		
		m_showTitleCkBox.setValue( properties.getShowTitleValue() );
		m_showDescCkBox.setValue( properties.getShowDescValue() );
		m_showEntriesOpenedCkBox.setValue( properties.getShowEntriesOpenedValue() );
		
		// Hide the search-results widget.
		m_findCtrl.hideSearchResults();
		
		// Populate the find control's text box with the name of the selected folder.
		m_findCtrl.setInitialSearchString( "" );
		
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
	}// end onKeyPress()

	
	/**
	 * Show the find control and give it the focus.
	 */
	private void showFindControl()
	{
		FocusWidget focusWidget;

		m_findPanel.setVisible( true );

		focusWidget = m_findCtrl.getFocusWidget();
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
		
		// Make sure we are dealing with a GwtFolder object.
		GwtTeamingItem selectedObj = event.getSearchResults();
		if ( selectedObj instanceof GwtFolder )
		{
			GwtFolder gwtFolder;
			
			gwtFolder = (GwtFolder) selectedObj;
			m_folderId = gwtFolder.getFolderId();
			
			// Hide the find control.
			hideFindControl();
			
			// Issue an ajax request to get information about the selected folder.
			getFolder( m_folderId );
		}
	}// end onSearchFindResults()
}// end FolderWidgetDlgBox
