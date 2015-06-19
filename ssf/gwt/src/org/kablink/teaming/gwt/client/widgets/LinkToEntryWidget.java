/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.lpe.LinkToEntryConfig;
import org.kablink.teaming.gwt.client.lpe.LinkToEntryProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * ?
 * 
 * @author jwootton
 */
public class LinkToEntryWidget extends VibeWidget
{
	private LinkToEntryProperties m_properties;
	private String m_style;
	private InlineLabel m_link;

	/**
	 * 
	 */
	public LinkToEntryWidget( LinkToEntryConfig config, WidgetStyles widgetStyles )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config, widgetStyles );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * 
	 */
	private void handleClickOnLink()
	{
		String viewEntryUrl;
		
		viewEntryUrl = m_properties.getViewEntryUrl();
		
		if ( GwtClientHelper.hasString( viewEntryUrl ) )
		{
			// Should we open a new window?
			if ( m_properties.getOpenInNewWindow() )
			{
				int height;
				int width;

				// Yes
				width = Window.getClientWidth();
				height = Window.getClientHeight();
				Window.open( viewEntryUrl, "_linkToEntryWidget", "height=" + String.valueOf( height ) + ",resizeable,scrollbars,width=" + String.valueOf( width ) );
			}
			else
			{
				// Fire the "view entry" event.
				GwtTeaming.fireEvent( new ViewForumEntryEvent( viewEntryUrl ) );
			}
		}
		else
			Window.alert( GwtTeaming.getMessages().cantAccessFolder() );
	}

	/**
	 * 
	 */
	private VibeFlowPanel init( LinkToEntryConfig config, WidgetStyles widgetStyles )
	{
		LinkToEntryProperties properties;
		VibeFlowPanel mainPanel;
		Scheduler.ScheduledCommand cmd;
		
		m_properties = new LinkToEntryProperties();
		properties = config.getProperties();
		m_properties.copy( properties );
		
		m_style = config.getLandingPageStyle();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "linkToEntryWidgetMainPanel" + m_style );
		
		GwtClientHelper.setLandingPageTitleContent(m_link, m_properties.getTitle(), GwtTeaming.getMessages().noTitle());
		m_link.addStyleName( "linkToEntryWidgetLink" + m_style );
		
		// Set the text color for the content.
		GwtClientHelper.setElementTextColor( m_link.getElement(), widgetStyles.getContentTextColor() );
		
		mainPanel.add( m_link );
		mainPanel.setVisible( false );
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Issue an rpc request to get information about the entry.
				m_properties.getDataFromServer( new GetterCallback<Boolean>()
				{
					/**
					 * 
					 */
					@Override
					public void returnValue( Boolean value )
					{
						// Did we successfully get the data from the server?
						if ( value )
						{
							updateWidget();
							getWidget().setVisible( true );
						}
					}
				} );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
		
		return mainPanel;
	}
	
	/**
	 * Now that we have the url to the entry, add an onclick handler to the link.
	 */
	private void updateWidget()
	{
		m_link.addClickHandler( new ClickHandler()
		{
			/**
			 * 
			 */
			@Override
			public void onClick( ClickEvent event )
			{
				handleClickOnLink();
			}
		} );
	}
}
