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
 * Base class for BinderBrief, FolderEntryBrief, ReplyBrief, UserBrief and GroupBrief objects.  Brief objects
 * are typically returned in list results because they are more efficient to build than their full counterparts.
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

    /**
     * Date and time that the entity was created and the user who created it.
     */
    public HistoryStamp getCreation() {
        return creation;
    }

    public void setCreation(HistoryStamp creation) {
        this.creation = creation;
    }

    /**
     * A string identifying the type of this entity.  Possible values are <code>user</code>, <code>group</code>,
     * <code>folder</code>, <code>workspace</code> and <code>folderEntry</code>.
     */
    @XmlElement(name = "entity_type")
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Undocumented
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    @Undocumented
    @XmlElement(name = "icon_href")
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

    @XmlTransient
    public Date getModificationDate() {
        return this.modification==null ? null : this.modification.getDate().getTime();
    }

    public void setModification(HistoryStamp modification) {
        this.modification = modification;
    }

    /**
     * Information about the binder where this entity resides.
     */
    @XmlElement(name = "parent_binder")
    public ParentBinder getParentBinder() {
        return parentBinder;
    }

    public void setParentBinder(ParentBinder parentBinder) {
        this.parentBinder = parentBinder;
    }

    @Undocumented
    @XmlElement(name = "definition")
    public StringIdLinkPair getDefinition() {
        return definition;
    }

    public void setDefinition(StringIdLinkPair definition) {
        this.definition = definition;
    }

    /**
     * The title or displayable name of the entity.
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Description of the entity.  For replies, this is the text of the comment.
     */
    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
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

    @Override
    public String toString() {
        return super.toString() + " id=" + this.id;
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
