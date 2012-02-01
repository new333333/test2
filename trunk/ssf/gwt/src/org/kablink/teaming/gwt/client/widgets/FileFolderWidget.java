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
import java.util.Collections;
import java.util.Comparator;

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtAttachment;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.GotoUrlEvent;
import org.kablink.teaming.gwt.client.lpe.FileFolderProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfFilesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfFilesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;



/**
 * 
 * This class is used to display a file folder widget in a landing page.  We will display the first
 * n entries found in the given folder.
 * @author jwootton
 *
 */
public class FileFolderWidget extends VibeWidget
{
	/**
	 * This class is used as the click handler when the user clicks on the name of a file
	 *
	 */
	private class FileClickHandler implements ClickHandler
	{
		private String m_viewFileUrl;
		
		/**
		 * 
		 */
		public FileClickHandler( String fileUrl )
		{
			super();
			
			m_viewFileUrl = fileUrl;
		}

		/**
		 * 
		 */
		private void handleClickOnLink()
		{
			if ( GwtClientHelper.hasString( m_viewFileUrl ) )
			{
				// Fire the "goto url" event.
				GwtTeaming.fireEvent( new GotoUrlEvent( m_viewFileUrl ) );
			}
			
		}
		
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
					handleClickOnLink();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	private FileFolderProperties m_properties;
	private String m_style;
	private Element m_folderTitleElement;
	private Element m_folderDescElement;
	private VibeFlowPanel m_listOfFilesPanel;

	/**
	 * 
	 */
	public FileFolderWidget( FileFolderProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( properties, widgetStyles, landingPageStyle );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * Add the given files to this widget
	 */
	private void addFiles( ArrayList<GwtAttachment> files )
	{
		if ( files == null || files.size() == 0 )
			return;
		
		// Do we have a panel to put the files in?
		if ( m_listOfFilesPanel != null )
		{
			int i;
			
			// Yes
			// Sort the files by name
			sortFilesByName( files );

			for (i = 0; i < files.size(); ++i)
			{
				GwtAttachment file;
				VibeFlowPanel panel;
				InlineLabel link;
				FileClickHandler clickHandler;
				
				file = files.get( i );
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "fileFolderWidgetLinkToFilePanel" + m_style );
				
				link = new InlineLabel( file.getFileName() );
				link.addStyleName( "fileFolderWidgetLinkToFile" + m_style );
				panel.add( link );
				
				clickHandler = new FileClickHandler( file.getViewFileUrl() );
				link.addClickHandler( clickHandler );
				
				m_listOfFilesPanel.add( panel );
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
	private VibeFlowPanel init( FileFolderProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		VibeFlowPanel contentPanel;
		int numEntries;
		
		m_properties = new FileFolderProperties();
		m_properties.copy( properties );
		
		m_style = landingPageStyle;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "fileFolderWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );
		
		// Set the border width and color.
		GwtClientHelper.setElementBorderStyles( mainPanel.getElement(), widgetStyles );
		
		// Create a panel that will hold the content
		{
			contentPanel = new VibeFlowPanel();
			
			// Set the width and height
			{
				Style style;
				int width;
				int height;
				Unit unit;
				
				style = contentPanel.getElement().getStyle();
				
				// Don't set the width if it is set to 100%.  This causes a scroll bar to appear
				width = m_properties.getWidth();
				unit = m_properties.getWidthUnits();
				if ( width != 100 || unit != Unit.PCT )
					style.setWidth( width, unit );
				
				// Don't set the height if it is set to 100%.  This causes a scroll bar to appear.
				height = m_properties.getHeight();
				unit = m_properties.getHeightUnits();
				if ( height != 100 || unit != Unit.PCT )
					style.setHeight( height, unit );
				
				style.setOverflow( m_properties.getOverflow() );
			}
		}

		// Should we show the name of the folder?
		if ( m_properties.getShowTitleValue() )
		{
			InlineLabel label;
			VibeFlowPanel titlePanel;
			
			// Yes, create a place for the title to live.
			titlePanel = new VibeFlowPanel();
			titlePanel.addStyleName( "landingPageWidgetTitlePanel" + m_style );
			titlePanel.addStyleName( "fileFolderWidgetTitlePanel" + m_style );
			
			label = new InlineLabel( " " );
			label.addStyleName( "fileFolderWidgetTitleLabel" + m_style );
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
							handleClickOnFolderTitle();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			titlePanel.add( label );
			m_folderTitleElement = label.getElement();
			
			// Set the title background color.
			GwtClientHelper.setElementBackgroundColor( titlePanel.getElement(), widgetStyles.getHeaderBgColor() );
			
			// Set the title text color.
			GwtClientHelper.setElementTextColor( m_folderTitleElement, widgetStyles.getHeaderTextColor() );

			mainPanel.add( titlePanel );
			
			// Create a panel for the description
			{
				VibeFlowPanel descPanel;
				Label descLabel;
				
				// Yes
				// Create a panel for the description to live in.
				descPanel = new VibeFlowPanel();
				descPanel.addStyleName( "fileFolderWidgetContentPanel" + m_style );
				
				descLabel = new Label( " " );
				descLabel.addStyleName( "fileFolderWidgetDesc" + m_style );
				descPanel.add( descLabel );
				m_folderDescElement = descLabel.getElement();
				
				// Set the text color for the content.
				GwtClientHelper.setElementTextColor( descPanel.getElement(), widgetStyles.getContentTextColor() );

				contentPanel.add( descPanel );
			}
		}
		
		// Issue an rpc request to get information about the folder.
		m_properties.getDataFromServer( new GetterCallback<Boolean>()
		{
			/**
			 * 
			 */
			@Override
			public void returnValue( Boolean value )
			{
				Scheduler.ScheduledCommand cmd;

				// Did we successfully get the folder information?
				if ( value )
				{
					// Yes
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
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
			GetListOfFilesCmd cmd;
			
			// Yes, create a panel for the entries to live in.
			m_listOfFilesPanel = new VibeFlowPanel();
			m_listOfFilesPanel.addStyleName( "fileFolderWidgetListOfEntriesPanel" + m_style );
			contentPanel.add( m_listOfFilesPanel );

			// Issue an rpc request to get the last n entries from the file folder.
			cmd = new GetListOfFilesCmd( m_properties.getZoneUUID(), m_properties.getFolderId(), numEntries );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFileFolderEntries(),
						m_properties.getFolderId() );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					GetListOfFilesRpcResponseData glofResponse;
					
					glofResponse = (GetListOfFilesRpcResponseData) response.getResponseData();
					
					if ( glofResponse != null )
					{
						final ArrayList<GwtAttachment> files;
						
						files = glofResponse.getFiles();
						if ( files != null )
						{
							Scheduler.ScheduledCommand cmd;
	
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									// Add the files to this widget
									addFiles( files );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					}
				}
			} );
		}
		
		mainPanel.add( contentPanel );

		return mainPanel;
	}
	
	/**
	 * Sort the given list of files by name
	 */
	@SuppressWarnings("unchecked")
	private void sortFilesByName( ArrayList<GwtAttachment> files )
	{
		if ( files == null || files.size() == 0 )
			return;
		
		Collections.sort( files, new Comparator()
		{
			@Override
			public int compare( Object obj1, Object obj2 )
			{
				GwtAttachment file1;
				GwtAttachment file2;
				
				file1 = (GwtAttachment) obj1;
				file2 = (GwtAttachment) obj2;
				
				return file1.getFileName().compareToIgnoreCase( file2.getFileName() );
			}
		} );
	}


	/**
	 * Update the folder's title and description. 
	 */
	private void updateWidget()
	{
		// Update the title if we are showing it.
		if ( m_properties.getShowTitleValue() )
		{
			if ( m_folderTitleElement != null )
			{
				String title;
				
				title = m_properties.getFolderTitle();
				if ( title == null || title.length() == 0 )
					title = GwtTeaming.getMessages().noTitle();
	
				m_folderTitleElement.setInnerHTML( title );
			}
			
			if ( m_folderDescElement != null )
			{
				String desc;
				
				desc = m_properties.getFolderDesc();
				if ( desc != null )
				{
					m_folderDescElement.setInnerHTML( desc );
				}
			}
		}
	}
}
