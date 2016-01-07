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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for RPC commands that return task
 * display data.
 * 
 * @author drfoster@novell.com
 */
public class TaskDisplayDataRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean m_expandGraphs;				//
	private boolean m_showModeSelect;			//
	private boolean	m_updateCalculatedDates;	//
	private Long	m_taskChangeId;				//
	private String	m_adaptedUrl;				//
	private String	m_filterType;				//
	private String	m_mode;						//
	private String	m_taskChangeReason;			//
	
	/**
	 * Class constructor.
	 * 
	 * For GWT serialization, must have a zero parameter
	 * constructor.
	 */
	public TaskDisplayDataRpcResponseData() {
		super();
	}

	/**
	 * Class constructor.
	 * 
	 * @param filterType
	 * @param mode
	 * @param taskChangeId
	 * @param taskChangeReason
	 * @param updateCalculatedDates
	 * @param showModeSelect
	 * @param expandGraphs
	 */
	public TaskDisplayDataRpcResponseData(String filterType, String mode, Long taskChangeId, String taskChangeReason, boolean updateCalculatedDates, boolean showModeSelect, boolean expandGraphs, String adaptedUrl) {
		this();
		
		setFilterType(           filterType           );
		setMode(                 mode                 );
		setTaskChangeId(         taskChangeId         );
		setTaskChangeReason(     taskChangeReason     );
		setUpdateCalculatedDates(updateCalculatedDates);
		setShowModeSelect(       showModeSelect       );
		setExpandGraphs(         expandGraphs         );
		setAdaptedUrl(           adaptedUrl           );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean getExpandGraphs()          {return m_expandGraphs;         }
	public boolean getShowModeSelect()        {return m_showModeSelect;       }
	public boolean getUpdateCalculatedDates() {return m_updateCalculatedDates;}
	public Long    getTaskChangeId()          {return m_taskChangeId;         }
	public String  getAdaptedUrl()            {return m_adaptedUrl;           }
	public String  getFilterType()            {return m_filterType;           }
	public String  getMode()                  {return m_mode;                 }
	public String  getTaskChangeReason()      {return m_taskChangeReason;     }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setExpandGraphs(         boolean expandGraphs)          {m_expandGraphs          = expandGraphs;         }
	public void setShowModeSelect(       boolean showModeSelect)        {m_showModeSelect        = showModeSelect;       }
	public void setUpdateCalculatedDates(boolean updateCalculatedDates) {m_updateCalculatedDates = updateCalculatedDates;}
	public void setTaskChangeId(         Long    taskChangeId)          {m_taskChangeId          = taskChangeId;         }
	public void setAdaptedUrl(           String  adaptedUrl)            {m_adaptedUrl            = adaptedUrl;           }
	public void setFilterType(           String  filterType)            {m_filterType            = filterType;           }
	public void setMode(                 String  mode)                  {m_mode                  = mode;                 }
	public void setTaskChangeReason(     String  taskChangeReason)      {m_taskChangeReason      = taskChangeReason;     }
}
