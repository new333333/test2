/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ShowCollectionEvent;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * This class is used to display a list of Filr actions; "My file", "Shared with Me"
 * "File Spaces", "Shared by Me" and "What's New"
 * @author jwootton
 *
 */
public class FilrActionsCtrl extends Composite
	implements ClickHandler
{
	private FilrAction m_selectedAction;
	
	
	/**
	 * 
	 */
	public class FilrAction extends Composite
		implements MouseOutHandler, MouseOverHandler
	{
		private Command m_cmd;
		private FlowPanel m_mainPanel;
		private boolean m_isSelected;
		
		/**
		 * 
		 */
		public FilrAction(
						String imgPath,
						String text,
						Command cmd )
		{
			Label label;
			Image img;
			
			m_isSelected = false;
			m_cmd = cmd;
			
			m_mainPanel = new FlowPanel();
			m_mainPanel.addStyleName( "FilrAction_MainPanel" );

			m_mainPanel.addDomHandler( this, MouseOutEvent.getType() );
			m_mainPanel.addDomHandler( this, MouseOverEvent.getType() );

			img = new Image( imgPath );
			img.setAltText( text );
			img.setTitle( text );
			img.setWidth( "40px" );
			img.setHeight( "40px" );
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
		@Override
		public void onMouseOut( MouseOutEvent event )
		{
			if ( m_isSelected )
				setSelectedBackground();
			else
				m_mainPanel.getElement().getStyle().clearBackgroundImage();
		}

		/**
		 * 
		 */
		@Override
		public void onMouseOver( MouseOverEvent event )
		{
			String url;
			
			url = GwtTeaming.m_requestInfo.getImagesPath() + "pics/Filr/trans20_91daf2.png";
			m_mainPanel.getElement().getStyle().setBackgroundImage( "url( " + url + " )" );
		}
		
		/**
		 * 
		 */
		public void setIsSelected( boolean selected )
		{
			m_isSelected = selected;
			
			if ( selected == false )
				m_mainPanel.getElement().getStyle().clearBackgroundImage();
		}
		
		/**
		 * 
		 */
		public void setSelectedBackground()
		{
			String url;
			
			url = GwtTeaming.m_requestInfo.getImagesPath() + "pics/trans30_black.png";
			m_mainPanel.getElement().getStyle().setBackgroundImage( "url( " + url + " )" );
		}
	}
	
	/**
	 * 
	 */
	public FilrActionsCtrl()
	{
		HorizontalPanel mainPanel;
		FilrAction action;
		GwtTeamingMessages messages;
		Command cmd;
		
		m_selectedAction = null;
		
		mainPanel = new HorizontalPanel();
		mainPanel.addStyleName( "FilrActionsCtrl_mainPanel" );
		
		messages = GwtTeaming.getMessages();
		
		// Add "My Files" action
		cmd = new Command()
		{
			@Override
			public void execute()
			{
				GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.MY_FILES ) );
			}
		};
		action = new FilrAction(
							GwtTeaming.m_requestInfo.getImagesPath() + "pics/Filr/myfiles_transparent_40.png",
							messages.myFiles(),
							cmd );
		action.addDomHandler( this, ClickEvent.getType() );
		mainPanel.add( action );
		
		// Add the "Shared with Me" action
		cmd = new Command()
		{
			@Override
			public void execute()
			{
				GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.SHARED_WITH_ME ) );
			}
		};
		action = new FilrAction(
							GwtTeaming.m_requestInfo.getImagesPath() + "pics/Filr/shared_with_me_transparent_40.png",
							messages.sharedWithMe(),
							cmd );
		action.addDomHandler( this, ClickEvent.getType() );
		mainPanel.add( action );
		
		// Add the "File Spaces" action
		cmd = new Command()
		{
			@Override
			public void execute()
			{
				GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.FILE_SPACES ) );
			}
		};
		action = new FilrAction(
							GwtTeaming.m_requestInfo.getImagesPath() + "pics/Filr/filespaces_transparent_40.png",
							messages.fileSpaces(),
							cmd );
		action.addDomHandler( this, ClickEvent.getType() );
		mainPanel.add( action );
		
		// Add the "Shared by Me" action
		cmd = new Command()
		{
			@Override
			public void execute()
			{
				GwtTeaming.fireEvent( new ShowCollectionEvent( CollectionType.SHARED_BY_ME ) );
			}
		};
		action = new FilrAction(
							GwtTeaming.m_requestInfo.getImagesPath() + "pics/Filr/shared_by_me_transparent_40.png",
							messages.sharedByMe(),
							cmd );
		action.addDomHandler( this, ClickEvent.getType() );
		mainPanel.add( action );
		
		// Add the "What's New" action
		cmd = new Command()
		{
			@Override
			public void execute()
			{
				ActivityStreamInfo asi;
				
				//!!!
				// Figure out which collection point is selected and invoke "what's new"
				// on that collection point.
				asi = new ActivityStreamInfo();
				asi.setActivityStream( ActivityStream.SHARED_WITH_ME );
				//!!!asi.setBinderId( m_contextBinder.getBinderId() );
				//!!!asi.setTitle( m_contextBinder.getBinderTitle() );

				GwtTeaming.fireEvent( new ActivityStreamEnterEvent( asi, ActivityStreamDataType.OTHER ) );
			}
		};
		action = new FilrAction(
							GwtTeaming.m_requestInfo.getImagesPath() + "pics/Filr/whatsnew_transparent_40.png",
							messages.whatsNew(),
							cmd );
		action.addDomHandler( this, ClickEvent.getType() );
		mainPanel.add( action );
		
		initWidget( mainPanel );
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
					// Do we have an action that is already selected?
					if ( m_selectedAction != null )
						m_selectedAction.setIsSelected( false );
					
					// Execute the action of the selected action.
					action.executeAction();
						
					m_selectedAction = action;
					m_selectedAction.setIsSelected( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
}
