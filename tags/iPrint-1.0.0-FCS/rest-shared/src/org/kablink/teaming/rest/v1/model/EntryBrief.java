package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:39 PM
 */
public abstract class EntryBrief extends DefinableEntityBrief {
    protected String entryType;

    protected EntryBrief() {
        setDocType("entry");
    }

    protected EntryBrief(String link) {
        super(link);
        setDocType("entry");
    }

    protected EntryBrief(EntryBrief orig) {
        super(orig);
        this.entryType = orig.entryType;
    }

    @XmlElement(name = "entry_type")
    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }
}
