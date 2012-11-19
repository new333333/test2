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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about a view between the
 * client and server.
 * 
 * @author drfoster@novell.com
 */
public class ViewInfo implements IsSerializable, VibeRpcResponseData {
	private BinderInfo			m_binderInfo;	// If m_viewType is BINDER or BINDER_WITH_ENTRY_VIEW, a BinderInfo object that describes it.
	private boolean				m_invokeShare;	// true -> Invoke the sharing dialog on the entity after the view initiates.
	private String				m_entryViewUrl;	// If m_viewType is BINDER_WITH_ENTRY_VIEW, the URL to use to view the entry AFTER loading the binder.
	private Long				m_baseBinderId;	// For all m_viewTypes, any binderId found in the URL.
	private ViewFolderEntryInfo	m_vfei;			// If m_viewType is FOLDER_ENTRY, a ViewFolderEntryInfo object that describes it.
	private ViewType			m_viewType;		// The type of the view.
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ViewInfo() {
		// Initialize the super class...
		super();
		
		// ...and any data members requiring it.
		m_viewType = ViewType.OTHER;
	}

	/**
	 * Constructor method.
	 * 
	 * @param viewType
	 */
	public ViewInfo(ViewType viewType) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setViewType(viewType);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean             isAdvancedSearchView() {return  ViewType.ADVANCED_SEARCH.equals(m_viewType);                                                       }
	public boolean             isBinderView()         {return (ViewType.BINDER.equals(         m_viewType) || ViewType.BINDER_WITH_ENTRY_VIEW.equals(m_viewType));}
	public boolean             isInvokeShare()        {return m_invokeShare;                                                                                      }
	public BinderInfo          getBinderInfo()        {return m_binderInfo;                                                                                       }
	public Long                getBaseBinderId()      {return m_baseBinderId;                                                                                     }
	public String              getEntryViewUrl()      {return m_entryViewUrl;                                                                                     }
	public ViewFolderEntryInfo getFolderEntryInfo()   {return m_vfei;                                                                                             }
	public ViewType            getViewType()          {return m_viewType;                                                                                         }
	
	/**
	 * Set'er methods.
	 * 
	 * @return
	 */
	public void setBinderInfo(         BinderInfo          binderInfo)   {m_binderInfo   = binderInfo;  }
	public void setInvokeShare(        boolean             invokeShare)  {m_invokeShare  = invokeShare; }
	public void setBaseBinderId(       Long                baseBinderId) {m_baseBinderId = baseBinderId;}
	public void setEntryViewUrl(       String              entryViewUrl) {m_entryViewUrl = entryViewUrl;}
	public void setViewFolderEntryInfo(ViewFolderEntryInfo vfei)         {m_vfei         = vfei;        }
	public void setViewType(           ViewType            viewType)     {m_viewType     = viewType;    }	
}
