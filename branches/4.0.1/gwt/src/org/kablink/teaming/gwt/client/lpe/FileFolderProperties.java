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

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define a "File Folder" widget in a landing page.
 * @author jwootton
 *
 */
public class FileFolderProperties
	implements PropertiesObj
{
	private boolean m_showTitle;
	private int m_numEntriesToBeShown;
	private String m_folderId;
	private String m_folderTitle;
	private String m_folderDesc;
	private String m_zoneUUID;
	private String m_viewFolderUrl;
	private AsyncCallback<VibeRpcResponse> m_folderCallback;
	private GetterCallback<Boolean> m_getterCallback;
	
	// The following data members are used to define the width and height of the view.
	private int m_width;
	private Style.Unit m_widthUnits;
	private int m_height;
	private Style.Unit m_heightUnits;
	private Style.Overflow m_overflow;

	/**
	 * 
	 */
	public FileFolderProperties()
	{
		m_showTitle = false;
		m_numEntriesToBeShown = 0;
		m_folderId = null;
		m_folderTitle = null;
		m_folderDesc = null;
		m_zoneUUID = null;
		m_viewFolderUrl = null;
		m_getterCallback = null;

		// Default the width and height to 100%
		m_width = 100;
		m_widthUnits = Style.Unit.PCT;
		m_height = 100;
		m_heightUnits = Style.Unit.PCT;
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
				if ( ((GwtTeamingException) t).getExceptionType() != ExceptionType.ACCESS_CONTROL_EXCEPTION )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolder(),
						m_folderId );
				}

				// Inform the callback that the rpc request failed.
				if ( m_getterCallback != null )
					m_getterCallback.returnValue( Boolean.FALSE );
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
					setFolderTitle( gwtFolder.getFolderName() );
					setViewFolderUrl( gwtFolder.getViewFolderUrl() );
					setFolderDesc( gwtFolder.getFolderDesc() );
				}
				
				// Inform the callback that the rpc request finished.
				if ( m_getterCallback != null )
					m_getterCallback.returnValue( Boolean.TRUE );
			}
		};
	}
	
	
	/**
	 * 
	 */
	@Override
	public void copy( PropertiesObj props )
	{
		if ( props instanceof FileFolderProperties )
		{
			FileFolderProperties folderProps;
			String newFolderId;
			
			folderProps = (FileFolderProperties) props;
			
			// Did the folder id change?
			newFolderId = folderProps.getFolderId();
			if ( m_folderId != null && newFolderId != null && !m_folderId.equalsIgnoreCase( newFolderId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			m_zoneUUID = folderProps.getZoneUUID();
			m_folderId = newFolderId;
			m_folderTitle = folderProps.getFolderTitle();
			m_showTitle = folderProps.getShowTitleValue();
			m_numEntriesToBeShown = folderProps.getNumEntriesToBeShownValue();
			m_viewFolderUrl = folderProps.getViewFolderUrl();
			m_folderDesc = folderProps.getFolderDesc();
			m_width = folderProps.getWidth();
			m_widthUnits = folderProps.getWidthUnits();
			m_height = folderProps.getHeight();
			m_heightUnits = folderProps.getHeightUnits();
			m_overflow = folderProps.getOverflow();
		}
	}
	

	/**
	 * We do not need to create a config string.  This class is used to support the
	 * "display a sorted list of files" option in the enhanced view widget.
	 */
	@Override
	public String createConfigString()
	{
		return null;
	}
	
	
	/**
	 * Issue an ajax request to get the folder's name from the server.
	 */
	public void getDataFromServer( GetterCallback<Boolean> callback )
	{
		// Do we have a folder id?
		if ( m_folderId != null )
		{
			GetFolderCmd cmd;
			
			// Yes, Issue an ajax request to get the GwtFolder object for the given folder id.
			m_getterCallback = callback;
			cmd = new GetFolderCmd( m_zoneUUID, m_folderId );
			GwtClientHelper.executeCommand( cmd, m_folderCallback );
		}
	}
	
	
	/**
	 * 
	 */
	public String getFolderDesc()
	{
		return m_folderDesc;
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
	public String getFolderTitle()
	{
		return m_folderTitle;
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
	 * Return the "show title" property.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitle;
	}
	
	/**
	 * Return the url that can be used to view this folder.
	 */
	public String getViewFolderUrl()
	{
		return m_viewFolderUrl;
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
	 * Return the zone uuid
	 */
	public String getZoneUUID()
	{
		return m_zoneUUID;
	}
	
	
	/**
	 * 
	 */
	public void setFolderDesc( String desc )
	{
		m_folderDesc = desc;
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
			m_folderTitle = "???";
		}
		
		m_folderId = folderId;
	}
	
	
	/**
	 * 
	 */
	public void setFolderTitle( String folderTitle )
	{
		m_folderTitle = folderTitle;
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
		m_heightUnits = units;
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
	public void setShowTitle( boolean showTitle)
	{
		m_showTitle = showTitle;
	}
	
	/**
	 * Set the url that can be used to view this folder.
	 */
	public void setViewFolderUrl( String url )
	{
		m_viewFolderUrl = url;
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
}// end FolderProperties
