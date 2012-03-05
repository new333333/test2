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
package org.kablink.teaming.module.file;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.util.search.Constants;

/**
 * This class represents those pieces of data associated with a specific file that
 * is stored in Lucene index. This class must not include any field or property 
 * that requires database lookup. It should only contain those fields that are
 * obtainable entirely from the Lucene index.
 *  
 * @author jong
 *
 */
public class FileIndexData {

	private static Log logger = LogFactory.getLog(FileIndexData.class);
	
	private String name; // file name
	private String id; // file database id
	private String title; // file title
	private Long binderId; // containing binder id
	private EntityType owningEntityType; // entity type of the owner
	private Long owningEntityId; // entity id of the owner
	private Long creatorId; // creator database id
	private String creatorName; // creator name
	private Long modifierId; // modifier database id
	private String modifierName; // modifier name
	private Date createdDate; // created date
	private Date modifiedDate; // modified date
	
	public FileIndexData(org.apache.lucene.document.Document doc)  throws IllegalArgumentException {
		name = doc.get(Constants.FILENAME_FIELD);
		id = doc.get(Constants.FILE_ID_FIELD);
		title = doc.get(Constants.TITLE_FIELD);
		binderId = Long.valueOf(doc.get(Constants.BINDER_ID_FIELD));
		String owningEntityTypeStr = doc.get(Constants.ENTITY_FIELD);
		owningEntityType = entityTypeFromString(owningEntityTypeStr);
		owningEntityId = Long.valueOf(doc.get(Constants.DOCID_FIELD));
		creatorId = Long.valueOf(doc.get(Constants.CREATORID_FIELD));
		creatorName = doc.get(Constants.CREATOR_NAME_FIELD);
		modifierId = Long.valueOf(doc.get(Constants.MODIFICATIONID_FIELD));
		modifierName = doc.get(Constants.MODIFICATION_NAME_FIELD);
		String dateStr = doc.get(Constants.CREATION_DATE_FIELD);
		try {
			createdDate =  DateTools.stringToDate(dateStr);
		} catch (ParseException e) {
			logger.warn("Error parsing creation date [" + dateStr 
					+ "] for file [" + id + "]. Setting it to current date");
		}
		dateStr = doc.get(Constants.MODIFICATION_DATE_FIELD);
		try {
			modifiedDate =  DateTools.stringToDate(dateStr);
		} catch (ParseException e) {
			logger.warn("Error parsing modification date [" + dateStr 
					+ "] for file [" + id + "]. Setting it to current date");
		}
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Long getBinderId() {
		return binderId;
	}

	public EntityType getOwningEntityType() {
		return owningEntityType;
	}

	public Long getOwningEntityId() {
		return owningEntityId;
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
