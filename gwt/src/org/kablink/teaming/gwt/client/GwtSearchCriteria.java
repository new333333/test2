/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used to hold the criteria used to perform a search for
 * something.
 * 
 * @author drfoster@novell.com
 */
public class GwtSearchCriteria implements IsSerializable {
	private boolean		m_addCurrentUser;								//
	private boolean		m_foldersOnly;									//
	private boolean		m_searchForExternalPrincipals;					// When searching for users/groups should we search for external users/groups?
	private boolean		m_searchForInternalPrincipals;					// When searching for users/groups should we search for internal users/groups?
	private boolean		m_searchForLdapGroups = true;					// When search for groups should we include ldap groups.
	private boolean		m_searchForLdapContainers;						// When searching for groups should we include ldap containers.
	private boolean		m_searchForTeamGroups;							// When searching for groups should we include "team groups".
	private boolean		m_searchSubFolders;								//
	private boolean		m_sendingEmail;									// Is the search part of a "sending email" ui?
	private int			m_maxResults = 10;								//
	private int			m_pageNumber;									//
	private SearchScope	m_searchScope = SearchScope.SEARCH_ENTIRE_SITE;	//
	private SearchType	m_searchType  = SearchType.ENTRIES;				//
	private String		m_binderId;										//
	private String		m_searchText;									//
	
	/**
	 * This enumeration defines the different search scopes
	 */
	public enum SearchScope implements IsSerializable {
		SEARCH_ENTIRE_SITE,
		SEARCH_LOCAL;
	}
	
	/**
	 * This enumeration defines all the possible types of searches.
	 * 
	 * Note:  There are distinct search types for PERSON and USER.  The
	 *   difference between this is that PERSON searches for those
	 *   user accounts that people generally login to (e.g., admin, ...)
	 *   and USER servers for all user accounts includes those that are
	 *   not typically logged into (e.g., E-mail posting agent, ...)
	 */
	public enum SearchType implements IsSerializable {
		APPLICATION,
		APPLICATION_GROUP,
		COMMUNITY_TAGS,
		ENTRIES,
		ENTRY_FIELDS,
		ENTRY_TYPES,
		FOLDERS,	// Folders only.
		GROUP,
		PERSON,
		PERSONAL_TAGS,
		PLACES,		// Folders and workspaces.
		PRINCIPAL,	// Users and Groups
		PROXY_IDENTITY,
		TAG,
		TEAMS,
		USER,
		WORKFLOWS,
		WORKFLOW_STEPS;
	}
	
	/**
	 * Constructor methods.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public GwtSearchCriteria() {
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean     getAddCurrentUser()              {return m_addCurrentUser;             }
	public boolean     getFoldersOnly()                 {return m_foldersOnly;                }
	public boolean     getIsSendingEmail()              {return m_sendingEmail;               }
	public boolean     getSearchForExternalPrincipals() {return m_searchForExternalPrincipals;}
	public boolean     getSearchForInternalPrincipals() {return m_searchForInternalPrincipals;}
	public boolean     getSearchForLdapContainers()     {return m_searchForLdapContainers;    }
	public boolean     getSearchForLdapGroups()         {return m_searchForLdapGroups;        }
	public boolean     getSearchForTeamGroups()         {return m_searchForTeamGroups;        }
	public boolean     getSearchSubfolders()            {return m_searchSubFolders;           }
	public int         getMaxResults()                  {return m_maxResults;                 }
	public int         getPageNumber()                  {return m_pageNumber;                 }
	public SearchScope getSearchScope()                 {return m_searchScope;                }
	public SearchType  getSearchType()                  {return m_searchType;                 }
	public String      getBinderId()                    {return m_binderId;                   }
	public String      getSearchText()                  {return m_searchText;                 }
	
	/**
	 * Decrements the page number, but not below 0.
	 * 
	 * @return
	 */
	public int decrementPageNumber() {
		if (m_pageNumber > 0) {
			m_pageNumber -= 1;
		}
		return m_pageNumber;
	}
	
	/**
	 * Increments the page number.
	 * 
	 * @return 
	 */
	public int incrementPageNumber() {
		m_pageNumber += 1;
		return m_pageNumber;
	}
	
	/**
	 * Set'err methods.
	 * 
	 * @param
	 */
	public void setAddCurrentUser(             boolean     addCurrentUser) {m_addCurrentUser              = addCurrentUser;            }
	public void setFoldersOnly(                boolean     foldersOnly)    {m_foldersOnly                 = foldersOnly;               }
	public void setIsSendingEmail(             boolean     sendingEmail)   {m_sendingEmail                = sendingEmail;              }
	public void setSearchForExternalPrincipals(boolean     external)       {m_searchForExternalPrincipals = external;                  }
	public void setSearchForInternalPrincipals(boolean     internal)       {m_searchForInternalPrincipals = internal;                  }
	public void setSearchForLdapContainers(    boolean     search)         {m_searchForLdapContainers     = search;                    }
	public void setSearchForLdapGroups(        boolean     search)         {m_searchForLdapGroups         = search;                    }
	public void setSearchForTeamGroups(        boolean     search)         {m_searchForTeamGroups         = search;                    }
	public void setSearchSubfolders(           boolean     search)         {m_searchSubFolders            = search;                    }
	public void setMaxResults(                 int         maxResults)     {m_maxResults                  = maxResults;                }
	public void setPageNumber(                 int         pageNumber)     {m_pageNumber                  = pageNumber;                }	// Set the page number where we want to get results from.  For example, if we know there are 100 search results and we want results 30-39 and there are m_maxResults equals 10, then we would set m_pageNumber to 3.
	public void setSearchScope(                SearchScope scope)          {m_searchScope                 = scope;                     }
	public void setSearchType(                 SearchType  searchType)     {m_searchType                  = searchType;                }
	public void setBinderId(                   String      binderId)       {m_binderId                    = binderId;                  }
	public void setSearchText(                 String      txt)            {m_searchText                  = ((txt == null) ? "" : txt);}
}
