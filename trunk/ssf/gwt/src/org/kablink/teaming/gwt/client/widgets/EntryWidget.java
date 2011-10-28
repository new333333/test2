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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.lpe.EntryConfig;
import org.kablink.teaming.gwt.client.lpe.EntryProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;



/**
 * 
 * @author jwootton
 *
 */
public class EntryWidget extends VibeWidget
{
	private Element m_titleElement;
	private Element m_descElement;
	private EntryProperties m_properties;
	private String m_style;
	private Timer m_timer = null;

	/**
	 * 
	 */
	public EntryWidget( EntryConfig config )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * 
	 */
	private void handleClickOnTitle()
	{
		String viewEntryUrl;
		
		viewEntryUrl = m_properties.getViewEntryUrl();
		
		if ( GwtClientHelper.hasString( viewEntryUrl ) )
		{
			// Fire the "view entry" event.
			GwtTeaming.fireEvent( new ViewForumEntryEvent( viewEntryUrl ) );
		}
		else
			Window.alert( GwtTeaming.getMessages().cantAccessEntry() );
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( EntryConfig config )
	{
		EntryProperties properties;
		VibeFlowPanel titlePanel;
		VibeFlowPanel mainPanel;
		
		m_properties = new EntryProperties();
		properties = config.getProperties();
		m_properties.copy( properties );
		
		m_style = config.getLandingPageStyle();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "entryWidgetMainPanel" + m_style );
		
		mainPanel.removeStyleName( "landingPageWidgetNoBorder" );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );

		// Create a place for the title to live.
		{
			InlineLabel label;
			
			// Yes, create a place for the title to live.
			titlePanel = new VibeFlowPanel();
			titlePanel.addStyleName( "landingPageWidgetTitlePanel" + m_style );
			titlePanel.addStyleName( "entryWidgetTitlePanel" + m_style );
			
			label = new InlineLabel( " " );
			label.addStyleName( "entryWidgetTitleLabel" + m_style );
			label.addClickHandler( new ClickHandler()
			{
				/**
				 * 
				 */
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					final Object src;
					
					src = event.getSource();

					cmd = new Scheduler.ScheduledCommand()
					{
						public void execute()
						{
							handleClickOnTitle();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			titlePanel.add( label );
			m_titleElement = label.getElement();
			
			mainPanel.add( titlePanel );
		}
		
		// Create a panel for the description of the entry to live in.
		{
			VibeFlowPanel contentPanel;
			Label label;
			
			contentPanel = new VibeFlowPanel();
			contentPanel.addStyleName( "entryWidgetContentPanel" + m_style );
			
			label = new Label( " " );
			label.addStyleName( "entryWidgetDesc" + m_style );
			contentPanel.add( label );
			m_descElement = label.getElement();
			
			mainPanel.add( contentPanel );
		}
		
		// Issue an ajax request to get the entry's title and description
		m_properties.getDataFromServer();
		updateWidget();
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	private void updateWidget()
	{
		// Are we waiting for the ajax call to do markup replacement?
		if ( m_properties.isRpcInProgress() )
		{
			// Yes
			// Have we already created a timer?
			if ( m_timer == null )
			{
				// No, create one.
				m_timer = new Timer()
				{
					/**
					 * 
					 */
					@Override
					public void run()
					{
						updateWidget();
					}
				};
			}
			
			m_timer.schedule( 250 );
			return;
		}

		// Update this widget with the entry's title.
		{
			String title;
		
			title = m_properties.getEntryTitle();
			if ( title == null || title.length() == 0 )
				title = GwtTeaming.getMessages().noTitle();

			m_titleElement.setInnerHTML( title );
		}
		
		// Update this widget with the entry's description.
		{
			String desc;
			
			desc = m_properties.getEntryDecs();
			if ( desc != null )
				m_descElement.setInnerHTML( desc );
		}
	}
}

