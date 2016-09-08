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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used in rpc calls and represents a serializable Folder class.
 * @author jwootton
 *
 */
public class GwtFolder extends GwtTeamingItem
	implements IsSerializable, VibeRpcResponseData
{
	private String m_folderId;
	private String m_folderName;
	private String m_folderDesc;
	private String m_parentBinderName;
	private String m_viewUrl;
	
	/**
	 * 
	 */
	public GwtFolder()
	{
		m_folderId = null;
		m_folderName = null;
		m_folderDesc = null;
		m_parentBinderName = null;
		m_viewUrl = null;
	}// end GwtFolder()
	
	
	/**
	 * 
	 */
	@Override
	public String getImageUrl()
	{
		return "";
	}
	
	/**
	 * 
	 */
	public String getFolderDesc()
	{
		return m_folderDesc;
	}
	
	/**
	 * 
	 */
	public String getFolderId()
	{
		return m_folderId;
	}// end getFolderId()
	
	/**
	 * 
	 */
	public String getFolderName()
	{
		return m_folderName;
	}// end getFolderName()
	
	/**
	 * 
	 */
	@Override
	public String getName()
	{
		return getFolderName();
	}

	/**
	 * 
	 */
	public String getParentBinderName()
	{
		return m_parentBinderName;
	}// end getParentBinderName()


	/**
	 * Return the name of the parent binder.
	 */
	@Override
	public String getSecondaryDisplayText()
	{
		String name;
		
		name = "";
		
		if ( m_parentBinderName != null )
			name += " (" + m_parentBinderName + ")";
		
		return name;
	}// end getSecondaryDisplayText()
	
	
	/**
	 * Return the short name that should be displayed when this folder is displayed.
	 */
	@Override
	public String getShortDisplayName()
	{
		return m_folderName;
	}// end getShortDisplayName()
	
	/**
	 * 
	 */
	@Override
	public String getTitle()
	{
		return "";
	}
	
	
	/**
	 * Return the url that can be used to view this folder.
	 */
	public String getViewFolderUrl()
	{
		return m_viewUrl;
	}// end getViewFolderUrl()
	
	
	/**
	 * 
	 */
	public void setFolderDesc( String folderDesc )
	{
		m_folderDesc = folderDesc;
	}
	
	/**
	 * 
	 */
	public void setFolderId( String folderId )
	{
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
	public void setParentBinderName( String parentBinderName )
	{
		m_parentBinderName = parentBinderName;
	}// end setParentBinderName()


	/**
	 * Set the url that can be used to view this folder.
	 */
	public void setViewFolderUrl( String url )
	{
		m_viewUrl = url;
	}// end createViewFolderUrl()
	
}// end GwtFolder
