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

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * This class holds all of the properties needed to define a "Custom jsp" widget in a landing page.
 * @author jwootton
 *
 */
public class CustomJspProperties
	implements PropertiesObj
{
	private String	 m_jspName;

	// The following data members are relevant when the user has opted to associate a folder with the custom jsp.
	private boolean m_showTitle;
	private int m_numEntriesToBeShown;
	private String m_folderId;
	private String m_folderName;
	private AsyncCallback<GwtFolder> m_folderCallback;

	// The following data members are relevant when the user has opted to associate an entry with the custom jsp.
	private String m_entryId;
	private String m_entryName;
	private String m_parentBinderName;	// Name of the binder the folder or entry is found in.
	private AsyncCallback<GwtFolderEntry> m_folderEntryCallback;
	
	// The following data members are relevant when the user has opted to associate either an entry or a folder with the custom jsp.
	private String m_zoneUUID;
	private boolean m_rpcInProgress;
	
	/**
	 * 
	 */
	public CustomJspProperties()
	{
		m_jspName = null;
		m_showTitle = false;
		m_folderId = null;
		m_folderName = null;
		m_numEntriesToBeShown = 0;
		m_entryId = null;
		m_entryName = null;
		m_parentBinderName = null;
		m_zoneUUID = null;
		m_rpcInProgress = false;

		// Create the callback that will be used when we issue an ajax call to get a GwtFolder object.
		m_folderCallback = new AsyncCallback<GwtFolder>()
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
				errMsg = GwtTeaming.getMessages().getFolderRPCFailed( cause );
				Window.alert( errMsg );
				m_rpcInProgress = false;
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtFolder gwtFolder )
			{
				if ( gwtFolder != null )
				{
					setFolderName( gwtFolder.getFolderName() );
					setParentBinderName( gwtFolder.getParentBinderName() );
				}
				
				m_rpcInProgress = false;
			}// end onSuccess()
		};

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
				}
				
				m_rpcInProgress = false;
			}// end onSuccess()
		};
	}// end CustomJspProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof CustomJspProperties )
		{
			CustomJspProperties customJspProps;
			String newFolderId;
			String newEntryId;
			
			customJspProps = (CustomJspProperties) props;
			setJspName( customJspProps.getJspName() );

			// Did the folder id change?
			newFolderId = customJspProps.getFolderId();
			if ( m_folderId != null && newFolderId != null && !m_folderId.equalsIgnoreCase( newFolderId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			// Did the entry id change?
			newEntryId = customJspProps.getEntryId();
			if ( m_entryId != null && m_entryId != null && !m_entryId.equalsIgnoreCase( newEntryId ) )
			{
				// Yes, throw away the zone id.
				m_zoneUUID = null;
			}
			
			m_entryId = newEntryId;
			m_entryName = customJspProps.getEntryName();
			m_folderId = newFolderId;
			m_folderName = customJspProps.getFolderName();
			m_parentBinderName = customJspProps.getParentBinderName();
			m_showTitle = customJspProps.getShowTitleValue();
			m_numEntriesToBeShown = customJspProps.getNumEntriesToBeShownValue();
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "customJsp,customJsp=some jsp name;"
		str = "customJsp,customJsp=";
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

		str += ";";
		
		return str;
	}// end createConfigString()
	
	
	/**
	 * Issue an ajax request to get the folder's name from the server.
	 */
	public void getDataFromServer()
	{
		GwtRpcServiceAsync rpcService;
		
		// Do we have a folder id?
		if ( m_folderId != null )
		{
			// Yes, Issue an ajax request to get the GwtFolder object for the given folder id.
			m_rpcInProgress = true;
			rpcService = GwtTeaming.getRpcService();
			rpcService.getFolder( m_zoneUUID, m_folderId, m_folderCallback );
		}
		// Do we have an entry id?
		else if ( m_entryId != null )
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
	 * Return the value of the jsp name property
	 */
	public String getJspName()
	{
		return m_jspName;
	}// end getJspName()
	

	/**
	 * Return the "number of entries to be shown" property.
	 */
	public int getNumEntriesToBeShownValue()
	{
		return m_numEntriesToBeShown;
	}// end getNumEntriesToBeShownValue()
	
	
	/**
	 * Return the name of the binder the folder lives in.
	 */
	public String getParentBinderName()
	{
		return m_parentBinderName;
	}// end getParentBinderName()
	
	
	/**
	 * Return the "show title" property.
	 */
	public boolean getShowTitleValue()
	{
		return m_showTitle;
	}// end getShowTitleValue()
	
	
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
	public void setJspName( String name )
	{
		m_jspName = name;
	}// end setJspName()


	/**
	 * Set the "number of entries to be shown" property.
	 */
	public void setNumEntriesToBeShownValue( int numEntries )
	{
		m_numEntriesToBeShown = numEntries;
	}// end setNumEntriesToBeShownValue()
	
	
	/**
	 * 
	 */
	public void setParentBinderName( String parentBinderName )
	{
		m_parentBinderName = parentBinderName;
	}// end setParentBinderName()
	
	
	/**
	 * 
	 */
	public void setShowTitle( boolean showTitle)
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
}// end CustomJspProperties
