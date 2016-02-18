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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.rest.v1.model;

import org.kablink.teaming.rest.v1.annotations.Undocumented;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Base class for binders, folder entries, replies and principals (users and groups)
 */
public abstract class DefinableEntity extends SearchableObject {
    private Long id;
   	private ParentBinder parentBinder;
   	private String title;
    private StringIdLinkPair definition;
    private Description description;
    private String entityType;
    private String family;
    private String icon;
    private String permaLink;
    @XmlElementWrapper(name="permalinks")
    @XmlElement(name="permalink")
    private List<Link> additionalPermaLinks;
    private HistoryStamp creation;
    private HistoryStamp modification;
   	private AverageRating averageRating;
   	private Boolean eventAsIcalString;
    private BaseFileProperties [] attachments;
    private CustomField [] customFields;

    public DefinableEntity() {
        super();
    }



    protected DefinableEntity(DefinableEntity orig) {
        super(orig);
        this.id = orig.id;
        this.parentBinder = orig.parentBinder;
        this.title = orig.title;
        this.definition = orig.definition;
        this.description = orig.description;
        this.entityType = orig.entityType;
        this.family = orig.family;
        this.icon = orig.icon;
        this.permaLink = orig.permaLink;
        this.creation = orig.creation;
        this.modification = orig.modification;
        this.averageRating = orig.averageRating;
        this.eventAsIcalString = orig.eventAsIcalString;
        this.attachments = orig.attachments;
        this.customFields = orig.customFields;
    }

    public DefinableEntity(DefinableEntityBrief orig) {
        super(orig);
        this.id = orig.getId();
        this.parentBinder = orig.getParentBinder();
        this.title = orig.getTitle();
        this.definition = orig.getDefinition();
        this.description = orig.getDescription();
        this.entityType = orig.getEntityType();
        this.family = orig.getFamily();
        this.icon = orig.getIcon();
        this.permaLink = orig.getPermaLink();
        this.creation = orig.getCreation();
        this.modification = orig.getModification();
    }

    @Undocumented
    @XmlElement(name="average_rating")
    public AverageRating getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(AverageRating averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Date and time that the entity was created and the user who created it.
     */
    @XmlElement(name="creation")
    public HistoryStamp getCreation() {
        return creation;
    }

    public void setCreation(HistoryStamp creation) {
        this.creation = creation;
    }

    @Undocumented
    @XmlElement(name="definition")
    public StringIdLinkPair getDefinition() {
        return definition;
    }

    public void setDefinition(StringIdLinkPair definition) {
        this.definition = definition;
    }

    /**
     * Description of the entity.  For replies, this is the text of the comment.
     */
    @XmlElement(name="description")
    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    /**
     * A string identifying the type of this entity.  Possible values are "user", "group", "folder", "workspace" and "folderEntry".
     */
    @XmlElement(name="entity_type")
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Undocumented
    @XmlElement(name="event_as_ical_string")
    public Boolean isEventAsIcalString() {
        return eventAsIcalString;
    }

    public void setEventAsIcalString(Boolean eventAsIcalString) {
        this.eventAsIcalString = eventAsIcalString;
    }

    @Undocumented
    @XmlElement(name="family")
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    @Undocumented
    @XmlElement(name="icon_href")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * An ID for the entity.  This is guaranteed to be unique for each entity type, but not necessarily unique among all entities.
     *
     * <p>For example, there will only be 1 user with an ID of 12, but there might also be a folder with an ID of 12.</p>
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * The date and time when the entity was last modified and the user who modified it.
     */
    public HistoryStamp getModification() {
        return modification;
    }

    public void setModification(HistoryStamp modification) {
        this.modification = modification;
    }

    /**
     * Information about the binder where this entity resides.
     */
    @XmlElement(name="parent_binder")
    public ParentBinder getParentBinder() {
        return parentBinder;
    }

    public void setParentBinder(ParentBinder parentBinder) {
        this.parentBinder = parentBinder;
    }

    /**
     * A URL in the web application for this entity.
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

    /**
     * The title or displayable name of the entity.
     */
    @XmlElement(name="title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Undocumented
    @XmlElementWrapper(name="attachments")
    @XmlElement(name = "attachment")
    public BaseFileProperties[] getAttachments() {
        return attachments;
    }

    public void setAttachments(BaseFileProperties[] attachments) {
        this.attachments = attachments;
    }

    @Undocumented
    @XmlElementWrapper(name="custom_fields")
    @XmlElement(name = "field")
    public CustomField [] getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomField [] customFields) {
        this.customFields = customFields;
    }

    public CustomField findField(String name) {
        if (this.customFields!=null) {
            for (CustomField field : this.customFields) {
                if (name.equals(field.getName())) {
                    return field;
                }
            }
        }
        return null;
    }

    @Override
    @XmlTransient
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public void setDisplayName(String name) {
        setTitle(name);
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
