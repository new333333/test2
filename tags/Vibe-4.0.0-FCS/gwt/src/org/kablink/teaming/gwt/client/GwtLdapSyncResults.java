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
package org.kablink.teaming.gwt.client;


import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtLdapSyncResult.GwtEntityType;
import org.kablink.teaming.gwt.client.GwtLdapSyncResult.GwtLdapSyncAction;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to represent ldap sync results.
 * @author jwootton
 *
 */
public class GwtLdapSyncResults implements IsSerializable, VibeRpcResponseData
{
	private GwtLdapSyncError m_syncError;
	private GwtLdapSyncStatus m_status;
	private String m_errorDesc;				// If an error happened, its description will be stored here.
	private String m_errorLdapServerId;		// The id of the ldap server being used when an error happened.
	private int m_numUsersAdded;
	private int m_numUsersDeleted;
	private int m_numUsersDisabled;
	private int m_numUsersModified;
	private int m_numGroupsAdded;
	private int m_numGroupsDeleted;
	private int m_numGroupsModified;

	private ArrayList<GwtLdapSyncResult> m_listOfSyncResults;

	
	/**
	 * 
	 */
	public enum GwtLdapSyncError implements IsSerializable
	{
		INVALID_SYNC_ID,
		SYNC_ID_IS_NULL
	}

	/**
	 * 
	 */
	public enum GwtLdapSyncStatus implements IsSerializable
	{
		STATUS_IN_PROGRESS,
		STATUS_COMPLETED,
		STATUS_STOP_COLLECTING_RESULTS,
		STATUS_ABORTED_BY_ERROR,
		STATUS_SYNC_ALREADY_IN_PROGRESS
	}

	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtLdapSyncResults()
	{
		m_syncError = null;
		m_numUsersAdded = 0;
		m_numUsersDeleted = 0;
		m_numUsersDisabled = 0;
		m_numUsersModified = 0;
		m_numGroupsAdded = 0;
		m_numGroupsDeleted = 0;
		m_numGroupsModified = 0;
	}
	
	/**
	 * 
	 */
	public void addLdapSyncResults(
		GwtLdapSyncAction syncAction,
		GwtEntityType entityType,
		ArrayList<String> listOfEntityNames )
	{
		if ( listOfEntityNames != null && listOfEntityNames.size() > 0 )
		{
			for ( String nextName : listOfEntityNames )
			{
				GwtLdapSyncResult syncResult;
				
				syncResult = new GwtLdapSyncResult();
				syncResult.setEntityName( nextName );
				syncResult.setEntityType( entityType );
				syncResult.setSyncAction( syncAction );
				
				addSyncResult( syncResult );
			}
		}
	}
	
	/**
	 * 
	 */
	private void addSyncResult( GwtLdapSyncResult syncResult )
	{
		GwtEntityType entityType;
		
		if ( m_listOfSyncResults == null )
			m_listOfSyncResults = new ArrayList<GwtLdapSyncResult>();
		
		m_listOfSyncResults.add( syncResult );

		entityType = syncResult.getEntityType();
		
		switch ( syncResult.getSyncAction() )
		{
		case ADDED_ENTITY:
			if ( entityType == GwtEntityType.USER )
				++m_numUsersAdded;
			else if ( entityType == GwtEntityType.GROUP )
				++m_numGroupsAdded;
			break;
			
		case DELETED_ENTITY:
			if ( entityType == GwtEntityType.USER )
				++m_numUsersDeleted;
			else if ( entityType == GwtEntityType.GROUP )
				++m_numGroupsDeleted;
			break;
		
		case MODIFIED_ENTITY:
			if ( entityType == GwtEntityType.USER )
				++m_numUsersModified;
			else if ( entityType == GwtEntityType.GROUP )
				++m_numGroupsModified;
			break;

		case DISABLED_ENTITY:
			if ( entityType == GwtEntityType.USER )
				++m_numUsersDisabled;
			break;
		}
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtLdapSyncResult> getListOfSyncResults()
	{
		return m_listOfSyncResults;
	}

	/**
	 * 
	 */
	public String getErrorDesc()
	{
		return m_errorDesc;
	}
	
	/**
	 * 
	 */
	public String getErrorLdapServerId()
	{
		return m_errorLdapServerId;
	}
	
	/**
	 * 
	 */
	public int getNumGroupsAdded()
	{
		return m_numGroupsAdded;
	}
	
	/**
	 * 
	 */
	public int getNumGroupsDeleted()
	{
		return m_numGroupsDeleted;
	}
	
	/**
	 * 
	 */
	public int getNumGroupsModified()
	{
		return m_numGroupsModified;
	}
	
	/**
	 * 
	 */
	public int getNumUsersAdded()
	{
		return m_numUsersAdded;
	}
	
	/**
	 * 
	 */
	public int getNumUsersDeleted()
	{
		return m_numUsersDeleted;
	}
	
	/**
	 * 
	 */
	public int getNumUsersDisabled()
	{
		return m_numUsersDisabled;
	}
	
	/**
	 * 
	 */
	public int getNumUsersModified()
	{
		return m_numUsersModified;
	}
	
	/**
	 * 
	 */
	public GwtLdapSyncError getSyncError()
	{
		return m_syncError;
	}
	
	/**
	 * 
	 */
	public GwtLdapSyncStatus getSyncStatus()
	{
		return m_status;
	}

	/**
	 * 
	 */
	public void setErrorDesc( String desc )
	{
		m_errorDesc = desc;
	}
	
	/**
	 * 
	 */
	public void setErrorLdapServerId( String id )
	{
		m_errorLdapServerId = id;
	}
	
	/**
	 * 
	 */
	public void setSyncError( GwtLdapSyncError error )
	{
		m_syncError = error;
	}
	
	/**
	 * 
	 */
	public void setSyncStatus( GwtLdapSyncStatus status )
	{
		m_status = status;
	}
}
