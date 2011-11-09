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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used in rpc calls and represents a serializable FolderEntry class.
 * @author jwootton
 *
 */
public class GwtFolderEntry extends GwtTeamingItem
	implements IsSerializable, VibeRpcResponseData
{
	private String m_entryId;
	private String m_entryName;
	private String m_entryDesc;
	private String m_author;
	private String m_authorId;
	private String m_authorWsId;		// Id of the author's workspace
	private String m_modificationDate;
	
	private Long m_parentBinderId;
	private String m_parentBinderName;
	private String m_viewUrl;
	private ArrayList<String> m_replyIds;	// Ids of the first n replies.
	
	/**
	 * 
	 */
	public GwtFolderEntry()
	{
		m_entryId = null;
		m_entryName = null;
		m_entryDesc = null;
		m_author = null;
		m_authorId = null;
		m_authorWsId = null;
		m_modificationDate = null;
		m_parentBinderId = null;
		m_parentBinderName = null;
		m_viewUrl = null;
		m_replyIds = null;
	}// end GwtFolderEntry()
	
	/**
	 * Add the given reply id to our list or reply ids. 
	 */
	public void addReplyId( String replyId )
	{
		if ( m_replyIds == null )
			m_replyIds = new ArrayList<String>();
		
		m_replyIds.add( replyId );
	}
	
	/**
	 * 
	 */
	public String getAuthor()
	{
		return m_author;
	}
	
	/**
	 * 
	 */
	public String getAuthorId()
	{
		return m_authorId;
	}
	
	/**
	 * 
	 */
	public String getAuthorWorkspaceId()
	{
		return m_authorWsId;
	}
	
	/**
	 * 
	 */
	public String getEntryDesc()
	{
		return m_entryDesc;
	}
	
	/**
	 * 
	 */
	public String getEntryId()
	{
		return m_entryId;
	}// end getEntryId()
	
	/**
	 * 
	 */
	public String getEntryName()
	{
		return m_entryName;
	}// end getEntryName()
	
	/**
	 * 
	 */
	public String getModificationDate()
	{
		return m_modificationDate;
	}
	
	/**
	 * 
	 */
	public String getName()
	{
		return getEntryName();
	}
	
	/**
	 * 
	 */
	public Long getParentBinderId()
	{
		return m_parentBinderId;
	}// end getParentBinderId()

	/**
	 * 
	 */
	public String getParentBinderName()
	{
		return m_parentBinderName;
	}// end getParentBinderName()


	/**
	 * Return the list of the reply ids
	 */
	public ArrayList<String> getReplyIds()
	{
		return m_replyIds;
	}
	
	/**
	 * Return the name of the parent binder.
	 */
	public String getSecondaryDisplayText()
	{
		String name;
		
		name = "";
		
		if ( m_parentBinderName != null )
			name += " (" + m_parentBinderName + ")";
		
		return name;
	}// end getSecondaryDisplayText()
	
	
	/**
	 * Return the name that should be displayed when this entry is displayed.
	 */
	public String getShortDisplayName()
	{
		return m_entryName;
	}// end getShortDisplayName()
	
	
	/**
	 * 
	 */
	public String getTitle()
	{
		return m_entryName;
	}
	
	/**
	 * Return the url that can be used to view this entry.
	 */
	public String getViewEntryUrl()
	{
		return m_viewUrl;
	}// end getViewEntryUrl()
	
	
	/**
	 * 
	 */
	public void setAuthor( String author )
	{
		m_author = author;
	}
	
	/**
	 * 
	 */
	public void setAuthorId( String authorId )
	{
		m_authorId = authorId;
	}
	
	/**
	 * 
	 */
	public void setAuthorWorkspaceId( String workspaceId )
	{
		m_authorWsId = workspaceId;
	}
	
	/**
	 * 
	 */
	public void setEntryDesc( String desc )
	{
		m_entryDesc = desc;
	}
	
	
	/**
	 * 
	 */
	public void setEntryId( String entryId )
	{
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
	public void setModificationDate( String date )
	{
		m_modificationDate = date;
	}
	
	/**
	 * 
	 */
	public void setParentBinderId( Long id )
	{
		m_parentBinderId = id;
	}// end setParentBinderId()
	
	
	/**
	 * 
	 */
	public void setParentBinderName( String parentBinderName )
	{
		m_parentBinderName = parentBinderName;
	}// end setParentBinderName()


	/**
	 * Set the url that can be used to view this entry.
	 */
	public void setViewEntryUrl( String url)
	{
		m_viewUrl = url;
	}// end createViewEntryUrl()
	
}// end GwtFolderEntry
