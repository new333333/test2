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

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define a "Link to Folder" widget in a landing page.
 * @author jwootton
 *
 */
public class LinkToFolderProperties
	implements PropertiesObj
{
	private String		m_title;
	private boolean	m_openInNewWindow;
	private String m_folderId;
	private String m_folderName;
	private String m_zoneUUID;
	private String m_url;
	private AsyncCallback<VibeRpcResponse> m_folderCallback;
	private GetterCallback<Boolean> m_getterCallback;
	
	/**
	 * 
	 */
	public LinkToFolderProperties()
	{
		m_title = null;
		m_openInNewWindow = false;
		m_folderId = null;
		m_folderName = null;
		m_zoneUUID = null;
		m_url = null;
		m_getterCallback = null;
		
		// Create the callback that will be used when we issue an ajax call to get a GwtFolder object.
		m_folderCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure(Throwable t)
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
					m_folderName = gwtFolder.getFolderName();
					setUrl( gwtFolder.getViewFolderUrl() );
				}
				
				// Inform the callback that the rpc request finished.
				if ( m_getterCallback != null )
					m_getterCallback.returnValue( Boolean.TRUE );
			}// end onSuccess()
		};
	}// end LinkToFolderProperties()
	
	
	/**
	 * 
	 */
	@Override
	public void copy( PropertiesObj props )
	{
		if ( props instanceof LinkToFolderProperties )
		{
			LinkToFolderProperties folderProps;
			String newFolderId;
			
			folderProps = (LinkToFolderProperties) props;
			
			// Did the folder id change?
			newFolderId = folderProps.getFolderId();
			if ( m_folderId != null && newFolderId != null && !m_folderId.equalsIgnoreCase( newFolderId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			setFolderId( newFolderId );
			setFolderName( folderProps.getFolderName() );
			setTitle( folderProps.getTitle() );
			setOpenInNewWindow( folderProps.getOpenInNewWindow() );
			setUrl( folderProps.getUrl() );
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	@Override
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "binderUrl,binderId=370,title=something,popup=1;"
		str = "binderUrl,binderId=";
		if ( m_folderId != null )
			str += m_folderId;
		str += ",";
		
		// Add the zone uuid if we have one.
		if ( m_zoneUUID != null && m_zoneUUID.length() > 0 )
			str += "zoneUUID=" + m_zoneUUID + ",";

		str += "title=";
		if ( m_title != null )
			str += ConfigData.encodeConfigData( m_title );
		
		if ( m_openInNewWindow )
			str += ",popup=1";
		str += ";";

		return str;
	}// end createConfigString()
	
	
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
	 * Return the folder id.
	 */
	public String getFolderId()
	{
		return m_folderId;
	}// end getFolderId()
	
	
	/**
	 * Return the name of the folder.
	 */
	public String getFolderName()
	{
		return m_folderName;
	}// end getFolderName()
	
	
	/**
	 * Return the value of the "Open the url in a new window" property
	 */
	public boolean getOpenInNewWindow()
	{
		return m_openInNewWindow;
	}// getOpenInNewWindow()
	
	
	/**
	 * Return the "title" property.
	 */
	public String getTitle()
	{
		return m_title;
	}// end getTitle()
	
	
	/**
	 * 
	 */
	public String getUrl()
	{
		return m_url;
	}
	
	
	/**
	 * Return the zone uuid
	 */
	public String getZoneUUID()
	{
		return m_zoneUUID;
	}// end getZoneUUID()
	
	
	/**
	 * 
	 */
	public void setFolderId( String folderId )
	{
		// Did the folder id change?
		if ( m_folderId != null && m_folderId.equalsIgnoreCase( folderId ) )
		{
			// Yes
			// Since we are changing the folder id clear out the folder name
			m_folderName = "???";
		}
		
		m_folderId = folderId;
	}// end setFolderId()
	
	
	/**
	 * 
	 */
	public void setFolderName( String folderName )
	{
		m_folderName = folderName;
	}// end setFolderName()
	
	
	/**
	 * 
	 */
	public void setOpenInNewWindow( boolean value )
	{
		m_openInNewWindow = value;
	}// end setOpenInNewWindow()
	
	
	/**
	 * 
	 */
	public void setTitle( String title )
	{
		m_title = title;
	}// end setTitle()
	
	
	/**
	 * 
	 */
	public void setUrl( String url )
	{
		m_url = url;
	}


	/**
	 * 
	 */
	public void setZoneUUID( String zoneUUID )
	{
		m_zoneUUID = zoneUUID;
	}// end setZoneUUID()
}// end LinkToFolderProperties
