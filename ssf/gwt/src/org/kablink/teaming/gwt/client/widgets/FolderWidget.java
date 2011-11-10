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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.lpe.EntryProperties;
import org.kablink.teaming.gwt.client.lpe.FolderConfig;
import org.kablink.teaming.gwt.client.lpe.FolderProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntriesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntriesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;



/**
 * 
 * This class is used to display a folder widget in a landing page.  We will display the first
 * n entries found in the given folder.
 * @author jwootton
 *
 */
public class FolderWidget extends VibeWidget
{
	/**
	 * This class is used as the click handler when the user clicks on the title of an entry.
	 *
	 */
	private class EntryClickHandler implements ClickHandler
	{
		private String m_viewEntryUrl;
		
		/**
		 * 
		 */
		public EntryClickHandler( String entryUrl )
		{
			super();
			
			m_viewEntryUrl = entryUrl;
		}

		/**
		 * 
		 */
		private void handleClickOnLink()
		{
			if ( GwtClientHelper.hasString( m_viewEntryUrl ) )
			{
				// Fire the "view entry" event.
				GwtTeaming.fireEvent( new ViewForumEntryEvent( m_viewEntryUrl ) );
			}
			
		}
		
		/**
		 * 
		 */
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				public void execute()
				{
					handleClickOnLink();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * This class is used as the click handler when the user clicks on the author of an entry.
	 *
	 */
	private class AuthorClickHandler implements ClickHandler
	{
		private Element m_element;
		private String m_authorWorkspaceId;
		private String m_authorName;
		
		/**
		 * 
		 */
		public AuthorClickHandler( Element element, String authorWorkspaceId, String authorName )
		{
			super();
			
			m_element = element;
			m_authorWorkspaceId = authorWorkspaceId;
			m_authorName = authorName;
		}

		/**
		 * 
		 */
		private void handleClickOnLink()
		{
			if ( GwtClientHelper.hasString( m_authorWorkspaceId ) )
			{
				SimpleProfileParams params;
				
				// Invoke the Simple Profile dialog.
				params = new SimpleProfileParams( m_element, m_authorWorkspaceId, m_authorName );
				GwtTeaming.fireEvent(new InvokeSimpleProfileEvent( params ));
			}
		}
		
		/**
		 * 
		 */
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				public void execute()
				{
					handleClickOnLink();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	private FolderProperties m_properties;
	private String m_style;
	private Element m_folderTitleElement;
	private Element m_folderDescElement;
	private VibeFlowPanel m_listOfEntriesPanel;

	/**
	 * 
	 */
	public FolderWidget( FolderConfig config )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config.getProperties(), config.getLandingPageStyle() );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * 
	 */
	public FolderWidget( FolderProperties properties, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( properties, landingPageStyle );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * Add the given entries to this widget
	 */
	private void addEntries( ArrayList<GwtFolderEntry> entries )
	{
		if ( entries == null || entries.size() == 0 )
			return;
		
		// Do we have a panel to put the entries in?
		if ( m_listOfEntriesPanel != null )
		{
			int i;
			
			// Yes
			for (i = 0; i < entries.size(); ++i)
			{
				GwtFolderEntry entry;
				
				entry = entries.get( i );
				
				// Should we display the entry's name and description?
				if ( m_properties.getShowEntriesOpenedValue() )
				{
					EntryProperties entryProperties;
					EntryWidget entryWidget;
					
					// Yes
					entryProperties = new EntryProperties();
					entryProperties.setEntryId( entry.getEntryId() );
					entryProperties.setEntryData( entry );
					entryProperties.setShowTitle( true );
					entryProperties.setShowAuthor( m_properties.getShowEntryAuthor() );
					entryProperties.setShowDate( m_properties.getShowEntryDate() );
					entryProperties.setNumRepliesToShow( m_properties.getNumRepliesToShow() );
					
					entryWidget = new EntryWidget( entryProperties, m_style );
					m_listOfEntriesPanel.add( entryWidget );
				}
				else
				{
					VibeFlowPanel panel;
					InlineLabel link;
					EntryClickHandler clickHandler;
					
					// No, just create a link from the entry's title
					panel = new VibeFlowPanel();
					panel.addStyleName( "folderWidgetLinkToEntryPanel" + m_style );
					
					link = new InlineLabel( entry.getEntryName() );
					link.addStyleName( "folderWidgetLinkToEntry" + m_style );
					panel.add( link );
					
					clickHandler = new EntryClickHandler( entry.getViewEntryUrl() );
					link.addClickHandler( clickHandler );
					
					// Should we show the author or date?
					if ( m_properties.getShowEntryAuthor() || m_properties.getShowEntryDate() )
					{
						VibeFlowPanel miscPanel;
						
						// Create a panel that will hold the author and date
						miscPanel = new VibeFlowPanel();
						miscPanel.addStyleName( "folderWidgetMiscPanel" + m_style );
						panel.add( miscPanel );

						// Are we suppose to show the author?
						if ( m_properties.getShowEntryAuthor() )
						{
							InlineLabel authorLabel;
							AuthorClickHandler authorClickHandler;
							
							// Yes
							authorLabel = new InlineLabel( entry.getAuthor() );
							authorLabel.addStyleName( "entryWidgetAuthorLabel" );
							
							authorClickHandler = new AuthorClickHandler( authorLabel.getElement(), entry.getAuthorWorkspaceId(), entry.getAuthor() );
							authorLabel.addClickHandler( authorClickHandler );
							
							miscPanel.add( authorLabel );
						}
						
						// Are we suppose to show the date?
						if ( m_properties.getShowEntryDate() )
						{
							InlineLabel dateLabel;
							
							// Yes
							dateLabel = new InlineLabel( entry.getModificationDate() );
							dateLabel.addStyleName( "entryWidgetDateLabel" );
							
							miscPanel.add( dateLabel );
						}
					}
					
					m_listOfEntriesPanel.add( panel );
				}
			}
		}
	}
	
	/**
	 * When the user clicks on the folder's title, fire the ChangeContextEvent event
	 */
	private void handleClickOnFolderTitle()
	{
		OnSelectBinderInfo binderInfo;
		
		binderInfo = new OnSelectBinderInfo( m_properties.getFolderId(), m_properties.getViewFolderUrl(), false, Instigator.UNKNOWN );
		GwtTeaming.fireEvent( new ChangeContextEvent( binderInfo ) );
	}
	
	
	/**
	 * 
	 */
	private VibeFlowPanel init( FolderProperties properties, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		int numEntries;
		
		m_properties = new FolderProperties();
		m_properties.copy( properties );
		
		m_style = landingPageStyle;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "folderWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );
		
		// Should we show the name of the folder?
		if ( m_properties.getShowTitleValue() )
		{
			InlineLabel label;
			VibeFlowPanel titlePanel;
			
			// Yes, create a place for the title to live.
			titlePanel = new VibeFlowPanel();
			titlePanel.addStyleName( "landingPageWidgetTitlePanel" + m_style );
			titlePanel.addStyleName( "folderWidgetTitlePanel" + m_style );
			
			label = new InlineLabel( " " );
			label.addStyleName( "folderWidgetTitleLabel" + m_style );
			label.addClickHandler( new ClickHandler()
			{
				/**
				 * 
				 */
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						public void execute()
						{
							handleClickOnFolderTitle();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			titlePanel.add( label );
			m_folderTitleElement = label.getElement();
			
			mainPanel.add( titlePanel );
		}
		
		// Should we show the folder description?
		{
			VibeFlowPanel contentPanel;
			Label label;
			
			// Yes
			// Create a panel for the description to live in.
			contentPanel = new VibeFlowPanel();
			contentPanel.addStyleName( "folderWidgetContentPanel" + m_style );
			
			label = new Label( " " );
			label.addStyleName( "folderWidgetDesc" + m_style );
			contentPanel.add( label );
			m_folderDescElement = label.getElement();
			
			mainPanel.add( contentPanel );
		}
		
		// Issue an rpc request to get information about the folder.
		m_properties.getDataFromServer( new GetterCallback<Boolean>()
		{
			/**
			 * 
			 */
			public void returnValue( Boolean value )
			{
				Scheduler.ScheduledCommand cmd;

				// Did we successfully get the folder information?
				if ( value )
				{
					// Yes
					cmd = new Scheduler.ScheduledCommand()
					{
						public void execute()
						{
							// Update this widget with the folder information
							updateWidget();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		} );
		
		// Are we supposed to show entries from this folder?
		numEntries = m_properties.getNumEntriesToBeShownValue();
		if ( numEntries > 0 )
		{
			GetFolderEntriesCmd cmd;
			
			// Yes, create a panel for the entries to live in.
			m_listOfEntriesPanel = new VibeFlowPanel();
			m_listOfEntriesPanel.addStyleName( "folderWidgetListOfEntriesPanel" + m_style );
			mainPanel.add( m_listOfEntriesPanel );

			// Issue an rpc request to get the last n entries from the folder.
			cmd = new GetFolderEntriesCmd( m_properties.getFolderId(), numEntries, m_properties.getNumRepliesToShow() );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolderEntries(),
						m_properties.getFolderId() );
				}
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( VibeRpcResponse response )
				{
					GetFolderEntriesRpcResponseData gfeResponse;
					
					gfeResponse = (GetFolderEntriesRpcResponseData) response.getResponseData();
					
					if ( gfeResponse != null )
					{
						final ArrayList<GwtFolderEntry> entries;
						
						entries = gfeResponse.getEntries();
						if ( entries != null )
						{
							Scheduler.ScheduledCommand cmd;
	
							cmd = new Scheduler.ScheduledCommand()
							{
								public void execute()
								{
									// Add the entries to this widget
									addEntries( entries );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					}
				}
			} );
		}
		
		return mainPanel;
	}
	
	
	/**
	 * Update the folder's title and description. 
	 */
	private void updateWidget()
	{
		// Update the title if we are showing it.
		if ( m_properties.getShowTitleValue() && m_folderTitleElement != null )
		{
			String title;
			
			title = m_properties.getFolderName();
			if ( title == null || title.length() == 0 )
				title = GwtTeaming.getMessages().noTitle();

			m_folderTitleElement.setInnerHTML( title );
		}
		
		// Update the description if we are showing it.
		if ( m_properties.getShowDescValue() && m_folderDescElement != null )
		{
			String desc;
			
			desc = m_properties.getFolderDesc();
			if ( desc != null )
				m_folderDescElement.setInnerHTML( desc );
		}
	}
}
