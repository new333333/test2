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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.lpe.EnhancedViewConfig;
import org.kablink.teaming.gwt.client.lpe.EnhancedViewProperties;
import org.kablink.teaming.gwt.client.lpe.EnhancedViewProperties.EnhancedViewType;
import org.kablink.teaming.gwt.client.lpe.EntryProperties;
import org.kablink.teaming.gwt.client.lpe.FileFolderProperties;
import org.kablink.teaming.gwt.client.lpe.FolderProperties;
import org.kablink.teaming.gwt.client.lpe.MyTasksProperties;
import org.kablink.teaming.gwt.client.lpe.TaskFolderProperties;
import org.kablink.teaming.gwt.client.rpc.shared.ExecuteEnhancedViewJspCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * ?
 *  
 * @author jwootton
 */
public class EnhancedViewWidget extends VibeWidget
{
	private String m_lpBinderId;	// landing page binder id
	private EnhancedViewProperties m_properties;
	private WidgetStyles m_widgetStyles;
	private AsyncCallback<VibeRpcResponse> m_executeJspCallback = null;
	private VibeFlowPanel m_mainPanel;
	private String m_html;		// The html that we got from executing a jsp
	private String m_landingPageStyle;
	
	/**
	 * Create the appropriate widget based on what type of enhanced view we are dealing with
	 */
	public static VibeWidget createWidget( EnhancedViewConfig config, WidgetStyles widgetStyles, String lpBinderId )
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
			
			widget = new EntryWidget( entryProperties, widgetStyles, landingPageStyle );
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
			
			widget = new FolderWidget( folderProperties, widgetStyles, landingPageStyle );
			break;
		}

		case DISPLAY_LIST_OF_RECENT_ENTRIES:
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
			folderProperties.setSortEntriesByTitle( false );
			folderProperties.setNumEntriesToBeShownValue( properties.getNumEntriesToBeShownValue() );
			folderProperties.setNumRepliesToShow( 0 );
			folderProperties.setWidth( properties.getWidth() );
			folderProperties.setWidthUnits( properties.getWidthUnits() );
			folderProperties.setHeight( properties.getHeight() );
			folderProperties.setHeightUnits( properties.getHeightUnits() );
			folderProperties.setOverflow( properties.getOverflow() );
			
			widget = new FolderWidget( folderProperties, widgetStyles, landingPageStyle );
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
			
			widget = new FolderWidget( folderProperties, widgetStyles, landingPageStyle );
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
			
			widget = new FileFolderWidget( fileFolderProperties, widgetStyles, landingPageStyle );
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
			
			widget = new TaskFolderWidget( taskFolderProperties, widgetStyles, landingPageStyle );
			break;
		}
		
		case DISPLAY_MY_TASKS:
		{
			MyTasksProperties myTasksProperties;
			
			myTasksProperties = new MyTasksProperties();
			myTasksProperties.setWidth( properties.getWidth() );
			myTasksProperties.setWidthUnits( properties.getWidthUnits() );
			myTasksProperties.setHeight( properties.getHeight() );
			myTasksProperties.setHeightUnits( properties.getHeightUnits() );
			myTasksProperties.setOverflow( properties.getOverflow() );
			
			widget = new MyTasksWidget( myTasksProperties, widgetStyles, landingPageStyle );
			break;
		}
		
		case DISPLAY_CALENDAR:
		case DISPLAY_MY_CALENDAR_EVENTS:
		case DISPLAY_SURVEY:
		case UNKNOWN:
		default:
			widget = new EnhancedViewWidget( config, widgetStyles, lpBinderId );
			break;
		}
		
		return widget;
	}
	
	
	/**
	 * This widget simply displays the name of the jsp file that is associated with the view type. 
	 */
	private EnhancedViewWidget( EnhancedViewConfig config, WidgetStyles widgetStyles, String lpBinderId )
	{
		VibeFlowPanel mainPanel;
		
		// Remember the landing page binderId.
		m_lpBinderId = lpBinderId;
		
		mainPanel = init( config.getProperties(), widgetStyles, config.getLandingPageStyle() );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * Evaluate scripts in the html found in the given element.
	 */
	public static native void executeJavaScript( com.google.gwt.dom.client.Element element ) /*-{
		//$wnd.alert( 'In executeJavaScript()' );
		if ( $wnd.top.ss_executeJavascript != null && (typeof $wnd.top.ss_executeJavascript != 'undefined') )
		{
			//$wnd.alert( 'found ss_executeJavascript()' );
			$wnd.top.ss_executeJavascript( element, true );
		}
	}-*/;

	/**
	 * Execute the jsp associated with this enhanced view
	 */
	private void executeJsp()
	{
		// Execute the jsp associated with this enhanced view by issuing an rpc request.
		executeJspViaRpc();
	}
	
	/**
	 * Execute the jsp associated with this enhanced view using GWT rpc
	 */
	private void executeJspViaRpc()
	{
		ExecuteEnhancedViewJspCmd cmd;
		
		// Create the callback that will be used when we issue an ajax call to execute the jsp.
		if ( m_executeJspCallback == null )
		{
			m_executeJspCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
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
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					
					m_html = "";
					
					if ( response.getResponseData() != null )
					{
						StringRpcResponseData responseData;
						
						responseData = (StringRpcResponseData) response.getResponseData();
						m_html = responseData.getStringValue();
						if(m_properties.getViewType() == EnhancedViewType.DISPLAY_CALENDAR || m_properties.getViewType() == EnhancedViewType.DISPLAY_MY_CALENDAR_EVENTS){
							//landingPageWidgetTitlePanel_dark folderWidgetTitlePanel_dark
							String styleToSearchAndReplace="ss_mashup_folder_header_view";
							String elementToSearchAndReplace="<span";
							String styleToSearchAndReplacePadding="ss_mashup_element";
							
							int index=m_html.indexOf(styleToSearchAndReplacePadding);
							if(index>=0){
								m_html=m_html.substring(0,index) + m_html.substring(index+styleToSearchAndReplacePadding.length());
							}
																			
							index=m_html.indexOf(styleToSearchAndReplace);
							if(index>=0){
								String landingPageTitleStyle="landingPageWidgetTitlePanel" + m_landingPageStyle;
								String calendarWidgetTitleStyle="folderWidgetTitlePanel" + m_landingPageStyle;
								String backgroundColor="\" style=\"background-color:"+m_widgetStyles.getHeaderBgColor();
								String widgetStyle=landingPageTitleStyle + " " + calendarWidgetTitleStyle+backgroundColor;
								m_html=m_html.substring(0,index) + widgetStyle + m_html.substring(index+styleToSearchAndReplace.length());
							}
							
							index=m_html.indexOf(elementToSearchAndReplace);
							if(index>=0){
								String titleStyle=" class=\"gwt-InlineLabel folderWidgetTitleLabel" + m_landingPageStyle+"\"";								
								String textStyle="";
								if(m_widgetStyles.getHeaderTextColor()!=null){
									textStyle="style=\"color:"+m_widgetStyles.getHeaderTextColor()+"\"";								
								}
								String styleToApply=titleStyle+" "+textStyle;
								m_html=m_html.substring(0,index+elementToSearchAndReplace.length())+styleToApply+m_html.substring(index+elementToSearchAndReplace.length());
							}
						}
					}

					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_mainPanel.getElement().setInnerHTML( m_html );
							executeJavaScript( m_mainPanel.getElement() );
							GwtClientHelper.jsOnLoadInit();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
		}

		// Execute the jsp associated with this enhanced view.
		cmd = new ExecuteEnhancedViewJspCmd( m_lpBinderId, m_properties.getJspName(), m_properties.createConfigString() );
		GwtClientHelper.executeCommand( cmd, m_executeJspCallback );
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( EnhancedViewProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		m_widgetStyles = widgetStyles;
		m_landingPageStyle = landingPageStyle;
		
		m_properties = new EnhancedViewProperties();
		m_properties.copy( properties );
		
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "landingPageWidgetMainPanel" + landingPageStyle );
		m_mainPanel.addStyleName( "enhancedViewWidgetMainPanel" + landingPageStyle );
		m_mainPanel.addStyleName( "landingPageWidgetShowBorder");

		// Set the width and height
		{
			Style style;
			int width;
			int height;
			Unit unit;
			
			style = m_mainPanel.getElement().getStyle();
			
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
			
			// Are we dealing with a calendar?
			if ( m_properties.getViewType() == EnhancedViewType.DISPLAY_CALENDAR )
			{
				// yes
				// Set the overflow to hidden.  The content of the jsp will control scrolling.
				style.setOverflow( Style.Overflow.HIDDEN );
				
				// Leave room for a scrollbar on the right.
				//style.setPaddingRight( 14, Unit.PX );
				
				// Leave room for a scrollbar on the bottom.
				//style.setPaddingBottom( 14, Unit.PX );
			}
			else
			{
				style.setOverflow( m_properties.getOverflow() );
			}
		}

		// Issue a request to execute the jsp associated with this enhanced view
		{
			Scheduler.ScheduledCommand schCmd;
			
			schCmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					executeJsp();
				}
			};
			Scheduler.get().scheduleDeferred( schCmd );
		}
		
		return m_mainPanel;
	}
}
