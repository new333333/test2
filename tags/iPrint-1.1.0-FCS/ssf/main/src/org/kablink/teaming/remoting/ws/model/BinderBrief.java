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
package org.kablink.teaming.remoting.ws.model;

import java.io.Serializable;

public class BinderBrief implements Serializable {

	private Long id;
	private String title;
	private String entityType;
	private String family;
	private Boolean library;
	private Integer definitionType; // Shows what kind binder this is, that is, whether workspace or folder. Corresponds to the constants in Definition.java
	private String path;
	private Timestamp creation;
	private Timestamp modification;
	private String permaLink;
	private Boolean mirrored;
	private Long parentBinderId;

	public BinderBrief() {}

	public BinderBrief(Long id, String title, String entityType, String family, Boolean library, Integer definitionType, String path, Timestamp creation, Timestamp modification, String permaLink, Boolean mirrored, Long parentBinderId) {
		this.id = id;
		this.title = title;
		this.entityType = entityType;
		this.family = family;
		this.library = library;
		this.definitionType = definitionType;
		this.path = path;
		this.creation = creation;
		this.modification = modification;
		this.permaLink = permaLink;
		this.mirrored = mirrored;
		this.parentBinderId = parentBinderId;
	}

	public Timestamp getCreation() {
		return creation;
	}
	public void setCreation(Timestamp creation) {
		this.creation = creation;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Timestamp getModification() {
		return modification;
	}
	public void setModification(Timestamp modification) {
		this.modification = modification;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public Long getParentBinderId() {
		return parentBinderId;
	}

	public void setParentBinderId(Long parentBinderId) {
		this.parentBinderId = parentBinderId;
	}

}
