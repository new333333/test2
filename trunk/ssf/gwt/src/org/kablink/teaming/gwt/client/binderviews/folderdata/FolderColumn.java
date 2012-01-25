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
	private Boolean m_columnIsShown;		//
	private String	m_columnName;			// For custom attributes this is 'defId,type,eleName,caption'.
	private String	m_columnTitle;			//
	private String	m_columnDefaultTitle;	//
	private String	m_columnCustomTitle;	//
	private String	m_columnSearchKey;		//
	private String	m_columnSortKey;		//
	
	private String	m_columnDefId;			// The definition ID for this column (only used for custom columns.)
	private String	m_columnEleName;		// The element name from the definition (if custom attribute), otherwise, it is the column name.
	private String	m_columnType;			// The type for this column (only used for custom columns.)
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderColumn() {
		// Simply initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 */
	public FolderColumn(String columnName) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setColumnName( columnName );
		setColumnTitle(columnName);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 * @param columnTitle
	 */
	public FolderColumn(String columnName, String columnTitle) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
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
		// Initialize this object...
		this(columnName, columnTitle);
		
		// ...and store the parameters.
		setColumnSearchKey(columnSearchKey);
		setColumnSortKey(  columnSortKey  );
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 * @param columnTitle
	 * @param columnSearchKey
	 * @param columnSortKey
	 * @param columnDefId
	 * @param columnType
	 */
	public FolderColumn(String columnName, String columnTitle, String columnSearchKey, String columnSortKey, String columnDefId, String columnType) {
		// Initialize this object...
		this(columnName, columnTitle, columnSearchKey, columnSortKey);
		
		// ...and store the parameters.
		setColumnDefId(columnDefId);
		setColumnType( columnType );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Boolean getColumnIsShown()      {return m_columnIsShown;     }
	public String  getColumnName()         {return m_columnName;        }
	public String  getColumnTitle()        {return m_columnTitle;       }
	public String  getColumnDefaultTitle() {return m_columnDefaultTitle;}
	public String  getColumnCustomTitle()  {return m_columnCustomTitle; }
	public String  getColumnSearchKey()    {return m_columnSearchKey;   }
	public String  getColumnType()         {return m_columnType;        }
	public String  getColumnDefId()        {return m_columnDefId;       }
	public String  getColumnSortKey() {
		String reply = m_columnSortKey;
		if ((null == reply) || (0 == reply.length())) {
			reply = m_columnSearchKey;
		}
		return reply;
	}
	public String getColumnEleName() {
		if ((null != m_columnEleName) && (0 < m_columnEleName.length())) {
			return m_columnEleName;
		}
		
		String eleName = m_columnName;
		if (eleName.contains(",")) {
			// This is a custom attribute structured as
			// 'defId,type,eleName,caption'.
			String[] colParts = eleName.split(",");
			eleName = colParts[2];
		}
		return eleName;
	}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setColumnName(        String columnName)      	 {m_columnName         = columnName;        }
	public void setColumnTitle(       String columnTitle)     	 {m_columnTitle        = columnTitle;       }
	public void setColumnDefaultTitle(String columnDefaultTitle) {m_columnDefaultTitle = columnDefaultTitle;}
	public void setColumnCustomTitle( String columnCustomTitle)  {m_columnCustomTitle  = columnCustomTitle; }
	public void setColumnSearchKey(   String columnSearchKey) 	 {m_columnSearchKey    = columnSearchKey;   }
	public void setColumnSortKey(     String columnSortKey)   	 {m_columnSortKey      = columnSortKey;     }
	public void setColumnDefId(       String columnDefId)     	 {m_columnDefId        = columnDefId;       }
	public void setColumnEleName(     String columnEleName)      {m_columnEleName      = columnEleName;     }
	public void setColumnType(        String columnType)         {m_columnType         = columnType;        }
	public void setColumnIsShown(     Boolean showThis)          {m_columnIsShown      = showThis;          }

	/**
	 * Returns true if the columns is a custom column (i.e., has a
	 * definition ID) and false otherwise.
	 * 
	 * @return
	 */
	public boolean isCustomColumn() {
		return ((null != m_columnDefId) && (0 < m_columnDefId.length()));
	}
}
