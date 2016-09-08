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
package org.kablink.teaming.domain;

import java.util.Date;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public abstract class UserPrincipal extends Principal {

	protected Long diskQuota;
	protected Long fileSizeLimit;
	protected Boolean downloadEnabled;
	protected Boolean webAccessEnabled;
	protected Boolean adHocFoldersEnabled;
    // lastConfigUpdate is updated whenever one of the above values changes.  It is specifically
    // used in the last-modified time calculation for the /self/my_files/library_children REST API.
    protected Date adHocFoldersLastModified;
    
	// For use by Hibernate only
	protected UserPrincipal() {
	}
	
	// For user by application
	protected UserPrincipal(IdentityInfo identityInfo) {
		setIdentityInfo(identityInfo);
	}
	
	/**
     * @hibernate.property
     */
	public Long getDiskQuota() {
		if (diskQuota == null) return new Long(0);
		return diskQuota;
	}
	/**
	 * @param diskQuota to set.
	 */
	public void setDiskQuota(Long diskQuota) {
		this.diskQuota = diskQuota;
	}

	/**
     * @hibernate.property
     */
	public Long getFileSizeLimit() {
		return fileSizeLimit;		//Can be null;  null means no limit.
	}
	/**
	 * @param fileSizeLimit to set.
	 */
	public void setFileSizeLimit(Long fileSizeLimit) {
		this.fileSizeLimit = fileSizeLimit;
	}
		
    /**
     * @hibernate.property
     * @return
     */
    public Boolean isDownloadEnabled() {
    	return downloadEnabled;
    }
    public void setDownloadEnabled(Boolean downloadEnabled) {
    	this.downloadEnabled = downloadEnabled;
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public Boolean isWebAccessEnabled() {
    	return webAccessEnabled;
    }
    public void setWebAccessEnabled(Boolean webAccessEnabled) {
    	this.webAccessEnabled = webAccessEnabled;
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public Boolean isAdHocFoldersEnabled() {
    	return adHocFoldersEnabled;
    }
    public void setAdHocFoldersEnabled(Boolean adHocFoldersEnabled) {
    	this.adHocFoldersEnabled = adHocFoldersEnabled;
    }

    public Date getAdHocFoldersLastModified() {
        return adHocFoldersLastModified;
    }

    public void setAdHocFoldersLastModified(Date adHocFoldersLastModified) {
        this.adHocFoldersLastModified = adHocFoldersLastModified;
    }
}
