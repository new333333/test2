/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.util.BinderFilter;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'Get Binder Filters' RPC
 * command.
 * 
 * @author drfoster@novell.com
 */
public class BinderFiltersRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<BinderFilter>	m_binderFiltersList;	//
	private List<String>		m_currentFilters;		//
	private String				m_filterEditUrl;		//
	private String				m_filtersOffUrl;		//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public BinderFiltersRpcResponseData() {
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param BinderFiltersList
	 */
	public BinderFiltersRpcResponseData(List<BinderFilter> BinderFiltersList) {
		this();
		m_binderFiltersList = BinderFiltersList;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<BinderFilter> getBinderFilters()  {return m_binderFiltersList;}
	public List<String>       getCurrentFilters() {return m_currentFilters;   }
	public String             getFilterEditUrl()  {return m_filterEditUrl;    }
	public String             getFiltersOffUrl()  {return m_filtersOffUrl;    }

	/**
	 * Set'er methods.
	 * 
	 * @param BinderFiltersList
	 * @param currentFilters
	 */
	public void setBinderFilters( List<BinderFilter> binderFiltersList) {m_binderFiltersList = binderFiltersList;}
	public void setCurrentFilters(List<String>       currentFilters)    {m_currentFilters    = currentFilters;   }
	public void setFilterEditUrl( String             filterEditUrl)     {m_filterEditUrl     = filterEditUrl;    }
	public void setFiltersOffUrl( String             filtersOffUrl)     {m_filtersOffUrl     = filtersOffUrl;    }
}
