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
package org.kablink.teaming.remoting.rest.v1.util;

import org.apache.lucene.document.DateTools;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * User: david
 * Date: 6/4/12
 * Time: 3:16 PM
 */
public class FilePropertiesBuilder implements SearchResultBuilder<FileProperties> {
    private AllModulesInjected ami;

    public FilePropertiesBuilder(AllModulesInjected ami) {
        this.ami = ami;
    }

    public void setDescriptionFormat(int descriptionFormat) {
    }

    public FileProperties build(Map doc) {
        FileProperties fp = null;
        if (SearchResultBuilderUtil.getNumValues(doc, Constants.FILE_ID_FIELD)==1) {
            fp = new FileProperties();
            fp.setName((String) doc.get(Constants.FILENAME_FIELD));
            fp.setId((String) doc.get(Constants.FILE_ID_FIELD));
            Long binderId = Long.valueOf((String) doc.get(Constants.BINDER_ID_FIELD));
            fp.setBinder(new ParentBinder(binderId, LinkUriUtil.getBinderLinkUri(binderId)));
            String owningEntityTypeStr = (String) doc.get(Constants.ENTITY_FIELD);
            EntityIdentifier.EntityType owningEntityType = EntityIdentifier.EntityType.valueOf(owningEntityTypeStr);
            Long owningEntityId = Long.valueOf((String) doc.get(Constants.DOCID_FIELD));

            fp.setOwningEntity(ResourceUtil.buildEntityId(owningEntityType, owningEntityId));
            fp.setPermaLink(PermaLinkUtil.getPermalink(owningEntityId, owningEntityType, null));

            Long creatorId = Long.valueOf((String) doc.get(Constants.CREATORID_FIELD));
            Date createdDate = (Date) doc.get(Constants.CREATION_DATE_FIELD);
            fp.setCreation(new HistoryStamp(new LongIdLinkPair(creatorId, LinkUriUtil.getUserLinkUri(creatorId)),
                    createdDate));

            Long modifierId = Long.valueOf((String) doc.get(Constants.MODIFICATIONID_FIELD));
            Long modTime = this.getLong(doc, Constants.FILE_TIME_FIELD);
            Date modifiedDate;
            if (modTime != null) {
                modifiedDate = new Date(modTime);
            } else {
                modifiedDate = (Date) doc.get(Constants.MODIFICATION_DATE_FIELD);
            }
            fp.setModification(new HistoryStamp(new LongIdLinkPair(modifierId, LinkUriUtil.getUserLinkUri(modifierId)),
                    modifiedDate));
            String sizeStr = (String) doc.get(Constants.FILE_SIZE_IN_BYTES_FIELD);
            Long size;
            if (sizeStr != null)
                size = Long.valueOf(sizeStr);
            else
                size = null;
            fp.setLength(size);
            fp.setMd5((String) doc.get(Constants.FILE_MD5_FIELD));
            fp.setVersionNumber(getInteger(doc, Constants.FILE_VERSION_FIELD));
            if (fp.getVersionNumber()==-1) {
                fp.setVersionNumber(1);
            }
            fp.setMajorVersion(getInteger(doc, Constants.FILE_MAJOR_VERSION_FIELD));
            fp.setMinorVersion(getInteger(doc, Constants.FILE_MINOR_VERSION_FIELD));
            LinkUriUtil.populateFileLinks(fp, owningEntityId, owningEntityType);
        } else {
            String primaryId = (String) doc.get(Constants.PRIMARY_FILE_ID_FIELD);
            if (primaryId!=null) {
                Long entryId = SearchResultBuilderUtil.getLong(doc, Constants.DOCID_FIELD);
                org.kablink.teaming.domain.FolderEntry entry = ami.getFolderModule().getEntry(null, entryId);
                fp = ResourceUtil.buildFileProperties(MiscUtil.getPrimaryFileAttachment(entry));
            }
        }
        return fp;
    }

    private Integer getInteger(Map doc, String fieldName) {
        String versionStr = (String) doc.get(fieldName);
        Integer version;
        if(versionStr != null)
            version = Integer.valueOf(versionStr);
        else
            version = null;
        return version;
    }

    private Long getLong(Map doc, String fieldName) {
        String versionStr = (String) doc.get(fieldName);
        Long version;
        if(versionStr != null)
            version = Long.valueOf(versionStr);
        else
            version = null;
        return version;
    }

    public Object getId(FileProperties obj) {
        return null;
    }

    public Object getParentId(FileProperties obj) {
        return null;
    }

    public SearchResultTreeNode<FileProperties> factoryTreeNode(FileProperties obj) {
        return null;
    }

    public Date getLastModified(FileProperties obj) {
        return obj.getModificationDate();
    }
}
