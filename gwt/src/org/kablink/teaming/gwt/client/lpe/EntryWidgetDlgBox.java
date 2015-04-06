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
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;
import org.kablink.teaming.gwt.client.widgets.SizeCtrl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author jwootton
 *
 */
public class EntryWidgetDlgBox extends DlgBox
	implements
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private SizeCtrl m_sizeCtrl = null;
	private CheckBox m_showTitleCkBox = null;
	private FindCtrl m_findCtrl = null;
	private FlowPanel m_findPanel;
	private InlineLabel m_currentEntryNameLabel = null;
	private Button m_editBtn;
	private String m_entryId = null;
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
	public EntryWidgetDlgBox(
		LandingPageEditor lpe,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EntryProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this );
		
		m_lpe = lpe;
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().entryProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end EntryWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		EntryProperties properties;
		InlineLabel		inlineLabel;
		VerticalPanel	mainPanel;
		FlexTable		table;
		FlowPanel panel;
		
		properties = (EntryProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		table = new FlexTable();
		table.setCellSpacing( 8 );

		mainPanel.add( table );
		
		// Add a label that will say Entry: name of the currently selected entry
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
			
			m_editBtn = new Button( GwtTeaming.getMessages().edit() );
			m_editBtn.addStyleName( "teamingButton" );
			panel.add( m_editBtn );
			
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
							showFindControl();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			};
			m_editBtn.addClickHandler( clickHandler );
		}

		table.setWidget( 0, 1, panel );
		
		// Add a "find" control.
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

			final String binderId = this.m_lpe.getBinderId();
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
					m_findCtrl = findCtrl;
					m_findCtrl.enableScope( binderId );
					findTable.setWidget( 0, 1, m_findCtrl );
				}// end onSuccess()
			} );
			
			m_findPanel.add( findTable );
			mainPanel.add( m_findPanel );
		}
		
		// Add the size control
		m_sizeCtrl = new SizeCtrl();
		mainPanel.add( m_sizeCtrl );

		// Add a checkbox for "Show title"
		table = new FlexTable();
		m_showTitleCkBox = new CheckBox( GwtTeaming.getMessages().showTitleBar() );
		table.setWidget( 0, 0, m_showTitleCkBox );
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
	@Override
	public PropertiesObj getDataFromDlg()
	{
		String entryId;
		
		EntryProperties	properties;
		
		properties = new EntryProperties();
		
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

		// Save away the "show border" value.
		properties.setShowTitle( getShowTitleValue() );
		
		// Save away the entry id.
		// Did the user select an entry?
		entryId = getEntryIdValue();
		if ( entryId == null || entryId.length() == 0 )
		{
			// No, tell them they need to
			Window.alert( GwtTeaming.getMessages().pleaseSelectAnEntry() );
			return null;
		}
		
		properties.setEntryId( entryId );
		
		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return entry id of the selected entry.
	 */
	public String getEntryIdValue()
	{
		// m_entryId will always hold the id of the selected entry.
		return m_entryId;
	}// end getEntryIdValue()
	
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
		if ( m_findCtrl != null )
			return m_findCtrl.getFocusWidget();
		
		return null;
	}// end getFocusWidget()
	
	
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
	 * 
	 */
	private Style.Overflow getOverflow()
	{
		return m_sizeCtrl.getOverflow();
	}

	/**
	 * Return true if the "show title" checkbox is checked.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitleCkBox.getValue().booleanValue();
	}// end getShowBorderValue()
	
	
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
	private void hideFindControl()
	{
		m_findPanel.setVisible( false );
		if ( m_findCtrl != null )
			m_findCtrl.hideSearchResults();
	}
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( PropertiesObj props )
	{
		EntryProperties properties;
		
		properties = (EntryProperties) props;
		
		// Remember the entry id that was passed to us.
		m_entryId = properties.getEntryId();

		// Do we have an entry?
		if ( m_entryId != null && m_entryId.length() > 0 )
		{
			// Yes
			// Update the name of the currently selected entry.
			m_currentEntryNameLabel.setText( properties.getEntryName() );
			m_currentEntryNameLabel.removeStyleName( "noEntrySelected" );
			m_currentEntryNameLabel.addStyleName( "bold" );
		}
		else
		{
			// No
			m_currentEntryNameLabel.setText( GwtTeaming.getMessages().noEntrySelected() );
			m_currentEntryNameLabel.addStyleName( "noEntrySelected" );
			m_currentEntryNameLabel.removeStyleName( "bold" );
		}
		
		// Initialize the size control.
		m_sizeCtrl.init( properties.getWidth(), properties.getWidthUnits(), properties.getHeight(), properties.getHeightUnits(), properties.getOverflow() );

		// Hide the find control.
		hideFindControl();
		
		// Show the edit button.
		m_editBtn.setVisible( true );

		m_showTitleCkBox.setValue( properties.getShowTitleValue() );
		
		if ( m_findCtrl != null )
		{
			// Hide the search-results widget.
			m_findCtrl.hideSearchResults();
			
			m_findCtrl.setInitialSearchString( "" );
		}
	}// end init()
	
	/**
	 * Show the find control and give it the focus.
	 */
	private void showFindControl()
	{
		FocusWidget focusWidget = null;

		m_findPanel.setVisible( true );

		if ( m_findCtrl != null )
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
		
		// Make sure we are dealing with a GwtFolderEntry object.
		GwtTeamingItem selectedObj = event.getSearchResults();
		if ( selectedObj instanceof GwtFolderEntry )
		{
			GwtFolderEntry gwtFolderEntry;
			
			gwtFolderEntry = (GwtFolderEntry) selectedObj;
			m_entryId = gwtFolderEntry.getEntryId();
			
			// Hide the find control.
			hideFindControl();
			
			// Issue an ajax request to get information about the selected entry.
			getEntry( m_entryId );
		}
	}// end onSearchFindResults()
}// end EntryWidgetDlgBox
