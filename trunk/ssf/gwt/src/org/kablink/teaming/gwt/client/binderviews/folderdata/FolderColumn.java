/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.binderviews.folderdata;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate information about a folder columns between
 * the client and server.
 * 
 * @author drfoster@novell.com
 *
 */
public class FolderColumn implements IsSerializable, VibeRpcResponseData {
	private String m_columnName;		//
	private String m_columnTitle;		//
	private String m_columnSearchKey;	//
	private String m_columnSortKey;		//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderColumn() {
		// Nothing to do.
	}

	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 * @param columnTitle
	 */
	public FolderColumn(String columnName, String columnTitle) {
		this();
		
		setColumnName( columnName );
		setColumnTitle(columnTitle);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 * @param columnTitle
	 * @param columnSearchKey
	 * @param columnSortKey
	 */
	public FolderColumn(String columnName, String columnTitle, String columnSearchKey, String columnSortKey) {
		this(columnName, columnTitle);
		
		setColumnSearchKey(columnSearchKey);
		setColumnSortKey(  columnSortKey  );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public String getColumnName()      {return m_columnName;     }
	public String getColumnTitle()     {return m_columnTitle;    }
	public String getColumnSearchKey() {return m_columnSearchKey;}
	public String getColumnSortKey() {
		String reply = m_columnSortKey;
		if ((null == reply) || (0 == reply.length())) {
			reply = m_columnSearchKey;
		}
		return reply;
	}
	
	/**
	 * Set'er methods.
	 * 
	 * @param columnName
	 * @param columnTitle
	 */
	public void setColumnName(     String columnName)      {m_columnName      = columnName;     }
	public void setColumnTitle(    String columnTitle)     {m_columnTitle     = columnTitle;    }
	public void setColumnSearchKey(String columnSearchKey) {m_columnSearchKey = columnSearchKey;}
	public void setColumnSortKey(  String columnSortKey)   {m_columnSortKey   = columnSortKey;  }
}
