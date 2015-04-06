/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define an "Enhanced View" widget in a landing page.
 * @author jwootton
 *
 */
public class EnhancedViewProperties
	implements PropertiesObj
{
	// This enum defines the different possible displays an enhanced view can display
	public enum EnhancedViewType
	{
		DISPLAY_CALENDAR,
		DISPLAY_ENTRY,
		DISPLAY_LIST_OF_RECENT_ENTRIES,
		DISPLAY_RECENT_ENTRIES,
		DISPLAY_SORTED_LIST_RECENT_ENTRIES,
		DISPLAY_SORTED_LIST_FILES,
		DISPLAY_SURVEY,
		DISPLAY_TASK_FOLDER,
		DISPLAY_MY_CALENDAR_EVENTS,
		DISPLAY_MY_TASKS,
		UNKNOWN
	}

	private String	 m_jspName;
	private EnhancedViewType m_viewType;

	// The following data members are relevant when the enhanced view requires a folder to be selected.
	private boolean m_showTitle;
	private int m_numEntriesToBeShown;
	private String m_folderId;
	private String m_folderName;
	private AsyncCallback<VibeRpcResponse> m_folderCallback;

	// The following data members are relevant when the enhanced view requires an entry to be selected.
	private String m_entryId;
	private String m_entryName;
	private String m_parentBinderName;	// Name of the binder the folder or entry is found in.
	private AsyncCallback<VibeRpcResponse> m_folderEntryCallback;
	
	// The following data members are relevant when the enhanced view requires either a folder or an entry to be selected.
	private String m_zoneUUID;
	private boolean m_rpcInProgress;
	
	// The following data members are used to define the width and height of the view.
	private int m_width;
	private Style.Unit m_widthUnits;
	private int m_height;
	private Style.Unit m_heightUnits;
	private Style.Overflow m_overflow;
	
	/**
	 * 
	 */
	public EnhancedViewProperties()
	{
		m_jspName = null;
		m_viewType = EnhancedViewType.UNKNOWN;
		m_showTitle = false;
		m_folderId = null;
		m_folderName = null;
		m_numEntriesToBeShown = 5;
		m_entryId = null;
		m_entryName = null;
		m_parentBinderName = null;
		m_zoneUUID = null;
		m_rpcInProgress = false;
		
		// Default the width and height to nothing
		m_width = -1;
		m_widthUnits = Style.Unit.PCT;
		m_height = -1;
		m_heightUnits = Style.Unit.PX;
		m_overflow = Style.Overflow.HIDDEN;

		// Create the callback that will be used when we issue an ajax call to get a GwtFolder object.
		m_folderCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetFolder(),
					m_folderId );
				
				m_rpcInProgress = false;
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				GwtFolder gwtFolder;
				
				gwtFolder = (GwtFolder) response.getResponseData();
				
				if ( gwtFolder != null )
				{
					setFolderName( gwtFolder.getFolderName() );
					setParentBinderName( gwtFolder.getParentBinderName() );
				}
				
				m_rpcInProgress = false;
			}// end onSuccess()
		};

		// Create the callback that will be used when we issue an ajax call to get a GwtFolderEntry object.
		m_folderEntryCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetFolderEntry(),
					m_entryId );
				
				m_rpcInProgress = false;
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				GwtFolderEntry gwtFolderEntry ;
				
				gwtFolderEntry = (GwtFolderEntry) response.getResponseData();
				
				if ( gwtFolderEntry != null )
				{
					m_entryName = gwtFolderEntry.getEntryName();
					m_parentBinderName = gwtFolderEntry.getParentBinderName();
				}
				
				m_rpcInProgress = false;
			}// end onSuccess()
		};
	}
	
	
	/**
	 * 
	 */
	@Override
	public void copy( PropertiesObj props )
	{
		if ( props instanceof EnhancedViewProperties )
		{
			EnhancedViewProperties evProps;
			String newFolderId;
			String newEntryId;
			
			evProps = (EnhancedViewProperties) props;
			setJspName( evProps.getJspName() );

			// Did the folder id change?
			newFolderId = evProps.getFolderId();
			if ( m_folderId != null && newFolderId != null && !m_folderId.equalsIgnoreCase( newFolderId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			// Did the entry id change?
			newEntryId = evProps.getEntryId();
			if ( m_entryId != null && m_entryId != null && !m_entryId.equalsIgnoreCase( newEntryId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			m_zoneUUID = evProps.getZoneUUID();
			m_entryId = newEntryId;
			m_entryName = evProps.getEntryName();
			m_folderId = newFolderId;
			m_folderName = evProps.getFolderName();
			m_parentBinderName = evProps.getParentBinderName();
			m_showTitle = evProps.getShowTitleValue();
			m_numEntriesToBeShown = evProps.getNumEntriesToBeShownValue();
			m_width = evProps.getWidth();
			m_widthUnits = evProps.getWidthUnits();
			m_height = evProps.getHeight();
			m_heightUnits = evProps.getHeightUnits();
			m_overflow = evProps.getOverflow();
		}
	}
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	@Override
	public String createConfigString()
	{
		String str;
		
		// The string should look like: enhancedView,jspName=some jsp name;"
		str = "enhancedView,jspName=";
		if ( m_jspName != null )
			str += ConfigData.encodeConfigData( m_jspName );
		
		if ( m_folderId != null && m_folderId.length() > 0 )
		{
			str += ",folderId=" + m_folderId;

			if ( m_showTitle )
				str += ",showTitle=1";

			str += ",entriesToShow=" + String.valueOf( m_numEntriesToBeShown );
		}
		else if ( m_entryId != null && m_entryId.length() > 0 )
		{
			str += ",entryId=" + m_entryId;
			
			if ( m_showTitle )
				str += ",showTitle=1";
		}

		if ( m_folderId != null || m_entryId != null )
		{
			// Add the zone uuid if we have one.
			if ( m_zoneUUID != null && m_zoneUUID.length() > 0 )
				str += ",zoneUUID=" + m_zoneUUID;
		}
		
		// Has a width been set?
		if ( m_width > 0 )
		{
			// Yes, Add the width
			str += ",width=" + String.valueOf( m_width );
			if ( m_widthUnits == Style.Unit.PCT )
				str += "%";
			else
				str += "px";
		}

		// Has a height been set?
		if ( m_height > 0 )
		{
			// Yes, Add the height
			str += ",height=" + String.valueOf( m_height );
			if ( m_heightUnits == Style.Unit.PCT )
				str += "%";
			else
				str += "px";
		}
		
		// Add overflow
		str += ",overflow=";
		if ( m_overflow == Style.Overflow.AUTO )
			str += "auto";
		else
			str += "hidden";

		str += ";";
		
		return str;
	}
	
	
	/**
	 * Issue an ajax request to get the folder's name from the server.
	 */
	public void getDataFromServer()
	{
		// Do we have a folder id?
		if ( m_folderId != null )
		{
			GetFolderCmd cmd;
			
			// Yes, Issue an ajax request to get the GwtFolder object for the given folder id.
			m_rpcInProgress = true;
			cmd = new GetFolderCmd( m_zoneUUID, m_folderId );
			GwtClientHelper.executeCommand( cmd, m_folderCallback );
		}
		// Do we have an entry id?
		else if ( m_entryId != null )
		{
			GetEntryCmd cmd;
			
			// Yes, Issue an ajax request to get the GwtFolderEntry object for the given entry id.
			m_rpcInProgress = true;
			cmd = new GetEntryCmd( m_zoneUUID, m_entryId );
			GwtClientHelper.executeCommand( cmd, m_folderEntryCallback );
		}
	}
	
	
	/**
	 * Return the entry id.
	 */
	public String getEntryId()
	{
		return m_entryId;
	}
	
	
	/**
	 * Return the name of the entry.
	 */
	public String getEntryName()
	{
		return m_entryName;
	}
	
	
	/**
	 * Return the folder id.
	 */
	public String getFolderId()
	{
		return m_folderId;
	}
	
	
	/**
	 * Return the name of the folder.
	 */
	public String getFolderName()
	{
		return m_folderName;
	}
	
	/**
	 * Return the value of height.
	 */
	public int getHeight()
	{
		return m_height;
	}
	
	/**
	 * Return the height units
	 */
	public Style.Unit getHeightUnits()
	{
		return m_heightUnits;
	}
	
	
	/**
	 * Return the value of the jsp name property
	 */
	public String getJspName()
	{
		return m_jspName;
	}
	

	/**
	 * Return the "number of entries to be shown" property.
	 */
	public int getNumEntriesToBeShownValue()
	{
		return m_numEntriesToBeShown;
	}
	
	
	/**
	 * Return the value of overflow.
	 */
	public Style.Overflow getOverflow()
	{
		return m_overflow;
	}
	
	/**
	 * Return the name of the binder the folder lives in.
	 */
	public String getParentBinderName()
	{
		return m_parentBinderName;
	}
	
	
	/**
	 * Return the "show title" property.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitle;
	}
	
	
	/**
	 * Return the value of width.
	 */
	public int getWidth()
	{
		return m_width;
	}
	
	/**
	 * Return the width units
	 */
	public Style.Unit getWidthUnits()
	{
		return m_widthUnits;
	}
	
	
	/**
	 * 
	 */
	public EnhancedViewType getViewType()
	{
		return m_viewType;
	}
	
	
	/**
	 * Return the zone uuid
	 */
	public String getZoneUUID()
	{
		return m_zoneUUID;
	}
	
	
	/**
	 * Return whether an rpc call is in progress.
	 */
	public boolean isRpcInProgress()
	{
		return m_rpcInProgress;
	}
	
	
	/**
	 * 
	 */
	public void setEntryId( String entryId )
	{
		// Did the entry id change?
		if ( m_entryId != null && m_entryId.equalsIgnoreCase( entryId ) )
		{
			// Yes
			// Since we are changing the entry id clear out the entry name and the name of the parent binder.
			m_entryName = "???";
			m_parentBinderName = "???";
		}
		
		m_entryId = entryId;
	}
	
	
	/**
	 * 
	 */
	public void setFolderId( String folderId )
	{
		// Did the folder id change?
		if ( m_folderId != null && m_folderId.equalsIgnoreCase( folderId ) )
		{
			// Yes
			// Since we are changing the folder id clear out the folder name and the name of the parent binder.
			m_folderName = "???";
			m_parentBinderName = "???";
		}
		
		m_folderId = folderId;
	}
	
	
	/**
	 * 
	 */
	public void setFolderName( String folderName )
	{
		m_folderName = folderName;
	}
	
	
	/**
	 * 
	 */
	public void setHeight( int height )
	{
		m_height = height;
	}
	
	/**
	 * 
	 */
	public void setHeightUnits( Style.Unit units )
	{
		// Ignore this.  The height is always in px
		m_heightUnits = Style.Unit.PX;
	}
	
	
	/**
	 * 
	 */
	public void setJspName( String name )
	{
		m_jspName = name;
		
		if ( m_jspName != null )
		{
			if ( m_jspName.equalsIgnoreCase( "landing_page_entry.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_ENTRY;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_folder.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_RECENT_ENTRIES;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_folder_list.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_LIST_OF_RECENT_ENTRIES;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_folder_list_sorted.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_SORTED_LIST_RECENT_ENTRIES;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_folder_list_sorted_files.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_SORTED_LIST_FILES;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_calendar.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_CALENDAR;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_task_folder.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_TASK_FOLDER;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_survey.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_SURVEY;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_my_calendar_events.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_MY_CALENDAR_EVENTS;
			else if ( m_jspName.equalsIgnoreCase( "landing_page_my_tasks.jsp" ) )
				m_viewType = EnhancedViewType.DISPLAY_MY_TASKS;
			else
				m_viewType = EnhancedViewType.UNKNOWN;
		}
	}


	/**
	 * Set the "number of entries to be shown" property.
	 */
	public void setNumEntriesToBeShownValue( int numEntries )
	{
		m_numEntriesToBeShown = numEntries;
	}
	
	
	/**
	 * 
	 */
	public void setOverflow( Style.Overflow overflow )
	{
		m_overflow = overflow;
	}
	
	/**
	 * 
	 */
	public void setParentBinderName( String parentBinderName )
	{
		m_parentBinderName = parentBinderName;
	}
	
	
	/**
	 * 
	 */
	public void setShowTitle( boolean showTitle)
	{
		m_showTitle = showTitle;
	}


	/**
	 * 
	 */
	public void setWidth( int width )
	{
		m_width = width;
	}
	
	/**
	 * 
	 */
	public void setWidthUnits( Style.Unit units )
	{
		m_widthUnits = units;
	}
	
	
	/**
	 * 
	 */
	public void setZoneUUID( String zoneUUID )
	{
		m_zoneUUID = zoneUUID;
	}
}
