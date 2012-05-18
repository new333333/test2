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
 * Class used to bundle a binder ID, entity ID and an entity type to
 * uniquely identify an entity with its containing binder for GWT RPC
 * requests.
 *  
 * @author drfoster@novell.com
 */
public class EntityId implements IsSerializable {
	private Long	m_binderId;		//
	private Long	m_entityId;		//
	private String	m_entityType;	// folderEntry, folder, workspace, ...
	
	public final static String FOLDER		= "folder";
	public final static String FOLDER_ENTRY	= "folderEntry";
	public final static String WORKSPACE	= "workspace";

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public EntityId() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param entityId
	 * @param entityType
	 */
	public EntityId(Long binderId, Long entityId, String entityType) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setBinderId(  binderId  );
		setEntityId(  entityId  );
		setEntityType(entityType);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Long   getBinderId()   {return m_binderId;  }
	public Long   getEntityId()   {return m_entityId;  }
	public String getEntityType() {return m_entityType;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBinderId(  Long   binderId)   {m_binderId   = binderId;  }
	public void setEntityId(  Long   entityId)   {m_entityId   = entityId;  }
	public void setEntityType(String entityType) {m_entityType = entityType;}
	
	/**
	 * Returns true if a List<EntityId> contains a binder references
	 * and false otherwise.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static boolean areBindersInEntityList(List<EntityId> entityIds) {
		if (null != entityIds) {
			for (EntityId entityId:  entityIds) {
				if (entityId.isBinder()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Convert a folder ID and List<Long> of entry IDs into a
	 * List<EntityId>'s.
	 * 
	 * @param folderId
	 * @param entityIds
	 * @param entityType
	 */
	public static List<EntityId> buildEntityIdListFromLongs(Long folderId, List<Long> entityIds, String entityType) {
		List<EntityId> reply = new ArrayList<EntityId>();
		for (Long entityId:  entityIds) {
			reply.add(new EntityId(folderId, entityId, entityType));
		}
		return reply;
	}

	/**
	 * Returns true if this row refers to a binder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinder() {
		String entityType = m_entityType;
		if (null == entityType) entityType = "";
		return (entityType.equals(EntityId.FOLDER) || entityType.equals(EntityId.WORKSPACE));
	}

	/**
	 * Returns a string that can be used to pass a List<EntityId> as a
	 * parameter on a URL.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static String getMultipleEntityIdsParam(List<EntityId> entityIds) {
		StringBuffer reply = new StringBuffer("");
		boolean firstId  = true;
		for (EntityId entityId:  entityIds) {
			if (firstId)
			     firstId = false;
			else reply.append(",");
			reply.append(
				String.valueOf(entityId.getBinderId()) + ":" +
				String.valueOf(entityId.getEntityId()) + ":" +
				               entityId.getEntityType());
		}
		return reply.toString();
	}
}
