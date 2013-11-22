/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.ldapbrowser;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Object used with LdapBrowserDlg to specify how the LDAP server is to
 * be searched.
 * 
 * @author rvasudevan
 * @author drfoster@novell.com
 */
public class LdapSearchInfo implements IsSerializable {
	private boolean	m_searchSubTree;		//
	private int		m_pageSize;				//
	private String 	m_baseDn;				//
	private String	m_searchObjectClass;	//

	// Default page size used to read information from the LDAP server.
	public static final int DEFAULT_PAGE_SIZE	= 1000;	//
	
	// Various object classes for searching.
	public static final String RETURN_EVERYTHING_EDIR = "(|(objectclass=person)(objectclass=groupOfNames)"
			+ "(objectclass=container)(objectclass=domain)(objectclass=groupWiseDistributionList)(objectclass=organization)(objectclass=country)(objectclass=locality)(objectclass=organizationalUnit)(objectclass=group)(objectclass=organizationalRole))";

	public static final String RETURN_EVERYTHING_AD = "(|(objectclass=person)(objectclass=groupOfNames)"
			+ "(objectclass=container)(objectclass=domain)(objectclass=groupWiseDistributionList)(objectclass=organization)(objectclass=country)(objectclass=locality)(objectclass=organizationalUnit)(objectclass=group)(objectclass=organizationalRole))(grouptype:OR: >= 1))";

	public static final String RETURN_USERS = "(|(objectclass=person)"
			+ "(objectclass=container)(objectclass=domain)(objectclass=organization)(objectclass=organizationalUnit)(objectclass=country)(objectclass=locality))";

	public static final String RETURN_GROUPS_EDIR = "(|"
			+ "(objectclass=container)(objectclass=domain)(objectclass=organization)(objectclass=organizationalUnit)(objectclass=organizationalRole)(objectclass=country)(objectclass=locality)(objectclass=group)(objectclass=groupWiseDistributionList)(objectclass=groupOfNames))";

	public static final String RETURN_GROUPS_AD = "(|"
			+ "(objectclass=container)(objectclass=domain)(objectclass=organization)(objectclass=organizationalUnit)(objectclass=organizationalRole)(objectclass=country)(objectclass=locality)(&(objectCategory=group)(!(groupType:1.2.840.113556.1.4.803:=2147483648)))(objectclass=groupWiseDistributionList)(objectclass=groupOfNames))";

	public static final String RETURN_CONTAINERS_ONLY = "(|(objectclass=organization)(objectclass=organizationalUnit)(objectclass=container)(objectclass=domain)(objectclass=country)(objectclass=locality)(objectclass=builtinDomain))";

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public LdapSearchInfo() {
		// Initialize this object.
		this(RETURN_EVERYTHING_EDIR, false);
	}

	/**
	 * Constructor method.
	 * 
	 * @param objectClass
	 * @param searchSubTree
	 */
	public LdapSearchInfo(String objectClass, boolean searchSubTree) {
		// Initialize the super class...
		super();

		// ...store the parameters...
		setSearchObjectClass( objectClass    );
		setSearchSubTree(     searchSubTree  );
		
		// ...and initialize everything else that requires it.
		setPageSize(DEFAULT_PAGE_SIZE);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isSearchSubTree()      {return m_searchSubTree;    }
	public int     getPageSize()          {return m_pageSize;         }
	public String  getBaseDn()            {return m_baseDn;           }
	public String  getSearchObjectClass() {return m_searchObjectClass;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setSearchSubTree(    boolean searchSubTree)      {m_searchSubTree     = searchSubTree;    }
	public void setPageSize(         int     pageSize)           {m_pageSize          = pageSize;         }
	public void setBaseDn(           String  baseDn)             {m_baseDn            = baseDn;           }
	public void setSearchObjectClass(String  searchObjectClass)  {m_searchObjectClass = searchObjectClass;}
}
