/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
 * uniquely identify an entity that can be passed through GWT RPC
 * requests.
 *  
 * @author drfoster@novell.com
 */
public class EntityId implements IsSerializable {
	private EntityIdType	m_entityType;			// The folder, folderEntry, mobileDevice, user, workspace, ... this EntityId represents.
	private Long			m_binderId;				// The entity's binder ID.
	private Long			m_entityId;				// The entity's ID.
	private String			m_emailTemplateName;	// If the entity is an email template, its name.
	private String			m_mobileDeviceId;		// If the entity is a  mobile device,  its ID as used by the mobile device applications.

	// The string form of the various EntityIdType's.
	public final static String APPLICATION			= EntityIdType.application.name();
	public final static String APPLICATION_GROUP	= EntityIdType.applicationGroup.name();
	public final static String EMAIL_TEMPLATE		= EntityIdType.emailTemplate.name();
	public final static String FOLDER				= EntityIdType.folder.name();
	public final static String FOLDER_ENTRY			= EntityIdType.folderEntry.name();
	public final static String GROUP				= EntityIdType.group.name();
	public final static String MOBILE_DEVICE		= EntityIdType.mobileDevice.name();
	public final static String NONE					= EntityIdType.none.name();
	public final static String PROFILES				= EntityIdType.profiles.name();
	public final static String PROXY_IDENTITY		= EntityIdType.proxyIdentity.name();
	public final static String SHARE_WITH			= EntityIdType.shareWith.name();
	public final static String USER					= EntityIdType.user.name();
	public final static String WORKSPACE			= EntityIdType.workspace.name();

	// The following is used to separate the parts when constructing
	// or parsing a string representation of an EntityId.
	private final static String PART_SEPARATOR	= ":";

	/**
	 * Enumeration that defines the entity types that can be
	 * represented by an EntityId.
	 * 
	 * 
	 * *** WARNING *** WARNING *** WARNING *** WARNING ***
	 *
	 * Note that the names used here must match EXACTLY the names used
	 *    in the EntityIdentifier.EntityType enumeration. Failure to
	 *    honor this may result in an exception when parseEntityIdType()
	 *    tries to convert a string to an EntityIdType.
	 *
	 * *** WARNING *** WARNING *** WARNING *** WARNING ***
	 */
	public enum EntityIdType implements IsSerializable {
		application,
		applicationGroup,
		emailTemplate,	// Unique to the GWT code.  This doesn't exist in EntityIdentifier.EntityType!
		folder,
		folderEntry,
		group,
		mobileDevice,	// Unique to the GWT code.  This doesn't exist in EntityIdentifier.EntityType!
		none,
		profiles,
		proxyIdentity,	// Unique to the GWT code.  This doesn't exist in EntityIdentifier.EntityType!
		shareWith,
		user,
		workspace;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isApplication()      {return this.equals(application     );}
		public boolean isApplicationGroup() {return this.equals(applicationGroup);}
		public boolean isEmailTemplate()    {return this.equals(emailTemplate   );}
		public boolean isEntry()            {return isFolderEntry();              }
		public boolean isFolder()           {return this.equals(folder          );}
		public boolean isFolderEntry()      {return this.equals(folderEntry     );}
		public boolean isGroup()            {return this.equals(group           );}
		public boolean isMobileDevice()     {return this.equals(mobileDevice    );}
		public boolean isNone()             {return this.equals(none            );}
		public boolean isProfiles()         {return this.equals(profiles        );}
		public boolean isProxyIdentity()    {return this.equals(proxyIdentity   );}
		public boolean isShareWith()        {return this.equals(shareWith       );}
		public boolean isUser()             {return this.equals(user            );}
		public boolean isWorkspace()        {return this.equals(workspace       );}

		/**
		 * Parses a string representation of an EntityIdType.
		 * 
		 * @param entityType
		 * 
		 * @return
		 */
		public static EntityIdType parseEntityIdType(String entityType) {
			EntityIdType reply;
			if ((null == entityType) || (0 == entityType.length()))
			     reply = null;
			else reply = EntityIdType.valueOf(entityType);
			return reply;
		}
	}

	/*
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	private EntityId() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param entityId
	 * @param entityType
	 * @param mobileDeviceId
	 * @param emailTemplateName
	 */
	public EntityId(Long binderId, Long entityId, EntityIdType entityType, String mobileDeviceId, String emailTemplateName) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setBinderId(         binderId         );
		setEntityId(         entityId         );
		setEntityTypeEnum(   entityType       );
		setMobileDeviceId(   mobileDeviceId   );
		setEmailTemplateName(emailTemplateName);
	}

	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param entityId
	 * @param entityType
	 * @param mobileDeviceId
	 */
	public EntityId(Long binderId, Long entityId, EntityIdType entityType, String mobileDeviceId) {
		// Initialize this object.
		this(binderId, entityId, entityType, mobileDeviceId, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 * @param entityType
	 * @param mobileDeviceId
	 */
	public EntityId(Long entityId, EntityIdType entityType, String mobileDeviceId) {
		// Initialize this object.
		this(null, entityId, entityType, mobileDeviceId, null);
	}

	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param entityId
	 * @param entityType
	 * @param mobileDeviceId
	 */
	public EntityId(Long binderId, Long entityId, String entityType, String mobileDeviceId) {
		// Initialize this object.
		this(binderId, entityId, EntityIdType.parseEntityIdType(entityType), mobileDeviceId, null);
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 * @param entityType
	 * @param mobileDeviceId
	 */
	public EntityId(Long entityId, String entityType, String mobileDeviceId) {
		// Initialize this object.
		this(null, entityId, EntityIdType.parseEntityIdType(entityType), mobileDeviceId, null);
	}

	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param entityId
	 * @param entityType
	 */
	public EntityId(Long binderId, Long entityId, String entityType) {
		// Initialize this object.
		this(binderId, entityId, EntityIdType.parseEntityIdType(entityType), null, null);
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 * @param entityType
	 */
	public EntityId(Long entityId, String entityType) {
		// Initialize this object.
		this(null, entityId, EntityIdType.parseEntityIdType(entityType), null, null);
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityType
	 * @param emailTemplateName
	 */
	public EntityId(String entityType, String emailTemplateName) {
		// Initialize this object.
		this(null, null, EntityIdType.parseEntityIdType(entityType), null, emailTemplateName);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean      isApplication()        {return ((null == m_entityType) ? false : m_entityType.isApplication());      }
	public boolean      isApplicationGroup()   {return ((null == m_entityType) ? false : m_entityType.isApplicationGroup()); }
	public boolean      isBinder()             {return (isFolder() || isWorkspace() || isProfiles());                        }
	public boolean      isEntry()              {return  isFolderEntry();                                                     }
	public boolean      isFolder()             {return ((null == m_entityType) ? false : m_entityType.isFolder());           }
	public boolean      isFolderEntry()        {return ((null == m_entityType) ? false : m_entityType.isFolderEntry());      }
	public boolean      isGroup()              {return ((null == m_entityType) ? false : m_entityType.isGroup());            }
	public boolean      isMobileDevice()       {return ((null == m_entityType) ? false : m_entityType.isMobileDevice());     }
	public boolean      isNone()               {return ((null == m_entityType) ? false : m_entityType.isNone());             }
	public boolean      isProfiles()           {return ((null == m_entityType) ? false : m_entityType.isProfiles());         }
	public boolean      isShareWith()          {return ((null == m_entityType) ? false : m_entityType.isShareWith());        }
	public boolean      isUser()               {return ((null == m_entityType) ? false : m_entityType.isUser());             }
	public boolean      isWorkspace()          {return ((null == m_entityType) ? false : m_entityType.isWorkspace());        }
	public EntityIdType getEntityTypeEnum()    {return m_entityType;                                                         }
	public Long         getBinderId()          {return m_binderId;                                                           }
	public Long         getEntityId()          {return m_entityId;                                                           }
	public String       getEmailTemplateName() {return m_emailTemplateName;                                                  }
	public String       getEntityType()        {return ((null == m_entityType) ? null : m_entityType.name());                }
	public String       getMobileDeviceId()    {return m_mobileDeviceId;                                                     }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBinderId(         Long         binderId)          {m_binderId          = binderId;                                  }
	public void setEntityId(         Long         entityId)          {m_entityId          = entityId;                                  }
	public void setEntityTypeEnum(   EntityIdType entityType)        {m_entityType        = entityType;                                }
	public void setEmailTemplateName(String       emailTemplateName) {m_emailTemplateName = emailTemplateName;                         }
	public void setEntityType(       String       entityType)        {m_entityType        = EntityIdType.parseEntityIdType(entityType);}
	public void setMobileDeviceId(   String       mobileDeviceId)    {m_mobileDeviceId    = mobileDeviceId;                            }
	
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
	 * If this EnityId refers to a binder, returns a BinderInfo that
	 * can reasonably be built using the information we've got.
	 * Otherwise, returns null.
	 * 
	 * @return
	 */
	public BinderInfo buildBaseBinderInfo() {
		// Is this entity a binder?
		BinderInfo reply;
		if (isBinder()) {
			// Yes!  Construct an appropriate BinderInfo.
			reply = new BinderInfo();
			if      (isFolder())    reply.setBinderType(BinderType.FOLDER);
			else if (isWorkspace()) reply.setBinderType(BinderType.WORKSPACE);
			else                    reply.setBinderType(BinderType.OTHER);
			reply.setFolderType(FolderType.OTHER);
			reply.setBinderId(getEntityId());
			reply.setParentBinderId(getBinderId());
		}
		
		else {
			// No, the entity isn't a binder!  Return null.
			reply = null;
		}
		
		// If we get here, reply refers to a BinderInfo that most
		// closely describes a binder referenced by this EntityId or
		// is false.  Return it.
		return reply;
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
	 * Returns a copy of this EntityId object.
	 * 
	 * @return
	 */
	public EntityId copyEntityId() {
		EntityId reply = new EntityId();
		reply.setBinderId(      m_binderId      );
		reply.setEntityId(      m_entityId      );
		reply.setEntityTypeEnum(m_entityType    );
		reply.setMobileDeviceId(m_mobileDeviceId);
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
		StringBuffer reply = new StringBuffer(getBinderId() + PART_SEPARATOR + getEntityId() + PART_SEPARATOR + getEntityType());
		if (isMobileDevice()) {
			reply.append(PART_SEPARATOR + getMobileDeviceId());
		}
		return reply.toString();
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
	 * Returns an EntityId constructed from a string returned from
	 * getEntityIdString().
	 * 
	 * @param eidString
	 * 
	 * @return
	 */
	public static EntityId parseEntityIdString(String eidString) {
		// String representation of an EntityId will split to either 3
		// or 4 parts.  Does it split to an appropriate number?
		String[] parts     = eidString.split(PART_SEPARATOR);
		int      partCount = ((null == parts) ? 0 : parts.length);
		EntityId reply;
		if ((3 == partCount) || (4 == partCount)) {
			// Yes!  Construct an EntityId from it's parts.
			reply = new EntityId(
				Long.parseLong(parts[0]),
				Long.parseLong(parts[1]),
				               parts[2]);
			if (4 == parts.length) {
				reply.setMobileDeviceId(parts[3]);
			}
		}
		else {
			// No, we have an invalid part count!  Return null.
			reply = null;
		}
		return reply;
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
