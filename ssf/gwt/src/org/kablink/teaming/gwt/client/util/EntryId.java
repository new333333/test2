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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to bundle a binder ID and entry ID to uniquely identify
 * an entry with its binder for GWT RPC requests.
 *  
 * @author drfoster
 */
public class EntryId implements IsSerializable {
	private Long m_binderId;	//
	private Long m_entryId;		//

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public EntryId() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param entryId
	 */
	public EntryId(Long binderId, Long entryId) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setBinderId(binderId);
		setEntryId( entryId );
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Long getBinderId() {return m_binderId;}
	public Long getEntryId()  {return m_entryId; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBinderId(Long binderId) {m_binderId = binderId;}
	public void setEntryId( Long entryId)  {m_entryId  = entryId; }
	
	/**
	 * Convert a folder ID and List<Long> of entry IDs into a
	 * List<EntryId>'s.
	 * 
	 * @param folderId
	 * @param entryIds
	 */
	public static List<EntryId> buildEntryIdListFromLongs(Long folderId, List<Long> entryIds) {
		List<EntryId> reply = new ArrayList<EntryId>();
		for (Long entryId:  entryIds) {
			reply.add(new EntryId(folderId, entryId));
		}
		return reply;
	}

	/**
	 * Returns a string that can be used to pass a List<EntryId> as a
	 * parameter on a URL.
	 * 
	 * @param entryIds
	 * 
	 * @return
	 */
	public static String getMultipleEntryIdsParam(List<EntryId> entryIds) {
		StringBuffer reply = new StringBuffer("");
		boolean firstId  = true;
		for (EntryId entryId:  entryIds) {
			if (firstId)
			     firstId = false;
			else reply.append(",");
			reply.append(String.valueOf(entryId.getBinderId()) + ":" + String.valueOf(entryId.getEntryId()));
		}
		return reply.toString();
	}
}
