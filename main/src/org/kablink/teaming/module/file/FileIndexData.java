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
import java.util.Map;

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
	private Long size; // size in bytes - this field is new in Hudson, so may not exist in old index
	private String md5; // md5 sum - this field is new in Hudson
    private Integer versionNumber; // highest version number - this field is new in Hudson
    private Integer majorVersionNumber; // Major version number - this field is new in Hudson
    private Integer minorVersionNumber; // Minor version number - this field is new in Hudson

	public FileIndexData(Map<String,Object> doc)  throws IllegalArgumentException {
		name = (String)doc.get(Constants.FILENAME_FIELD);
		id = (String)doc.get(Constants.FILE_ID_FIELD);
		title = (String)doc.get(Constants.TITLE_FIELD);
		binderId = Long.valueOf((String)doc.get(Constants.BINDER_ID_FIELD));
		String owningEntityTypeStr = (String)doc.get(Constants.ENTITY_FIELD);
		owningEntityType = entityTypeFromString(owningEntityTypeStr);
		owningEntityId = Long.valueOf((String)doc.get(Constants.DOCID_FIELD));
		creatorId = Long.valueOf((String)doc.get(Constants.CREATORID_FIELD));
		creatorName = (String)doc.get(Constants.CREATOR_NAME_FIELD);
		modifierId = Long.valueOf((String)doc.get(Constants.MODIFICATIONID_FIELD));
		modifierName = (String)doc.get(Constants.MODIFICATION_NAME_FIELD);
        createdDate = getDate(id, doc, Constants.CREATION_DATE_FIELD);
        modifiedDate = getDate(id, doc, Constants.MODIFICATION_DATE_FIELD);
		String sizeStr = (String)doc.get(Constants.FILE_SIZE_IN_BYTES_FIELD);
		if(sizeStr != null)
			size = Long.valueOf(sizeStr);
		else
			size = null;
        String versionStr = (String)doc.get(Constants.FILE_VERSION_FIELD);
        if(versionStr != null)
            versionNumber = Integer.valueOf(versionStr);
        else
            versionNumber = null;
        versionStr = (String)doc.get(Constants.FILE_MAJOR_VERSION_FIELD);
        if(versionStr != null)
            majorVersionNumber = Integer.valueOf(versionStr);
        else
            majorVersionNumber = null;
        versionStr = (String) doc.get(Constants.FILE_MINOR_VERSION_FIELD);
        if(versionStr != null)
            minorVersionNumber = Integer.valueOf(versionStr);
        else
            minorVersionNumber = null;
        md5 = (String)doc.get(Constants.FILE_MD5_FIELD);
	}

    private static Date getDate(String id, Map<String, Object> doc, String key) {
        Object value = doc.get(key);
        if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof String) {
            try {
                return DateTools.stringToDate((String) value);
            } catch (ParseException e) {
                logger.warn("Error parsing " + key + " date [" + value
                        + "] for file [" + id + "]. Setting it to current date");
            }
        }
        return null;
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
	
	public Long getSize() {
		return size;
	}

    public String getMd5() {
        return md5;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public Integer getMajorVersionNumber() {
        return majorVersionNumber;
    }

    public Integer getMinorVersionNumber() {
        return minorVersionNumber;
    }

    private EntityType entityTypeFromString(String entityTypeStr) throws IllegalArgumentException {
		if(entityTypeStr == null)
			throw new IllegalArgumentException("Entity type is null");
		return EntityType.valueOf(entityTypeStr);
	}
}
