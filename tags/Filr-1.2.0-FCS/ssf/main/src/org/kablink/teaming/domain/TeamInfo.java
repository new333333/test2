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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

/**
 * User: david
 * Date: 5/18/12
 * Time: 9:16 AM
 */
public class TeamInfo {
    private Long id;
   	private String title;
   	private String entityType;
   	private String family;
   	private Boolean library;
   	private Integer definitionType; // Shows what kind binder this is, that is, whether workspace or folder. Corresponds to the constants in Definition.java
   	private String path;
   	private HistoryStampBrief creation;
   	private HistoryStampBrief modification;
   	private String permaLink;
   	private Boolean mirrored;
   	private Boolean homeDir;
   	private Boolean myFilesDir;
   	private Long parentBinderId;

    public HistoryStampBrief getCreation() {
        return creation;
    }

    public void setCreation(HistoryStampBrief creation) {
        this.creation = creation;
    }

    public Integer getDefinitionType() {
        return definitionType;
    }

    public void setDefinitionType(Integer definitionType) {
        this.definitionType = definitionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLibrary() {
        return library;
    }

    public void setLibrary(Boolean library) {
        this.library = library;
    }

    public Boolean getMirrored() {
        return mirrored;
    }

    public void setMirrored(Boolean mirrored) {
        this.mirrored = mirrored;
    }

    public Boolean getHomeDir() {
		return homeDir;
	}

	public void setHomeDir(Boolean homeDir) {
		this.homeDir = homeDir;
	}

    public Boolean getMyFilesDir() {
		return myFilesDir;
	}

	public void setMyFilesDir(Boolean myFilesDir) {
		this.myFilesDir = myFilesDir;
	}

	public HistoryStampBrief getModification() {
        return modification;
    }

    public void setModification(HistoryStampBrief modification) {
        this.modification = modification;
    }

    public Long getParentBinderId() {
        return parentBinderId;
    }

    public void setParentBinderId(Long parentBinderId) {
        this.parentBinderId = parentBinderId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
