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
package org.kablink.teaming.gwt.client.mainmenu;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate recent place information between the
 * client (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getRecentPlaces().)
 * 
 * @author drfoster@novell.com
 *
 */
public class RecentPlaceInfo implements IsSerializable {
	/**
	 * Enumeration used to communicate the type of a recent place item
	 * between the client and the server as part of a GWT RPC request.
	 *
	 */
	public enum RecentPlaceType implements IsSerializable {
		BINDER,
		SEARCH,
		
		UNKNOWN,
	}

	private boolean			m_searchQuick;							// true -> This is a quick search, false -> It's not.
	private RecentPlaceType m_typeEnum = RecentPlaceType.UNKNOWN;	// 
	private String          m_binderId;								// The place's binder ID.
	private String          m_entityPath;							// The place's entity path.
	private String          m_entryId;								// The place's binder ID.
	private String          m_id;									// The place's ID.
	private String          m_permalinkUrl;							// The place's permalink URL.
	private String          m_searchQuery;							// The place's search query.
	private String          m_title;								// The place's title.
	private String          m_type;									// The place's type.
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public RecentPlaceInfo() {
		// Nothing to do.
	}

	/**
	 * Returns the place's binder ID.
	 * 
	 * @return
	 */
	public String getBinderId() {
		return m_binderId;
	}
	
	/**
	 * Returns the place's entity path.
	 * 
	 * @return
	 */
	public String getEntityPath() {
		return m_entityPath;
	}
	
	/**
	 * Returns the place's entry ID.
	 * 
	 * @return
	 */
	public String getEntryId() {
		return m_entryId;
	}
	
	/**
	 * Returns the place's ID.
	 * 
	 * @return
	 */
	public String getId() {
		return m_id;
	}
	
	/**
	 * Returns the place's permalink URL.
	 * 
	 * @return
	 */
	public String getPermalinkUrl() {
		return m_permalinkUrl;
	}

	/**
	 * Returns the place's search query.
	 * 
	 * @return
	 */
	public String getSearchQuery() {
		return m_searchQuery;
	}

	/**
	 * Returns true if this recent place is a quick search and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean getSearchQuick() {
		return m_searchQuick;
	}

	/**
	 * Returns the place's title.
	 * 
	 * @return
	 */
	public String getTitle() {
		return m_title;
	}

	/**
	 * Returns the place's type.
	 * 
	 * @return
	 */
	public String getType() {
		return m_type;
	}

	/**
	 * Returns the place's type enumeration value.
	 * 
	 * @return
	 */
	public RecentPlaceType getTypeEnum() {
		return m_typeEnum;
	}

	/**
	 * Stores the binder ID of the place.
	 * 
	 * @param binderId
	 */
	public void setBinderId(String binderId) {
		m_binderId = binderId;
	}
	
	/**
	 * Stores the entity path to the place.
	 * 
	 * @param entityPath
	 */
	public void setEntityPath(String entityPath) {
		m_entityPath = entityPath;
	}
	
	/**
	 * Stores the entry ID of the place.
	 * 
	 * @param entryId
	 */
	public void setEntryId(String entryId) {
		m_entryId = entryId;
	}
	
	/**
	 * Stores the ID of the place.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		m_id = id;
	}
	
	/**
	 * Stores the permalink URL to the place.
	 *  
	 * @param permalinkUrl
	 */
	public void setPermalink(String permalinkUrl) {
		m_permalinkUrl = permalinkUrl;
	}
	
	/**
	 * Stores the search query string to the place.
	 *  
	 * @param searchQuery
	 */
	public void setSearchQuery(String searchQuery) {
		m_searchQuery = searchQuery;
	}
	
	/**
	 * Stores whether this recent place is a quick search or not.
	 *  
	 * @param searchQuick
	 */
	public void setSearchQuick(String searchQuick) {
		setSearchQuick(null != searchQuick && searchQuick.equalsIgnoreCase("true"));
	}
	
	/**
	 * Stores whether this recent place is a quick search or not.
	 *  
	 * @param searchQuick
	 */
	public void setSearchQuick(boolean searchQuick) {
		if (RecentPlaceType.SEARCH == m_typeEnum)
			 m_searchQuick = searchQuick;
		else m_searchQuick = false;
	}
	
	/**
	 * Stores the title to the place.
	 *  
	 * @param title
	 */
	public void setTitle(String title) {
		m_title = title;
	}
	
	/**
	 * Stores the type to the place.
	 *  
	 * @param type
	 */
	public void setType(String type) {
		m_type = type;
		if (null != type) {
			m_typeEnum = (type.equals("search") ? RecentPlaceType.SEARCH : RecentPlaceType.BINDER);
		}
		else {
			m_typeEnum = RecentPlaceType.UNKNOWN;
		}
	}
}
