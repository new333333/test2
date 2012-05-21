package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:37 PM
 */
public abstract class DefinableEntityBrief extends BaseRestObject {
    private Long id;
   	private Long parentBinderId;
   	private String title;
    private String definitionId;
    private Integer definitionType; // Shows what kind binder this is, that is, whether workspace or folder. Corresponds to the constants in Definition.java
   	private String entityType;
   	private String family;
    private HistoryStamp creation;
   	private HistoryStamp modification;

    protected DefinableEntityBrief() {
    }

    protected DefinableEntityBrief(String link) {
        super(link);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoryStamp getModification() {
        return modification;
    }

    public void setModification(HistoryStamp modification) {
        this.modification = modification;
    }

    @XmlElement(name = "parent_binder_id")
    public Long getParentBinderId() {
        return parentBinderId;
    }

    public void setParentBinderId(Long parentBinderId) {
        this.parentBinderId = parentBinderId;
    }

    @XmlElement(name = "definition_id")
    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    @XmlElement(name="definition_type")
    public Integer getDefinitionType() {
        return definitionType;
    }

    public void setDefinitionType(Integer definitionType) {
        this.definitionType = definitionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
