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
package org.kablink.teaming.module.binder;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.util.search.Constants;

/**
 * @author jong
 *
 */
public class BinderIndexData {

	private static Log logger = LogFactory.getLog(BinderIndexData.class);
	
	private Long id;
	private EntityType entityType;
	private String title;
	private Long parentId;
	private String family;
	private String path;
	private boolean library;
	private boolean mirrored;
	private boolean homeDir;
	private Long ownerId;
	private String ownerName;
	private Long creatorId; // creator database id
	private String creatorName; // creator name
	private Long modifierId; // modifier database id
	private String modifierName; // modifier name
	private Date createdDate; // created date
	private Date modifiedDate; // modified date
	
	public BinderIndexData(org.apache.lucene.document.Document doc)  throws IllegalArgumentException {
		id = Long.valueOf(doc.get(Constants.DOCID_FIELD));
		String entityTypeStr = doc.get(Constants.ENTITY_FIELD);
		entityType = entityTypeFromString(entityTypeStr);
		title = doc.get(Constants.TITLE_FIELD);
		parentId = Long.valueOf(doc.get(Constants.BINDERS_PARENT_ID_FIELD));
		family = doc.get(Constants.FAMILY_FIELD);
		path = doc.get(Constants.ENTITY_PATH);
		String libraryStr = doc.get(Constants.IS_LIBRARY_FIELD);
		if(String.valueOf(true).equals(libraryStr))
			library = true;
		else
			library = false;
		String mirroredStr = doc.get(Constants.IS_MIRRORED_FIELD);
		if(String.valueOf(true).equals(mirroredStr))
			mirrored = true;
		else
			mirrored = false;
		String homeDirStr = doc.get(Constants.IS_HOME_DIR_FIELD);
		if(String.valueOf(true).equals(homeDirStr))
			homeDir = true;
		else
			homeDir = false;
		ownerId = Long.valueOf(doc.get(Constants.OWNERID_FIELD));
		ownerName = doc.get(Constants.OWNER_NAME_FIELD);
		creatorId = Long.valueOf(doc.get(Constants.CREATORID_FIELD));
		creatorName = doc.get(Constants.CREATOR_NAME_FIELD);
		modifierId = Long.valueOf(doc.get(Constants.MODIFICATIONID_FIELD));
		modifierName = doc.get(Constants.MODIFICATION_NAME_FIELD);
		String dateStr = doc.get(Constants.CREATION_DATE_FIELD);
		try {
			createdDate =  DateTools.stringToDate(dateStr);
		} catch (ParseException e) {
			logger.warn("Error parsing creation date [" + dateStr 
					+ "] for binder [" + id + "]. Setting it to current date");
		}
		dateStr = doc.get(Constants.MODIFICATION_DATE_FIELD);
		try {
			modifiedDate =  DateTools.stringToDate(dateStr);
		} catch (ParseException e) {
			logger.warn("Error parsing modification date [" + dateStr 
					+ "] for binder [" + id + "]. Setting it to current date");
		}

	}
	
	public Long getId() {
		return id;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public String getTitle() {
		return title;
	}

	public Long getParentId() {
		return parentId;
	}

	public String getFamily() {
		return family;
	}

	public String getPath() {
		return path;
	}

	public boolean isLibrary() {
		return library;
	}

	public boolean isMirrored() {
		return mirrored;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public Long getModifierId() {
		return modifierId;
	}

	public String getModifierName() {
		return modifierName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	private EntityType entityTypeFromString(String entityTypeStr) throws IllegalArgumentException {
		if(entityTypeStr == null)
			throw new IllegalArgumentException("Entity type is null");
		return EntityType.valueOf(entityTypeStr);
	}
}
