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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Used by LdapBrowserDlg to encapsulate a DirectoryServer and an
 * LdapSearchInfo into a single object. 
 *  
 * @author drfoster@novell.com
 */
public class LdapBrowseSpec implements IsSerializable {
	private DirectoryServer	m_ds;	//
	private LdapSearchInfo	m_si;	//

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public LdapBrowseSpec() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param ds
	 * @param si
	 */
	public LdapBrowseSpec(DirectoryServer ds, LdapSearchInfo si) {
		// Initialize this object...
		this();
		
		// ...and store the parameters...
		setDirectoryServer(ds);
		if (null == si) {
			// ...synthesizing an LdapSearchInfo if necessary.
			si = new LdapSearchInfo();
			if (ds.isActiveDirectory())
			     si.setSearchObjectClass(LdapSearchInfo.RETURN_EVERYTHING_AD  );
			else si.setSearchObjectClass(LdapSearchInfo.RETURN_CONTAINERS_ONLY);
		}
		setSearchInfo(si);
	}

	/**
	 * Constructor method.
	 * 
	 * @param ds
	 */
	public LdapBrowseSpec(DirectoryServer ds) {
		// Initialize this object.
		this(ds, null);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public DirectoryServer getDirectoryServer() {return m_ds;}
	public LdapSearchInfo  getSearchInfo()      {return m_si;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setDirectoryServer(DirectoryServer ds) {m_ds = ds;}
	public void setSearchInfo(     LdapSearchInfo  si) {m_si = si;}

	/**
	 * Scans a List<LdapBrowseSpec> for the first one that references
	 * the given server address.
	 * 
	 * @param browseList
	 * @param address
	 * 
	 * @return
	 */
	public static LdapBrowseSpec findBrowseSpecByAddress(List<LdapBrowseSpec> browseList, String address) {
		// If we don't have anything to search or anything to search
		// for...
		if ((null == browseList) || browseList.isEmpty() || (null == address) || (0 == address.length())) {
			// ...bail.
			return null;
		}

		// Scan the LdapBrowseSpec's.
		for (LdapBrowseSpec browse:  browseList) {
			// Is this the one in question?
			DirectoryServer ds = browse.getDirectoryServer();
			if (ds.getAddress().equals(address)) {
				// Yes!  Return it.
				return browse;
			}
		}

		// If we get here, we couldn't find the one in question.
		// Return null.
		return null;
	}
}
