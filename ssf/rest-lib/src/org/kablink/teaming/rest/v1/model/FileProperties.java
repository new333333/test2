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
package org.kablink.teaming.rest.v1.model;

import org.kablink.teaming.rest.v1.annotations.Undocumented;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * File metadata.
 */
@XmlRootElement(name="file")
public class FileProperties extends BaseFileProperties {

    private EntityId owningEntity;
    private ParentBinder binder;
	private String name;
	private Long lockedBy;
	private Calendar lockExpiration;
    private String permaLink;
    @XmlElementWrapper(name="permalinks")
    @XmlElement(name="permalink")
    private List<Link> additionalPermaLinks;

	public FileProperties() {
		super();
	}

    protected FileProperties(FileProperties orig) {
        super(orig);
        this.owningEntity = orig.owningEntity;
        this.binder = orig.binder;
        this.name = orig.name;
        this.lockedBy = orig.lockedBy;
        this.lockExpiration = orig.lockExpiration;
        this.permaLink = orig.permaLink;
        this.additionalPermaLinks = orig.additionalPermaLinks;
    }

    /**
     * Entity to which the file belongs.
     */
    @XmlElement(name="owning_entity")
    public EntityId getOwningEntity() {
        return owningEntity;
    }

    public void setOwningEntity(EntityId owningEntity) {
        this.owningEntity = owningEntity;
    }

    /**
     * The parent folder where the file resides.
     */
    @XmlElement(name="parent_binder")
    public ParentBinder getBinder() {
        return binder;
    }

    public void setBinder(ParentBinder binder) {
        this.binder = binder;
    }

    /**
     * The file name.
     */
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @XmlElement(name="locked_by")
	public Long getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(Long lockedBy) {
		this.lockedBy = lockedBy;
	}

    @XmlElement(name="lock_expiration")
	public Calendar getLockExpiration() {
		return lockExpiration;
	}

	public void setLockExpiration(Calendar lockExpiration) {
		this.lockExpiration = lockExpiration;
	}

    /**
     * The web application URL of the file.
     */
    @XmlElement(name="permalink")
    public String getPermaLink() {
        return permaLink;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    @Undocumented
    public List<Link> getAdditionalPermaLinks() {
        return additionalPermaLinks;
    }

    public void addAdditionalPermaLink(String relation, String uri) {
        addAdditionalPermaLink(new Link(relation, uri));
    }

    public void addAdditionalPermaLink(String uri) {
        addAdditionalPermaLink(new Link(null, uri));
    }

    public void addAdditionalPermaLink(Link link) {
        if (additionalPermaLinks ==null) {
            additionalPermaLinks = new ArrayList<Link>();
        }
        additionalPermaLinks.add(link);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new FileProperties(this);
    }

    @Override
    @XmlTransient
    public String getDisplayName() {
        return getName();
    }

    @Override
    public void setDisplayName(String name) {
        setName(name);
    }

    @Override
    public Calendar getCreateDate() {
        HistoryStamp stamp = getCreation();
        if (stamp!=null) {
            return stamp.getDate();
        } else {
            return new GregorianCalendar(1970, 0, 0);
        }
    }
}
