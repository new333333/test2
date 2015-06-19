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
import org.kablink.teaming.gwt.client.event.AccessToItemDeniedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.lpe.LinkToFolderConfig;
import org.kablink.teaming.gwt.client.lpe.LinkToFolderProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ?
 *  
 * @author jwootton
 */
public class LinkToBinderWidget extends VibeWidget
{
	private LinkToFolderProperties m_properties;
	private String m_style;
	private InlineLabel m_link;

	/**
	 * 
	 */
	public LinkToBinderWidget( LinkToFolderConfig config, WidgetStyles widgetStyles )
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
		String viewBinderUrl;
		
		viewBinderUrl = m_properties.getUrl();
		
		if ( GwtClientHelper.hasString( viewBinderUrl ) )
		{
			// Should we open a new window?
			if ( m_properties.getOpenInNewWindow() )
			{
				int height;
				int width;

				// Yes
				width = Window.getClientWidth();
				height = Window.getClientHeight();
				Window.open( viewBinderUrl, "_linkToBinderWidget", "height=" + String.valueOf( height ) + ",resizeable,scrollbars,width=" + String.valueOf( width ) );
			}
			else
			{
				EventHelper.fireChangeContextEventAsync( m_properties.getFolderId(), viewBinderUrl, Instigator.GOTO_CONTENT_URL );
			}
		}
		else
			Window.alert( GwtTeaming.getMessages().cantAccessFolder() );
	}

	/**
	 * 
	 */
	private VibeFlowPanel init( LinkToFolderConfig config, WidgetStyles widgetStyles )
	{
		LinkToFolderProperties properties;
		VibeFlowPanel mainPanel;
		ScheduledCommand cmd;
		final Widget widget;
		
		m_properties = new LinkToFolderProperties();
		properties = config.getProperties();
		m_properties.copy( properties );
		
		m_style = config.getLandingPageStyle();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "linkToBinderWidgetMainPanel" + m_style );
		
		m_link = new InlineLabel();
		GwtClientHelper.setLandingPageTitleContent(m_link, m_properties.getTitle(), GwtTeaming.getMessages().noTitle());
		m_link.addStyleName( "linkToBinderWidgetLink" + m_style );
		
		// Set the text color for the content.
		GwtClientHelper.setElementTextColor( m_link.getElement(), widgetStyles.getContentTextColor() );
		
		mainPanel.add( m_link );
		mainPanel.setVisible( false );
		
		widget = this;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Issue an rpc request to get information about the binder.
				m_properties.getDataFromServer( new GetterCallback<Boolean>()
				{
					@Override
					public void returnValue(Boolean value)
					{
						// Did we successfully get the data from the server?
						if ( value )
						{
							updateWidget();
							getWidget().setVisible( true );
						}
						else
						{
							// We could not get information about the given binder probably
							// because the user does not have access to the binder.
							// Fire the AccessToItemDenied event to notify whoever created this widget.
							// Fire the event to invoke the "Manage users" dialog.
							GwtTeaming.fireEvent( new AccessToItemDeniedEvent( widget ) );
						}
					}
				} );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
		
		return mainPanel;
	}
	
	/**
	 * Now that we have the url to the binder, add an onclick handler to the link.
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
