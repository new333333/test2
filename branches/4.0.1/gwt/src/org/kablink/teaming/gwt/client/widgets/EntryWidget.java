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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.lpe.EntryConfig;
import org.kablink.teaming.gwt.client.lpe.EntryProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * ?
 *  
 * @author jwootton
 */
public class EntryWidget extends VibeWidget
{
	private Element m_titleElement;
	private Element m_descElement;
	private Element m_authorElement;
	private Element m_dateElement;
	private VibeFlowPanel m_repliesPanel;
	private EntryProperties m_properties;
	private WidgetStyles m_widgetStyles;
	private String m_style;

	/**
	 * 
	 */
	public EntryWidget( EntryConfig config, WidgetStyles widgetStyles )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config.getProperties(), widgetStyles, config.getLandingPageStyle() );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * 
	 */
	public EntryWidget( EntryProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( properties, widgetStyles, landingPageStyle );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * 
	 */
	private void handleClickOnAuthor()
	{
		SimpleProfileParams params;
		
		// Invoke the Simple Profile dialog.
		params = new SimpleProfileParams( m_authorElement, m_properties.getAuthorId(), m_properties.getAuthorWorkspaceId(), m_properties.getAuthor() );
		GwtTeaming.fireEvent(new InvokeSimpleProfileEvent( params ));
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
	private VibeFlowPanel init( EntryProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel titlePanel;
		VibeFlowPanel mainPanel;
		VibeFlowPanel contentPanel;
		int width;
		Unit widthUnits;
		int height;
		Unit heightUnits;
		Scheduler.ScheduledCommand cmd1;

		m_properties = new EntryProperties();
		m_properties.copy( properties );
		
		m_widgetStyles = widgetStyles;
		m_style = landingPageStyle;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "entryWidgetMainPanel" + m_style );
		
		mainPanel.removeStyleName( "landingPageWidgetNoBorder" );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );
		
		// Set the border width and color.
		GwtClientHelper.setElementBorderStyles( mainPanel.getElement(), widgetStyles );
		
		// Get the width and height
		width = m_properties.getWidth();
		widthUnits = m_properties.getWidthUnits();
		height = m_properties.getHeight();
		heightUnits = m_properties.getHeightUnits();

		// Set the width of the entire widget
		GwtClientHelper.setWidth( width, widthUnits, mainPanel );
		
		// If the height is a percentage, set the height of the entire widget.
		if ( heightUnits == Unit.PCT )
			GwtClientHelper.setHeight( height, heightUnits, mainPanel );
		
		// Create a place for the title to live.
		if ( m_properties.getShowTitleValue() )
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
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;

					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
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
			
			// Set the title background color.
			GwtClientHelper.setElementBackgroundColor( titlePanel.getElement(), widgetStyles.getHeaderBgColor() );
			
			// Set the title text color.
			GwtClientHelper.setElementTextColor( m_titleElement, widgetStyles.getHeaderTextColor() );
			
			mainPanel.add( titlePanel );
		}
		
		// Create a content panel that will hold everything else
		{
			contentPanel = new VibeFlowPanel();
			mainPanel.add( contentPanel );
			
			// If the height is not a percentage, set the height of the contentPanel.
			if ( heightUnits != Unit.PCT )
				GwtClientHelper.setHeight( height, heightUnits, contentPanel );

			// Set the overflow value
			GwtClientHelper.setOverflow( m_properties.getOverflow(), contentPanel );
		}
		
		// Are we supposed to show the author or date?
		if ( m_properties.getShowAuthor() || m_properties.getShowDate()  )
		{
			VibeFlowPanel authorPanel;
			
			// Yes
			authorPanel = new VibeFlowPanel();
			authorPanel.addStyleName( "entryWidgetAuthorPanel" + m_style );
			
			// Are we suppose to show the author?
			if ( m_properties.getShowAuthor() )
			{
				InlineLabel authorLabel;
				
				// Yes
				authorLabel = new InlineLabel( " " );
				authorLabel.addStyleName( "entryWidgetAuthorLabel" );
				GwtClientHelper.setElementTextColor( authorLabel.getElement(), widgetStyles.getContentTextColor() );
				authorLabel.addClickHandler( new ClickHandler()
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
							@Override
							public void execute()
							{
								handleClickOnAuthor();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
				m_authorElement = authorLabel.getElement();
				
				authorPanel.add( authorLabel );
			}
			
			// Are we suppose to show the date?
			if ( m_properties.getShowDate() )
			{
				InlineLabel dateLabel;
				
				// Yes
				dateLabel = new InlineLabel( " " );
				dateLabel.addStyleName( "entryWidgetDateLabel" );
				GwtClientHelper.setElementTextColor( dateLabel.getElement(), widgetStyles.getContentTextColor() );
				m_dateElement = dateLabel.getElement();
				
				authorPanel.add( dateLabel );
			}
			
			contentPanel.add( authorPanel );
		}
		
		// Create a panel for the description of the entry to live in.
		{
			VibeFlowPanel descPanel;
			Label label;
			
			descPanel = new VibeFlowPanel();
			descPanel.addStyleName( "entryWidgetContentPanel" + m_style );
			
			label = new Label( " " );
			label.addStyleName( "entryWidgetDesc" + m_style );
			descPanel.add( label );
			m_descElement = label.getElement();
			
			contentPanel.add( descPanel );
		}
		
		// Are we supposed to show replies?
		if ( m_properties.getNumRepliesToShow() > 0 )
		{
			// Yes, create a panel for the replies to go in.
			m_repliesPanel = new VibeFlowPanel();
			m_repliesPanel.addStyleName( "entryWidgetRepliesPanel" );
			
			contentPanel.add( m_repliesPanel );
		}
		
		mainPanel.setVisible( false );
		
		cmd1 = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Issue an ajax request to get the entry's data.
				m_properties.getDataFromServer( new GetterCallback<Boolean>()
				{
					/**
					 * 
					 */
					@Override
					public void returnValue( final Boolean value )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								if ( value )
								{
									updateWidget();
									getWidget().setVisible( true );
								}
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
			}
		};
		Scheduler.get().scheduleDeferred( cmd1 );
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	private void updateWidget()
	{
		// Update this widget with the entry's title.
		if ( m_titleElement != null )
		{
			String title;
		
			title = m_properties.getEntryTitle();
			if ( title == null || title.length() == 0 )
				title = GwtTeaming.getMessages().noTitle();

			m_titleElement.setInnerHTML( title );
		}
		
		// Update the author's name
		if ( m_authorElement != null )
		{
			String author;
			
			author = m_properties.getAuthor();
			if ( author != null )
				m_authorElement.setInnerHTML( author );
		}
		
		// Update the modification date
		if ( m_dateElement != null )
		{
			String date;
			
			date = m_properties.getModificationDate();
			if ( date != null )
				m_dateElement.setInnerHTML( date );
		}
		
		// Update this widget with the entry's description.
		{
			String desc;
			
			desc = m_properties.getEntryDecs();
			if ( desc != null )
				m_descElement.setInnerHTML( desc );
		}
		
		// Are we supposed to show replies?
		if ( m_repliesPanel != null )
		{
			ArrayList<String> replyIds;
			
			// Yes, do we have any replies?
			replyIds = m_properties.getReplyIds();
			if ( replyIds != null && replyIds.size() > 0 )
			{
				// Yes
				for (String replyId: replyIds)
				{
					EntryWidget entryWidget;
					EntryProperties entryProperties;
					
					entryProperties = new EntryProperties();
					entryProperties.setEntryId( replyId );
					entryProperties.setShowAuthor( true );
					entryProperties.setShowDate( true );
					entryProperties.setShowTitle( true );
					entryProperties.setNumRepliesToShow( 0 );
					
					// Add an EntryWidget to the panel that holds all the replies.
					entryWidget = new EntryWidget( entryProperties, m_widgetStyles, m_style );
					m_repliesPanel.add( entryWidget );
				}
			}
		}
	}
}

