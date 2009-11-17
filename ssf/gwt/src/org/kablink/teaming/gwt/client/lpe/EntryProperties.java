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

package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define an "Entry" widget in a landing page.
 * @author jwootton
 *
 */
public class EntryProperties
	implements PropertiesObj
{
	private boolean m_showTitle;
	private String m_entryId;
	private String m_entryName;
	private String m_parentBinderName;	// Name of the binder the entry is found in.
	private String m_zoneUUID;
	private String m_viewEntryUrl;
	private AsyncCallback<GwtFolderEntry> m_folderEntryCallback;
	private boolean m_rpcInProgress;
	
	/**
	 * 
	 */
	public EntryProperties()
	{
		m_showTitle = false;
		m_entryId = null;
		m_entryName = null;
		m_parentBinderName = null;
		m_zoneUUID = null;
		m_viewEntryUrl = null;
		
		// Create the callback that will be used when we issue an ajax call to get a GwtFolderEntry object.
		m_folderEntryCallback = new AsyncCallback<GwtFolderEntry>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				String errMsg;
				String cause;
				
				cause = t.getLocalizedMessage();
				if ( cause == null )
					cause = t.toString();
				errMsg = GwtTeaming.getMessages().getFolderEntryRPCFailed( cause );
				Window.alert( errMsg );
				m_rpcInProgress = false;
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtFolderEntry gwtFolderEntry )
			{
				if ( gwtFolderEntry != null )
				{
					m_entryName = gwtFolderEntry.getEntryName();
					m_parentBinderName = gwtFolderEntry.getParentBinderName();
					m_viewEntryUrl = gwtFolderEntry.getViewEntryUrl();
				}
				
				m_rpcInProgress = false;
			}// end onSuccess()
		};
		m_rpcInProgress = false;
	}// end EntryProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof EntryProperties )
		{
			EntryProperties entryProps;
			String newEntryId;
			
			entryProps = (EntryProperties) props;
			
			// Did the entry id change?
			newEntryId = entryProps.getEntryId();
			if ( m_entryId != null && m_entryId != null && !m_entryId.equalsIgnoreCase( newEntryId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			m_entryId = newEntryId;
			m_entryName = entryProps.getEntryName();
			m_parentBinderName = entryProps.getBinderName();
			m_showTitle = entryProps.getShowTitleValue();
			m_viewEntryUrl = entryProps.getViewEntryUrl();
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "entry,entryId=some id,showTitle=1;"
		str = "entry,entryId=";
		if ( m_entryId != null )
			str += m_entryId;
		str += ",";
		
		// Add the zone uuid if we have one.
		if ( m_zoneUUID != null && m_zoneUUID.length() > 0 )
			str += "zoneUUID=" + m_zoneUUID + ",";
		
		str += "showTitle=";
		if ( m_showTitle )
			str += "1";
		else
			str += "0";
		str += ";";
		
		return str;
	}// end createConfigString()
	
	
	/**
	 * Return the name of the binder the entry lives in.
	 */
	public String getBinderName()
	{
		return m_parentBinderName;
	}// end getBinderName()
	
	
	/**
	 * Issue an ajax request to get the entry's name from the server.
	 */
	public void getDataFromServer()
	{
		GwtRpcServiceAsync rpcService;
		
		// Do we have an entry id?
		if ( m_entryId != null )
		{
			// Yes, Issue an ajax request to get the GwtFolderEntry object for the given entry id.
			m_rpcInProgress = true;
			rpcService = GwtTeaming.getRpcService();
			rpcService.getEntry( m_zoneUUID, m_entryId, m_folderEntryCallback );
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
	 * Return the "show title" property.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitle;
	}// end getShowTitleValue()
	
	
	/**
	 * Return the url that can be used to view this entry.
	 */
	public String getViewEntryUrl()
	{
		return m_viewEntryUrl;
	}// end getViewEntryUrl()
	
	
	/**
	 * Return the zone uuid
	 */
	public String getZoneUUID()
	{
		return m_zoneUUID;
	}// end getZoneUUID()
	
	
	/**
	 * Return whether an rpc call is in progress.
	 */
	public boolean isRpcInProgress()
	{
		return m_rpcInProgress;
	}// end isRpcInProgress()
	
	
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
	}// end setEntryId()
	
	
	/**
	 * 
	 */
	public void setShowTitle( boolean showTitle )
	{
		m_showTitle = showTitle;
	}// end setShowBorder()


	/**
	 * 
	 */
	public void setZoneUUID( String zoneUUID )
	{
		m_zoneUUID = zoneUUID;
	}// end setZoneUUID()
}// end EntryProperties
