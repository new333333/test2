/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
	 * Returns true if a List<EntityId> contains any binder references
	 * and false otherwise.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static boolean areBindersInEntityIds(List<EntityId> entityIds) {
		return (0 < countBindersInEntityIds(entityIds));
	}
	
	/**
	 * Returns true if a List<EntityId> contains any entry references
	 * and false otherwise.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static boolean areEntriesInEntityIds(List<EntityId> entityIds) {
		return (0 < countEntriesInEntityIds(entityIds));
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
	 * Returns a count of the binders in a List<EntityId>.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static int countBindersInEntityIds(List<EntityId> entityIds) {
		int reply = 0;
		if (null != entityIds) {
			for (EntityId entityId:  entityIds) {
				if (entityId.isBinder()) {
					reply += 1;
				}
			}
		}
		return reply;
	}
	
	/**
	 * Returns a count of the entries in a List<EntityId>.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static int countEntriesInEntityIds(List<EntityId> entityIds) {
		int reply = 0;
		if (null != entityIds) {
			for (EntityId entityId:  entityIds) {
				if (entityId.isEntry()) {
					reply += 1;
				}
			}
		}
		return reply;
	}

	/**
	 * Returns true if this EntityId matches the given EntityId.
	 * 
	 * @param entityId
	 * 
	 * @return
	 */
	public boolean equalsEntityId(EntityId entityId) {
		Long entryId;
		Long binderId;
		String entityType;
		boolean reply = false;
		
		entryId = getEntityId();
		binderId = getBinderId();
		entityType = getEntityType();

		if ( (entryId == null && entityId.getEntityId() == null) ||
			 (entryId != null && entryId.equals( entityId.getEntityId() )) )
		{
			if ( (binderId == null && entityId.getBinderId() == null) ||
				 (binderId != null && binderId.equals( entityId.getBinderId() )) )
			{
				if ( (entityType == null && entityId.getEntityType() == null) ||
					 (entityType != null && entityType.equalsIgnoreCase( entityId.getEntityType() )) )
				{
					reply = true;
				}
			}
		}
		
		return reply;
	}
	
	/**
	 * Returns a List<Long> of the IDs of the entries from a List<EntityId>.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static List<Long> getBinderLongsFromEntityIds(List<EntityId> entityIds) {
		return getLongsFromEntityIdsImpl(entityIds, false, true);
	}

	/**
	 * Returns a string representation of this EntityId.
	 * 
	 * @return
	 */
	public String getEntityIdString() {
		return (getBinderId() + ":" + getEntityId() + ":" + getEntityType());
	}
	
	/**
	 * Returns a List<Long> of the IDs of the entries from a List<EntityId>.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static List<Long> getEntryLongsFromEntityIds(List<EntityId> entityIds) {
		return getLongsFromEntityIdsImpl(entityIds, true, false);
	}
	
	/*
	 * Returns a List<Long> of the IDs of the entries from a List<EntityId>.
	 */
	private static List<Long> getLongsFromEntityIdsImpl(List<EntityId> entityIds, boolean entriesOnly, boolean bindersOnly) {
		List<Long> reply = new ArrayList<Long>();
		if (null != entityIds) {
			for (EntityId entityId:  entityIds) {
				boolean include = ((!entriesOnly) && (!bindersOnly));
				if (!include) {
					include = (entriesOnly && entityId.isEntry());
					if (!include) {
						include = (bindersOnly && entityId.isBinder());
					}
				}
				if (include) {
					reply.add(entityId.getEntityId());
				}
			}
		}
		return reply;
	}
	
	/**
	 * Returns a List<Long> of the IDs of the entries from a List<EntityId>.
	 * 
	 * @param entityIds
	 * 
	 * @return
	 */
	public static List<Long> getLongsFromEntityIds(List<EntityId> entityIds) {
		return getLongsFromEntityIdsImpl(entityIds, false, false);
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
	
	/**
	 * Returns true if this row refers to a binder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinder() {
		return (isFolder() || isWorkspace());
	}

	/**
	 * Returns true if a List<EntityId> contains a binder reference to
	 * the given binder ID and false otherwise.
	 * 
	 * @param binderId
	 * @param entityIds
	 * 
	 * @return
	 */
	public static boolean isBinderInEntityIds(Long binderId, List<EntityId> entityIds) {
		// Do we have anything to check?
		boolean reply = false;
		if ((null != binderId) && (null != entityIds) && (!(entityIds.isEmpty()))) {
			// Yes!  Scan the entities.
			for (EntityId eid:  entityIds) {
				// Is this entity the binder in question?
				if (eid.isBinder() && eid.getEntityId().equals(binderId)) {
					// Yes!  Return true.
					reply = true;
					break;
				}
			}
		}
		
		// If we get here, reply contains true if the List<EntityId>
		// contains a specific binder ID and false otherwise.  Return
		// it.
		return reply;
	}
	
	/**
	 * Returns true if this row refers to an entry and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEntry() {
		String entityType = m_entityType;
		if (null == entityType) entityType = "";
		return (entityType.equals(EntityId.FOLDER_ENTRY));
	}

	/**
	 * Returns true if this row refers to a folder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isFolder() {
		String entityType = m_entityType;
		if (null == entityType) entityType = "";
		return entityType.equals(EntityId.FOLDER);
	}

	/**
	 * Returns true if this row refers to a binder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isWorkspace() {
		String entityType = m_entityType;
		if (null == entityType) entityType = "";
		return entityType.equals(EntityId.WORKSPACE);
	}

	/**
	 * Returns an EntityId constructed from a string returned from
	 * getEntityIdString().
	 * 
	 * @param eidString
	 * 
	 * @return
	 */
	public static EntityId parseEntityIdString(String eidString) {
		String[] parts = eidString.split(":");
		return
			new EntityId(
				Long.parseLong(parts[0]),
				Long.parseLong(parts[1]),
				               parts[2]);
	}
	
	/**
	 * Removes the EntityId's that reference a binder from a
	 * List<EntityId>.
	 * 
	 * @param entityIds
	 */
	public static void removeBindersFromEntityIds(List<EntityId> entityIds) {
		// If there are any entities in the list...
		if (null != entityIds) {
			// ...scan them...
			int entities = entityIds.size();
			for (int i = (entities - 1); i >= 0; i -= 1) {
				EntityId eid = entityIds.get(i);
				if (eid.isBinder()) {
					// ...removing any binders.
					entityIds.remove(i);
				}
			}
		}
	}

	/**
	 * Removes the EntityId's that reference an entry from a
	 * List<EntityId>.
	 * 
	 * @param entityIds
	 */
	public static void removeEntriesFromEntityIds(List<EntityId> entityIds) {
		// If there are any entities in the list...
		if (null != entityIds) {
			// ...scan them...
			int entities = entityIds.size();
			for (int i = (entities - 1); i >= 0; i -= 1) {
				EntityId eid = entityIds.get(i);
				if (eid.isEntry()) {
					// ...removing any entries.
					entityIds.remove(i);
				}
			}
		}
	}
}
