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

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.util.PrincipalInfo;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate information about a row of data from a
 * folder.
 * 
 * @author drfoster@novell.com
 */
public class FolderRow implements IsSerializable {
	private List<FolderColumn>			m_columns;			// The FolderColumns that contribute to this FolderRow.
	private Long 						m_entryId;			// The entry ID of the FolderEntry this FolderRow corresponds to.
	private Map<String, PrincipalInfo>	m_rowPrincipals;	// A map of column names to PrincipalInfo's possibly stored for the column.
	private Map<String, String>			m_rowStrings;		// A map of column names to String values   possible stored for the column.
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderRow() {
		// Initialize the super class...
		super();
		
		// ...and allocate the maps.
		m_rowPrincipals = new HashMap<String, PrincipalInfo>();
		m_rowStrings    = new HashMap<String, String>();
	}

	/**
	 * Constructor method.
	 * 
	 * @param entryId
	 * @param columns
	 */
	public FolderRow(Long entryId, List<FolderColumn> columns) {
		// Initialize the class...
		this();

		// ...and store the parameters.
		m_entryId = entryId;
		m_columns = columns;
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<FolderColumn>  getColumns() {return m_columns;}
	public Long                getEntryId() {return m_entryId;}
	
	/**
	 * Stores the value for a specific column.
	 * 
	 * @param fc
	 * @param v
	 */
	public void setColumnValue(FolderColumn fc, Object v) {
		String vk = getValueKey(fc);
		if (v instanceof String)
		     m_rowStrings.put(   vk, ((String) v));
		else m_rowPrincipals.put(vk, ((PrincipalInfo) v));
	}
	
	/**
	 * Returns the PrincipalInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public PrincipalInfo getColumnValueAsPrincipalInfo(FolderColumn fc) {
		return m_rowPrincipals.get(getValueKey(fc));
	}

	/**
	 * Returns the String value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public String getColumnValueAsString(FolderColumn fc) {
		String vk = getValueKey(fc);
		String reply = m_rowStrings.get(vk);
		if (null == reply) {
			PrincipalInfo pi = m_rowPrincipals.get(vk);
			if (null != pi) {
				reply = pi.getTitle();
			}
		}
		return ((null == reply) ? "" : reply); 
	}

	/*
	 * Returns the key that we should use into the data map to
	 * determine a column's value.
	 */
	private String getValueKey(FolderColumn fc) {
		return fc.getColumnName().toLowerCase();
	}

	/**
	 * Returns true if a column's value is a PrincipalInfo and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValuePrincipalInfo(FolderColumn fc) {
		return (null != m_rowPrincipals.get(getValueKey(fc)));
	}

	/**
	 * Returns true if a column's value is a String and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueString(FolderColumn fc) {
		return (null != m_rowStrings.get(getValueKey(fc)));
	}
}
