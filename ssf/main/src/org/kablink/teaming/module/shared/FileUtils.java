/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.shared;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.util.SpringContextUtil;

public class FileUtils {

	public static void setFileVersionAging(DefinableEntity entity) {
		Binder binder = entity.getParentBinder();
		if (!entity.getEntityType().equals(EntityType.folderEntry) && 
				!entity.getEntityType().equals(EntityType.user) &&
				!entity.getEntityType().equals(EntityType.group) &&
				!entity.getEntityType().equals(EntityType.application) &&
				!entity.getEntityType().equals(EntityType.applicationGroup)) {
			binder = (Binder)entity;
		}
		Boolean versionAgingEnabled = binder.getVersionAgingEnabled();
		if (versionAgingEnabled == null) versionAgingEnabled = Boolean.FALSE;
		Boolean zoneVersionAgingEnabled = Boolean.FALSE;
		AdminModule adminModule = (AdminModule) SpringContextUtil.getBean("adminModule");
		Long zoneVersionAgingMaxDays = adminModule.getFileVersionsMaxAge();
		if (zoneVersionAgingMaxDays != null && zoneVersionAgingMaxDays > 0) {
			zoneVersionAgingEnabled = Boolean.TRUE;
		}

		Long versionAgingDays = binder.getVersionAgingDays();
    	Collection<FileAttachment> atts = entity.getFileAttachments();
    	for (FileAttachment fa : atts) {
			Integer currentMajorVersion = -1;
    		Set<VersionAttachment> fileVersions = fa.getFileVersions();
    		for (VersionAttachment va : fileVersions) {
				//Is this version in the same major version category?
    			if (va.getMajorVersion() != currentMajorVersion) {
    				//This is a new major version category, reset the counters and clear aging flag
    				currentMajorVersion = va.getMajorVersion();
    				if (va.isAgingEnabled()) {
    					va.setAgingEnabled(Boolean.FALSE);
    				}
    			} else {
    				//This is a minor version that is not the highest in its major class. It is subject to aging
    				//Binder aging has both agingEnabled=true and agingDate != null
    				//Zone aging has agingEnabled=true and agingDate=null
    				if ((zoneVersionAgingEnabled.booleanValue() || versionAgingEnabled.booleanValue()) && 
    						(va.getAgingEnabled() == null || !va.isAgingEnabled())) {
    					va.setAgingEnabled(Boolean.TRUE);
    				} else if (!zoneVersionAgingEnabled.booleanValue() && !versionAgingEnabled.booleanValue() && 
    						(va.getAgingEnabled() != null && va.isAgingEnabled())) {
    					va.setAgingEnabled(Boolean.FALSE);
    				}
    				//Calculate the binder aging date (if any)
    				if (versionAgingDays != null) {
    					Date creationDate = va.getCreation().getDate();
    					Date agingDate = new Date(creationDate.getTime() + versionAgingDays*24*60*60*1000);
    					va.setAgingDate(agingDate);
    				} else if (va.getAgingDate() != null) {
    					//Make sure the aging days is null when binder aging is off so it is subject to zone wide aging
    					va.setAgingDate(null);
    				}
    			}
			}
    	}
	}
    
}
