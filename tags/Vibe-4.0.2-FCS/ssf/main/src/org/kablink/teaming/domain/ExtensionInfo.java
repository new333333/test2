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
package org.kablink.teaming.domain;

/**
 * 
 * @author Nathan Jensen
 *
 */
public class ExtensionInfo extends PersistentObject  {

    public static String STATE_INSTALLED = "installed";
    public static String STATE_DELETED = "deleted";
	
	private String name="";
    private String title="";
	private String description="";
	private String version;
    
	private int type;
	private String author;
	private String authorEmail;
	private String authorSite;
	private String dateCreated;
	private String dateDeployed;

	public ExtensionInfo() {
		
	}    
	
    /**
     * Unique name of definition.
     * @hibernate.property length="64"
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    
    /**
     * Description of the Extension
     * @hibernate.property length="255"
     * 
     */
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * Return the extension type.
     * @hibernate.property 
     */
    public int getType() {
    	return type;
    }
    public void setType(int type) {
    	this.type = type;
    }

	/**
	 * Return the extension author
	 * @hibernate.property
	 */
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Return the extension author mail
	 * @hibernate.property
	 */
	public String getAuthorEmail() {
		return authorEmail;
	}
	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	/**
	 * Return the extension author site
	 * @hibernate.property
	 */
	public String getAuthorSite() {
		return authorSite;
	}
	public void setAuthorSite(String authorSite) {
		this.authorSite = authorSite;
	}

	/**
	 * Return the extension creation Date
	 * @hibernate.property
	 */
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String createDate) {
		this.dateCreated = createDate;
	}
	
    /**
     * Return the extension name.
     */
    public String toString() {
    	return name;
    }
    
    /**
     * Return zone id
     * @hibernate.property 
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
    
    public void setDateDeployed(String dateDeployed) {
		this.dateDeployed = dateDeployed;
	}

	public String getDateDeployed() {
		return dateDeployed;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}
	
    public boolean equals(Object obj) {
    	if (obj == null) return false;
		if (obj == this) return true;
    	if(obj instanceof ExtensionInfo){
    		ExtensionInfo info = ((ExtensionInfo)obj);
    		if(info.getName().equals(name))
    	    	return true;
    	}
    	
    	return false;
    }
	
}
