/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent;
import org.kablink.teaming.gwt.client.event.ShowCollectionEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to display a list of Filr actions; "My file", "Shared with Me"
 * "Net Folders", "Shared by Me" and "What's New"
 * 
 * @author jwootton
 */
public class FilrActionsCtrl extends Composite
	implements
		ActivityStreamExitEvent.Handler,
		ClickHandler,
		ContextChangedEvent.Handler,
		GetSidebarCollectionEvent.Handler
{
	private FilrAction m_selectedAction;
	private HorizontalPanel m_mainPanel;
	private FilrAction m_myFilesAction;
	private FilrAction m_sharedWithMeAction;
	private FilrAction m_netFoldersAction;
	private FilrAction m_sharedByMeAction;
	private FilrAction m_sharedPublicAction;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
	{
		// Context events.
		TeamingEvents.ACTIVITY_STREAM_EXIT,
		TeamingEvents.CONTEXT_CHANGED,
		TeamingEvents.GET_SIDEBAR_COLLECTION
	};

	
	/**
	 * 
	 */
	public enum FilrActionType
	{
		MY_FILES,
		NET_FOLDERS,
		SHARED_BY_ME,
		SHARED_WITH_ME,
		SHARED_PUBLIC,
		UNKNOWN
	}
	/**
	 * 
	 */
	public class FilrAction extends Composite
	{
		private Command m_cmd;
		private FlowPanel m_mainPanel;
		private FilrActionType m_type;
		
		/**
		 * 
		 */
		public FilrAction(
						FilrActionType type,
						ImageResource imgResource,
						String text,
						Command cmd )
		{
			Label label;
			Image img;
			
			m_type = type;
			m_cmd = cmd;
			
			m_mainPanel = new FlowPanel();
			m_mainPanel.addStyleName( "FilrAction_MainPanel" );

			img = new Image( imgResource );
			img.setAltText( text );
			img.setTitle( text );
			img.setWidth( "48px" );
			img.setHeight( "48px" );
			img.getElement().setAttribute( "border", "0" );
			img.getElement().setAttribute( "align", "top" );
			img.addStyleName( "FilrAction_Img" );
			m_mainPanel.add( img );
			
			label = new Label( text );
			label.addStyleName( "FilrAction_Text" );
			m_mainPanel.add( label );
			
			initWidget( m_mainPanel );
		}
		
		/**
		 * 
		 */
		public void executeAction()
		{
			if ( m_cmd != null )
				m_cmd.execute();
		}

		/**
		 * 
		 */
		private FilrActionType getActionType()
		{
			return m_type;
		}
		
		/**
		 * Set the font color used to display the name of the action
		 */
		public void setFontColor( String fontColor )
		{
			Style style;
			
			style = m_mainPanel.getElement().getStyle();
			
			// Do we have a font color?
			if ( fontColor != null && fontColor.length() > 0 )
			{
				// Yes
				// Change the color of the font used to display the user's name.
				style.setColor( fontColor );
			}
			else
			{
				// Go back to the font color defined in the style sheet.
				style.clearColor();
			}
		}
		
		/**
		 * 
		 */
		public void setIsSelected( boolean selected )
		{
			if ( selected == false )
				m_mainPanel.removeStyleName( "FilrAction_Selected" );
			else
				m_mainPanel.addStyleName( "FilrAction_Selected" );
		}
	}
	
	/**
	 * 
	 */
	public FilrActionsCtrl()
	{
		GwtTeamingMessages messages;
		GwtTeamingFilrImageBundle imgBundle;
		Command cmd;
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
									GwtTeaming.getEventBus(),
									m_registeredEvents,
									this );

		m_selectedAction = null;
		
		m_mainPanel = new HorizontalPanel();
		m_mainPanel.addStyleName( "FilrActionsCtrl_mainPanel" );
		
		messages = GwtTeaming.getMessages();
		imgBundle = GwtTeaming.getFilrImageBundle();

		if ( ! ( GwtClientHelper.isGuestUser() ) )
		{
			// Add "My Files" action
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					FilrActionsCtrl.closeAdminConsole();
	
					GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.MY_FILES ) );
				}
			};
			m_myFilesAction = new FilrAction(
											FilrActionType.MY_FILES,
											imgBundle.myFiles_transparent_48(),
											messages.myFiles(),
											cmd );
			m_myFilesAction.addDomHandler( this, ClickEvent.getType() );
			m_mainPanel.add( m_myFilesAction );
			
			// Add the "Shared with Me" action
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					FilrActionsCtrl.closeAdminConsole();
	
					GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.SHARED_WITH_ME ) );
				}
			};
			m_sharedWithMeAction = new FilrAction(
												FilrActionType.SHARED_WITH_ME,
												imgBundle.sharedWithMe_transparent_48(),
												messages.sharedWithMe(),
												cmd );
			m_sharedWithMeAction.addDomHandler( this, ClickEvent.getType() );
			m_mainPanel.add( m_sharedWithMeAction );
			
			// Add the "Shared by Me" action
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					FilrActionsCtrl.closeAdminConsole();
	
					GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.SHARED_BY_ME ) );
				}
			};
			m_sharedByMeAction = new FilrAction(
											FilrActionType.SHARED_BY_ME,
											imgBundle.sharedByMe_transparent_40(),
											messages.sharedByMe(),
											cmd );
			m_sharedByMeAction.addDomHandler( this, ClickEvent.getType() );
			m_mainPanel.add( m_sharedByMeAction );
			
			// Add the "File Spaces" action
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					FilrActionsCtrl.closeAdminConsole();
	
					GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.NET_FOLDERS ) );
				}
			};
			m_netFoldersAction = new FilrAction(
											FilrActionType.NET_FOLDERS,
											imgBundle.netFolders_transparent_48(),
											messages.netFolders(),
											cmd );
			m_netFoldersAction.addDomHandler( this, ClickEvent.getType() );
			m_mainPanel.add( m_netFoldersAction );
		}
		
		if ( GwtClientHelper.isGuestUser() || GwtClientHelper.isShowPublicCollection() )
		{
			// Add the "Public" action
			cmd = new Command()
			{
				@Override
				public void execute()
				{
					FilrActionsCtrl.closeAdminConsole();
	
					GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.SHARED_PUBLIC ) );
				}
			};
			m_sharedPublicAction = new FilrAction(
												FilrActionType.SHARED_PUBLIC,
												imgBundle.sharedPublic_transparent_48(),
												messages.sharedPublic(),
												cmd );
			m_sharedPublicAction.addDomHandler( this, ClickEvent.getType() );
			m_mainPanel.add( m_sharedPublicAction );
		}
		
		initWidget( m_mainPanel );
	}
	
	/**
	 * 
	 */
	public static void closeAdminConsole()
	{
		// Is the admin console active?
		if ( GwtTeaming.getMainPage().isAdminActive() )
		{
			// Yes, close it
			AdministrationExitEvent.fireOne();
		}
	}
	
	/**
	 * 
	 */
	public FilrActionType getSelectedActionType()
	{
		if ( m_selectedAction != null )
			return m_selectedAction.getActionType();
		
		return FilrActionType.UNKNOWN;
	}
	
	/**
	 * 
	 */
	@Override
	public void onClick( ClickEvent event )
	{
		Object src;
		
		src = event.getSource();
		if ( src instanceof FilrAction )
		{
			final FilrAction action;
			Scheduler.ScheduledCommand cmd;

			action = (FilrAction) src;

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					selectAction( action );
					
					// Execute the action of the selected action.
					action.executeAction();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * Handles ActivityStreamExitEvent's received by this class.
	 *
	 * Implements the ActivityStreamExitEvent.Handler.onActivityStreamExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamExit(ActivityStreamExitEvent event) {
	}

	/**
	 * Handles ContextChangedEvent's received by this class.
	 * 
	 * Implements the ContextChangedEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanged( final ContextChangedEvent event )
	{
		OnSelectBinderInfo osbInfo;

		// Is the selection valid?
		osbInfo = event.getOnSelectBinderInfo();
		if ( GwtClientHelper.validateOSBI( osbInfo, false ))
		{
			// Yes
			// Are we dealing with a collection?
			BinderInfo binderInfo = osbInfo.getBinderInfo();
			if ( GwtClientHelper.isBinderInfoMyFilesHome( binderInfo ) )
			{
				binderInfo = GwtClientHelper.buildMyFilesBinderInfo();
			}
			if ( binderInfo.isBinderCollection() )
			{
				FilrAction action = null;
				
				// Yes
				// Select the appropriate collection point.
				switch ( binderInfo.getCollectionType() )
				{
				case MY_FILES:
					action = m_myFilesAction;
					break;
				
				case NET_FOLDERS:
					action = m_netFoldersAction;
					break;
				
				case SHARED_BY_ME:
					action = m_sharedByMeAction;
					break;
				
				case SHARED_WITH_ME:
					action = m_sharedWithMeAction;
					break;
				
				case SHARED_PUBLIC:
					action = m_sharedPublicAction;
					break;
				
				default:
					break;
				}
				
				if ( action != null )
					selectAction( action );
			}
		}
	}
	
	/**
	 * Handles GetSidebarCollectionEvent's received by this class.
	 * 
	 * Implements the GetSidebarCollectionEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onGetSidebarCollection( GetSidebarCollectionEvent event )
	{
		CollectionType collectionType;
		
		collectionType = CollectionType.NOT_A_COLLECTION;
		if ( m_selectedAction != null )
		{
			if ( m_selectedAction == m_myFilesAction )
				collectionType = CollectionType.MY_FILES;
			else if ( m_selectedAction == m_netFoldersAction )
				collectionType = CollectionType.NET_FOLDERS;
			else if ( m_selectedAction == m_sharedWithMeAction )
				collectionType = CollectionType.SHARED_WITH_ME;
			else if ( m_selectedAction == m_sharedPublicAction )
				collectionType = CollectionType.SHARED_PUBLIC;
			else if ( m_selectedAction == m_sharedByMeAction )
				collectionType = CollectionType.SHARED_BY_ME;
		}
		
		event.getCollectionCallback().collection( collectionType );
	}

	/**
	 * Select the given action
	 */
	private void selectAction( FilrAction action )
	{
		// Do we have an action that is already selected?
		if ( m_selectedAction != null )
			m_selectedAction.setIsSelected( false );

		m_selectedAction = action;
		m_selectedAction.setIsSelected( true );
	}
	
	/**
	 * Set the color of the font used by this control.
	 */
	public void setFontColor( String fontColor )
	{
		int i;
		
		// Change the font color for each FilrAction.
		for ( i = 0; i < m_mainPanel.getWidgetCount(); ++i)
		{
			Widget widget;
			
			widget = m_mainPanel.getWidget( i );
			if ( widget instanceof FilrAction )
			{
				FilrAction filrAction;
				
				filrAction = (FilrAction) widget;
				filrAction.setFontColor( fontColor );
			}
		}
	}
}
