package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:37 PM
 */
public abstract class DefinableEntityBrief extends SearchableObject {
    private Long id;
   	private ParentBinder parentBinder;
   	private String title;
    private Description description;
    private StringIdLinkPair definition;
   	private String entityType;
   	private String family;
    private String icon;
    private String permaLink;
    @XmlElementWrapper(name="permalinks")
    @XmlElement(name="permalink")
    private List<Link> additionalPermaLinks;
    private HistoryStamp creation;
   	private HistoryStamp modification;

    protected DefinableEntityBrief() {
    }

    protected DefinableEntityBrief(String link) {
        super(link);
    }

    protected DefinableEntityBrief(DefinableEntityBrief orig) {
        super(orig);
        this.id = orig.id;
        this.parentBinder = orig.parentBinder;
        this.title = orig.title;
        this.description = orig.description;
        this.definition = orig.definition;
        this.entityType = orig.entityType;
        this.family = orig.family;
        this.icon = orig.icon;
        this.permaLink = orig.permaLink;
        this.creation = orig.creation;
        this.modification = orig.modification;
        this.additionalPermaLinks = orig.additionalPermaLinks;
    }

    public HistoryStamp getCreation() {
        return creation;
    }

    public void setCreation(HistoryStamp creation) {
        this.creation = creation;
    }

    @XmlElement(name = "entity_type")
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

    @XmlElement(name = "icon_href")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoryStamp getModification() {
        return modification;
    }

    @XmlTransient
    public Date getModificationDate() {
        return this.modification==null ? null : this.modification.getDate().getTime();
    }

    public void setModification(HistoryStamp modification) {
        this.modification = modification;
    }

    @XmlElement(name = "parent_binder")
    public ParentBinder getParentBinder() {
        return parentBinder;
    }

    public void setParentBinder(ParentBinder parentBinder) {
        this.parentBinder = parentBinder;
    }

    @XmlElement(name = "definition")
    public StringIdLinkPair getDefinition() {
        return definition;
    }

    public void setDefinition(StringIdLinkPair definition) {
        this.definition = definition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    @XmlElement(name="permalink")
    public String getPermaLink() {
        return permaLink;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

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
    public String toString() {
        return super.toString() + " id=" + this.id;
    }
}
