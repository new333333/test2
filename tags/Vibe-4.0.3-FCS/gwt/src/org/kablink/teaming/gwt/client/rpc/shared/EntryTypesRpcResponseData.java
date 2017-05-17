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
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for a request for entry
 * type information.
 * 
 * @author drfoster@novell.com
 */
public class EntryTypesRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private EntryType		m_baseEntryType;		// EntryType for EntityId passed in the get request.
	private List<EntryType>	m_entryTypes;		// The List<EntryType> defined for a collection of binders.
	private String			m_baseEntryTitle;	// The title of the entry passed in the get request.
	
	/**
	 * Inner class used to represent an entry type.
	 */
	public static class EntryType implements IsSerializable {
		private boolean m_localDef;	//
		private String	m_defId;	//
		private String	m_defKey;	//
		
		/**
		 * Constructor method.
		 * 
		 * For GWT serialization, must have a zero parameter
		 * constructor.
		 */
		public EntryType() {
			// Initialize the super class.
			super();
		}

		/**
		 * Constructor method.
		 */
		public EntryType(String defId, String defKey, boolean localDef) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setDefId(   defId   );
			setDefKey(  defKey  );
			setLocalDef(localDef);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isLocalDef() {return m_localDef;}
		public String  getDefId()   {return m_defId;   }
		public String  getDefKey()  {return m_defKey;  }

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setLocalDef(boolean localDef) {m_localDef = localDef;}
		public void setDefId(   String  defId)    {m_defId    = defId;   }
		public void setDefKey(  String  defKey)   {m_defKey   = defKey;  }
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public EntryTypesRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_entryTypes = new ArrayList<EntryType>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public EntryType       getBaseEntryType()  {return m_baseEntryType; }
	public String          getBaseEntryTitle() {return m_baseEntryTitle;}
	public List<EntryType> getEntryTypes()     {return m_entryTypes;    }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBaseEntryType( EntryType       baseEntryType)  {m_baseEntryType  = baseEntryType; }
	public void setBaseEntryTitle(String          baseEntryTitle) {m_baseEntryTitle = baseEntryTitle;}
	public void setEntryTypes(    List<EntryType> entryTypes)     {m_entryTypes     = entryTypes;    }

	/**
	 * Adds an EntryType to a List<EntryType>.
	 * 
	 * @param entryType
	 */
	public void addEntryType(EntryType entryType) {
		m_entryTypes.add(entryType);
	}

	/**
	 * Returns true if an EntryType with a given ID is already in the
	 * List<EntryType> and false otherwise.
	 * 
	 * @param defId
	 * 
	 * @return
	 */
	public boolean isEntryTypeInList(String defId) {
		for (EntryType et:  m_entryTypes) {
			if (defId.equals(et.getDefId())) {
				return true;
			}
		}
		return false;
	}
}
