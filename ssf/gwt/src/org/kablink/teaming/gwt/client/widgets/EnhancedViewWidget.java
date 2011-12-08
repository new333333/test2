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
import org.kablink.teaming.gwt.client.lpe.EnhancedViewConfig;
import org.kablink.teaming.gwt.client.lpe.EnhancedViewProperties;
import org.kablink.teaming.gwt.client.lpe.EntryProperties;
import org.kablink.teaming.gwt.client.lpe.FileFolderProperties;
import org.kablink.teaming.gwt.client.lpe.FolderProperties;
import org.kablink.teaming.gwt.client.lpe.TaskFolderProperties;
import org.kablink.teaming.gwt.client.rpc.shared.ExecuteEnhancedViewJspCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * 
 * @author jwootton
 *
 */
public class EnhancedViewWidget extends VibeWidget
{
	private String m_lpBinderId;	// landing page binder id
	private VibeFlowPanel m_mainPanel;
	private EnhancedViewProperties m_properties;
	private AsyncCallback<VibeRpcResponse> m_executeJspCallback = null;
	
	/**
	 * Create the appropriate widget based on what type of enhanced view we are dealing with
	 */
	public static VibeWidget createWidget( EnhancedViewConfig config, String lpBinderId )
	{
		EnhancedViewProperties properties;
		String landingPageStyle;
		VibeWidget widget;
		
		landingPageStyle = config.getLandingPageStyle();
		properties = config.getProperties();
		switch ( properties.getViewType() )
		{
		case DISPLAY_ENTRY:
		{
			EntryProperties entryProperties;
			
			entryProperties = new EntryProperties();
			entryProperties.setZoneUUID( properties.getZoneUUID() );
			entryProperties.setEntryId( properties.getEntryId() );
			entryProperties.setShowAuthor( true );
			entryProperties.setShowDate( true );
			entryProperties.setShowTitle( true );
			entryProperties.setNumRepliesToShow( 10 );
			entryProperties.setWidth( properties.getWidth() );
			entryProperties.setWidthUnits( properties.getWidthUnits() );
			entryProperties.setHeight( properties.getHeight() );
			entryProperties.setHeightUnits( properties.getHeightUnits() );
			entryProperties.setOverflow( properties.getOverflow() );
			
			widget = new EntryWidget( entryProperties, landingPageStyle );
			break;
		}

		case DISPLAY_RECENT_ENTRIES:
		{
			FolderProperties folderProperties;
			
			folderProperties = new FolderProperties();
			folderProperties.setZoneUUID( properties.getZoneUUID() );
			folderProperties.setFolderId( properties.getFolderId() );
			folderProperties.setShowTitle( true );
			folderProperties.setShowDescValue( false );
			folderProperties.setShowEntriesOpenedValue( true );
			folderProperties.setShowEntryAuthor( true );
			folderProperties.setShowEntryDate( true );
			folderProperties.setSortEntriesByTitle( false );
			folderProperties.setNumEntriesToBeShownValue( properties.getNumEntriesToBeShownValue() );
			folderProperties.setNumRepliesToShow( 10 );
			folderProperties.setWidth( properties.getWidth() );
			folderProperties.setWidthUnits( properties.getWidthUnits() );
			folderProperties.setHeight( properties.getHeight() );
			folderProperties.setHeightUnits( properties.getHeightUnits() );
			folderProperties.setOverflow( properties.getOverflow() );
			
			widget = new FolderWidget( folderProperties, landingPageStyle );
			break;
		}

		case DISPLAY_LIST_OF_RECENT_ENTRIES:
		{
			FolderProperties folderProperties;
			
			folderProperties = new FolderProperties();
			folderProperties.setZoneUUID( properties.getZoneUUID() );
			folderProperties.setFolderId( properties.getFolderId() );
			folderProperties.setShowTitle( true );
			folderProperties.setShowDescValue( true );
			folderProperties.setShowEntriesOpenedValue( false );
			folderProperties.setShowEntryAuthor( true );
			folderProperties.setShowEntryDate( true );
			folderProperties.setSortEntriesByTitle( false );
			folderProperties.setNumEntriesToBeShownValue( properties.getNumEntriesToBeShownValue() );
			folderProperties.setNumRepliesToShow( 0 );
			folderProperties.setWidth( properties.getWidth() );
			folderProperties.setWidthUnits( properties.getWidthUnits() );
			folderProperties.setHeight( properties.getHeight() );
			folderProperties.setHeightUnits( properties.getHeightUnits() );
			folderProperties.setOverflow( properties.getOverflow() );
			
			widget = new FolderWidget( folderProperties, landingPageStyle );
			break;
		}
		
		case DISPLAY_SORTED_LIST_RECENT_ENTRIES:
		{
			FolderProperties folderProperties;
			
			folderProperties = new FolderProperties();
			folderProperties.setZoneUUID( properties.getZoneUUID() );
			folderProperties.setFolderId( properties.getFolderId() );
			folderProperties.setShowTitle( properties.getShowTitleValue() );
			folderProperties.setShowDescValue( true );
			folderProperties.setShowEntriesOpenedValue( false );
			folderProperties.setShowEntryAuthor( true );
			folderProperties.setShowEntryDate( true );
			folderProperties.setSortEntriesByTitle( true );
			folderProperties.setNumEntriesToBeShownValue( properties.getNumEntriesToBeShownValue() );
			folderProperties.setNumRepliesToShow( 0 );
			folderProperties.setWidth( properties.getWidth() );
			folderProperties.setWidthUnits( properties.getWidthUnits() );
			folderProperties.setHeight( properties.getHeight() );
			folderProperties.setHeightUnits( properties.getHeightUnits() );
			folderProperties.setOverflow( properties.getOverflow() );
			
			widget = new FolderWidget( folderProperties, landingPageStyle );
			break;
		}

		case DISPLAY_SORTED_LIST_FILES:
		{
			FileFolderProperties fileFolderProperties;
			
			fileFolderProperties = new FileFolderProperties();
			fileFolderProperties.setZoneUUID( properties.getZoneUUID() );
			fileFolderProperties.setFolderId( properties.getFolderId() );
			fileFolderProperties.setShowTitle( properties.getShowTitleValue() );
			fileFolderProperties.setNumEntriesToBeShownValue( properties.getNumEntriesToBeShownValue() );
			fileFolderProperties.setWidth( properties.getWidth() );
			fileFolderProperties.setWidthUnits( properties.getWidthUnits() );
			fileFolderProperties.setHeight( properties.getHeight() );
			fileFolderProperties.setHeightUnits( properties.getHeightUnits() );
			fileFolderProperties.setOverflow( properties.getOverflow() );
			
			widget = new FileFolderWidget( fileFolderProperties, landingPageStyle );
			break;
		}
		
		case DISPLAY_TASK_FOLDER:
		{
			TaskFolderProperties taskFolderProperties;
			
			taskFolderProperties = new TaskFolderProperties();
			taskFolderProperties.setZoneUUID( properties.getZoneUUID() );
			taskFolderProperties.setFolderId( properties.getFolderId() );
			taskFolderProperties.setNumTasksToBeShownValue( properties.getNumEntriesToBeShownValue() );
			taskFolderProperties.setWidth( properties.getWidth() );
			taskFolderProperties.setWidthUnits( properties.getWidthUnits() );
			taskFolderProperties.setHeight( properties.getHeight() );
			taskFolderProperties.setHeightUnits( properties.getHeightUnits() );
			taskFolderProperties.setOverflow( properties.getOverflow() );
			
			widget = new TaskFolderWidget( taskFolderProperties, landingPageStyle );
			break;
		}
		
		case DISPLAY_CALENDAR:
		case DISPLAY_FULL_ENTRY:
		case DISPLAY_SURVEY:
		case UNKNOWN:
		default:
			widget = new EnhancedViewWidget( config, lpBinderId );
			break;
		}
		
		return widget;
	}
	
	
	/**
	 * This widget simply displays the name of the jsp file that is associated with the view type. 
	 */
	private EnhancedViewWidget( EnhancedViewConfig config, String lpBinderId )
	{
		// Remember the landing page binderId.
		m_lpBinderId = lpBinderId;
		
		m_mainPanel = init( config.getProperties(), config.getLandingPageStyle() );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainPanel );
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( EnhancedViewProperties properties, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		m_properties = new EnhancedViewProperties();
		m_properties.copy( properties );
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + landingPageStyle );
		mainPanel.addStyleName( "entryWidgetMainPanel" + landingPageStyle );

		mainPanel.addStyleName( "landingPageWidgetShowBorder" );

		// Set the width and height
		{
			Style style;
			int width;
			int height;
			Unit unit;
			
			style = mainPanel.getElement().getStyle();
			
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

		// Create the callback that will be used when we issue an ajax call to get a GwtFolder object.
		if ( m_executeJspCallback == null )
		{
			m_executeJspCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_executeEnhancedViewJsp(),
						m_properties.getJspName() );
					
				}
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( VibeRpcResponse response )
				{
					String html;
					
					html = null;
					
					if ( response.getResponseData() != null )
					{
						StringRpcResponseData responseData;
						
						responseData = (StringRpcResponseData) response.getResponseData();
						html = responseData.getStringValue();
					}

					if ( html == null )
						html = "";
					
					m_mainPanel.getElement().setInnerHTML( html );
					
				}
			};
		}

		// Issue an ajax request to execute the jsp associated with this enhanced view.
		{
			Scheduler.ScheduledCommand schCmd;
			
			schCmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					ExecuteEnhancedViewJspCmd cmd;
					
					cmd = new ExecuteEnhancedViewJspCmd( m_lpBinderId, m_properties.getJspName(), m_properties.createConfigString() );
					GwtClientHelper.executeCommand( cmd, m_executeJspCallback );
				}
			};
			Scheduler.get().scheduleDeferred( schCmd );
		}
		
		return mainPanel;
	}
}

