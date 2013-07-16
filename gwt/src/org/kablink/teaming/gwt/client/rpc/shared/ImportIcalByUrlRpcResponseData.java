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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for import an iCal by URL
 * command.
 * 
 * @author drfoster@novell.com
 */
public class ImportIcalByUrlRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<Long>			m_addedEntryIds;	// The entry IDs of the entries that were added.
	private List<Long>			m_modifiedEntryIds;	// The entry IDs of the entries that were modified.
	private Map<FailureReason, String> m_errors;	//
	
	public enum FailureReason implements IsSerializable {
		IMPORT_FAILED,
		PARSE_EXCEPTION,
		URL_EXCEPTION,
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param addedEntryIds
	 */
	public ImportIcalByUrlRpcResponseData(List<Long> addedEntryIds, List<Long> modifiedEntryIds) {
		// Initialize the superclass..
		super();
		
		// ...store the parameters...
		setAddedEntryIds(   addedEntryIds   );
		setModifiedEntryIds(modifiedEntryIds);
		
		// ...and initialize everything else.
		m_errors = new HashMap<FailureReason, String>();
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public ImportIcalByUrlRpcResponseData() {
		// Always use the initial form of the constructor.
		this(new ArrayList<Long>(), new ArrayList<Long>());
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<Long>                 getAddedEntryIds()    {return m_addedEntryIds;   }
	public List<Long>                 getModifiedEntryIds() {return m_modifiedEntryIds;}
	public Map<FailureReason, String> getErrors()           {return m_errors;          }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAddedEntryIds(   List<Long> addedEntryIds)     {m_addedEntryIds    = addedEntryIds;   }
	public void setModifiedEntryIds(List<Long> modifiedEntryIds)  {m_modifiedEntryIds = modifiedEntryIds;}
	
	/**
	 * Returns true if any errors were detected and false otherwise.
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		return (!(m_errors.isEmpty()));
	}
	
	/**
	 * Stores an error.
	 * 
	 * @param reason
	 * @param detail
	 */
	public void setError(FailureReason reason, String detail) {
		// Store the error...
		m_errors.put(reason, detail);

		// ...and remove any IDs we're storing.
		m_addedEntryIds.clear();
		m_modifiedEntryIds.clear();
	}
}
