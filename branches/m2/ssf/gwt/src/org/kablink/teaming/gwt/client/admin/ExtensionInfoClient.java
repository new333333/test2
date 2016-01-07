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
package org.kablink.teaming.gwt.client.admin;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author nbjensen
 *
 */
public class ExtensionInfoClient implements IsSerializable {
	
	private String author;
	private String authorEmail;
	private String authorSite;
	private String dateCreated;
	private String dateDeployed;
	private String description;
	private String id;
	private String name;
	private String version;
	private String title;
	private Long zoneId;
	private String zoneName;
	
	public ExtensionInfoClient(){
	}

	public String getAuthor() {
		return author;
	}
    public String getAuthorEmail() {
		return authorEmail;
	}
	public String getAuthorSite() {
		return authorSite;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public String getDateDeployed() {
		return dateDeployed;
	}
	public String getDescription() {
		return description;
	}

	public String getId() {
        return id;
    }
	
	public String getName() {
		return name;
	}

	public Long getZoneId(){
		return zoneId;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}
	
	public void setAuthorSite(String authorSite) {
		this.authorSite = authorSite;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateDeployed(String dateDeployed) {
		this.dateDeployed = dateDeployed;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
        this.id = id;
    }

	public void setName(String name) {
		this.name = name;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getZoneName() {
		return zoneName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
