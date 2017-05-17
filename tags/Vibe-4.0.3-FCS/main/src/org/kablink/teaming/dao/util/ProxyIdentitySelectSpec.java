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
package org.kablink.teaming.dao.util;

import org.kablink.teaming.ObjectKeys;

/**
 * This class encapsulates select specifications used to retrieve only
 * those ProxyIdentity's that fulfill specified criteria.
 * 
 * @author drfoster@novell.com
 */
public class ProxyIdentitySelectSpec {
	private boolean	m_sortAscend;	// true -> Sort ascending.  false -> Sort descending.
	private int		m_startIndex;	// Return the ProxyIdentity's starting from here.
	private int		m_pageSize;		// Return up to this many ProxyIdentity's with each query.
	private String	m_proxyName;	// The name of a specific proxy identifier, when required.
	private String	m_quickFilter;	// Any filtering for what's being queried.
	private String	m_sortBy;		// Attribute to sort on.
	private String	m_title;		// The title of a specific proxy identifier, when required.
	
	/**
	 * Constructor method.
	 * 
	 * @param sortAscend
	 * @param sortBy
	 * @param startIndex
	 * @param pageSize
	 * @param proxyName
	 * @param title
	 * @param quickFilter
	 */
	public ProxyIdentitySelectSpec(boolean sortAscend, String sortBy, int startIndex, int pageSize, String proxyName, String title, String quickFilter) {
		// Initialize the super class.
		super();
		
		// ...and store the parameters.
		setSortAscend( sortAscend );
		setSortBy(     sortBy     );
		setStartIndex( startIndex );
		setPageSize(   pageSize   );
		setProxyName(  proxyName  );
		setTitle(      title      );
		setQuickFilter(quickFilter);
	}
	
	/**
	 * Constructor method. 
	 */
	public ProxyIdentitySelectSpec() {
		// Initialize the this object.
		this(
			false,									// false -> Sort descending.
			ObjectKeys.FIELD_PROXY_IDENTITY_TITLE,	// Primary sort key.
			(-1),									// (-1) -> No starting index.
			(-1),									// (-1) -> No page size.
			null,									// null -> No proxy name.
			null,									// null -> No title.
			null);									// null -> No quick filter.
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isSortAscend()   {return m_sortAscend; }
	public int     getPageSize()    {return m_pageSize;   }
	public int     getStartIndex()  {return m_startIndex; }
	public String  getProxyName()   {return m_proxyName;  }
	public String  getQuickFilter() {return m_quickFilter;}
	public String  getSortBy()      {return m_sortBy;     }
	public String  getTitle()       {return m_title;      }
	
	/**
	 * Set'er method.
	 * 
	 * @param
	 */
	public void setSortAscend( boolean sortAscend) {m_sortAscend  = sortAscend; }
	public void setPageSize(   int    pageSize)    {m_pageSize    = pageSize;   }
	public void setStartIndex( int    startIndex)  {m_startIndex  = startIndex; }
	public void setProxyName(  String proxyName)   {m_proxyName   = proxyName;  }
	public void setQuickFilter(String quickFilter) {m_quickFilter = quickFilter;}
	public void setSortBy(     String sortBy)      {m_sortBy      = sortBy;     }
	public void setTitle(      String title)       {m_title       = title;      }
}
