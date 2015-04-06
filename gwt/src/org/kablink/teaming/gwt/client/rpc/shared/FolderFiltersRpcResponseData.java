/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get folder filters'
 * command.
 * 
 * @author drfoster@novell.com
 */
public class FolderFiltersRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<FolderFilter>	m_globalFilters;	//
	private List<FolderFilter>	m_personalFilters;	//

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public FolderFiltersRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_globalFilters   = new ArrayList<FolderFilter>();
		m_personalFilters = new ArrayList<FolderFilter>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int                getGlobalFiltersCount()   {return m_globalFilters.size();                               }
	public int                getPersonalFiltersCount() {return m_personalFilters.size();                             }
	public int                getTotalFiltersCount()    {return (getPersonalFiltersCount() + getGlobalFiltersCount());}
	public List<FolderFilter> getGlobalFilters()        {return m_globalFilters;                                      }
	public List<FolderFilter> getPersonalFilters()      {return m_personalFilters;                                    }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void addGlobalFilter(  FolderFilter globalFilter)   {m_globalFilters.add(  globalFilter  );}
	public void addPersonalFilter(FolderFilter personalFilter) {m_personalFilters.add(personalFilter);}

	/**
	 * Returns true if a filter by the given name already exists in
	 * this FolderFilterRpcResponseData and false otherwise.
	 * 
	 * @param name
	 * 
	 * @return
	 */
	public boolean filterExists(String name) {
		// If we weren't given a name to check...
		if ((null == name) || (0 == name.length())) {
			// ...it doesn't exist.  Return false.
			return false;
		}

		// Scan the global filters.
		for (FolderFilter globalFilter:  getGlobalFilters()) {
			// Is this the one in question?
			if (globalFilter.getFilterName().equalsIgnoreCase(name)) {
				// Yes!  Then we're done looking.  Return true.
				return true;
			}
		}
		
		// Scan the personal filters.
		for (FolderFilter personalFilter:  getPersonalFilters()) {
			// Is this the one in question?
			if (personalFilter.getFilterName().equalsIgnoreCase(name)) {
				// Yes!  Then we're done looking.  Return true.
				return true;
			}
		}

		// If we get here, we couldn't find a filter by the given
		// name.  Return false.
		return false;
	}
}
