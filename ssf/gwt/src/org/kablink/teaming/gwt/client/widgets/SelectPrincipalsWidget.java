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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtPrincipal;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtPrincipal.PrincipalClass;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This widget is intended to display a list of selected principals and to let the user add
 * and remove principals to the list.
 * 
 * @author jwootton
 *
 */
public class SelectPrincipalsWidget extends Composite
	implements SearchFindResultsEvent.Handler
{
	private int m_numCols = 0;
	private FindCtrl m_findCtrl;
	private FlexTable m_principalsTable;
	private FlowPanel m_principalsTablePanel;
	private FlexCellFormatter m_principalsCellFormatter;
	private HTMLTable.RowFormatter m_principalsRowFormatter;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private ImageResource m_deleteImgR;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
	};

	
	
	/**
	 * This widget is used to display a principal's name.  If the principal is a group
	 * then the user can click on the name and see the members of the group.
	 */
	public class PrincipalNameWidget extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private GwtPrincipal m_principal;
		private InlineLabel m_nameLabel;
		private GroupMembershipPopup m_groupMembershipPopup;
		
		/**
		 * 
		 */
		public PrincipalNameWidget( GwtPrincipal principal )
		{
			FlowPanel panel;
			String name;
			
			m_principal = principal;
			
			panel = new FlowPanel();
			
			name = principal.getName();
			if ( principal.getPrincipalClass() == PrincipalClass.GROUP )
			{
				if ( "allusers".equalsIgnoreCase( name ) || "allextusers".equalsIgnoreCase( name ) )
					name = principal.getTitle();
			}
			m_nameLabel = new InlineLabel( name );
			m_nameLabel.setTitle( principal.getSecondaryDisplayText() );
			m_nameLabel.addStyleName( "selectPrincipalsWidget_PrincipalNameLabel" );
			panel.add( m_nameLabel );
			
			// If we are dealing with a group, let the user click on the group.
			if ( principal.getPrincipalClass() == PrincipalClass.GROUP )
			{
				m_nameLabel.addClickHandler( this );
				m_nameLabel.addMouseOverHandler( this );
				m_nameLabel.addMouseOutHandler( this );
			}
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * Close the group membership popup if it is open.
		 */
		public void closePopups()
		{
			if ( m_groupMembershipPopup != null )
				m_groupMembershipPopup.closePopups();
		}
		
		/**
		 * This gets called when the user clicks on the principal's name.  This will only
		 * be called if the principal is a group.
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			// Create a popup that will display the membership of this group.
			if ( m_groupMembershipPopup == null )
			{
				m_groupMembershipPopup = new GroupMembershipPopup(
															true,
															false,
															m_principal.getName(),
															m_principal.getIdLong().toString() );
			}
			
			m_groupMembershipPopup.setPopupPosition( getAbsoluteLeft(), getAbsoluteTop() );
			m_groupMembershipPopup.show();
		}
		
		/**
		 * Remove the mouse-over style from the name. 
		 */
		@Override
		public void onMouseOut( MouseOutEvent event )
		{
			m_nameLabel.removeStyleName( "selectPrincipalsWidget_NameHover" );
		}

		
		/**
		 * Add the mouse-over style to the name.
		 */
		@Override
		public void onMouseOver( MouseOverEvent event )
		{
			m_nameLabel.addStyleName( "selectPrincipalsWidget_NameHover" );
		}
	}
	

	/**
	 * This widget is used to remove a principal from the list
	 */
	private class RemovePrincipalWidget extends Composite
		implements ClickHandler
	{
		private GwtPrincipal m_principal;
		
		/**
		 * 
		 */
		public RemovePrincipalWidget( GwtPrincipal principal )
		{
			FlowPanel panel;
			Image delImg;
			
			m_principal = principal;
			
			panel = new FlowPanel();
			panel.addStyleName( "selectPrincipalsWidget_RemovePrincipalPanel" );
			
			delImg = new Image( m_deleteImgR );
			delImg.addStyleName( "cursorPointer" );
			delImg.getElement().setAttribute( "title", getRemovePrincipalHint() );
			delImg.addClickHandler( this );
			
			panel.add( delImg );
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * 
		 */
		public GwtPrincipal getPrincipal()
		{
			return m_principal;
		}

		/**
		 * This gets called when the user clicks on the remove principal image.
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					removePrincipal( m_principal );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	


	/**
	 * 
	 */
	@SuppressWarnings("unused")
	public SelectPrincipalsWidget()
	{
		FlowPanel mainPanel;
		final FlexTable table;
		HTMLTable.RowFormatter rowFormatter;
		FlexCellFormatter cellFormatter;
		int nextRow;

		mainPanel = new FlowPanel();
		
		table = new FlexTable();
		rowFormatter = table.getRowFormatter();
		cellFormatter = table.getFlexCellFormatter();
		nextRow = 0;
		mainPanel.add( table );
		
		// Create the FindCtrl
		{
			final int findCtrlRow;
			InlineLabel label;
			
			label = new InlineLabel( getSelectPrincipalLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			findCtrlRow = nextRow;
			++nextRow;
			FindCtrl.createAsync( this, GwtSearchCriteria.SearchType.PRINCIPAL, new FindCtrlClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( FindCtrl findCtrl )
				{
					m_findCtrl = findCtrl;
					m_findCtrl.setIsSendingEmail( true );

					// Set the filter of the Find Control to only search for users and groups.
					m_findCtrl.setSearchType( SearchType.PRINCIPAL );

					table.setWidget( findCtrlRow, 1, m_findCtrl );
				}
			});
		}

		// Create a table to hold the list of principals
		{
			m_principalsTablePanel = new FlowPanel();
			m_principalsTablePanel.addStyleName( getPrincipalsTablePanelStyle() );

			m_principalsTable = new FlexTable();
			m_principalsTable.addStyleName( getPrincipalsTableStyle() );
			m_principalsTable.setCellSpacing( 0 );

			m_principalsRowFormatter = m_principalsTable.getRowFormatter();
			m_principalsCellFormatter = m_principalsTable.getFlexCellFormatter();

			m_principalsTablePanel.add( m_principalsTable );

			m_principalsCellFormatter = m_principalsTable.getFlexCellFormatter();

			mainPanel.add( m_principalsTablePanel );

			setColumnHeaders();
		}

		// Create an image resource for the delete image.
		m_deleteImgR = GwtTeaming.getImageBundle().delete();
		
		initWidget( mainPanel );
	}
	
	/**
	 * Add the "No users or groups have been selected" text to the table that holds the list of principals.
	 */
	private void addNoPrincipalsMessage()
	{
		int row;
		String txt;
		
		row = 1;
		m_principalsCellFormatter.setColSpan( row, 0, m_numCols );
		m_principalsCellFormatter.setWordWrap( row, 0, false );
		m_principalsCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_principalsCellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_principalsCellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_principalsCellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		txt = getNoPrincipalsHint();
		m_principalsTable.setText( row, 0, txt );
	}

	/**
	 * Override this method to add additional information to the principal object.
	 */
	protected void addAdditionalPrincipalInfo( GwtPrincipal principal )
	{
		
	}
	
	/**
	 * Add the given principal to the first of the table that holds the list of principals
	 */
	private void addPrincipal( GwtPrincipal principal, boolean highlight )
	{
		int row;
		int col;
		int i;
		RemovePrincipalWidget removeWidget;

		row = m_principalsTable.getRowCount();
		
		// Do we have any principals in the table?
		if ( row == 2 )
		{
			String text;
			
			// Maybe
			// The first row might be the message, "No principals selected"
			// Get the text from the first row.
			text = m_principalsTable.getText( 1, 0 );
			
			// Does the first row contain a message?
			if ( text != null && text.equalsIgnoreCase( getNoPrincipalsHint() ) )
			{
				// Yes
				m_principalsTable.removeRow( 1 );
			}
		}
		
		// Remove any highlight that may be on the first row.
		unhighlightPrincipal( 1 );
		
		// Add the principal as the first item in the table.
		row = 1;
		m_principalsTable.insertRow( row );
		
		// Should we highlight the row?
		if ( highlight )
		{
			// Yes
			highlightPrincipal( row );
		}
		
		for ( col = 0; col < getNumCols(); ++col)
		{
			Widget widget;
			
			m_principalsCellFormatter.setColSpan( row, col, 1 );
			m_principalsCellFormatter.setWordWrap( row, col, false );
			m_principalsCellFormatter.addStyleName( row, col, "selectPrincipals_PrincipalsTable_Cell" );
			widget = getWidgetForCol( col, principal );
			m_principalsTable.setWidget( row, col, widget );
		}

		// Add the "remove principal" widget
		{
			removeWidget = new RemovePrincipalWidget( principal );
			m_principalsTable.setWidget( row, col, removeWidget );
			++col;
		}
		
		// Add the necessary styles to the cells in the row.
		m_principalsCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_principalsCellFormatter.addStyleName( row, m_numCols-1, "oltBorderRight" );
		for (i = 0; i < m_numCols; ++i)
		{
			m_principalsCellFormatter.addStyleName( row, i, "oltContentBorderBottom" );
			m_principalsCellFormatter.addStyleName( row, i, "oltContentPadding" );
		}
		
		adjustPrincipalsTablePanelHeight();
	}
	
	/**
	 * 
	 */
	private void adjustPrincipalsTablePanelHeight()
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int height;
				
				// Get the height of the table that holds the list of shares.
				height = m_principalsTable.getOffsetHeight();
				
				// If the height of the table is greater than the fixed height put an overflow auto on the panel
				// and give the panel a fixed height.
				if ( height >= getPrincipalsTableFixedHeight() )
					m_principalsTablePanel.addStyleName( getPrincipalsTablePanelStyle() );
				else
					m_principalsTablePanel.removeStyleName( getPrincipalsTablePanelStyle() );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Close any group popups that may be visible.
	 */
	public void closePopups()
	{
		int i;
		
		// Go through the list of principals and close any "Group Membership" popups that may be open.
		for (i = 1; i < m_principalsTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_principalsTable.getCellCount( i ) > 2 )
			{
				// Get the PrincipalNameWidget from the first column.
				widget = m_principalsTable.getWidget( i, 0 );
				if ( widget != null && widget instanceof PrincipalNameWidget )
				{
					// Close any group membership popup that this widget may have open.
					((PrincipalNameWidget) widget).closePopups();
				}
			}
		}
	}

	/**
	 * Find the given principal in the table that holds the principals.
	 */
	private int findPrincipal( GwtPrincipal principal )
	{
		int i;

		if ( principal == null )
			return -1;
		
		// Look through the table for the given principal.
		// Recipients start in row 1.
		for (i = 1; i < m_principalsTable.getRowCount() && m_principalsTable.getCellCount( i ) == m_numCols; ++i)
		{
			Widget widget;
			
			// Get the RemovePrincipalWidget from the last column.
			widget = m_principalsTable.getWidget( i, m_numCols-1 );
			if ( widget != null && widget instanceof RemovePrincipalWidget )
			{
				GwtPrincipal nextPrincipal;
				
				nextPrincipal = ((RemovePrincipalWidget) widget).getPrincipal();
				if ( nextPrincipal != null )
				{
					if ( principal.equals( nextPrincipal ) )
					{
						// We found the principal
						return i;
					}
				}
			}
		}// end for()
		
		// If we get here we did not find the principal.
		return -1;
	}

	/**
	 * Override this method if you need a different string displayed
	 */
	protected String getCantSelectExternalUserPrompt()
	{
		return GwtTeaming.getMessages().selectPrincipalsWidget_CantSelectExternalUserPrompt();
	}
	
	/**
	 * Override this method to supply the name of the given column
	 */
	protected String getColName( int col )
	{
		if ( col == 0 )
			return GwtTeaming.getMessages().selectPrincipalsWidget_NameCol();
	
		if ( col == 1 )
			return GwtTeaming.getMessages().selectPrincipalsWidget_TypeCol();
		
		return "Unknown";
	}
	
	/**
	 * Override this method to supply the width of the given column
	 */
	protected String getColWidth( int col )
	{
		if ( col == 0 )
			return "75%";
	
		if ( col == 1 )
			return "20%";
		
		return "10%";
	}
	
	/**
	 * Return a list of selected principals
	 */
	public ArrayList<GwtPrincipal> getListOfSelectedPrincipals()
	{
		int i;
		ArrayList<GwtPrincipal> listOfPrincipals;
		
		listOfPrincipals = new ArrayList<GwtPrincipal>();
		
		// Look through the table and add each GwtPrincipal to the list.
		for (i = 1; i < m_principalsTable.getRowCount() && m_principalsTable.getCellCount( i ) == m_numCols; ++i)
		{
			Widget widget;
			
			// Get the RemovePrincipalWidget from the last column.
			widget = m_principalsTable.getWidget( i, m_numCols-1 );
			if ( widget != null && widget instanceof RemovePrincipalWidget )
			{
				GwtPrincipal nextPrincipal;
				
				nextPrincipal = ((RemovePrincipalWidget) widget).getPrincipal();
				listOfPrincipals.add( nextPrincipal );
			}
		}
		
		return listOfPrincipals;
	}

	/**
	 * Override this method if you need a different string displayed
	 */
	protected String getNoPrincipalsHint()
	{
		return GwtTeaming.getMessages().selectPrincipalsWidget_NoPrincipalsHint();
	}
	
	/**
	 * Override this method to return the number of columns to be displayed in the list of selected principals
	 */
	protected int getNumCols()
	{
		return 2;
	}
	
	/**
	 * Override this method if you need a different string displayed
	 */
	protected String getPrincipalAlreadyInListPrompt( String principalName )
	{
		return GwtTeaming.getMessages().selectPrincipalsWidget_PrincipalAlreadyInListPrompt( principalName );
	}
	
	/**
	 * Override this method to provide a different style name
	 */
	protected String getPrincipalsTablePanelStyle()
	{
		return "selectPrincipalsWidget_PrincipalsTablePanelHeight";
	}
	
	/**
	 * Override this method to provide a different fixed height
	 */
	protected int getPrincipalsTableFixedHeight()
	{
		return 200;
	}
	
	/**
	 * Override this method to provide a different style name
	 */
	protected String getPrincipalsTableStyle()
	{
		return "selectPrincipalsWidget_PrincipalsTable";
	}
	
	/**
	 * Override this method if you need a different string displayed.
	 */
	protected String getRemovePrincipalHint()
	{
		return GwtTeaming.getMessages().selectPrincipalsWidget_RemovePrincipalHint();
	}
	
	/**
	 * Override this method if you need a different string displayed.
	 */
	protected String getSelectPrincipalLabel()
	{
		return GwtTeaming.getMessages().selectPrincipalsWidget_SelectPrincipalsLabel();
	}
	
	/**
	 * Override this method to return the widget used for a particular column
	 */
	protected Widget getWidgetForCol( int col, GwtPrincipal principal )
	{
		if ( col == 0 )
		{
			// Add the principal name
			return new PrincipalNameWidget( principal );
		}
		
		if ( col == 1 )
		{
			// Add the recipient type
			return new InlineLabel( principal.getTypeAsString() );
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private void highlightPrincipal( int row )
	{
		if ( row < m_principalsTable.getRowCount() )
			m_principalsRowFormatter.addStyleName( row, "selectPrincipals_PrincipalsTable_highlightRow" );
	}
	
	/**
	 * 
	 */
	private void init()
	{
		if ( m_findCtrl != null )
			m_findCtrl.setInitialSearchString( "" );

		// Remove all of the rows from the table.
		// We start at row 1 so we don't delete the header.
		while ( m_principalsTable.getRowCount() > 1 )
		{
			// Remove the 1st row that holds share information.
			m_principalsTable.removeRow( 1 );
		}
		
		// Add a message to the table telling the user there are no principals.
		addNoPrincipalsMessage();
		adjustPrincipalsTablePanelHeight();
	}
	
	/**
	 * 
	 */
	public void init( ArrayList<GwtPrincipal> listOfPrincipals )
	{
		init();
		
		if ( listOfPrincipals != null )
		{
			// Add the list of principals
			for ( GwtPrincipal nextPrincipal : listOfPrincipals )
			{
				addPrincipal( nextPrincipal, false );
			}
		}
	}
	
	/**
	 * 
	 */
	public boolean isReady()
	{
		if ( m_findCtrl != null )
			return true;
		
		return false;
	}
	
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
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
		final GwtTeamingItem selectedObj;
		Scheduler.ScheduledCommand cmd;

		// If the find results aren't for this share this dialog...
		if ( !((Widget) event.getSource()).equals( this ) )
		{
			// ...ignore the event.
			return;
		}
		
		selectedObj = event.getSearchResults();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				GwtPrincipal principal = null;
				
				// Hide the search-results widget.
				m_findCtrl.hideSearchResults();
				
				// Clear the text from the find control.
				m_findCtrl.clearText();
				
				// Are we dealing with a User?
				if ( selectedObj instanceof GwtUser )
				{
					GwtUser user;
					
					// Yes
					user = (GwtUser) selectedObj;
					
					principal = user;
				}
				// Are we dealing with a group?
				else if ( selectedObj instanceof GwtGroup )
				{
					GwtGroup group;
					
					// Yes
					group = (GwtGroup) selectedObj;
					principal = group;
				}

				// Do we have a principal to add to our list of principals?
				if ( principal != null )
				{
					// Yes
					// Is this principal already in the list?
					if ( findPrincipal( principal ) == -1 )
					{
						// No
						// Give the opportunity to add additional info to this principal
						addAdditionalPrincipalInfo( principal );
						
						// Add the principal to our list of principals
						addPrincipal( principal, true );

						// Notify that a principal was added
						principalAdded( principal );
					}
					else
					{
						// Yes, tell the user
						Window.alert( getPrincipalAlreadyInListPrompt( principal.getName() ) );
					}
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * This method gets called when a principal is added to the list.  You can override this method if
	 * you want to be notified when a user is added.
	 */
	protected void principalAdded( GwtPrincipal principal )
	{
		// Nothing to do.
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we haven't allocated a list to track events we've registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
											GwtTeaming.getEventBus(),
											REGISTERED_EVENTS,
											this,
											m_registeredEventHandlers );
		}
	}

	/**
	 * Remove the given principal from the table
	 */
	private void removePrincipal( GwtPrincipal principal )
	{
		int row;
		
		// Find the row this principal lives in.
		row = findPrincipal( principal );
		
		// Did we find the principal in the table?
		if ( row > 0 )
		{
			// Yes
			// Remove the share from the table.
			m_principalsTable.removeRow( row );

			// Did we remove the last share from the table?
			if ( m_principalsTable.getRowCount() == 1 )
			{
				// Yes
				// Add the "no users..." message to the table.
				addNoPrincipalsMessage();
			}
			
			adjustPrincipalsTablePanelHeight();
		}
	}
	
	/**
	 * Set the text in each of the header of each column.
	 */
	private void setColumnHeaders()
	{
		int col;

		// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
		// That is why we are calling DOM.setElementAttribute(...) instead.

		m_principalsRowFormatter.addStyleName( 0, "oltHeader" );

		for ( col = 0; col < getNumCols(); ++col )
		{
			String colName;
			String width;
			InlineLabel label;
			
			colName = getColName( col );
			label = new InlineLabel( colName );
			label.addStyleName( "gwtUI_nowrap" );
			width = getColWidth( col );
			m_principalsTable.setWidget( 0, col, label );
			m_principalsCellFormatter.getElement( 0, col ).setAttribute( "width", width );
		}
		
		m_principalsTable.setHTML( 0, col, "&nbsp;" );	// The delete image will go in this column.
		m_principalsCellFormatter.getElement( 0, col ).setAttribute( "width", "14px" );
		++col;

		m_numCols = col;
		
		m_principalsCellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
		for (col=0; col < m_numCols; ++col)
		{
			m_principalsCellFormatter.addStyleName( 0, col, "oltHeaderBorderTop" );
			m_principalsCellFormatter.addStyleName( 0, col, "oltHeaderBorderBottom" );
			m_principalsCellFormatter.addStyleName( 0, col, "oltHeaderPadding" );
		}
		m_principalsCellFormatter.addStyleName( 0, m_numCols-1, "oltBorderRight" );
	}
	
	/**
	 * Set the flag that determines if the user can search for external users.
	 */
	public void setSearchForExternalPrincipals( boolean canSearch )
	{
		if ( m_findCtrl != null )
			m_findCtrl.setSearchForExternalPrincipals( canSearch );
	}
	
	/**
	 * Set the flag that determines if the user can search for internal users.
	 */
	public void setSearchForInternalPrincipals( boolean canSearch )
	{
		if ( m_findCtrl != null )
			m_findCtrl.setSearchForInternalPrincipals( canSearch );
	}
	
	/**
	 * Set the flag that determines if we are going to look for ldap container objects.
	 */
	public void setSearchForLdapContainers( boolean search )
	{
		if ( m_findCtrl != null )
			m_findCtrl.setSearchForLdapContainers( search );
	}
	
	/**
	 * Unhighlight the given row in the table that holds the list of principals
	 */
	private void unhighlightPrincipal( int row )
	{
		if ( row < m_principalsTable.getRowCount() )
			m_principalsRowFormatter.removeStyleName( row, "selectPrincipals_PrincipalsTable_highlightRow" );
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}

	/**
	 * 
	 */
	public void relayout()
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				adjustPrincipalsTablePanelHeight();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	

	
	
	/**
	 * Callback interface to interact with the "SelectPrincipalsWidget"
	 * asynchronously after it loads. 
	 */
	public interface SelectPrincipalsWidgetClient
	{
		void onSuccess( SelectPrincipalsWidget widget );
		void onUnavailable();
	}

	/**
	 * Loads the SelectPrincipalsWidget split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
		final SelectPrincipalsWidgetClient client )
	{
		GWT.runAsync( SelectPrincipalsWidget.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_SelectPrincipalsWidget() );
				if ( client != null )
				{
					client.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				SelectPrincipalsWidget widget;
				
				widget= new SelectPrincipalsWidget();
				client.onSuccess( widget );
			}
		});
	}
}
