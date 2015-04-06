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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about filters between the
 * client and server.
 * 
 * @author drfoster@novell.com
 */
public class BinderFilter implements IsSerializable, VibeRpcResponseData {
	private String	m_filterName;		//
	private String	m_filterScope;		//
	private String	m_filterAddUrl;		//
	private String	m_filterClearUrl;	//
	
	/*
	 * Constructor method.
	 */
	private BinderFilter() {
		// Simply initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param filterName
	 * @param filterScope
	 * @param filterAddUrl
	 * @param filterClearUrl
	 */
	public BinderFilter(String filterName, String filterScope, String filterAddUrl, String filterClearUrl) {
		// Initialize the object...
		this();
		
		// ...and store the parameters.
		setFilterName(    filterName    );
		setFilterScope(   filterScope   );
		setFilterAddUrl(  filterAddUrl  );
		setFilterClearUrl(filterClearUrl);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isGlobal()          {return m_filterScope.equals("global");                    }
	public boolean isPersonal()        {return (!(isGlobal()));                                   }
	public String  getFilterName()     {return m_filterName;                                      }
	public String  getFilterScope()    {return m_filterScope;                                     }
	public String  getFilterSpec()     {return buildFilterSpec(getFilterName(), getFilterScope());}
	public String  getFilterAddUrl()   {return m_filterAddUrl;                                    }
	public String  getFilterClearUrl() {return m_filterClearUrl;                                  }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setFilterName(    String filterName    ) {m_filterName     = filterName;    }
	public void setFilterScope(   String filterScope   ) {m_filterScope    = filterScope;   }
	public void setFilterAddUrl(  String filterAddUrl  ) {m_filterAddUrl   = filterAddUrl;  }
	public void setFilterClearUrl(String filterClearUrl) {m_filterClearUrl = filterClearUrl;}

	/**
	 * Constructs a string used as a filter specification that combines
	 * for the filter's name and scope.
	 * 
	 * @param filterName
	 * @param filterScope
	 * 
	 * @return
	 */
	public static String buildFilterSpec(String filterName, String filterScope) {
		return (filterScope + ":" + filterName);
	}
}
