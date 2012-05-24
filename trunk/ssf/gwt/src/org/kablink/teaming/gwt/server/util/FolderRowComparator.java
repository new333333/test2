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
package org.kablink.teaming.gwt.server.util;

import java.util.Comparator;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;

/**
 * Class used to compare two FolderRow's.
 *
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class FolderRowComparator implements Comparator<FolderRow> {
	private boolean 			m_sortAscending;	//
	private List<FolderColumn>	m_folderColumns;	//
	private String				m_sortKey;			//
	
	private final static int LESS		= (-1);
	private final static int EQUAL		=   0;
	private final static int GREATER	=   1;

	/**
	 * Class constructor.
	 * 
	 * @param sortKey
	 * @param sortAscending
	 * @param folderColumns
	 */
	public FolderRowComparator(String sortKey, boolean sortAscending, List<FolderColumn> folderColumns) {
		// Initialize the super class...
		super();

		// ...and store the parameters.
		m_sortKey       = sortKey;
		m_sortAscending = sortAscending;
		m_folderColumns = folderColumns;
	}

	/**
	 * Compares two FolderRow's by their sort key.
	 * 
	 * Implements the Comparator.compare() method.
	 * 
	 * @param fr1
	 * @param fr2
	 * 
	 * @return
	 */
	@Override
	public int compare(FolderRow fr1, FolderRow fr2) {
		int reply = EQUAL;

//!		...this needs to be implemented...
		
		// If we get here, reply contains the appropriate value for
		// the compare.  Return it.
		return reply;
	}
}
