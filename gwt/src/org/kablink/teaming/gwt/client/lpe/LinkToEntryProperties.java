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
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define a "Link to Entry" widget in a landing page.
 * @author jwootton
 *
 */
public class LinkToEntryProperties
	implements PropertiesObj
{
	private String		m_title;
	private boolean	m_openInNewWindow;
	private String m_entryId;
	private String m_entryName;
	private String m_zoneUUID;
	private String m_viewEntryUrl;
	private AsyncCallback<VibeRpcResponse> m_folderEntryCallback;
	private GetterCallback<Boolean> m_getterCallback;
	
	/**
	 * 
	 */
	public LinkToEntryProperties()
	{
		m_title = null;
		m_openInNewWindow = false;
		m_entryId = null;
		m_entryName = null;
		m_zoneUUID = null;
		m_viewEntryUrl = null;
		m_getterCallback = null;
		
		// Create the callback that will be used when we issue an ajax call to get a GwtFolderEntry object.
		m_folderEntryCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure(Throwable t)
			{
				if ( ((GwtTeamingException) t).getExceptionType() != ExceptionType.ACCESS_CONTROL_EXCEPTION )
				{
					// This is not an access control exception.  Tell the user about the problem.
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolderEntry(),
						m_entryId );
				}
				
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
				GwtFolderEntry gwtFolderEntry;
				
				gwtFolderEntry = (GwtFolderEntry) response.getResponseData();
				
				if ( gwtFolderEntry != null )
				{
					m_entryName = gwtFolderEntry.getEntryName();
					m_viewEntryUrl = gwtFolderEntry.getViewEntryUrl();
				}
				
				// Notify the callback
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
		if ( props instanceof LinkToEntryProperties )
		{
			LinkToEntryProperties entryProps;
			String newEntryId;
			
			entryProps = (LinkToEntryProperties) props;
			
			// Did the entry id change?
			newEntryId = entryProps.getEntryId();
			if ( m_entryId != null && m_entryId != null && !m_entryId.equalsIgnoreCase( newEntryId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			setEntryId( newEntryId );
			setEntryName( entryProps.getEntryName() );
			setTitle( entryProps.getTitle() );
			setOpenInNewWindow( entryProps.getOpenInNewWindow() );
			setViewEntryUrl( entryProps.getViewEntryUrl() );
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	@Override
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "entryUrl,entryId=some id,title=some title,popup=1;"
		str = "entryUrl,entryId=";
		if ( m_entryId != null )
			str += m_entryId;
		str += ",";
		
		// Add the zone uuid if we have one.
		if ( m_zoneUUID != null && m_zoneUUID.length() > 0 )
			str += "zoneUUID=" + m_zoneUUID + ",";

		str += "title=";
		if ( m_title != null )
			str += ConfigData.encodeConfigData( m_title );
		
		if ( m_openInNewWindow )
			str += ",popup=1;";
		str += ";";

		return str;
	}// end createConfigString()
	
	
	/**
	 * Issue an ajax request to get the entry's name from the server.
	 */
	public void getDataFromServer( GetterCallback<Boolean> callback )
	{
		m_getterCallback = callback;
		
		// Do we have an entry id?
		if ( m_entryId != null )
		{
			GetEntryCmd cmd;
			
			// Yes, Issue an ajax request to get the GwtFolderEntry object for the given entry id.
			cmd = new GetEntryCmd( m_zoneUUID, m_entryId );
			GwtClientHelper.executeCommand( cmd, m_folderEntryCallback );
		}
	}// end getDataFromServer()
	
	
	/**
	 * Return the entry id.
	 */
	public String getEntryId()
	{
		return m_entryId;
	}// end getEntryId()
	
	
	/**
	 * Return the name of the entry.
	 */
	public String getEntryName()
	{
		return m_entryName;
	}// end getEntryName()
	
	
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
	public String getViewEntryUrl()
	{
		return m_viewEntryUrl;
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
	public void setEntryId( String entryId )
	{
		// Did the entry id change?
		if ( m_entryId != null && m_entryId.equalsIgnoreCase( entryId ) )
		{
			// Yes
			// Since we are changing the entry id clear out the entry name and the name of the parent binder.
			m_entryName = "???";
			m_viewEntryUrl = "";
		}
		
		m_entryId = entryId;
	}// end setEntryId()
	
	
	/**
	 * 
	 */
	public void setEntryName( String entryName )
	{
		m_entryName = entryName;
	}// end setEntryName()
	
	
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
	public void setViewEntryUrl( String viewEntryUrl )
	{
		m_viewEntryUrl = viewEntryUrl;
	}
	
	
	/**
	 * 
	 */
	public void setZoneUUID( String zoneUUID )
	{
		m_zoneUUID = zoneUUID;
	}// end setZoneUUID()
}// end LinkToEntryProperties
